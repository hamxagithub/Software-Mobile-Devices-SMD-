package com.example.myapplication.FirebaseExamples.FirebaseExampleTwo;

public class Student_BSAI {
    String Name ,Picture;

    public Student_BSAI(String picture, String name) {
        Picture = picture;
        Name = name;
    }

    public Student_BSAI() {

    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getPicture() {
        return Picture;
    }

    public void setPicture(String picture) {
        Picture = picture;
    }
}
