package com.example.recipeapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;


import com.example.recipeapp.adapter.LatestRecipesRecyclerViewAdapter;
import com.example.recipeapp.data.RecipeFirebaseAsyncResponse;
import com.example.recipeapp.data.RecipeBankFirebase;
import com.example.recipeapp.data.SharedPreferencesLanguage;
import com.example.recipeapp.model.Comment;
import com.example.recipeapp.model.Recipe;
import com.example.recipeapp.ui.LoadingDialog;
import com.example.recipeapp.util.LanguageUtils;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Objects;

public class HomeActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    private SharedPreferences sharedPreferences;
    private SharedPreferencesLanguage sharedPreferencesLanguage;
    private LoadingDialog loadingDialog;
    private RecipeBankFirebase recipeBankFirebase;
    private RecyclerView latestRecipesRecyclerView;
    private LatestRecipesRecyclerViewAdapter latestRecipesAdapter;
    private BottomNavigationView bottomNavigationView;
    private FirebaseAuth auth;


    @Override
    protected void onDestroy() {
        super.onDestroy();
        recipeBankFirebase = null;
        sharedPreferences = null;
        sharedPreferencesLanguage = null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        sharedPreferences = getSharedPreferences(LanguageUtils.LANGUAGE_ID, MODE_PRIVATE);
        sharedPreferencesLanguage = new SharedPreferencesLanguage(sharedPreferences);
        String language = sharedPreferencesLanguage.getLanguage();
        loadingDialog = new LoadingDialog(this);
        recipeBankFirebase = new RecipeBankFirebase();
        auth = FirebaseAuth.getInstance();
        loadingDialog.startLoadingDialog();

        /*----------- Hooks -----------*/
        Toolbar toolbar = findViewById(R.id.toolbar);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        latestRecipesRecyclerView = findViewById(R.id.latest_recipes_recyclerView);

        /*---------- Set Up Toolbar ----------*/
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle(null);

        /*---------- Display item as selected ----------*/
        bottomNavigationView.setSelectedItemId(R.id.home_item);

        recipeBankFirebase.getRecipes(new RecipeFirebaseAsyncResponse() {

            @Override
            public void processFinishedRecipeList(final ArrayList<Recipe> latestRecipes) {

                latestRecipesAdapter = new LatestRecipesRecyclerViewAdapter(latestRecipes, HomeActivity.this);
                latestRecipesRecyclerView.setHasFixedSize(true);
                latestRecipesRecyclerView.setLayoutManager(new LinearLayoutManager(HomeActivity.this, LinearLayoutManager.HORIZONTAL, false));
                latestRecipesRecyclerView.setAdapter(latestRecipesAdapter);

                loadingDialog.dismissDialog();
            }

            @Override
            public void processFinishedCommentList(ArrayList<Comment> comments) {

            }

        }, language, true);


        /*----------- Event Listeners -----------*/
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        Objects.requireNonNull(toolbar.getOverflowIcon()).setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        menu.getItem(1).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                auth.signOut();
                startActivity(new Intent(HomeActivity.this, LogInActivity.class));
                finish();
                return true;
            }
        });

        menu.getItem(0).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                String uriText =
                        "mailto:" + getResources().getString(R.string.app_email);

                Uri uri = Uri.parse(uriText);

                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setType("text/plain");
                intent.setData(uri);
                startActivity(Intent.createChooser(intent, getResources().getString(R.string.contact_us)));

                return true;
            }
        });
        return true;
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.categories_item:
                startActivity(new Intent(this, CategoryDashboardActivity.class));
                overridePendingTransition(R.anim.slide_up_in, R.anim.slide_up_out);
                finish();
                break;
            case R.id.favourites_item:
                startActivity(new Intent(this, FavouritesActivity.class));
                overridePendingTransition(R.anim.slide_up_in, R.anim.slide_up_out);
                finish();
                break;
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        bottomNavigationView.setSelectedItemId(R.id.home_item);
    }
}