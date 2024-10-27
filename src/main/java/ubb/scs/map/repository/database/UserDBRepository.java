package ubb.scs.map.repository.database;

import ubb.scs.map.domain.User;
import ubb.scs.map.domain.validators.Validator;
import ubb.scs.map.repository.Repository;
import ubb.scs.map.repository.memory.InMemoryRepository;

import java.sql.*;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class UserDBRepository extends InMemoryRepository<Long, User> {

    private final String url;
    private final String username;
    private final String password;

    public UserDBRepository(String url, String username, String password, Validator<User> validator) {
        super(validator);
        this.url = url;
        this.username = username;
        this.password = password;
        loadFromDB();
    }


    private void loadFromDB() {
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM users");
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Long id = resultSet.getLong("id");
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");
                User user = new User(firstName, lastName);
                user.setId(id);
                super.save(user);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /*
    @Override
    public Iterable<User> findAll() {
        Set<User> users = new HashSet<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM users");
             ResultSet resultSet = statement.executeQuery();) {

            while (resultSet.next()) {
                Long id = resultSet.getLong("id");
                String firstName = resultSet.getString("firstName");
                String lastName = resultSet.getString("lastName");
                User user = new User(firstName, lastName);
                user.setId(id);
                users.add(user);
            }
            return users;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

     */

    @Override
    public Optional<User> save(User entity) {

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("INSERT INTO users (first_name, last_name) VALUES (?, ?) RETURNING id");) {
            statement.setString(1, entity.getFirstName());
            statement.setString(2, entity.getLastName());
            ResultSet rez = statement.executeQuery();
            if (rez.next()) {
                Long id = rez.getLong("id");
                entity.setId(id);
                return super.save(entity);
            }
        } catch (SQLException exception) {
            throw new RuntimeException(exception);
        }
        return Optional.empty();

    }

    @Override
    public Optional<User> delete(Long aLong) {
        Optional<User> entity = super.delete(aLong);
        if (entity.isPresent()) {
            try (Connection connection = DriverManager.getConnection(url, username, password);
                 PreparedStatement statement = connection.prepareStatement("DELETE FROM users WHERE id = ?");) {
                statement.setLong(1, aLong);
                statement.executeUpdate();
            } catch (SQLException exception) {
                throw new RuntimeException(exception);
            }
        }
        return entity;
    }

    @Override
    public Optional<User> update(User entity) {
        Optional<User> entity2 = super.update(entity);
        if (entity2.isEmpty()) {
            try (Connection connection = DriverManager.getConnection(url, username, password);
                 PreparedStatement statement = connection.prepareStatement("UPDATE users SET \"first_name\" = ?, \"last_name\" = ? WHERE id = ?");) {
                statement.setString(1, entity.getFirstName());
                statement.setString(2, entity.getLastName());
                statement.setLong(3, entity.getId());
                statement.executeUpdate();
            } catch (SQLException exception) {
                throw new RuntimeException(exception);
            }
        }
        return entity2;
    }
}
