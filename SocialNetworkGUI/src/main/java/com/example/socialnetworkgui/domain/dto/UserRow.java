package com.example.socialnetworkgui.domain.dto;

import com.example.socialnetworkgui.domain.User;

public class UserRow {
    private final User user1;
    private final User user2;
    private final User user3;
    private final User user4;

    public UserRow(User user1, User user2, User user3, User user4) {
        this.user1 = user1;
        this.user2 = user2;
        this.user3 = user3;
        this.user4 = user4;
    }

    public User getUser1() {
        return user1;
    }

    public User getUser2() {
        return user2;
    }

    public User getUser3() {
        return user3;
    }

    public User getUser4() {
        return user4;
    }

}
