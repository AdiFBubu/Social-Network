package com.example.socialnetworkgui.service;



import com.example.socialnetworkgui.domain.Friendship;
import com.example.socialnetworkgui.domain.Tuple;
import com.example.socialnetworkgui.domain.User;
import com.example.socialnetworkgui.repository.Repository;
import com.example.socialnetworkgui.utils.Dfs;
import com.example.socialnetworkgui.utils.RepoOperations;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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

    private Optional<User> getUser(String firstName, String lastName) {
        return userOperations.getAllValues().stream()
                .filter(u -> u.getFirstName().equals(firstName) && u.getLastName().equals(lastName))
                .findFirst();
    }


    public Optional<User> save(String firstName, String LastName) {
        User entity = new User(firstName, LastName);
        return userRepository.save(entity);
    }

    public Optional<User> update(String initialFirstName, String initialLastName, String firstName, String lastName) {
        Optional<User> user = getUser(initialFirstName, initialLastName);
        Long ID = -1L;
        if (user.isPresent())
            ID = user.get().getId();
        User entity = new User(firstName, lastName);
        entity.setId(ID);
        return userRepository.update(entity);
    }

    public Optional<User> delete(String firstName, String lastName) {
        Optional<User> user = getUser(firstName, lastName);
        if (user.isPresent()) {
            var friendshipList = friendshipOperations.getAllIDs();
            friendshipList.forEach(friendshipID -> {
                var user1ID = friendshipID.getE1();
                var user2ID = friendshipID.getE2();
                if (Objects.equals(user1ID, user.get().getId()) || Objects.equals(user2ID, user.get().getId()))
                    friendshipRepository.delete(friendshipID);
            });
            userRepository.delete(user.get().getId());

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

    public Optional<Friendship> save(String user1FirstName, String user1LastName, String user2FirstName, String user2LastName) {
        Optional<User> user1 = getUser(user1FirstName, user1LastName);
        Optional<User> user2 = getUser(user2FirstName, user2LastName);
        Friendship friendship = new Friendship();
        Long user1ID = -1L, user2ID = -1L;
        if (user1.isPresent())
            user1ID = user1.get().getId();
        if (user2.isPresent())
            user2ID = user2.get().getId();
        friendship.setId(new Tuple<>(user1ID, user2ID));

        return friendshipRepository.save(friendship);
    }

    private LocalDateTime convertToLocalDateTime(String dateTimeString) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        return LocalDateTime.parse(dateTimeString, formatter);
    }

    public Optional<Friendship> update(String user1FirstName, String user1LastName, String user2FirstName, String user2LastName, String dateTimeString) {
        Optional<User> user1 = getUser(user1FirstName, user1LastName);
        Optional<User> user2 = getUser(user2FirstName, user2LastName);
        Long user1ID = -1L, user2ID = -1L;
        if (user1.isPresent())
            user1ID = user1.get().getId();
        if (user2.isPresent())
            user2ID = user2.get().getId();
        Friendship friendship = new Friendship();
        friendship.setId(new Tuple<>(user1ID, user2ID));
        friendship.setDate(convertToLocalDateTime(dateTimeString));
        return friendshipRepository.update(friendship);
    }

    public Optional<Friendship> delete(String user1FirstName, String user1LastName, String user2FirstName, String user2LastName) {
        Optional<User> user1 = getUser(user1FirstName, user1LastName);
        Optional<User> user2 = getUser(user2FirstName, user2LastName);
        Long user1ID = -1L, user2ID = -1L;
        if (user1.isPresent())
            user1ID = user1.get().getId();
        if (user2.isPresent())
            user2ID = user2.get().getId();
        Tuple<Long, Long> friendshipID = new Tuple<>(user1ID, user2ID);

        return friendshipRepository.delete(friendshipID);
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

    public Iterable<User> getAllUsers() {
        return userRepository.findAll();
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
