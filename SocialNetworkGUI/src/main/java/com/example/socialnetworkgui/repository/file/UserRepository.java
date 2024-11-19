package com.example.socialnetworkgui.repository.file;

import com.example.socialnetworkgui.domain.User;
import com.example.socialnetworkgui.domain.validators.Validator;

public class UserRepository extends AbstractFileRepository<Long, User>{

    public UserRepository(Validator<User> validator, String fileName) {
        super(validator, fileName);
    }

    @Override
    public User createEntity(String line) {
        String[] splitted = line.split(";");
        User u = new User(splitted[1], splitted[2]);
        u.setId(Long.parseLong(splitted[0]));
        return u;
    }

    @Override
    public String saveEntity(User entity) {
        String s = entity.getId() + ";" + entity.getFirstName() + ";" + entity.getLastName();
        return s;
    }

}
