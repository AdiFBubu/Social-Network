package com.example.socialnetworkgui.domain;

public class Entity<ID>  {

    private ID id;
    public ID getId() {
        return id;
    }

    /**
     *
     * @param id it will be set to the current Entity
     */
    public void setId(ID id) {
        this.id = id;
    }
}