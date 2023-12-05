package com.example.gcc;

import java.util.List;

public class Club {

    String Name;

    Integer Rating;

    List<String> Comment;

    String ID;

    Club(String Name,Integer Rating, List<String> Comment){
        this.Name=Name;
        this.Rating=Rating;
        this.Comment=Comment;
    }
    Club(String Name,Integer Rating){
        this.Name=Name;
        this.Rating=Rating;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getID() {
        return ID;
    }

    public String getName() {
        return Name;
    }

    public Integer getRating() {
        return Rating;
    }

    public List<String> getComment() {
        return Comment;
    }
}
