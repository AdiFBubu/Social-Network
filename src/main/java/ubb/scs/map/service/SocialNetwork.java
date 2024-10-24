package ubb.scs.map.service;

import ubb.scs.map.domain.User;
import ubb.scs.map.repository.file.FriendshipRepository;
import ubb.scs.map.repository.file.UserRepository;
import ubb.scs.map.utils.dfs;

import java.util.ArrayList;

public class SocialNetwork {

    private UserRepository userRepository;
    private FriendshipRepository friendshipRepository;

    public SocialNetwork(UserRepository userRepository, FriendshipRepository friendshipRepository) {
        this.userRepository = userRepository;
        this.friendshipRepository = friendshipRepository;
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
