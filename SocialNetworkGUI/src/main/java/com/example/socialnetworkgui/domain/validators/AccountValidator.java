package com.example.socialnetworkgui.domain.validators;

import com.example.socialnetworkgui.domain.Account;
import com.example.socialnetworkgui.domain.User;

public class AccountValidator {
    public static void validate(Account entity) {
        //TODO: implement method validate
        if (entity.getEmail().isEmpty())
            throw new ValidationException("Email is invalid!");
        if (entity.getPassword().isEmpty())
            throw new ValidationException("Password is invalid!");
    }
}
