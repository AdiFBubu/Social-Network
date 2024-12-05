package com.example.socialnetworkgui.domain.dto;

import java.util.Optional;

public class UserFilterDTO {

    private Optional<String> firstName = Optional.empty();
    private Optional<String> lastName = Optional.empty();
    private Optional<Long> idUser = Optional.empty();
    private Optional<Boolean> state = Optional.empty();


    public Optional<String> getLastName() {
        return lastName;
    }

    public void setLastName(Optional<String> lastName) {
        this.lastName = lastName;
    }

    public Optional<String> getFirstName() {
        return firstName;
    }

    public void setFirstName(Optional<String> firstName) {
        this.firstName = firstName;
    }

    public Optional<Long> getIdUser() {
        return idUser;
    }

    public void setIdUser(Optional<Long> idUser) {
        this.idUser = idUser;
    }

    public Optional<Boolean> getState() {
        return state;
    }

    public void setState(Optional<Boolean> state) {
        this.state = state;
    }

}
