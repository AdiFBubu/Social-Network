package com.example.socialnetworkgui.repository.paging;

import com.example.socialnetworkgui.domain.Friendship;
import com.example.socialnetworkgui.domain.Tuple;
import com.example.socialnetworkgui.domain.dto.UserFilterDTO;
import com.example.socialnetworkgui.domain.validators.Validator;
import com.example.socialnetworkgui.utils.paging.Page;
import com.example.socialnetworkgui.utils.paging.Pageable;
import javafx.util.Pair;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

public class FriendshipPagingDBRepository implements FriendshipPagingRepository {

    private String url;
    private String username;
    private String password;
    private final Validator<Friendship> validator;

    public FriendshipPagingDBRepository(String url, String username, String password, Validator<Friendship> validator) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.validator = validator;
    }


    @Override
    public Optional<Friendship> findOne(Tuple<Long, Long> ID) {
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM friendships WHERE (\"user1ID\" = ? AND \"user2ID\" = ?) OR (\"user1ID\" = ? AND \"user2ID\" = ?)")) {

            statement.setLong(1, ID.getE1());
            statement.setLong(2, ID.getE2());
            statement.setLong(3, ID.getE2());
            statement.setLong(4, ID.getE1());

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                LocalDateTime localDateTime = resultSet.getTimestamp("LocalDateTime").toLocalDateTime();
                Boolean state = resultSet.getBoolean("state");
                Friendship friendship = new Friendship();
                friendship.setId(ID);
                friendship.setDate(localDateTime);
                friendship.setState(state);
                return Optional.of(friendship);
            }
            return Optional.empty();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Iterable<Friendship> findAll() {
        Set<Friendship> friendships = new HashSet<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM friendships");
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Tuple<Long, Long> id = new Tuple<>(resultSet.getLong("user1ID"), resultSet.getLong("user2ID"));
                LocalDateTime localDateTime = resultSet.getTimestamp("LocalDateTime").toLocalDateTime();
                Boolean state = resultSet.getBoolean("state");
                Friendship friendship = new Friendship();
                friendship.setId(id);
                friendship.setDate(localDateTime);
                friendship.setState(state);
                friendships.add(friendship);
            }
            return friendships;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Friendship> save(Friendship entity) {

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("INSERT INTO friendships (\"user1ID\", \"user2ID\", \"LocalDateTime\", state) VALUES (?, ?, ?, ?)");) {

            validator.validate(entity);

            Optional<Friendship> friendship = findOne(entity.getId());
            if (friendship.isPresent())
                return Optional.empty();

            statement.setLong(1, entity.getId().getE1());
            statement.setLong(2, entity.getId().getE2());
            statement.setTimestamp(3, Timestamp.valueOf(entity.getDate()));
            statement.setBoolean(4, entity.getState());
            statement.executeUpdate();

            return Optional.of(entity);

        } catch (SQLException exception) {
            throw new RuntimeException(exception);
        }
    }

    @Override
    public Optional<Friendship> delete(Tuple<Long, Long> aLong) {

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("DELETE FROM friendships WHERE (\"user1ID\" = ? AND \"user2ID\" = ?) OR (\"user1ID\" = ? AND \"user2ID\" = ?)")) {

            Optional<Friendship> friendship = findOne(aLong);
            if (friendship.isEmpty())
                return Optional.empty();

            statement.setLong(1, aLong.getE1());
            statement.setLong(2, aLong.getE2());
            statement.setLong(3, aLong.getE2());
            statement.setLong(4, aLong.getE1());

            int rez = statement.executeUpdate();
            if (rez == 0)
                return Optional.empty();
            return friendship;

        } catch (SQLException exception) {
            throw new RuntimeException(exception);
        }

    }

    @Override
    public Optional<Friendship> update(Friendship entity) {

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("UPDATE friendships SET state = ? WHERE (\"user1ID\" = ? AND \"user2ID\" = ?) OR (\"user1ID\" = ? AND \"user2ID\" = ?)")) {

            validator.validate(entity);

            statement.setBoolean(1, entity.getState());
            statement.setLong(2, entity.getId().getE1());
            statement.setLong(3, entity.getId().getE2());
            statement.setLong(4, entity.getId().getE2());
            statement.setLong(5, entity.getId().getE1());

            int rez = statement.executeUpdate();
            if (rez == 0)
                return Optional.empty();
            return Optional.of(entity);

        } catch (SQLException exception) {
            throw new RuntimeException(exception);
        }

    }

    private List<Friendship> findAllOnPage(Connection connection, Pageable pageable, UserFilterDTO filter) {
        List<Friendship> friendshipsOnPage = new ArrayList<>();
        String sql = "select * from friendships";
        Pair<String, List<Object>> sqlFilter = toSql(filter);
        if (!sqlFilter.getValue().isEmpty()) {
            sql += " where " + sqlFilter.getKey();
        }
        sql += " limit ? offset ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            int paramIndex = 0;
            for (Object param : sqlFilter.getValue()) {
                statement.setObject(++ paramIndex, param);
            }
            statement.setInt(++ paramIndex, pageable.getPageSize());
            statement.setInt(++ paramIndex, pageable.getPageSize() * pageable.getPageNumber());
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Long user1ID = resultSet.getLong("user1ID");
                    Long user2ID = resultSet.getLong("user2ID");
                    LocalDateTime localDateTime = resultSet.getTimestamp("LocalDateTime").toLocalDateTime();
                    boolean state = resultSet.getBoolean("state");
                    Friendship friendship = new Friendship();
                    friendship.setId(new Tuple<>(user1ID, user2ID));
                    friendship.setDate(localDateTime);
                    friendship.setState(state);
                    friendshipsOnPage.add(friendship);
                }
                return friendshipsOnPage;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Page<Friendship> findAllOnPage(Pageable pageable, UserFilterDTO userFilter) {
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            int totalNumberOfMovies = count(connection, userFilter);
            List<Friendship> friendships;
            if (totalNumberOfMovies > 0) {
                friendships = findAllOnPage(connection, pageable, userFilter);
            }
            else {
                friendships = Collections.emptyList();
            }
            return new Page<>(friendships, totalNumberOfMovies);
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Page<Friendship> findAllOnPage(Pageable pageable) {
        return findAllOnPage(pageable, null);
    }

    private Pair<String, List<Object>> toSql(UserFilterDTO filter) {
        if (filter == null) {
            return new Pair<>("", Collections.emptyList());
        }
        List<String> conditions = new ArrayList<>();
        List<Object> params = new ArrayList<>();
        filter.getIdUser().ifPresent(idUserFilter -> {
            conditions.add("(\"user1ID\" = ? OR \"user2ID\" = ?)");
            params.add(idUserFilter);
            params.add(idUserFilter);
        });
        filter.getState().ifPresent(stateFilter -> {
            conditions.add("\"state\" = ?");
            params.add(stateFilter);
        });
        filter.getFirstName().ifPresent(firstNameFilter -> {
            conditions.add("first_name LIKE ?");
            params.add("%" + firstNameFilter + "%");
        });
        filter.getLastName().ifPresent(lastNameFilter -> {
            conditions.add("last_name LIKE ?");
            params.add("%" + lastNameFilter + "%");
        });
        String sql = String.join(" AND ", conditions);
        return new Pair<>(sql, params);
    }

    private int count(Connection connection, UserFilterDTO filter) throws SQLException {
        String sql = "SELECT count(*) as count from friendships";
        Pair<String, List<Object>> sqlFilter = toSql(filter);
        if (!sqlFilter.getKey().isEmpty()) {
            sql += " WHERE " + sqlFilter.getKey();
        }
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            int paramIndex = 0;
            for (Object param : sqlFilter.getValue()) {
                statement.setObject(++ paramIndex, param);
            }
            try (ResultSet resultSet = statement.executeQuery()) {
                int totalNumberOfUsers = 0;
                if (resultSet.next()) {
                    totalNumberOfUsers = resultSet.getInt("count");
                }
                return totalNumberOfUsers;
            }
        }
    }


}
