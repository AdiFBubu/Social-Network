package ubb.scs.map.domain.validators;

import ubb.scs.map.domain.Friendship;
import ubb.scs.map.domain.User;
import ubb.scs.map.repository.file.UserRepository;

import java.util.Map;

public class FriendshipValidator implements Validator<Friendship> {

    UserRepository userRepository;
    public FriendshipValidator(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    @Override
    public void validate(Friendship entity) {
        if (entity.getId().getE1() <= 0)
            throw new ValidationException("E1 must be greater than 0");
        if (entity.getId().getE2() <= 0)
            throw new ValidationException("E2 must be greater than 0");
        if (userRepository.getEntities().get(entity.getId().getE1()) == null ||
                userRepository.getEntities().get(entity.getId().getE2()) == null) {
            throw new ValidationException("User not found");
        }
        if (entity.getId().getE1() == entity.getId().getE2())
            throw new ValidationException("A user cannot be friend with himself");
    }
}
