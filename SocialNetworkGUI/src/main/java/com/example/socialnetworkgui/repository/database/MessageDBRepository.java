package com.example.socialnetworkgui.repository.database;

import com.example.socialnetworkgui.domain.Account;
import com.example.socialnetworkgui.domain.Message;
import com.example.socialnetworkgui.domain.validators.Validator;
import com.example.socialnetworkgui.repository.Repository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

public class MessageDBRepository implements Repository<Long, Message> {

    private final String url;
    private final String username;
    private final String password;
    private final Validator<Message> validator;

    public MessageDBRepository(String url, String username, String password, Validator<Message> validator) {
        this.validator = validator;
        this.url = url;
        this.username = username;
        this.password = password;
    }

    @Override
    public Optional<Message> findOne(Long aLong) {
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("""
                                                                            SELECT * FROM messages WHERE id = ?
                                                                            """))
        {
            statement.setLong(1, aLong);
            ResultSet resultSet = statement.executeQuery();
            Long from = null;
            LocalDateTime date = null;
            String message = null;
            List<Long> to = new ArrayList<>();
            Long reply = null;
            if (resultSet.next()) {
                from = resultSet.getLong("from");
                to.add(resultSet.getLong("to"));
                date = resultSet.getTimestamp("date").toLocalDateTime();
                message = resultSet.getString("message");
                reply = (Long) resultSet.getObject("reply");
            }
            else
                return Optional.empty();
            Message m = new Message(from, to, date, message, reply);
            m.setId(aLong);
            return Optional.of(m);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Iterable<Message> findAll() {
        Set<Message> messages = new HashSet<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("""
                                                                               SELECT * FROM messages
                                                                            """))
        {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Long id = resultSet.getLong("id");
                Long from = resultSet.getLong("from");
                List<Long> to = new ArrayList<>();
                to.add(resultSet.getLong("to"));
                LocalDateTime date = resultSet.getTimestamp("date").toLocalDateTime();
                String message = resultSet.getString("message");
                Long reply = (Long) resultSet.getObject("reply");
                Message m = new Message(from, to, date, message, reply);
                m.setId(id);
                messages.add(m);
            }
            return messages;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }



        @Override
    public Optional<Message> save(Message entity) {

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statementAdd = connection.prepareStatement("INSERT INTO messages (date, message, \"from\", \"to\", reply) VALUES (?, ?, ?, ?, ?)")) {

            validator.validate(entity);

            entity.getTo()
                    .forEach(to -> {
                                try {
                                        statementAdd.setTimestamp(1, Timestamp.valueOf(entity.getDate()));
                                        statementAdd.setString(2, entity.getMessage());
                                        statementAdd.setLong(3, entity.getFrom());
                                        statementAdd.setLong(4, to);
                                        if (entity.getReply() == null) {
                                            statementAdd.setObject(5, null, java.sql.Types.BIGINT); // Setează valoarea NULL explicit
                                        } else {
                                            statementAdd.setLong(5, entity.getReply()); // Setează valoarea LONG
                                        }
                                        statementAdd.executeUpdate();
                                    } catch (SQLException e) {
                                        throw new RuntimeException(e);
                                    }

                                    }

                            );

            return Optional.of(entity);

        } catch (SQLException exception) {
            throw new RuntimeException("The message is already saved!");
        }
    }

    @Override
    public Optional<Message> delete(Long aLong) {

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statementDelete = connection.prepareStatement("DELETE FROM messages WHERE id = ?")) {

            Optional<Message> message = findOne(aLong);
            if (message.isEmpty())
                return Optional.empty();

            statementDelete.setLong(1, aLong);
            int rez = statementDelete.executeUpdate();
            if (rez == 0)
                return Optional.empty();
            return message;

        } catch (SQLException exception) {
            throw new RuntimeException(exception);
        }

    }

    @Override
    public Optional<Message> update(Message entity) {

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("UPDATE messages SET date = ?, message = ? WHERE id = ?")) {

            validator.validate(entity);

            statement.setTimestamp(1, Timestamp.valueOf(entity.getDate()));
            statement.setString(2, entity.getMessage());
            statement.setLong(3, entity.getId());

            int rez = statement.executeUpdate();
            if (rez == 0)
                return Optional.of(entity);
            return Optional.empty();

        } catch (SQLException exception) {
            throw new RuntimeException("The message is already saved!");
        }
    }

}

