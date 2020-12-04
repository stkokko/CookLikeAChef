package com.example.recipeapp.model;

import java.io.Serializable;
import java.util.ArrayList;

public class Recipe implements Serializable {


    private String name;
    private RecipeCategory category;
    private String imageURL;
    private ArrayList<Ingredient> ingredients;
    private String steps;
    private ArrayList<Comment> comment;
    private String language;

    public Recipe() {

    }


    public Recipe(String name, RecipeCategory category, String imageURL, ArrayList<Ingredient> ingredients, String steps, ArrayList<Comment> comment, String language) {
        this.name = name;
        this.category = category;
        this.imageURL = imageURL;
        this.ingredients = ingredients;
        this.steps = steps;
        this.comment = comment;
        this.language = language;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public RecipeCategory getCategory() {
        return category;
    }

    public void setCategory(RecipeCategory category) {
        this.category = category;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public ArrayList<Ingredient> getIngredients() {
        return ingredients;
    }

    public void setIngredients(ArrayList<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }

    public String getSteps() {
        return steps;
    }

    public void setSteps(String steps) {
        this.steps = steps;
    }

    public ArrayList<Comment> getComment() {
        return comment;
    }

    public void setComment(ArrayList<Comment> comment) {
        this.comment = comment;
    }

    @Override
    public String toString() {
        return "Recipe{" +
                "name='" + name + '\'' +
                ", category=" + category +
                ", imageURL='" + imageURL + '\'' +
                ", ingredients=" + ingredients +
                ", steps='" + steps + '\'' +
                ", comment=" + comment +
                ", language='" + language + '\'' +
                '}';
    }
}
