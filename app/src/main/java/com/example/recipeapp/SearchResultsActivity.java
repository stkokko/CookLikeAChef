package com.example.recipeapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;


import com.example.recipeapp.adapter.RecipeRecyclerViewAdapter;
import com.example.recipeapp.model.Recipe;

import java.util.ArrayList;
import java.util.Objects;

public class SearchResultsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);

        /*----------- Hooks -----------*/
        Toolbar toolbar = findViewById(R.id.toolbar);
        TextView empty_list_textView = findViewById(R.id.empty_list_textView);
        RecyclerView recyclerView = findViewById(R.id.filtered_recipes_recyclerView);

        /*---------- Getting Extras ----------*/
        Bundle bundle = getIntent().getExtras();
        ArrayList<Recipe> recipes;
        if (bundle != null) {
            recipes = (ArrayList) bundle.get("filteredRecipes");
        } else {
            recipes = new ArrayList<>();
        }

        if (recipes != null) {
            if (recipes.size() == 0) {
                empty_list_textView.setVisibility(View.VISIBLE);
            } else {
                empty_list_textView.setVisibility(View.GONE);
            }
        }

        /*---------- Set Up Toolbar ----------*/
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle(null);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationIcon(R.drawable.return_white_icon);

        /*---------- Event Listeners ----------*/
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        /*---------- Set Up Adapter ----------*/
        RecipeRecyclerViewAdapter adapter = new RecipeRecyclerViewAdapter(this, recipes);

        /*---------- Set Up RecyclerView ----------*/
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }
}