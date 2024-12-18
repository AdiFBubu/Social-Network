package com.example.socialnetworkgui.utils;

import com.example.socialnetworkgui.domain.Friendship;
import com.example.socialnetworkgui.domain.Tuple;
import com.example.socialnetworkgui.domain.User;

import java.util.*;

public class Dfs {

    private final ArrayList<User> viz;
    private final Map<User, List<User>> relations;

    /**
     *
     * @param u the map of users and their ids
     * @param m the map of friendships and their ids
     */
    public Dfs(Map<Long, User> u, Map<Tuple<Long, Long>, Friendship> m) {
        viz = new ArrayList<>();
        relations = new HashMap<>();
        /*
        for (var el : m.values()) {
            var u1 = u.get(el.getId().getE1());
            var u2 = u.get(el.getId().getE2());
            relations.computeIfAbsent(u1, k -> new ArrayList<>());
            relations.computeIfAbsent(u2, k -> new ArrayList<>());
            relations.get(u1).add(u2);
            relations.get(u2).add(u1);
        }
        */
        m.values().forEach(el -> {
            var u1 = u.get(el.getId().getE1());
            var u2 = u.get(el.getId().getE2());
            relations.computeIfAbsent(u1, k -> new ArrayList<>());
            relations.computeIfAbsent(u2, k -> new ArrayList<>());
            relations.get(u1).add(u2);
            relations.get(u2).add(u1);
        });
    }

    /**
     *
     * @return a list of lists of all communities; every list has at least 2 Users
     */
    public ArrayList<ArrayList<User>> run() {
        ArrayList<ArrayList<User>> rez = new ArrayList<>();
        /*
        for (var u : relations.keySet())
            if (!viz.contains(u)) {
                ArrayList<User> l = new ArrayList<>();
                alg(u, l);
                rez.add(l);
            }
         */
        relations.keySet().forEach(u -> {
            if (!viz.contains(u)) {
                ArrayList<User> l = new ArrayList<>();
                alg(u, l);
                rez.add(l);
            }
        });
        return rez;
    }

    /**
     *
     * @param u a User
     * @param rez output; the list of the Users that form the most sociable community
     */
    public void alg (User u, ArrayList<User> rez) {
        viz.add(u);
        rez.add(u);
        /*
        for (var friend : relations.get(u))
            if (!viz.contains(friend))
                alg(friend, rez);
         */
        relations.get(u).forEach(friend -> {
            if (!viz.contains(friend))
                alg(friend, rez);
        });
    }
}
