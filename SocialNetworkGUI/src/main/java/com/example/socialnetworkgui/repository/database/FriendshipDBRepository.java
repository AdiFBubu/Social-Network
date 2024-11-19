package com.example.socialnetworkgui.repository.database;


import com.example.socialnetworkgui.domain.Friendship;
import com.example.socialnetworkgui.domain.Tuple;
import com.example.socialnetworkgui.domain.validators.Validator;
import com.example.socialnetworkgui.repository.Repository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class FriendshipDBRepository implements Repository<Tuple<Long, Long>, Friendship> {
    private final String url;
    private final String username;
    private final String password;
    private final Validator<Friendship> validator;

    public FriendshipDBRepository(Validator<Friendship> validator, String url, String username, String password) {
        this.validator = validator;
        this.url = url;
        this.username = username;
        this.password = password;
    }


//    private void loadFromDB() {
//        try (Connection connection = DriverManager.getConnection(url, username, password);
//             PreparedStatement statement = connection.prepareStatement("SELECT * FROM friendships");
//             ResultSet resultSet = statement.executeQuery()) {
//
//            while (resultSet.next()) {
//                Long id1 = resultSet.getLong("user1ID");
//                Long id2 = resultSet.getLong("user2ID");
//                LocalDateTime createdAt = resultSet.getTimestamp("LocalDateTime").toLocalDateTime();
//                Friendship friendship = new Friendship();
//                friendship.setId(new Tuple<>(id1, id2));
//                friendship.setDate(createdAt);
//                super.save(friendship);
//            }
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }
//    }


    @Override
    public Iterable<Friendship> findAll() {
        Set<Friendship> friendships = new HashSet<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM friendships");
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Tuple<Long, Long> id = new Tuple<>(resultSet.getLong("user1ID"), resultSet.getLong("user2ID"));
                LocalDateTime localDateTime = resultSet.getTimestamp("LocalDateTime").toLocalDateTime();
                Friendship friendship = new Friendship();
                friendship.setId(id);
                friendship.setDate(localDateTime);
                friendships.add(friendship);
            }
            return friendships;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
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
                Friendship friendship = new Friendship();
                friendship.setId(ID);
                friendship.setDate(localDateTime);
                return Optional.of(friendship);
            }
            return Optional.empty();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Friendship> save(Friendship entity) {

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("INSERT INTO friendships (\"user1ID\", \"user2ID\", \"LocalDateTime\") VALUES (?, ?, ?)");) {

            validator.validate(entity);

            statement.setLong(1, entity.getId().getE1());
            statement.setLong(2, entity.getId().getE2());
            statement.setTimestamp(3, Timestamp.valueOf(entity.getDate()));
            statement.executeUpdate();

            return Optional.empty();

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
             PreparedStatement statement = connection.prepareStatement("UPDATE friendships SET \"LocalDateTime\" = ? WHERE (\"user1ID\" = ? AND \"user2ID\" = ?) OR (\"user1ID\" = ? AND \"user2ID\" = ?)")) {

            validator.validate(entity);

            statement.setTimestamp(1, Timestamp.valueOf(entity.getDate().toString()));
            statement.setLong(2, entity.getId().getE1());
            statement.setLong(3, entity.getId().getE2());
            statement.setLong(4, entity.getId().getE2());
            statement.setLong(5, entity.getId().getE1());

            int rez = statement.executeUpdate();
            if (rez == 0)
                return Optional.of(entity);
            return Optional.empty();

        } catch (SQLException exception) {
            throw new RuntimeException(exception);
        }

    }
}
