package com.example.recipeapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import com.example.recipeapp.adapter.RecipeRecyclerViewAdapter;
import com.example.recipeapp.data.RecipeBankFirebase;
import com.example.recipeapp.data.SharedPreferencesLanguage;
import com.example.recipeapp.model.Recipe;
import com.example.recipeapp.util.LanguageUtils;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FavouritesActivity extends AppCompatActivity implements TextWatcher, BottomNavigationView.OnNavigationItemSelectedListener, AdapterView.OnItemClickListener, TextView.OnEditorActionListener {


    /*----- XML Element Variables -----*/
    private RecyclerView recipesRecyclerView;
    private AutoCompleteTextView searchAutoCompleteEditText;
    private TextView empty_list_textView;
    private BottomNavigationView bottomNavigationView;

    /*----- Variables -----*/
    private ArrayList<String> recipeNames;
    private String language;
    private boolean isListUpdated;
    private List<Recipe> favouriteRecipes;
    private RecipeRecyclerViewAdapter adapter;
    private ArrayAdapter<String> adapterNames;
    private String currentUser;

    /*----- Database Variables -----*/
    private RecipeBankFirebase recipeBankFirebase;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourites);

        /*----- Hooks -----*/
        searchAutoCompleteEditText = findViewById(R.id.searchAutoCompleteEditText);
        recipesRecyclerView = findViewById(R.id.search_recipes_recyclerView);
        empty_list_textView = findViewById(R.id.empty_list_textView);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        Toolbar toolbar = findViewById(R.id.toolbar);

        /*----- Get Selected Language -----*/
        SharedPreferences sharedPreferences = getSharedPreferences(LanguageUtils.LANGUAGE_ID, MODE_PRIVATE);
        SharedPreferencesLanguage sharedPreferencesLanguage = new SharedPreferencesLanguage(sharedPreferences);
        language = sharedPreferencesLanguage.getLanguage();

        /*----- Init Variables -----*/
        recipeBankFirebase = new RecipeBankFirebase();
        auth = FirebaseAuth.getInstance();
        currentUser = recipeBankFirebase.getCurrentUser();
        isListUpdated = false;

        /*----- Init Recycler View -----*/
        favouriteRecipes = recipeBankFirebase.getFavouriteRecipes(favouriteRecipes -> {

            recipeNames = new ArrayList<>();
            adapter = new RecipeRecyclerViewAdapter(FavouritesActivity.this, favouriteRecipes);

            for (Recipe recipe : favouriteRecipes) {
                recipeNames.add(recipe.getName());
            }

            adapterNames = new ArrayAdapter<>(FavouritesActivity.this, android.R.layout.simple_dropdown_item_1line, recipeNames);
            searchAutoCompleteEditText.setThreshold(1);
            searchAutoCompleteEditText.setAdapter(adapterNames);

            recipesRecyclerView.setAdapter(adapter);
            recipesRecyclerView.setHasFixedSize(true);
            recipesRecyclerView.setLayoutManager(new LinearLayoutManager(FavouritesActivity.this));


            if (favouriteRecipes.size() == 0) {
                empty_list_textView.setVisibility(View.VISIBLE);
            } else {
                empty_list_textView.setVisibility(View.GONE);
            }
        }, currentUser, language);

        /*---------- Display item as selected ----------*/
        bottomNavigationView.setSelectedItemId(R.id.favourites_item);

        /*----------- Event Listeners -----------*/
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        searchAutoCompleteEditText.setOnItemClickListener(this);
        searchAutoCompleteEditText.setOnEditorActionListener(this);
        searchAutoCompleteEditText.addTextChangedListener(this);

        /*---------- Set Up Toolbar ----------*/
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle(null);
        Objects.requireNonNull(toolbar.getOverflowIcon()).setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        menu.getItem(1).setOnMenuItemClickListener(item -> {
            auth.signOut();
            startActivity(new Intent(FavouritesActivity.this, LogInActivity.class));
            finish();
            return true;
        });

        menu.getItem(0).setOnMenuItemClickListener(item -> {

            String uriText =
                    "mailto:" + getResources().getString(R.string.app_email);

            Uri uri = Uri.parse(uriText);

            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setDataAndType(uri, "text/plain");
            startActivity(Intent.createChooser(intent, getResources().getString(R.string.contact_us)));

            return true;
        });

        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        isListUpdated = true;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        adapterNames.getFilter().filter(s);
        adapter.getFilter().filter(s);

    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    protected void onResume() {
        super.onResume();

        if (isListUpdated) {
            recipeNames = new ArrayList<>();
            recipeBankFirebase = new RecipeBankFirebase();
            bottomNavigationView.setSelectedItemId(R.id.favourites_item);
            recipeBankFirebase.getFavouriteRecipes(favouriteRecipes -> {


                adapter = new RecipeRecyclerViewAdapter(FavouritesActivity.this, favouriteRecipes);

                for (Recipe recipe : favouriteRecipes) {
                    recipeNames.add(recipe.getName());
                }

                adapterNames = new ArrayAdapter<>(FavouritesActivity.this, android.R.layout.simple_dropdown_item_1line, recipeNames);
                searchAutoCompleteEditText.setThreshold(1);
                searchAutoCompleteEditText.setAdapter(adapterNames);


                recipesRecyclerView.setAdapter(adapter);
                recipesRecyclerView.setHasFixedSize(true);
                recipesRecyclerView.setLayoutManager(new LinearLayoutManager(FavouritesActivity.this));


                if (favouriteRecipes.size() == 0) {
                    empty_list_textView.setVisibility(View.VISIBLE);
                } else {
                    empty_list_textView.setVisibility(View.GONE);
                }
            }, currentUser, language);
        }

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.categories_item:
                startActivity(new Intent(this, CategoryDashboardActivity.class));
                overridePendingTransition(R.anim.slide_up_in, R.anim.slide_up_out);
                finish();
                break;
            case R.id.home_item:
                startActivity(new Intent(this, HomeActivity.class));
                overridePendingTransition(R.anim.slide_up_in, R.anim.slide_up_out);
                finish();
                break;
        }
        return true;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        for (Recipe recipe : favouriteRecipes) {
            if (recipe.getName().equalsIgnoreCase(adapterNames.getItem(position))) {
                Intent recipeIntent = new Intent(FavouritesActivity.this, RecipeActivity.class);
                recipeIntent.putExtra("recipe", recipe);
                startActivity(recipeIntent);
                break;
            }
        }
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        // IME_ACTION: actionSearch onClick
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            View view = this.getCurrentFocus();
            if (view != null) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                Objects.requireNonNull(imm).hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
            searchAutoCompleteEditText.clearFocus();
            return true;
        }
        return false;
    }

}