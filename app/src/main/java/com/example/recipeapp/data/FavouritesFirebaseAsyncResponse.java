package com.example.recipeapp.data;

import com.example.recipeapp.model.Recipe;

import java.util.ArrayList;

public interface FavouritesFirebaseAsyncResponse {

    void processFinishedFavouritesList(ArrayList<Recipe> recipes);
}
