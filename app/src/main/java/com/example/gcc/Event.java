package com.example.gcc;

public class Event {

    private String name;
    private ClubOwner host;

    private eventType type;
    private User[] users;
    private String startTime;
    private String location;

    private Float pace;

    private Integer level;
    private String ID;

    public Event(String name,eventType type, User[] participants, String startTime, String location,Float pace, Integer level){
        this.name = name;
        this.type=type;
        this.users=participants;
        this.startTime=startTime;
        this.location=location;
        this.pace=pace;
        this.level=level;

    }
    public Event(String name,eventType type, User[] participants, String startTime, String location,Float pace, Integer level, String ID){
        this.name = name;
        this.type=type;
        this.users=participants;
        this.startTime=startTime;
        this.location=location;
        this.pace=pace;
        this.level=level;
        this.ID = ID;

    }
    public String getName(){
        return this.name;
    }
    public Float getPace(){
        return this.pace;
    }
    public Integer getLevel(){
        return this.level;  
    }

    public ClubOwner getHost() {
        return this.host;
    }

    public User[] getUsers() {
        return this.users;
    }
    public String getStartTime() {
        return this.startTime;
    }
    public String getLocation() {
        return this.location;
    }

    public eventType getType(){
        return type;
    }

    public String getID() { return this.ID; }

    public String name(){ return  this.name;}
    public  eventType type(){ return this.type;}
    public User[] users(){ return this.users;}
    public String startTime(){ return this.startTime;}
    public String location(){ return this.location;}
    public Float pace(){ return this.pace;}
    public void setHost(ClubOwner host) {
        this.host = host;
    }

    public void setStartTime(String time) {
        this.startTime = time;
    }

    public void setLocation(String location) {
        this.location = location;
    }

}
