package com.example.gcc;

public class eventType {
    private String name;

    private String description;

    private Integer level;
    private Float paceMin;
    private Float paceMax;
    private Integer age;
    private boolean status;

    public eventType(){}

    public eventType(String Name, String Description, Integer Level, Float PaceMin, Float PaceMax, Integer age,boolean status){
        this.name =Name;
        this.description =Description;
        this.level =Level;
        this.paceMin =PaceMin;
        this.paceMax =PaceMax;
        this.age =age;
        this.status=status;
    }
    public eventType(String Description, Integer Level, Float PaceMin, Float PaceMax, Integer age,boolean status){
        this.description =Description;
        this.level =Level;
        this.paceMin =PaceMin;
        this.paceMax =PaceMax;
        this.age =age;
        this.status=status;

    }



    public String getName() {
        return name;
    }
    public String getDescription(){
        return description;
    }

    public Integer getLevel(){
        return level;
    }

    public Float getPaceMin(){ return paceMin; }

    public Float getPaceMax() { return paceMax; }

    public Integer getAge() { return age; }

    public Boolean getStatus() { return status; }

    public void name(String name) {
        this.name = name;
    }

    public void description(String description) {
        this.description = description;
    }

    public void level(Integer level) {
        this.level = level;
    }

    public void paceMin(Float paceMin) {
        this.paceMin = paceMin;
    }

    public void paceMax(Float paceMax) {
        this.paceMax = paceMax;
    }

    public void age(Integer age) {
        this.age = age;
    }

    public String toString() {
        return name; // Return the 'name' property when converting to a string
    }

}
