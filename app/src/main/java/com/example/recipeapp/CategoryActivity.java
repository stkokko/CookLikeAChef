package com.example.recipeapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.example.recipeapp.adapter.IngredientRecyclerViewAdapter;
import com.example.recipeapp.adapter.RecipeCategoryRecyclerViewAdapter;
import com.example.recipeapp.model.Ingredient;
import com.example.recipeapp.model.Recipe;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class CategoryActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, TextView.OnEditorActionListener,
        TextWatcher, View.OnClickListener, IngredientRecyclerViewAdapter.OnIngredientListener {

    private TextView categoryTextView;
    private AutoCompleteTextView searchAutoCompleteEditText;
    private TextView cancelFilters;
    private BottomSheetDialog bottomSheetDialog;
    private CheckBox saltyCheckbox;
    private CheckBox sweetCheckbox;
    private RecyclerView ingredientRecyclerView;
    private IngredientRecyclerViewAdapter ingredientRecyclerViewAdapter;
    private ArrayList<Recipe> recipes;
    private ArrayAdapter<String> adapterNames;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        /*---------- Hooks ----------*/
        Toolbar toolbar = findViewById(R.id.toolbar);
        categoryTextView = findViewById(R.id.category);
        searchAutoCompleteEditText = findViewById(R.id.searchAutoCompleteEditText);
        ImageView filterImageView = findViewById(R.id.filter_imageView);
        RecyclerView recipesRecyclerView = findViewById(R.id.category_recipes_recyclerView);

        /*---------- Set Up Bundle ----------*/
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            categoryTextView.setText((String) bundle.get("category"));
            recipes = (ArrayList<Recipe>) bundle.get("recipes");
        } else {
            recipes = new ArrayList<>();
        }

        ArrayList<String> recipeNames = new ArrayList<>();
        for (Recipe recipe : recipes) {
            recipeNames.add(recipe.getName());
        }

        if (categoryTextView.getText().toString().equalsIgnoreCase(getResources().getString(R.string.desserts)))
            filterImageView.setVisibility(View.GONE);

        adapterNames = new ArrayAdapter<>(CategoryActivity.this, android.R.layout.simple_dropdown_item_1line, recipeNames);
        searchAutoCompleteEditText.setThreshold(1);
        searchAutoCompleteEditText.setAdapter(adapterNames);

        /*---------- Set Up Toolbar ----------*/
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle(null);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationIcon(R.drawable.return_white_icon);

        /*---------- Set Up Adapter ----------*/
        RecipeCategoryRecyclerViewAdapter adapter = new RecipeCategoryRecyclerViewAdapter(this, recipes);

        /*---------- Set Up Recycler View ----------*/
        recipesRecyclerView.setHasFixedSize(true);
        recipesRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recipesRecyclerView.setAdapter(adapter);


        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        filterImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openBottomSheetDialog();
            }
        });
        searchAutoCompleteEditText.addTextChangedListener(this);
        searchAutoCompleteEditText.setOnEditorActionListener(this);
        searchAutoCompleteEditText.setOnItemClickListener(this);
    }

    private void openBottomSheetDialog() {

        View bottomSheetView;
        FloatingActionButton ingredientFilterFab;
        if (categoryTextView.getText().toString().equalsIgnoreCase(getResources().getString(R.string.brunch))) {
            bottomSheetDialog = new BottomSheetDialog(CategoryActivity.this, R.style.BottomSheetDialogTheme);
            bottomSheetView = LayoutInflater.from(this).inflate(R.layout.layout_bottom_sheet_brunch,
                    (RelativeLayout) findViewById(R.id.bottomSheetContainer));
            ImageView closeImageView = bottomSheetView.findViewById(R.id.close_imageView);
            ingredientRecyclerView = bottomSheetView.findViewById(R.id.ingredients_recyclerView);
            cancelFilters = bottomSheetView.findViewById(R.id.cancel_textView);
            saltyCheckbox = bottomSheetView.findViewById(R.id.checkbox_salty);
            sweetCheckbox = bottomSheetView.findViewById(R.id.checkbox_sweet);
            ingredientFilterFab = bottomSheetView.findViewById(R.id.ingredient_filter_fab);

            if (!sweetCheckbox.isChecked() && !saltyCheckbox.isChecked()) {
                String[] brunchIngredients = getResources().getStringArray(R.array.brunch_ingredients);
                ingredientRecyclerViewAdapter = new IngredientRecyclerViewAdapter(CategoryActivity.this, new ArrayList<>(Arrays.asList(brunchIngredients)), this);
            }

            closeImageView.setOnClickListener(this);
            saltyCheckbox.setOnClickListener(this);
            sweetCheckbox.setOnClickListener(this);


            bottomSheetDialog.setContentView(bottomSheetView);
            bottomSheetDialog.show();


            ingredientRecyclerView.setHasFixedSize(true);
            ingredientRecyclerView.setLayoutManager(new LinearLayoutManager(CategoryActivity.this));
            ingredientRecyclerView.setAdapter(ingredientRecyclerViewAdapter);
            cancelFilters.setOnClickListener(this);
            ingredientFilterFab.setOnClickListener(this);


        } else if (categoryTextView.getText().toString().equalsIgnoreCase(getResources().getString(R.string.salads))) {

            bottomSheetDialog = new BottomSheetDialog(CategoryActivity.this, R.style.BottomSheetDialogTheme);
            bottomSheetView = LayoutInflater.from(this).inflate(R.layout.layout_bottom_sheet_all,
                    (RelativeLayout) findViewById(R.id.bottomSheetContainer));
            ImageView closeImageView = bottomSheetView.findViewById(R.id.close_imageView);
            ingredientRecyclerView = bottomSheetView.findViewById(R.id.ingredients_recyclerView);
            cancelFilters = bottomSheetView.findViewById(R.id.cancel_textView);

            String[] saladsIngredients = getResources().getStringArray(R.array.salads_ingredients);
            ingredientRecyclerViewAdapter = new IngredientRecyclerViewAdapter(CategoryActivity.this, new ArrayList<>(Arrays.asList(saladsIngredients)), this);
            ingredientFilterFab = bottomSheetView.findViewById(R.id.ingredient_filter_fab);

            closeImageView.setOnClickListener(this);
            bottomSheetDialog.setContentView(bottomSheetView);
            bottomSheetDialog.show();

            ingredientRecyclerView.setHasFixedSize(true);
            ingredientRecyclerView.setLayoutManager(new LinearLayoutManager(CategoryActivity.this));
            ingredientRecyclerView.setAdapter(ingredientRecyclerViewAdapter);
            cancelFilters.setOnClickListener(this);
            ingredientFilterFab.setOnClickListener(this);


        } else if (categoryTextView.getText().toString().equalsIgnoreCase(getResources().getString(R.string.main_dishes))) {

            bottomSheetDialog = new BottomSheetDialog(CategoryActivity.this, R.style.BottomSheetDialogTheme);
            bottomSheetView = LayoutInflater.from(this).inflate(R.layout.layout_bottom_sheet_all,
                    (RelativeLayout) findViewById(R.id.bottomSheetContainer));
            ImageView closeImageView = bottomSheetView.findViewById(R.id.close_imageView);
            ingredientRecyclerView = bottomSheetView.findViewById(R.id.ingredients_recyclerView);
            cancelFilters = bottomSheetView.findViewById(R.id.cancel_textView);
            ingredientFilterFab = bottomSheetView.findViewById(R.id.ingredient_filter_fab);

            String[] mainDishesIngredients = getResources().getStringArray(R.array.main_dishes_ingredients);
            ingredientRecyclerViewAdapter = new IngredientRecyclerViewAdapter(CategoryActivity.this, new ArrayList<>(Arrays.asList(mainDishesIngredients)), this);

            closeImageView.setOnClickListener(this);
            bottomSheetDialog.setContentView(bottomSheetView);
            bottomSheetDialog.show();

            ingredientRecyclerView.setHasFixedSize(true);
            ingredientRecyclerView.setLayoutManager(new LinearLayoutManager(CategoryActivity.this));
            ingredientRecyclerView.setAdapter(ingredientRecyclerViewAdapter);
            cancelFilters.setOnClickListener(this);
            ingredientFilterFab.setOnClickListener(this);

        } else if (categoryTextView.getText().toString().equalsIgnoreCase(getResources().getString(R.string.burgers))) {

            bottomSheetDialog = new BottomSheetDialog(CategoryActivity.this, R.style.BottomSheetDialogTheme);
            bottomSheetView = LayoutInflater.from(this).inflate(R.layout.layout_bottom_sheet_all,
                    (RelativeLayout) findViewById(R.id.bottomSheetContainer));
            ImageView closeImageView = bottomSheetView.findViewById(R.id.close_imageView);
            ingredientRecyclerView = bottomSheetView.findViewById(R.id.ingredients_recyclerView);
            cancelFilters = bottomSheetView.findViewById(R.id.cancel_textView);
            ingredientFilterFab = bottomSheetView.findViewById(R.id.ingredient_filter_fab);

            String[] burgerIngredients = getResources().getStringArray(R.array.burger_ingredients);
            ingredientRecyclerViewAdapter = new IngredientRecyclerViewAdapter(CategoryActivity.this, new ArrayList<>(Arrays.asList(burgerIngredients)), this);

            closeImageView.setOnClickListener(this);
            bottomSheetDialog.setContentView(bottomSheetView);
            bottomSheetDialog.show();

            ingredientRecyclerView.setHasFixedSize(true);
            ingredientRecyclerView.setLayoutManager(new LinearLayoutManager(CategoryActivity.this));
            ingredientRecyclerView.setAdapter(ingredientRecyclerViewAdapter);
            cancelFilters.setOnClickListener(this);
            ingredientFilterFab.setOnClickListener(this);

        }


    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        for (Recipe recipe : recipes) {
            if (recipe.getName().equalsIgnoreCase(adapterNames.getItem(position))) {
                Intent recipeIntent = new Intent(CategoryActivity.this, RecipeActivity.class);
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


            Intent searchResultsIntent = new Intent(CategoryActivity.this, SearchResultsActivity.class);
            searchResultsIntent.putExtra("filteredRecipes", filterByName());
            startActivity(searchResultsIntent);

            return true;
        }
        return false;
    }

    private ArrayList<Recipe> filterByName() {

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
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        adapterNames.getFilter().filter(s);
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    public void onClick(View v) {
        String[] brunchSaltyIngredients;
        String[] brunchSweetIngredients;
        switch (v.getId()) {
            case R.id.close_imageView:
                bottomSheetDialog.dismiss();
                break;
            case R.id.checkbox_salty:

                cancelFilters.setVisibility(View.GONE);
                if (saltyCheckbox.isChecked() && !sweetCheckbox.isChecked()) {
                    brunchSaltyIngredients = getResources().getStringArray(R.array.salty_ingredients);
                    ingredientRecyclerViewAdapter = new IngredientRecyclerViewAdapter(CategoryActivity.this, new ArrayList<>(Arrays.asList(brunchSaltyIngredients)), this);
                } else if (!saltyCheckbox.isChecked() && sweetCheckbox.isChecked()) {
                    brunchSweetIngredients = getResources().getStringArray(R.array.sweet_ingredients);
                    ingredientRecyclerViewAdapter = new IngredientRecyclerViewAdapter(CategoryActivity.this, new ArrayList<>(Arrays.asList(brunchSweetIngredients)), this);
                } else {
                    String[] brunchIngredients = getResources().getStringArray(R.array.brunch_ingredients);
                    ingredientRecyclerViewAdapter = new IngredientRecyclerViewAdapter(CategoryActivity.this, new ArrayList<>(Arrays.asList(brunchIngredients)), this);
                }
                ingredientRecyclerView.setAdapter(ingredientRecyclerViewAdapter);
                break;
            case R.id.checkbox_sweet:

                cancelFilters.setVisibility(View.GONE);
                if (sweetCheckbox.isChecked() && !saltyCheckbox.isChecked()) {
                    brunchSweetIngredients = getResources().getStringArray(R.array.sweet_ingredients);
                    ingredientRecyclerViewAdapter = new IngredientRecyclerViewAdapter(CategoryActivity.this, new ArrayList<>(Arrays.asList(brunchSweetIngredients)), this);
                } else if (!sweetCheckbox.isChecked() && saltyCheckbox.isChecked()) {
                    brunchSaltyIngredients = getResources().getStringArray(R.array.salty_ingredients);
                    ingredientRecyclerViewAdapter = new IngredientRecyclerViewAdapter(CategoryActivity.this, new ArrayList<>(Arrays.asList(brunchSaltyIngredients)), this);
                } else {
                    String[] brunchIngredients = getResources().getStringArray(R.array.brunch_ingredients);
                    ingredientRecyclerViewAdapter = new IngredientRecyclerViewAdapter(CategoryActivity.this, new ArrayList<>(Arrays.asList(brunchIngredients)), this);
                }
                ingredientRecyclerView.setAdapter(ingredientRecyclerViewAdapter);
                break;
            case R.id.cancel_textView:

                if (saltyCheckbox != null && saltyCheckbox.isChecked()) {
                    brunchSaltyIngredients = getResources().getStringArray(R.array.salty_ingredients);
                    ingredientRecyclerViewAdapter = new IngredientRecyclerViewAdapter(CategoryActivity.this, new ArrayList<>(Arrays.asList(brunchSaltyIngredients)), this);
                    saltyCheckbox.setChecked(false);
                } else if (sweetCheckbox != null && sweetCheckbox.isChecked()) {
                    brunchSweetIngredients = getResources().getStringArray(R.array.sweet_ingredients);
                    ingredientRecyclerViewAdapter = new IngredientRecyclerViewAdapter(CategoryActivity.this, new ArrayList<>(Arrays.asList(brunchSweetIngredients)), this);
                    sweetCheckbox.setChecked(false);
                }

                if (categoryTextView.getText().toString().equalsIgnoreCase(getResources().getString(R.string.brunch))) {
                    String[] brunchIngredients = getResources().getStringArray(R.array.brunch_ingredients);
                    ingredientRecyclerViewAdapter = new IngredientRecyclerViewAdapter(CategoryActivity.this, new ArrayList<>(Arrays.asList(brunchIngredients)), this);
                } else if (categoryTextView.getText().toString().equalsIgnoreCase(getResources().getString(R.string.salads))) {
                    String[] saladsIngredients = getResources().getStringArray(R.array.salads_ingredients);
                    ingredientRecyclerViewAdapter = new IngredientRecyclerViewAdapter(CategoryActivity.this, new ArrayList<>(Arrays.asList(saladsIngredients)), this);
                } else if (categoryTextView.getText().toString().equalsIgnoreCase(getResources().getString(R.string.main_dishes))) {
                    String[] mainDishesIngredients = getResources().getStringArray(R.array.main_dishes_ingredients);
                    ingredientRecyclerViewAdapter = new IngredientRecyclerViewAdapter(CategoryActivity.this, new ArrayList<>(Arrays.asList(mainDishesIngredients)), this);
                } else if (categoryTextView.getText().toString().equalsIgnoreCase(getResources().getString(R.string.burgers))) {
                    String[] burgersIngredients = getResources().getStringArray(R.array.burger_ingredients);
                    ingredientRecyclerViewAdapter = new IngredientRecyclerViewAdapter(CategoryActivity.this, new ArrayList<>(Arrays.asList(burgersIngredients)), this);
                }

                ingredientRecyclerView.setAdapter(ingredientRecyclerViewAdapter);
                cancelFilters.setVisibility(View.GONE);
                break;
            case R.id.ingredient_filter_fab:
                if (ingredientRecyclerViewAdapter.getSelectedIngredients().size() == 0) {
                    Snackbar.make(v, getResources().getString(R.string.please_choose_ingredient), Snackbar.LENGTH_LONG).show();
                } else {
                    Intent searchResultsIntent = new Intent(CategoryActivity.this, SearchResultsActivity.class);
                    searchResultsIntent.putExtra("filteredRecipes", filterByIngredients());
                    startActivity(searchResultsIntent);
                }

        }
    }

    private ArrayList<Recipe> filterByIngredients() {
        ArrayList<Recipe> filteredRecipes = new ArrayList<>();


        for (Recipe recipe : recipes) {
            int counter = 0;

            for (Ingredient ingredient : recipe.getIngredients()) {

                for (int i = 0; i < ingredientRecyclerViewAdapter.getSelectedIngredients().size(); i++) {

                    if (ingredient.getName().equalsIgnoreCase(ingredientRecyclerViewAdapter.getSelectedIngredients().get(i))) {

                        counter++;

                        if (counter == ingredientRecyclerViewAdapter.getSelectedIngredients().size()) {
                            filteredRecipes.add(recipe);
                            break;
                        }
                    }
                }
            }

        }


        return filteredRecipes;
    }


    @Override
    public void onIngredientClick(int position) {

        if (ingredientRecyclerViewAdapter.getSelectedIngredients().size() == 0)
            cancelFilters.setVisibility(View.GONE);
        else
            cancelFilters.setVisibility(View.VISIBLE);
    }
}