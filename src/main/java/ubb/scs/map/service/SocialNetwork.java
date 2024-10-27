package ubb.scs.map.service;

import ubb.scs.map.domain.Friendship;
import ubb.scs.map.domain.Tuple;
import ubb.scs.map.domain.User;
import ubb.scs.map.repository.Repository;
import ubb.scs.map.repository.file.FriendshipRepository;
import ubb.scs.map.repository.file.UserRepository;
import ubb.scs.map.utils.Dfs;
import ubb.scs.map.utils.RepoOperations;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;

public class SocialNetwork {

    private final Repository<Long, User> userRepository;
    private final Repository<Tuple<Long, Long>, Friendship> friendshipRepository;
    private final RepoOperations<Long, User> userOperations;
    private final RepoOperations<Tuple<Long, Long>, Friendship> friendshipOperations;

    public SocialNetwork(Repository<Long, User> userRepository, Repository<Tuple<Long, Long>, Friendship> friendshipRepository) {
        this.userRepository = userRepository;
        this.friendshipRepository = friendshipRepository;
        userOperations = new RepoOperations<>(userRepository);
        friendshipOperations = new RepoOperations<>(friendshipRepository);
    }


    public Optional<User> save(User entity) {
        return userRepository.save(entity);
    }

    public Optional<User> update(User entity) {
        return userRepository.update(entity);
    }

    public Optional<User> delete(Long ID) {
        var user = userRepository.delete(ID);
        if (user.isPresent()) {
            var friendshipList = friendshipOperations.getAllIDs();
            friendshipList.forEach(friendshipID -> {
                var user1ID = friendshipID.getE1();
                var user2ID = friendshipID.getE2();
                if (Objects.equals(user1ID, user.get().getId()) || Objects.equals(user2ID, user.get().getId()))
                    friendshipRepository.delete(friendshipID);
            });

            /*
            friendshipList.removeIf( friendshipID-> {
                var user1ID = friendshipID.getE1();
                var user2ID = friendshipID.getE2();
                return (Objects.equals(user1ID, user.get().getId()) || Objects.equals(user2ID, user.get().getId()));
            });
            friendshipRepository.refreshFile();
             */
        }

        return user;
    }

    public Optional<Friendship> save(Friendship entity) {
        return friendshipRepository.save(entity);
    }

    public Optional<Friendship> update(Friendship entity) {
        return friendshipRepository.update(entity);
    }

    public Optional<Friendship> delete(Tuple<Long, Long> ID) {
        return friendshipRepository.delete(ID);
    }

        /**
         *
         * @return a list of lists of all communities; every list has at least 2 Users
         */
    public ArrayList<ArrayList<User>> getCommunities() {
        int nr = 0;
        Dfs util = new Dfs(userOperations.getEntities(), friendshipOperations.getEntities());
        return util.run();
    }

    /**
     *
     * @return the list of the Users that form the most sociable community
     */
    public ArrayList<User> MostSociableCommunity() {
        Dfs util = new Dfs(userOperations.getEntities(), friendshipOperations.getEntities());
        var list = util.run();
        /*
        ArrayList<User> rez = new ArrayList<>(list.get(0));
        for (var l : list) {
            if (l.size() > rez.size())
                rez = l;
        }
         */
        return list.stream()
                .reduce(list.get(0), (x, y) -> x.size() > y.size() ? x : y);
    }
}
