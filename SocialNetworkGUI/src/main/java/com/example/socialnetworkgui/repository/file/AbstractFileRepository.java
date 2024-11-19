package com.example.socialnetworkgui.repository.file;


import com.example.socialnetworkgui.domain.Entity;
import com.example.socialnetworkgui.domain.validators.Validator;
import com.example.socialnetworkgui.repository.memory.InMemoryRepository;

import java.io.*;
import java.util.Optional;

public abstract class AbstractFileRepository<ID, E extends Entity<ID>> extends InMemoryRepository<ID, E> {
    private String filename;

    public AbstractFileRepository(Validator<E> validator, String fileName) {
        super(validator);
        filename=fileName;
        loadData();
    }


    /**
     *
     * @param line refers the entity in its string form; its components are separated by a special character
     * @return the entity as an object
     */
    public abstract E createEntity(String line); // template method -> sablonul folosit aici; la fel ca la sortare -> comparatorul difera

    /**
     *
     * @param entity refers an object from the entities
     * @return the entity in its string form; its components are separated by a special character
     */
    public abstract String saveEntity(E entity);
    /*
    nu are rost sa le mai suprascriem
    @Override
    public E findOne(ID id) {
        return null;
    }

    @Override
    public Iterable<E> findAll() {
        return null;
    }

     */

    @Override
    public Optional<E> save(E entity) {
        Optional<E> e = super.save(entity);
        if (e.isEmpty())
            writeToFile();
        return e;
    }

    /*
    try {
        BufferedWriter br = ...
    }
    catch() {}
    finally() {}

    SAU: (try with resources -> nu mai e necesara finally: buffered implementeaza deja auto closer -> cand o clasa implementeaza deja aia, preferam varianta asta
    try(BufferedWriter br = ...) {
    } catch() {}
     */

    private void writeToFile() {

        try  ( BufferedWriter writer = new BufferedWriter(new FileWriter(filename))){
            for (E entity: entities.values()) {
                String ent = saveEntity(entity);
                writer.write(ent);
                writer.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private void loadData() {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                E entity = createEntity(line);
                super.save(entity);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<E> delete(ID id) {
        Optional<E> entity = super.delete(id);
        if (entity.isPresent()) writeToFile();
        return entity;
    }

    @Override
    public Optional<E> update(E entity) {
        Optional<E> entity2 = super.update(entity);
        if (entity2.isEmpty()) writeToFile();
        return entity2;
    }
}
