package com.example.gcc;

import java.io.Serializable;

public class ClubOwner extends Account implements Serializable {
    public String clubName;
    public Event[] hostedEvents;

    public ClubOwner(String clubName, String username, String password, String role) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.clubName = clubName;
    }

    public ClubOwner(String username, String password, String role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }
    public ClubOwner(String password, String role) {
        this.password = password;
        this.role = role;
    }


}
