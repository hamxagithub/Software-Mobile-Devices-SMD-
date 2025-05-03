package com.example.mid2_sir_tahir.CardViewExample;

public class MyModel {
    public String Name;
    int TotalDownload;
    int Thumbnail;

    public MyModel(String name, int totalDownload, int thumbnail) {
        Name = name;
        TotalDownload = totalDownload;
        Thumbnail = thumbnail;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public int getTotalDownload() {
        return TotalDownload;
    }

    public void setTotalDownload(int totalDownload) {
        TotalDownload = totalDownload;
    }

    public int getThumbnail() {
        return Thumbnail;
    }

    public void setThumbnail(int thumbnail) {
        Thumbnail = thumbnail;
    }
}
