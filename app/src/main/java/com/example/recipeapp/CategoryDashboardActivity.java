package com.example.recipeapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import com.example.recipeapp.data.RecipeBankFirebase;
import com.example.recipeapp.data.RecipeFirebaseAsyncResponse;
import com.example.recipeapp.data.SharedPreferencesLanguage;
import com.example.recipeapp.model.Comment;
import com.example.recipeapp.model.Recipe;
import com.example.recipeapp.ui.LoadingDialog;
import com.example.recipeapp.util.LanguageUtils;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CategoryDashboardActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener, TextWatcher, AdapterView.OnItemClickListener, TextView.OnEditorActionListener, View.OnClickListener {

    /*----- XML Element Variables -----*/
    private BottomNavigationView bottomNavigationView;
    private AutoCompleteTextView searchAutoCompleteEditText;

    /*----- Variables -----*/
    private ArrayList<String> recipeNames;
    private ArrayAdapter<String> adapterNames;
    private ArrayList<Recipe> brunchRecipes;
    private ArrayList<Recipe> saladsRecipes;
    private ArrayList<Recipe> mainDishesRecipes;
    private ArrayList<Recipe> burgersRecipes;
    private ArrayList<Recipe> dessertsRecipes;
    private LoadingDialog loadingDialog;
    private List<Recipe> recipes;

    /*----- Database Variables -----*/
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_dashboard);

        /*---------- Hooks ----------*/
        Toolbar toolbar = findViewById(R.id.toolbar);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        searchAutoCompleteEditText = findViewById(R.id.searchAutoCompleteEditText);
        CardView brunchCardView = findViewById(R.id.brunch);
        CardView saladsCardView = findViewById(R.id.salads);
        CardView mainDishesCardView = findViewById(R.id.main_dishes);
        CardView burgersCardView = findViewById(R.id.burgers);
        CardView dessertsCardView = findViewById(R.id.desserts);

        /*----- Get Selected Language -----*/
        SharedPreferences sharedPreferences = getSharedPreferences(LanguageUtils.LANGUAGE_ID, MODE_PRIVATE);
        SharedPreferencesLanguage sharedPreferencesLanguage = new SharedPreferencesLanguage(sharedPreferences);
        String language = sharedPreferencesLanguage.getLanguage();

        /*----- Init Variables -----*/
        auth = FirebaseAuth.getInstance();
        loadingDialog = new LoadingDialog(this);
        loadingDialog.startLoadingDialog();
        RecipeBankFirebase recipeBankFirebase = new RecipeBankFirebase();


        recipes = recipeBankFirebase.getRecipes(new RecipeFirebaseAsyncResponse() {
            @Override
            public void processFinishedRecipeList(ArrayList<Recipe> recipes) {

                initCategoryLists(recipes);

                adapterNames = new ArrayAdapter<>(CategoryDashboardActivity.this, android.R.layout.simple_dropdown_item_1line, recipeNames);
                searchAutoCompleteEditText.setThreshold(1);
                searchAutoCompleteEditText.setAdapter(adapterNames);

                loadingDialog.dismissDialog();
            }

            @Override
            public void processFinishedCommentList(ArrayList<Comment> comments) {

            }
        }, language, false);

        /*---------- Set Up Toolbar ----------*/
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle(null);
        Objects.requireNonNull(toolbar.getOverflowIcon()).setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);

        /*---------- Display item as selected ----------*/
        bottomNavigationView.setSelectedItemId(R.id.categories_item);

        /*----------- Event Listeners -----------*/
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        searchAutoCompleteEditText.addTextChangedListener(this);
        searchAutoCompleteEditText.setOnEditorActionListener(this);
        brunchCardView.setOnClickListener(this);
        saladsCardView.setOnClickListener(this);
        mainDishesCardView.setOnClickListener(this);
        burgersCardView.setOnClickListener(this);
        dessertsCardView.setOnClickListener(this);
        searchAutoCompleteEditText.setOnItemClickListener(this);
    }

    /*
        In this method we separate recipes in categories
     */
    private void initCategoryLists(ArrayList<Recipe> recipes) {
        recipeNames = new ArrayList<>();
        brunchRecipes = new ArrayList<>();
        saladsRecipes = new ArrayList<>();
        mainDishesRecipes = new ArrayList<>();
        burgersRecipes = new ArrayList<>();
        dessertsRecipes = new ArrayList<>();
        for (Recipe recipe : recipes) {
            recipeNames.add(recipe.getName());

            switch (recipe.getCategory().getName()) {
                case "Brunch":
                    brunchRecipes.add(recipe);
                    break;
                case "Salads":
                    saladsRecipes.add(recipe);
                    break;
                case "Main Dishes":
                    mainDishesRecipes.add(recipe);
                    break;
                case "Burgers":
                    burgersRecipes.add(recipe);
                    break;
                case "Desserts":
                    dessertsRecipes.add(recipe);
                    break;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        menu.getItem(1).setOnMenuItemClickListener(item -> {
            auth.signOut();
            startActivity(new Intent(CategoryDashboardActivity.this, LogInActivity.class));
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
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.home_item:
                startActivity(new Intent(this, HomeActivity.class));
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
        searchAutoCompleteEditText.setText(null);
        searchAutoCompleteEditText.clearFocus();
        bottomNavigationView.setSelectedItemId(R.id.categories_item);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (adapterNames != null) {
            adapterNames.getFilter().filter(s);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        for (Recipe recipe : recipes) {
            if (recipe.getName().equalsIgnoreCase(adapterNames.getItem(position))) {
                Intent recipeIntent = new Intent(CategoryDashboardActivity.this, RecipeActivity.class);
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
            searchAutoCompleteEditText.setText("");

            Intent searchResultsIntent = new Intent(CategoryDashboardActivity.this, SearchResultsActivity.class);
            searchResultsIntent.putExtra("filteredRecipes", getByName());
            startActivity(searchResultsIntent);

            return true;
        }
        return false;
    }

    /*
        In this method we return an array list which contains all
        possible recipe results based on user's input
     */
    private ArrayList<Recipe> getByName() {

        ArrayList<Recipe> filteredRecipes = new ArrayList<>();
        for (Recipe recipe : recipes) {
            for (int i = 0; i < adapterNames.getCount(); i++) {
                if (recipe.getName().equalsIgnoreCase(adapterNames.getItem(i))) {
                    filteredRecipes.add(recipe);
                    break;
                }//end if
            }//end for
        }//end for
        return filteredRecipes;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.brunch:
                //Start Brunch
                Intent brunchIntent = new Intent(CategoryDashboardActivity.this, CategoryActivity.class);
                brunchIntent.putExtra("category", getResources().getString(R.string.brunch));
                brunchIntent.putExtra("recipes", brunchRecipes);
                startActivity(brunchIntent);
                break;
            case R.id.salads:
                //Start Salads
                Intent saladsIntent = new Intent(CategoryDashboardActivity.this, CategoryActivity.class);
                saladsIntent.putExtra("category", getResources().getString(R.string.salads));
                saladsIntent.putExtra("recipes", saladsRecipes);
                startActivity(saladsIntent);
                break;
            case R.id.main_dishes:
                //Start Main Dishes
                Intent mainDishesIntent = new Intent(CategoryDashboardActivity.this, CategoryActivity.class);
                mainDishesIntent.putExtra("category", getResources().getString(R.string.main_dishes));
                mainDishesIntent.putExtra("recipes", mainDishesRecipes);
                startActivity(mainDishesIntent);
                break;
            case R.id.burgers:
                //Start Burgers
                Intent burgersIntent = new Intent(CategoryDashboardActivity.this, CategoryActivity.class);
                burgersIntent.putExtra("category", getResources().getString(R.string.burgers));
                burgersIntent.putExtra("recipes", burgersRecipes);
                startActivity(burgersIntent);
                break;
            case R.id.desserts:
                //Start Desserts
                Intent dessertsIntent = new Intent(CategoryDashboardActivity.this, CategoryActivity.class);
                dessertsIntent.putExtra("category", getResources().getString(R.string.desserts));
                dessertsIntent.putExtra("recipes", dessertsRecipes);
                startActivity(dessertsIntent);
                break;
        }

    }
}