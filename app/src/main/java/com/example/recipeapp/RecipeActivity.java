package com.example.recipeapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;


import android.content.SharedPreferences;
import android.os.Bundle;

import com.example.recipeapp.data.RecipeBankFirebase;
import com.example.recipeapp.data.SharedPreferencesLanguage;
import com.example.recipeapp.fragments.CommentFragment;
import com.example.recipeapp.fragments.RecipeFragment;
import com.example.recipeapp.model.Recipe;
import com.example.recipeapp.util.LanguageUtils;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Objects;

public class RecipeActivity extends AppCompatActivity {

    /*---------- Fragments Variables ----------*/
    public static BottomNavigationView bottomNavigationView;
    private final BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener =
            item -> {
                Fragment selectedFragment = null;

                switch (item.getItemId()) {

                    case R.id.recipeNavIcon:

                        selectedFragment = new RecipeFragment();
                        updateBadge();
                        break;

                    case R.id.commentsNavIcon:
                        selectedFragment = new CommentFragment();
                        updateBadge();
                        break;
                }

                getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, Objects.requireNonNull(selectedFragment)).commit();
                return true;
            };

    /*---------- Variables ----------*/
    private Recipe recipe;
    private SharedPreferencesLanguage language;
    private BadgeDrawable badgeDrawable;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        language = null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);

        /*----- Get Selected Language -----*/
        SharedPreferences sharedPreferences = getSharedPreferences(LanguageUtils.LANGUAGE_ID, MODE_PRIVATE);
        language = new SharedPreferencesLanguage(sharedPreferences);

        /*----- Getting Extras -----*/
        recipe = (Recipe) getIntent().getSerializableExtra("recipe");

        /*----- Putting Extras -----*/
        getIntent().putExtra("recipe", recipe);

        /*---------- Hooks ----------*/
        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        /*---------- Setting Up Badge ----------*/
        badgeDrawable = bottomNavigationView.getOrCreateBadge(R.id.commentsNavIcon);
        badgeDrawable.setBackgroundColor(
                ContextCompat.getColor(getApplicationContext(),
                        R.color.colorAccent)
        );

        /*---------- Fragment Transaction ----------*/
        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, new RecipeFragment()).commit();

        /*---------- Display item as selected ----------*/
        bottomNavigationView.setSelectedItemId(R.id.recipeNavIcon);

        /*---------- Event Listeners ----------*/
        bottomNavigationView.setOnNavigationItemSelectedListener(navigationItemSelectedListener);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private void updateBadge() {
        RecipeBankFirebase recipeBankFirebase = new RecipeBankFirebase();
        recipeBankFirebase.getCommentsCount(commentsCount -> {
            if (commentsCount > 0) {
                badgeDrawable.setNumber(commentsCount);
                badgeDrawable.setVisible(true);
            } else {
                badgeDrawable.setVisible(false);
            }
        }, recipe.getName(), language.getLanguage());

    }
}