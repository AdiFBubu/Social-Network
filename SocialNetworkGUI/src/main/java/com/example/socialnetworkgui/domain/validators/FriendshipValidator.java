package com.example.socialnetworkgui.domain.validators;


import com.example.socialnetworkgui.domain.Friendship;
import com.example.socialnetworkgui.domain.User;
import com.example.socialnetworkgui.repository.Repository;

import java.util.Objects;

public class FriendshipValidator {

    Repository<Long, User> userRepository;
    public FriendshipValidator(Repository<Long, User> userRepository) {
        this.userRepository = userRepository;
    }
    public void validate(Friendship entity) {
//        if (entity.getId().getE1() <= 0)
//            throw new ValidationException("E1 must be greater than 0");
//        if (entity.getId().getE2() <= 0)
//            throw new ValidationException("E2 must be greater than 0");
        if (userRepository.findOne(entity.getId().getE1()).isEmpty())
            throw new ValidationException("User 1 not found");
        if (userRepository.findOne(entity.getId().getE2()).isEmpty())
            throw new ValidationException("User 2 not found");
        if (Objects.equals(entity.getId().getE1(), entity.getId().getE2()))
            throw new ValidationException("A user cannot be friend with himself");
    }
}
