package ubb.scs.map.domain.validators;

import ubb.scs.map.domain.Friendship;
import ubb.scs.map.domain.User;
import ubb.scs.map.repository.file.UserRepository;

import java.util.Map;

public class FriendshipValidator implements Validator<Friendship> {

    @Override
    public void validate(Friendship entity) {
        if (entity.getId().getE1() <= 0)
            throw new ValidationException("E1 must be greater than 0");
        if (entity.getId().getE2() <= 0)
            throw new ValidationException("E2 must be greater than 0");
    }
}
