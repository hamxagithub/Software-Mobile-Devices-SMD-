package com.example.assignmentno1.Q2;


public class Contact {
    private String name;
    private String phone;
    private boolean isFavorite;

    public Contact(String name, String phone, boolean isFavorite) {
        this.name = name;
        this.phone = phone;
        this.isFavorite = isFavorite;
    }

    // Getter methods
    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    // Setter methods
    public void setName(String name) {
        this.name = name;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }
}
