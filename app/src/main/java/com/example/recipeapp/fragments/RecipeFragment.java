package com.example.recipeapp.fragments;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.recipeapp.R;
import com.example.recipeapp.data.RecipeBankFirebase;
import com.example.recipeapp.data.SharedPreferencesLanguage;
import com.example.recipeapp.model.Ingredient;
import com.example.recipeapp.model.Recipe;
import com.example.recipeapp.util.LanguageUtils;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Objects;

import static android.content.Context.MODE_PRIVATE;

public class RecipeFragment extends Fragment implements View.OnClickListener {

    /*----- Variables -----*/
    private Recipe recipe;
    private String language;
    private String currentUser;

    /*----- XML Element Variables -----*/
    private FloatingActionButton addToFavouritesFab;

    /*----- Database Variables -----*/
    private DatabaseReference mDatabase;


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        /*----- Hooks -----*/
        View view = inflater.inflate(R.layout.recipe_fragment, container, false);
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        ImageView recipeImage = view.findViewById(R.id.recipeFragment_ImageView);
        TextView recipeIngredients = view.findViewById(R.id.ingredientsTextView);
        TextView recipeSteps = view.findViewById(R.id.recipeStepsTextView);
        CollapsingToolbarLayout collapsingToolbarLayout = view.findViewById(R.id.collapsingToolbarLayout);
        addToFavouritesFab = view.findViewById(R.id.add_to_favourites_fab);

        /*----- Get Selected Language -----*/
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(LanguageUtils.LANGUAGE_ID, MODE_PRIVATE);
        SharedPreferencesLanguage sharedPreferencesLanguage = new SharedPreferencesLanguage(sharedPreferences);
        language = sharedPreferencesLanguage.getLanguage();

        /*---------- Set Up Toolbar ----------*/
        ((AppCompatActivity) requireActivity()).setSupportActionBar(toolbar);
        Objects.requireNonNull(((AppCompatActivity) requireActivity()).getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationIcon(R.drawable.return_white_icon);

        /*---------- Getting Extras ----------*/
        Intent intent = requireActivity().getIntent();
        recipe = (Recipe) intent.getSerializableExtra("recipe");

        /*---------- Set Up glide options ----------*/
        RequestOptions options = new RequestOptions().centerCrop().placeholder(R.drawable.progress_animation).error(R.drawable.ic_warning);

        /*---------- Init Variables ----------*/
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        currentUser = Objects.requireNonNull(user).getUid();


        new RecipeBankFirebase().getFavouriteRecipes(favourites -> {

            for (Recipe favouriteRecipe : favourites) {
                if (favouriteRecipe.getName().equalsIgnoreCase(recipe.getName())) {
                    addToFavouritesFab.setImageResource(R.drawable.ic_baseline_favorite_36);
                    break;
                } else {
                    addToFavouritesFab.setImageResource(R.drawable.ic_baseline_favorite_border);
                }

            }
        }, currentUser, language);


        StringBuilder ingredients = new StringBuilder();
        for (Ingredient i : recipe.getIngredients()) {
            String name = i.getName().trim().substring(0, 1).toUpperCase() + i.getName().trim().substring(1).toLowerCase();
            ingredients.append("\b• ").append(name).append(" (").append(i.getQuantity()).append(")\n");
        }

        /*---------- Init XML Variables ----------*/
        collapsingToolbarLayout.setTitle(recipe.getName());
        recipeIngredients.setText(ingredients.toString());
        recipeSteps.setText(recipe.getSteps());
        Glide.with(requireActivity().getApplicationContext()).load(recipe.getImageURL()).apply(options).into(recipeImage);

        /*---------- Event Listeners ----------*/
        addToFavouritesFab.setOnClickListener(this);
        toolbar.setNavigationOnClickListener(v -> requireActivity().onBackPressed());

        return view;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.add_to_favourites_fab) {
            addToFavourites(currentUser, recipe);
        }
    }

    private void addToFavourites(final String currentUser, final Recipe recipe) {

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Favourites").child(language);
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean exists = false;
                HashMap<String, Object> hashMap = new HashMap<>();

                mDatabase = mDatabase.child(currentUser);
                for (DataSnapshot ds : snapshot.child(currentUser).getChildren()) {

                    if (Objects.requireNonNull(ds.child("name").getValue()).toString().equalsIgnoreCase(recipe.getName())) {
                        addToFavouritesFab.setImageResource(R.drawable.ic_baseline_favorite_border);
                        mDatabase.child(Objects.requireNonNull(ds.getKey())).removeValue();
                        exists = true;
                        break;
                    }
                }

                if (!exists) {
                    addToFavouritesFab.setImageResource(R.drawable.ic_baseline_favorite_36);
                    hashMap.put("name", recipe.getName());
                    hashMap.put("category", recipe.getCategory());
                    hashMap.put("ingredients", recipe.getIngredients());
                    hashMap.put("imageURL", recipe.getImageURL());
                    hashMap.put("language", language);
                    hashMap.put("steps", recipe.getSteps());
                    mDatabase.push().setValue(hashMap);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}
