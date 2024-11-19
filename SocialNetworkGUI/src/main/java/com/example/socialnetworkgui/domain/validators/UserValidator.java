package com.example.socialnetworkgui.domain.validators;

import com.example.socialnetworkgui.domain.User;

public class UserValidator {
    public static void validate(User entity) {
        //TODO: implement method validate
        if(entity.getFirstName().isEmpty())
            throw new ValidationException("First name invalid!");
        if (entity.getLastName().isEmpty())
            throw new ValidationException("Last name invalid!");
//        if(entity.getId() == null || entity.getId() <= 0)
//            throw new ValidationException("ID invalid!");
    }
}
