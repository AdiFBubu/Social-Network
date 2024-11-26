package com.example.socialnetworkgui.domain.validators;

import com.example.socialnetworkgui.domain.Message;
import com.example.socialnetworkgui.domain.User;

public class MessageValidator {

    public static void validate(Message message) {
        if (message.getMessage().isEmpty())
            throw new ValidationException("You cannot send an empty message!");
    }
}
