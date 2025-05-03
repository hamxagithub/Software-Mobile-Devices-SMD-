package com.example.myapplication.FIREBASE_WITHRECYCLERVIEW;

public class DataModel {
    public String id;
    public String name;
    public  String title;
    public  String description;

    public DataModel() {
    }

    public DataModel(String id, String name, String title, String description) {
        this.id = id;
        this.name = name;
        this.title = title;
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public String getTitle() {
        return title;
    }

}
