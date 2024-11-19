package com.example.socialnetworkgui.repository.file;


import com.example.socialnetworkgui.domain.Friendship;
import com.example.socialnetworkgui.domain.Tuple;
import com.example.socialnetworkgui.domain.validators.Validator;

public class FriendshipRepository extends AbstractFileRepository<Tuple<Long, Long>, Friendship> {

    public FriendshipRepository(Validator<Friendship> validator, String path) {
        super(validator, path);
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

}
