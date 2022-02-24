package com.example.recipeapp.data;

import com.example.recipeapp.model.Comment;
import com.example.recipeapp.model.Recipe;

import java.util.ArrayList;

public interface RecipeFirebaseAsyncResponse {

    void processFinishedRecipeList(ArrayList<Recipe> recipes);

    void processFinishedCommentList(ArrayList<Comment> comments);
}
