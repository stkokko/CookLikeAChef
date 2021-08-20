package com.example.recipeapp.data;

import androidx.annotation.NonNull;

import com.example.recipeapp.model.Comment;
import com.example.recipeapp.model.Ingredient;
import com.example.recipeapp.model.Recipe;
import com.example.recipeapp.model.RecipeCategory;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class RecipeBankFirebase {

    /*----- Variables -----*/
    private ArrayList<Recipe> recipeArrayList;
    private ArrayList<Comment> comments;
    private ArrayList<Recipe> favouriteRecipesList;
    private final DatabaseReference db;
    private final FirebaseUser user;

    public RecipeBankFirebase() {
        db = FirebaseDatabase.getInstance().getReference();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
    }


    /*----- Getting Recipes From Firebase -----*/
    public List<Recipe> getRecipes(final RecipeFirebaseAsyncResponse callBack, String language, final boolean isLatest) {

        recipeArrayList = new ArrayList<>();
        /*----- Database Variables -----*/
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("Recipes").child(language);

        /*----- Event Listener For A Single Record -----*/
        mDatabase.orderByChild("timestamp").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) { /*----- For Every Record -----*/

                    Recipe recipe = new Recipe();
                    recipe.setName(ds.child("name").getValue(String.class));

                    ArrayList<Ingredient> ingredients = new ArrayList<>();
                    int counter = 0;
                    Ingredient ingredient = new Ingredient();
                    for (DataSnapshot ingredientDS : ds.child("ingredients").getChildren()) { /*----- Separating For Every Ingredient, Its Name And Quantity -----*/

                        /*----- When Counter Is Even, Ingredient Name Is Added  -----*/
                        if (counter % 2 == 0) {
                            ingredient.setName(ingredientDS.getValue(String.class));
                            counter++;
                            continue;
                        } else ingredient.setQuantity(ingredientDS.getValue(String.class));

                        /*----- When Counter Is Odd, Ingredient Quantity Is Added  -----*/
                        ingredients.add(ingredient);
                        counter++;
                        ingredient = new Ingredient();

                    }//end for ingredients
                    recipe.setIngredients(ingredients);

                    RecipeCategory recipeCategory = new RecipeCategory();
                    recipeCategory.setName(ds.child("category").getValue(String.class));
                    recipe.setCategory(recipeCategory);
                    recipe.setImageURL(ds.child("imageURL").getValue(String.class));
                    recipe.setSteps(ds.child("steps").getValue(String.class));
                    recipeArrayList.add(recipe);


                }
                if (callBack != null) {
                    if (!isLatest)
                        callBack.processFinishedRecipeList(recipeArrayList);
                    else {

                        ArrayList<Recipe> latest_recipes = new ArrayList<>();
                        latest_recipes.add(recipeArrayList.get(recipeArrayList.size() - 1));
                        latest_recipes.add(recipeArrayList.get(recipeArrayList.size() - 2));
                        latest_recipes.add(recipeArrayList.get(recipeArrayList.size() - 3));
                        latest_recipes.add(recipeArrayList.get(recipeArrayList.size() - 4));
                        callBack.processFinishedRecipeList(latest_recipes);
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }


        });


        return recipeArrayList;
    }

    public void getComments(final RecipeFirebaseAsyncResponse callBack, String name, String language) {
        comments = new ArrayList<>();

        /*----- Database Variables -----*/
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("Recipes").child(language).child(name).child("comments");

        mDatabase.orderByChild("date").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot ds : snapshot.getChildren()) {

                    Comment comment = new Comment();

                    comment.setAuthor(ds.child("userId").getValue(String.class));
                    comment.setComment(ds.child("comment").getValue(String.class));
                    comment.setDate(ds.child("date").getValue(String.class));
                    comments.add(comment);
                }

                if (callBack != null) callBack.processFinishedCommentList(comments);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public List<Recipe> getFavouriteRecipes(final FavouritesFirebaseAsyncResponse callback, String userId, String language) {
        favouriteRecipesList = new ArrayList<>();

        /*----- Database Variables -----*/
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("Favourites").child(language).child(userId);
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {


                for (DataSnapshot ds : snapshot.getChildren()) {
                    Recipe recipe;
                    recipe = ds.getValue(Recipe.class);
                    favouriteRecipesList.add(recipe);
                }

                if (callback != null) callback.processFinishedFavouritesList(favouriteRecipesList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return favouriteRecipesList;
    }

    public void getCommentsCount(final CommentsFirebaseAsyncResponse callback, String recipeName, String language) {
        if (db != null) {
            final int[] count = new int[1];
            String path = "Recipes/" + language + "/" + recipeName + "/comments";
            db.child(path).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    count[0] = (int) snapshot.getChildrenCount();

                    if (callback != null) callback.processFinishedCommentsCount(count[0]);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

    }

    public String getCurrentUser() {
        if (user != null) {
            return user.getUid();
        }
        return null;
    }


}
