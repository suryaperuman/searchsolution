package com.example.gcc;

import java.io.Serializable;

public class User extends Account implements Serializable {


    public Event[] joinedEvents;

    public User(String password, String role, String username) {
        this.password = password;
        this.role = role;
        this.username = username;
    }
    public User(String password, String role) {
        this.password = password;
        this.role = role;
    }

    public void getJoinedEvents() throws Exception {
        throw new Exception("To be implemented");
    }

    public void joinEvent() throws Exception {
        throw new Exception("To be implemented");
    }
}
