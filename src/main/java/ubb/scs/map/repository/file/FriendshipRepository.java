package ubb.scs.map.repository.file;

import ubb.scs.map.domain.Friendship;
import ubb.scs.map.domain.Tuple;
import ubb.scs.map.domain.User;
import ubb.scs.map.domain.validators.ValidationException;
import ubb.scs.map.domain.validators.Validator;
import ubb.scs.map.repository.memory.InMemoryRepository;

public class FriendshipRepository extends AbstractFileRepository<Tuple<Long, Long>, Friendship> {

    private final UserRepository userRepository;
    public FriendshipRepository(UserRepository userRepository, Validator<Friendship> validator, String path) {
        super(validator, path);
        this.userRepository = userRepository;
    }

    @Override
    public Friendship createEntity(String line) {
        String[] split = line.split("-");
        Friendship friendship = new Friendship();
        friendship.setId(new Tuple<Long, Long>(Long.parseLong(split[0]), Long.parseLong(split[1])));
        return friendship;
    }

    @Override
    public String saveEntity(Friendship entity) {
        return entity.getId().getE1() + "-" + entity.getId().getE2();
    }

    @Override
    public Friendship save(Friendship entity) {
        if (userRepository.getEntities().get(entity.getId().getE1()) == null ||
        userRepository.getEntities().get(entity.getId().getE2()) == null) {
            throw new ValidationException("User not found");
        }
        return super.save(entity);
    }
}
