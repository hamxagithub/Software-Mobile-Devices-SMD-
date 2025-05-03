package com.example.a22f_3189_ai_6b.RecyclerViewExamples;

public class MyMobile {
    private String Name, Company, Price;

    public MyMobile(String name, String company, String price) {
        Name = name;
        Company = company;
        Price = price;
    }

    public MyMobile() {
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getCompany() {
        return Company;
    }

    public void setCompany(String company) {
        Company = company;
    }

    public String getPrice() {
        return Price;
    }

    public void setPrice(String price) {
        Price = price;
    }
}
