package ubb.scs.map.repository.database;

import ubb.scs.map.domain.Friendship;
import ubb.scs.map.domain.Tuple;
import ubb.scs.map.domain.User;
import ubb.scs.map.domain.validators.Validator;
import ubb.scs.map.repository.file.FriendshipRepository;
import ubb.scs.map.repository.memory.InMemoryRepository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.Optional;

public class FriendshipDBRepository extends InMemoryRepository<Tuple<Long, Long>, Friendship> {
    private final String url;
    private final String username;
    private final String password;

    public FriendshipDBRepository(Validator<Friendship> validator, String url, String username, String password) {
        super(validator);
        this.url = url;
        this.username = username;
        this.password = password;
        loadFromDB();
    }


    private void loadFromDB() {
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM friendships");
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Long id1 = resultSet.getLong("user1ID");
                Long id2 = resultSet.getLong("user2ID");
                LocalDateTime createdAt = resultSet.getTimestamp("LocalDateTime").toLocalDateTime();
                Friendship friendship = new Friendship();
                friendship.setId(new Tuple<>(id1, id2));
                friendship.setDate(createdAt);
                super.save(friendship);
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
    public Optional<Friendship> save(Friendship entity) {

        Optional<Friendship> e = super.save(entity);
        if (e.isEmpty()) {
            try (Connection connection = DriverManager.getConnection(url, username, password);
                 PreparedStatement statement = connection.prepareStatement("INSERT INTO friendships (\"user1ID\", \"user2ID\", \"LocalDateTime\") VALUES (?, ?, ?)");) {
                statement.setLong(1, entity.getId().getE1());
                statement.setLong(2, entity.getId().getE2());
                statement.setTimestamp(3, Timestamp.valueOf(entity.getDate()));
                statement.executeUpdate();
            } catch (SQLException exception) {
                throw new RuntimeException(exception);
            }
        }
        return e;
    }

    @Override
    public Optional<Friendship> delete(Tuple<Long, Long> aLong) {
        Optional<Friendship> entity = super.delete(aLong);
        if (entity.isPresent()) {
            try (Connection connection = DriverManager.getConnection(url, username, password);
                 PreparedStatement statement = connection.prepareStatement("DELETE FROM friendships WHERE (\"user1ID\" = ? AND \"user2ID\" = ?) OR (\"user1ID\" = ? AND \"user2ID\" = ?)")) {
                statement.setLong(1, aLong.getE1());
                statement.setLong(2, aLong.getE2());
                statement.setLong(3, aLong.getE2());
                statement.setLong(4, aLong.getE1());
                statement.executeUpdate();
            } catch (SQLException exception) {
                throw new RuntimeException(exception);
            }
        }
        return entity;
    }

    @Override
    public Optional<Friendship> update(Friendship entity) {
        Optional<Friendship> entity2 = super.update(entity);
        if (entity2.isEmpty()) {
            try (Connection connection = DriverManager.getConnection(url, username, password);
                 PreparedStatement statement = connection.prepareStatement("UPDATE friendships SET \"LocalDateTime\" = ? WHERE (\"user1ID\" = ? AND \"user2ID\" = ?) OR (\"user1ID\" = ? AND \"user2ID\" = ?)");) {
                statement.setTimestamp(1, Timestamp.valueOf(entity.getDate().toString()));
                statement.setLong(2, entity.getId().getE1());
                statement.setLong(3, entity.getId().getE2());
                statement.setLong(4, entity.getId().getE2());
                statement.setLong(5, entity.getId().getE1());
                statement.executeUpdate();
            } catch (SQLException exception) {
                throw new RuntimeException(exception);
            }
        }
        return entity2;
    }
}
