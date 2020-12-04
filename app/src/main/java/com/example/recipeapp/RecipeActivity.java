package com.example.recipeapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;


import com.example.recipeapp.data.CommentsFirebaseAsyncResponse;
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

    //Fragments Variables
    public static BottomNavigationView bottomNavigationView;
    private BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
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
                }
            };

    //Recipe
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

        SharedPreferences sharedPreferences = getSharedPreferences(LanguageUtils.LANGUAGE_ID, MODE_PRIVATE);
        language = new SharedPreferencesLanguage(sharedPreferences);
        //recipe
        recipe = (Recipe) getIntent().getSerializableExtra("recipe");

        getIntent().putExtra("recipe", recipe);

        //Views of Bottom Navigation
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(navigationItemSelectedListener);
        badgeDrawable = bottomNavigationView.getOrCreateBadge(R.id.commentsNavIcon);
        badgeDrawable.setBackgroundColor(
                ContextCompat.getColor(getApplicationContext(),
                        R.color.colorAccent)
        );


        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, new RecipeFragment()).commit();
        //Views and click Listeners of drawer
        bottomNavigationView.setSelectedItemId(R.id.recipeNavIcon);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private void updateBadge() {
        RecipeBankFirebase recipeBankFirebase = new RecipeBankFirebase();
        recipeBankFirebase.getCommentsCount(new CommentsFirebaseAsyncResponse() {
            @Override
            public void processFinishedCommentsCount(int commentsCount) {
                if (commentsCount > 0) {
                    badgeDrawable.setNumber(commentsCount);
                    badgeDrawable.setVisible(true);
                } else {
                    badgeDrawable.setVisible(false);
                }
            }
        }, recipe.getName(), language.getLanguage());

    }
}