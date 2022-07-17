package com.wingy.myapplication;

public class Product {
    private String name, description, imageURL, type;
    private User seller;

    public Product(String name, String description, String imageURL, String type, User seller) {
        this.name = name;
        this.description = description;
        this.imageURL = imageURL;
        this.type = type;
        this.seller = seller;
    }
    public Product() {

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

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public User getSeller() {
        return seller;
    }

    public void setSeller(User seller) {
        this.seller = seller;
    }
}
