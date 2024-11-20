package com.example.socialnetworkgui.repository.database;


import com.example.socialnetworkgui.domain.User;
import com.example.socialnetworkgui.domain.validators.Validator;
import com.example.socialnetworkgui.repository.Repository;

import java.sql.*;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class UserDBRepository implements Repository<Long, User> {

    private final String url;
    private final String username;
    private final String password;
    private final Validator<User> validator;

    public UserDBRepository(String url, String username, String password, Validator<User> validator) {
        this.validator = validator;
        this.url = url;
        this.username = username;
        this.password = password;
    }


//    private void loadFromDB() {
//        try (Connection connection = DriverManager.getConnection(url, username, password);
//             PreparedStatement statement = connection.prepareStatement("SELECT * FROM users");
//             ResultSet resultSet = statement.executeQuery()) {
//
//            while (resultSet.next()) {
//                Long id = resultSet.getLong("id");
//                String firstName = resultSet.getString("first_name");
//                String lastName = resultSet.getString("last_name");
//                User user = new User(firstName, lastName);
//                user.setId(id);
//                super.save(user);
//            }
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }
//    }

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
                User user = new User(firstName, lastName);
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
                User user = new User(firstName, lastName);
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
             PreparedStatement statementAdd = connection.prepareStatement("INSERT INTO users (first_name, last_name) VALUES (?, ?) RETURNING id")) {

            statementFind.setString(1, entity.getFirstName());
            statementFind.setString(2, entity.getLastName());
            ResultSet resultSetFind = statementFind.executeQuery();
            if (resultSetFind.next())
                return Optional.empty();

            validator.validate(entity);

            statementAdd.setString(1, entity.getFirstName());
            statementAdd.setString(2, entity.getLastName());
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
                 PreparedStatement statement = connection.prepareStatement("UPDATE users SET \"first_name\" = ?, \"last_name\" = ? WHERE id = ? RETURNING id")) {

                validator.validate(entity);

                statement.setString(1, entity.getFirstName());
                statement.setString(2, entity.getLastName());
                statement.setLong(3, entity.getId());

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

}
