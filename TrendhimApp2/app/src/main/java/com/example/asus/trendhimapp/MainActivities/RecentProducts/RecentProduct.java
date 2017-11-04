package com.example.asus.trendhimapp.MainActivities.RecentProducts;

public class RecentProduct {

    private String email, category, key;

    public RecentProduct(){}

    RecentProduct(String key, String email, String category) {
        this.email = email;
        this.category = category;
        this.key = key;
    }

    String getEmail() {
        return email;
    }

    String getCategory() {
        return category;
    }

    public String getKey() {
        return key;
    }

}
