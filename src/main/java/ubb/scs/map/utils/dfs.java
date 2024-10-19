package ubb.scs.map.utils;

import ubb.scs.map.domain.Friendship;
import ubb.scs.map.domain.Tuple;
import ubb.scs.map.domain.User;
import ubb.scs.map.repository.file.UserRepository;

import java.util.*;

public class dfs {
    private Map<Long, User> u;
    private Map<Tuple<Long, Long>, Friendship> m;
    private ArrayList<User> viz;
    private Map<User, List<User>> relations;

    /**
     *
     * @param u the map of users and their ids
     * @param m the map of friendships and their ids
     */
    public dfs(Map<Long, User> u, Map<Tuple<Long, Long>, Friendship> m) {
        this.u = u;
        this.m = m;
        viz = new ArrayList<>();
        relations = new HashMap<>();
        for (var el : m.values()) {
            var u1 = u.get(el.getId().getE1());
            var u2 = u.get(el.getId().getE2());
            if (relations.get(u1) == null)
                relations.put(u1, new ArrayList<>());
            if (relations.get(u2) == null)
                relations.put(u2, new ArrayList<>());
            relations.get(u1).add(u2);
            relations.get(u2).add(u1);
        }
    }

    /**
     *
     * @return a list of lists of all communities; every list has at least 2 Users
     */
    public ArrayList<ArrayList<User>> run() {
        ArrayList<ArrayList<User>> rez = new ArrayList<>();
        for (var u : relations.keySet())
            if (!viz.contains(u)) {
                ArrayList<User> l = new ArrayList<>();
                alg(u, l);
                rez.add(l);
            }
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
        for (var friend : relations.get(u))
            if (!viz.contains(friend))
                alg(friend, rez);
    }
}
