package com.example.myapplication.CPFIREBASETASK;

public class Data {
    public String name,cnic,roll,gpa;

    public Data(String name, String cnic, String roll, String gpa) {
        this.name = name;
        this.cnic = cnic;
        this.roll = roll;
        this.gpa = gpa;
    }

    public Data() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCnic() {
        return cnic;
    }

    public void setCnic(String cnic) {
        this.cnic = cnic;
    }

    public String getRoll() {
        return roll;
    }

    public void setRoll(String roll) {
        this.roll = roll;
    }

    public String getGpa() {
        return gpa;
    }

    public void setGpa(String gpa) {
        this.gpa = gpa;
    }
}
