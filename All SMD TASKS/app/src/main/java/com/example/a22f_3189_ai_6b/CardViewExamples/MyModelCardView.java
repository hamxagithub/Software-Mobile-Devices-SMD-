package com.example.a22f_3189_ai_6b.CardViewExamples;

public class MyModelCardView {
    public String Name;
    public int Total_Download;
    public int thumbnail;

    public MyModelCardView(String name, int total_Download, int thumbnail) {
        Name = name;
        Total_Download = total_Download;
        this.thumbnail = thumbnail;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public int getTotal_Download() {
        return Total_Download;
    }

    public void setTotal_Download(int total_Download) {
        Total_Download = total_Download;
    }

    public int getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(int thumbnail) {
        this.thumbnail = thumbnail;
    }
}
