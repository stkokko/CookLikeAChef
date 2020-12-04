package com.example.recipeapp.model;

import java.io.Serializable;

public class RecipeCategory implements Serializable {

    private String name;

    public RecipeCategory() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
