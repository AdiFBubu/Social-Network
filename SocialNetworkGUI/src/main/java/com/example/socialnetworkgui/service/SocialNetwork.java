package com.example.socialnetworkgui.service;



import com.example.socialnetworkgui.domain.Friendship;
import com.example.socialnetworkgui.domain.Tuple;
import com.example.socialnetworkgui.domain.User;
import com.example.socialnetworkgui.events.ChangeEventType;
import com.example.socialnetworkgui.events.FriendshipEntityChangeEvent;
import com.example.socialnetworkgui.events.UserEntityChangeEvent;
import com.example.socialnetworkgui.observer.Observable;
import com.example.socialnetworkgui.observer.Observer;
import com.example.socialnetworkgui.repository.Repository;
import com.example.socialnetworkgui.utils.Dfs;
import com.example.socialnetworkgui.utils.RepoOperations;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class SocialNetwork implements Observable<FriendshipEntityChangeEvent> {

    private final Repository<Long, User> userRepository;
    private final Repository<Tuple<Long, Long>, Friendship> friendshipRepository;
    private final RepoOperations<Long, User> userOperations;
    private final RepoOperations<Tuple<Long, Long>, Friendship> friendshipOperations;

    //private final List<Observer<UserEntityChangeEvent>> userObservers = new ArrayList<>();
    private final List<Observer<FriendshipEntityChangeEvent>> friendshipObserver = new ArrayList<>();

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
        var rez = userRepository.save(entity);
//        if ( rez.isPresent() ) {
//            UserEntityChangeEvent event = new UserEntityChangeEvent(ChangeEventType.ADD, entity);
//            notifyObservers(event);
//        }
        return rez;
    }

    public Optional<User> update(String initialFirstName, String initialLastName, String firstName, String lastName) {
        Optional<User> user = getUser(initialFirstName, initialLastName);
        Long ID = -1L;
        if (user.isPresent())
            ID = user.get().getId();
        User entity = new User(firstName, lastName);
        entity.setId(ID);
        var rez = userRepository.update(entity);
//        if ( rez.isPresent() )
//            notifyObservers(new UserEntityChangeEvent(ChangeEventType.UPDATE, entity, user.get()));
        return rez;
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
            UserEntityChangeEvent event = new UserEntityChangeEvent(ChangeEventType.REMOVE, user.get());
//            notifyObservers(event);
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

    public Optional<User> getUser(Long ID) {
        return userRepository.findOne(ID);
    }

    public Optional<Friendship> getFriendship(Tuple<Long, Long> ID) {
        return friendshipRepository.findOne(ID);
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

        Optional<Friendship> rez = friendshipRepository.save(friendship);
        if (rez.isPresent()) {
            FriendshipEntityChangeEvent event = new FriendshipEntityChangeEvent(ChangeEventType.ADD, rez.get());
            notifyObservers(event);
        }
        return rez;
    }

    private LocalDateTime convertToLocalDateTime(String dateTimeString) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        return LocalDateTime.parse(dateTimeString, formatter);
    }

    public Optional<Friendship> update(String user1FirstName, String user1LastName, String user2FirstName, String user2LastName, Boolean state) {
        Optional<User> user1 = getUser(user1FirstName, user1LastName);
        Optional<User> user2 = getUser(user2FirstName, user2LastName);
        Long user1ID = -1L, user2ID = -1L;
        if (user1.isPresent())
            user1ID = user1.get().getId();
        if (user2.isPresent())
            user2ID = user2.get().getId();
        Friendship friendship = new Friendship();
        friendship.setId(new Tuple<>(user1ID, user2ID));
        friendship.setState(state);
        Optional<Friendship> rez = friendshipRepository.update(friendship);
        if (rez.isPresent()) {
            FriendshipEntityChangeEvent event = new FriendshipEntityChangeEvent(ChangeEventType.UPDATE, rez.get());
            notifyObservers(event);
        }
        return rez;
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

        Optional<Friendship> rez = friendshipRepository.delete(friendshipID);
        if (rez.isPresent()) {
            FriendshipEntityChangeEvent event = new FriendshipEntityChangeEvent(ChangeEventType.ADD, rez.get());
            notifyObservers(event);
        }
        return rez;
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

    public Iterable<Friendship> getAllFriendships() {
        return friendshipRepository.findAll();
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

    @Override
    public void addObserver(Observer<FriendshipEntityChangeEvent> observer) {
        friendshipObserver.add(observer);
    }

    @Override
    public void removeObserver(Observer<FriendshipEntityChangeEvent> observer) {
        friendshipObserver.remove(observer);
    }

    @Override
    public void notifyObservers(FriendshipEntityChangeEvent event) {
        friendshipObserver.forEach(observer -> observer.update(event));
    }

//    @Override
//    public void addObserver(Observer<UserEntityChangeEvent> observer) {
//        userObservers.add(observer);
//    }
//
//    @Override
//    public void removeObserver(Observer<UserEntityChangeEvent> observer) {
//        userObservers.remove(observer);
//    }
//
//    @Override
//    public void notifyObservers(UserEntityChangeEvent event) {
//        userObservers.forEach(observer -> observer.update(event));
//    }

}
