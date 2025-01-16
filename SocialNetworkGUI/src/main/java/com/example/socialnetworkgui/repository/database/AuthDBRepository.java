package com.example.socialnetworkgui.repository.database;

import com.example.socialnetworkgui.domain.Account;
import com.example.socialnetworkgui.domain.User;
import com.example.socialnetworkgui.domain.validators.Validator;
import com.example.socialnetworkgui.repository.Repository;

import java.sql.*;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.mindrot.jbcrypt.BCrypt;


public class AuthDBRepository implements Repository<Long, Account> {

    private final String url;
    private final String username;
    private final String password;
    private final Validator<Account> validator;

    public AuthDBRepository(String url, String username, String password, Validator<Account> validator) {
        this.validator = validator;
        this.url = url;
        this.username = username;
        this.password = password;
    }

    @Override
    public Optional<Account> findOne(Long aLong) {
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM auth WHERE id = ?")) {

            statement.setLong(1, aLong);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                Long id = resultSet.getLong("id");
                String email = resultSet.getString("email");
                String password = resultSet.getString("password");
                Account account = new Account(email, password);
                account.setId(id);
                return Optional.of(account);
            }
            return Optional.empty();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Iterable<Account> findAll() {
        Set<Account> accounts = new HashSet<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM auth");
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Long id = resultSet.getLong("id");
                String email = resultSet.getString("email");
                String password = resultSet.getString("password");
                Account account = new Account(email, password);
                account.setId(id);
                accounts.add(account);
            }
            return accounts;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public Optional<Account> save(Account entity) {

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statementAdd = connection.prepareStatement("INSERT INTO auth (id, email, password) VALUES (?, ?, ?)")) {

            validator.validate(entity);

            String hashedPassword = BCrypt.hashpw(entity.getPassword(), BCrypt.gensalt(12));

            statementAdd.setLong(1, entity.getId());
            statementAdd.setString(2, entity.getEmail());
            statementAdd.setString(3, hashedPassword);
            statementAdd.executeUpdate();

            return Optional.empty();

        } catch (SQLException exception) {
            throw new RuntimeException("The email address is already registered!");
        }
    }

    @Override
    public Optional<Account> delete(Long aLong) {

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statementDelete = connection.prepareStatement("DELETE FROM auth WHERE id = ?")) {

            Optional<Account> account = findOne(aLong);
            if (account.isEmpty())
                return Optional.empty();

            statementDelete.setLong(1, aLong);
            int rez = statementDelete.executeUpdate();
            if (rez == 0)
                return Optional.empty();
            return account;

        } catch (SQLException exception) {
            throw new RuntimeException(exception);
        }

    }

    @Override
    public Optional<Account> update(Account entity) {

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("UPDATE auth SET email = ?, password = ? WHERE id = ?")) {

            validator.validate(entity);

            String hashedPassword = BCrypt.hashpw(entity.getPassword(), BCrypt.gensalt(12));

            statement.setString(1, entity.getEmail());
            statement.setString(2, hashedPassword);
            statement.setLong(3, entity.getId());

            int rez = statement.executeUpdate();
            if (rez == 0)
                return Optional.of(entity);
            return Optional.empty();

        } catch (SQLException exception) {
            throw new RuntimeException("The email address is already registered!");
        }
    }

}
