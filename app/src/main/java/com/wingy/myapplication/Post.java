package com.wingy.myapplication;

import java.util.ArrayList;

public class Post {
    private User user;
    private Product product;

    public Post(User user, Product product) {
        this.user = user;
        this.product = product;
    }

    public Post() {

    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }
}
