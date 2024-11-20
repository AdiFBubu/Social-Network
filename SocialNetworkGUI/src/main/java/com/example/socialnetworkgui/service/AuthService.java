package com.example.socialnetworkgui.service;

import com.example.socialnetworkgui.domain.Account;
import com.example.socialnetworkgui.domain.User;
import com.example.socialnetworkgui.events.ChangeEventType;
import com.example.socialnetworkgui.events.UserEntityChangeEvent;
import com.example.socialnetworkgui.repository.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class AuthService {

    private final Repository<Long, Account> authRepository;

    private List<Account> getAccounts() {
        Iterable<Account> accounts = authRepository.findAll();
        return StreamSupport.stream(accounts.spliterator(), false)
                .collect(Collectors.toList());
    }

    public Optional<Account> getAccount(String email) {
        return getAccounts().stream().
                filter(account -> account.getEmail().equals(email)).
                findFirst();
    }

    public AuthService(Repository<Long, Account> authRepository) {
        this.authRepository = authRepository;
    }

    public Optional<Account> add(Long ID, String email, String password) {
        Account account = new Account(email, password);
        account.setId(ID);
        return authRepository.save(account);
    }

    public Optional<Account> update(String email, String password) {
        Optional<Account> account = getAccount(email);
        Long ID = -1L;
        if (account.isPresent())
            ID = account.get().getId();
        Account entity = new Account(email, password);
        entity.setId(ID);
        return authRepository.update(entity);
    }

    public Optional<Account> delete(String email) {
        Optional<Account> account = getAccount(email);
        Long ID = -1L;
        if(account.isPresent())
            ID = account.get().getId();
        return authRepository.delete(ID);
    }
}
