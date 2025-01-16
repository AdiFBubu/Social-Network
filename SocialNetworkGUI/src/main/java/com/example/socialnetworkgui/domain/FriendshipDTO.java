package com.example.socialnetworkgui.domain;

import java.time.LocalDateTime;
import java.util.Objects;

public class FriendshipDTO extends Entity<Long> {
    private String firstName;
    private String lastName;
    private LocalDateTime date;
    private String state;

    public FriendshipDTO(String firstName, String lastName, LocalDateTime date, String state) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.date = date;
        this.state = state;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FriendshipDTO that = (FriendshipDTO) o;
        return Objects.equals(firstName, that.firstName) && Objects.equals(lastName, that.lastName) && Objects.equals(date, that.date) && Objects.equals(state, that.state);
    }

    @Override
    public int hashCode() {
        return Objects.hash(firstName, lastName, date, state);
    }

    @Override
    public String toString() {
        return "FriendshipDTO{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", date=" + date +
                ", state=" + state +
                '}';
    }
}
