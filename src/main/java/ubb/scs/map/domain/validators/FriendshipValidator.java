package ubb.scs.map.domain.validators;

import ubb.scs.map.domain.Friendship;
import ubb.scs.map.domain.User;
import ubb.scs.map.repository.Repository;
import ubb.scs.map.utils.RepoOperations;

import java.util.Objects;

public class FriendshipValidator {

    Repository<Long, User> userRepository;
    public FriendshipValidator(Repository<Long, User> userRepository) {
        this.userRepository = userRepository;
    }
    public void validate(Friendship entity) {
        RepoOperations<Long, User> userOperations = new RepoOperations<>(userRepository);
        if (entity.getId().getE1() <= 0)
            throw new ValidationException("E1 must be greater than 0");
        if (entity.getId().getE2() <= 0)
            throw new ValidationException("E2 must be greater than 0");
        if (userOperations.findById(entity.getId().getE1()).isEmpty() ||
                userOperations.findById(entity.getId().getE2()).isEmpty()) {
            throw new ValidationException("User not found");
        }
        if (Objects.equals(entity.getId().getE1(), entity.getId().getE2()))
            throw new ValidationException("A user cannot be friend with himself");
    }
}
