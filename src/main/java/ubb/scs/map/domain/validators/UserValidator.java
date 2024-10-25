package ubb.scs.map.domain.validators;


import ubb.scs.map.domain.User;

public class UserValidator implements Validator<User> {
    @Override
    public void validate(User entity) {
        //TODO: implement method validate
        if(entity.getFirstName().isEmpty())
            throw new ValidationException("First name invalid!");
        if (entity.getLastName().equals(""))
            throw new ValidationException("Last name invalid!");
        if(entity.getId() == null || entity.getId() <= 0)
            throw new ValidationException("ID invalid!");
    }
}
