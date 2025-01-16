package com.example.socialnetworkgui.repository.paging;

import com.example.socialnetworkgui.domain.User;
import com.example.socialnetworkgui.domain.dto.UserFilterDTO;
import com.example.socialnetworkgui.domain.validators.Validator;
import com.example.socialnetworkgui.utils.paging.Page;
import com.example.socialnetworkgui.utils.paging.Pageable;
import javafx.util.Pair;

import java.sql.*;
import java.util.*;

public class UserPagingDBRepository implements UserPagingRepository {

    private String url;
    private String username;
    private String password;
    private final Validator<User> validator;

    public UserPagingDBRepository(String url, String username, String password, Validator<User> validator) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.validator = validator;
    }


    @Override
    public Optional<User> findOne(Long aLong) {
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM users WHERE id = ?")) {

            statement.setLong(1, aLong);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                Long id = resultSet.getLong("id");
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");
                String urlImage = resultSet.getString("image_url");
                User user = new User(firstName, lastName);
                user.setImageUrl(urlImage);
                user.setId(id);
                return Optional.of(user);
            }
            return Optional.empty();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Iterable<User> findAll() {
        Set<User> users = new HashSet<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM users");
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Long id = resultSet.getLong("id");
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");
                String urlImage = resultSet.getString("image_url");
                User user = new User(firstName, lastName);
                user.setImageUrl(urlImage);
                user.setId(id);
                users.add(user);
            }
            return users;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<User> save(User entity) {

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statementFind = connection.prepareStatement("Select * FROM users WHERE first_name = ? AND last_name = ?");
             PreparedStatement statementAdd = connection.prepareStatement("INSERT INTO users (first_name, last_name, image_url) VALUES (?, ?, ?) RETURNING id")) {

            statementFind.setString(1, entity.getFirstName());
            statementFind.setString(2, entity.getLastName());
            ResultSet resultSetFind = statementFind.executeQuery();
            if (resultSetFind.next())
                return Optional.empty();

            validator.validate(entity);

            statementAdd.setString(1, entity.getFirstName());
            statementAdd.setString(2, entity.getLastName());
            statementAdd.setString(3, entity.getImageUrl());
            ResultSet resultSet =  statementAdd.executeQuery();
            if (!resultSet.next())
                return Optional.empty();
            int generatedId = resultSet.getInt("id");
            entity.setId((long) generatedId);

            return Optional.of(entity);

        } catch (SQLException exception) {
            throw new RuntimeException(exception);
        }
    }

    @Override
    public Optional<User> delete(Long aLong) {

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statementDelete = connection.prepareStatement("DELETE FROM users WHERE id = ?")) {

            Optional<User> user = findOne(aLong);
            if (user.isEmpty())
                return Optional.empty();

            statementDelete.setLong(1, aLong);
            int rez = statementDelete.executeUpdate();
            if (rez == 0)
                return Optional.empty();
            return user;

        } catch (SQLException exception) {
            throw new RuntimeException(exception);
        }

    }

    @Override
    public Optional<User> update(User entity) {

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("UPDATE users SET \"first_name\" = ?, \"last_name\" = ?, \"image_url\" = ? WHERE id = ? RETURNING id")) {

            validator.validate(entity);

            statement.setString(1, entity.getFirstName());
            statement.setString(2, entity.getLastName());
            statement.setString(3, entity.getImageUrl());
            statement.setLong(4, entity.getId());

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                long id = resultSet.getLong("id");
                entity.setId((long) id);
                return Optional.of(entity);
            }
            return Optional.empty();

        } catch (SQLException exception) {
            throw new RuntimeException(exception);
        }
    }

    private List<User> findAllOnPage(Connection connection, Pageable pageable, UserFilterDTO filter) {
        List<User> usersOnPage = new ArrayList<>();
        String sql = "select * from users";
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
                    Long id = resultSet.getLong("id");
                    String firstName = resultSet.getString("first_name");
                    String lastName = resultSet.getString("last_name");
                    String imageUrl = resultSet.getString("image_url");
                    User user = new User(firstName, lastName);
                    user.setImageUrl(imageUrl);
                    user.setId(id);
                    usersOnPage.add(user);
                }
                return usersOnPage;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Page<User> findAllOnPage(Pageable pageable, UserFilterDTO userFilter) {
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            int totalNumberOfMovies = count(connection, userFilter);
            List<User> users;
            if (totalNumberOfMovies > 0) {
                users = findAllOnPage(connection, pageable, userFilter);
            }
            else {
                users = Collections.emptyList();
            }
            return new Page<>(users, totalNumberOfMovies);
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Page<User> findAllOnPage(Pageable pageable) {
        return findAllOnPage(pageable, null);
    }

    private Pair<String, List<Object>> toSql(UserFilterDTO filter) {
        if (filter == null) {
            return new Pair<>("", Collections.emptyList());
        }
        List<String> conditions = new ArrayList<>();
        List<Object> params = new ArrayList<>();
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
        String sql = "SELECT count(*) as count from users";
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
