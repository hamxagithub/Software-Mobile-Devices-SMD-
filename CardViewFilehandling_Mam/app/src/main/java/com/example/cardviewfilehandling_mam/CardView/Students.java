package com.example.cardviewfilehandling_mam.CardView;

public class Students {
    private final String name;
    private final int imageResId;

    public Students(String name, int imageResId) {
        this.name = name;
        this.imageResId = imageResId;
    }

    public String getName() {
        return name;
    }

    public int getImageResId() {
        return imageResId;
    }
}
