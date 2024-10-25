package ubb.scs.map.service;

import ubb.scs.map.domain.Friendship;
import ubb.scs.map.domain.Tuple;
import ubb.scs.map.domain.User;
import ubb.scs.map.domain.validators.ValidationException;
import ubb.scs.map.repository.file.FriendshipRepository;
import ubb.scs.map.repository.file.UserRepository;
import ubb.scs.map.utils.dfs;

import java.util.ArrayList;
import java.util.Objects;

public class SocialNetwork {

    private UserRepository userRepository;
    private FriendshipRepository friendshipRepository;

    public SocialNetwork(UserRepository userRepository, FriendshipRepository friendshipRepository) {
        this.userRepository = userRepository;
        this.friendshipRepository = friendshipRepository;
    }


    public User save(User entity) {
        return userRepository.save(entity);
    }

    public User update(User entity) {
        return userRepository.update(entity);
    }

    public User delete(Long ID) {
        var user = userRepository.delete(ID);
        if (user != null) {
            var userList = userRepository.getEntities().keySet();
            var friendshipList = friendshipRepository.getEntities().keySet();
            friendshipList.removeIf( friendshipID-> {
                var user1ID = friendshipID.getE1();
                var user2ID = friendshipID.getE2();
                return (Objects.equals(user1ID, user.getId()) || Objects.equals(user2ID, user.getId()));
            });
            friendshipRepository.refreshFile();
        }
        return user;
    }

    public Friendship save(Friendship entity) {
        return friendshipRepository.save(entity);
    }

    public Friendship update(Friendship entity) {
        return friendshipRepository.update(entity);
    }

    public Friendship delete(Tuple<Long, Long> ID) {
        return friendshipRepository.delete(ID);
    }

        /**
         *
         * @return a list of lists of all communities; every list has at least 2 Users
         */
    public ArrayList<ArrayList<User>> getCommunities() {
        int nr = 0;
        dfs util = new dfs(userRepository.getEntities(), friendshipRepository.getEntities());
        return util.run();
    }

    /**
     *
     * @return the list of the Users that form the most sociable community
     */
    public ArrayList<User> MostSociableCommunity() {
        dfs util = new dfs(userRepository.getEntities(), friendshipRepository.getEntities());
        var list = util.run();
        ArrayList<User> rez = new ArrayList<>(list.get(0));
        for (var l : list) {
            if (l.size() > rez.size())
                rez = l;
        }
        return rez;
    }
}
