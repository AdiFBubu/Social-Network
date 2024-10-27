package ubb.scs.map.utils;

import ubb.scs.map.domain.Entity;
import ubb.scs.map.repository.Repository;

import java.util.*;
import java.util.stream.StreamSupport;

public class RepoOperations<ID, E extends Entity<ID>> {

    private Repository<ID, E> repo;
    public RepoOperations(Repository<ID, E> repository) {
        repo = repository;
    }

    public Optional<E> findById(ID id) {
        return StreamSupport.stream(repo.findAll().spliterator(), false)
                .filter(e -> e.getId().equals(id))
                .findFirst();
    }

    public Set<ID> getAllIDs() {
        Set<ID> ids = new HashSet<>();
        repo.findAll().forEach(e -> ids.add(e.getId()));
        return ids;
    }

    public Collection<E> getAllValues() {
        Collection<E> users = new HashSet<>();
        repo.findAll().forEach(users::add);
        return users;
    }

    public Map<ID, E> getEntities() {
        Map<ID, E> entities = new HashMap<>();
        repo.findAll().forEach(e -> entities.put(e.getId(), e));
        return entities;
    }

}
