package com.example.recipeapp.adapter;


import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.recipeapp.R;
import com.example.recipeapp.RecipeActivity;
import com.example.recipeapp.model.Recipe;

import java.util.ArrayList;

public class LatestRecipesRecyclerViewAdapter extends RecyclerView.Adapter<LatestRecipesRecyclerViewAdapter.ViewHolder> {

    /*----- Variables -----*/
    private final ArrayList<Recipe> latestRecipes;
    private final Context context;
    private final RequestOptions options;

    /*----- Constructor -----*/
    public LatestRecipesRecyclerViewAdapter(ArrayList<Recipe> latestRecipes, Context context) {
        this.latestRecipes = latestRecipes;
        this.context = context;

        options = new RequestOptions().centerCrop().placeholder(R.drawable.progress_animation).error(R.drawable.ic_warning);
    }

    @NonNull
    @Override
    public LatestRecipesRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.latest_recipes_column, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LatestRecipesRecyclerViewAdapter.ViewHolder holder, int position) {

        /*----- Init Variables -----*/
        Recipe current_latest_recipe = latestRecipes.get(position);
        holder.recipeTitleTextView.setText(current_latest_recipe.getName());
        Glide.with(context).load(current_latest_recipe.getImageURL()).apply(options).into(holder.recipeImageView);

    }

    @Override
    public int getItemCount() {
        return latestRecipes.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements AdapterView.OnClickListener {

        /*----- XML Element Variables -----*/
        public ImageView recipeImageView;
        public TextView recipeTitleTextView;
        public Button readMoreButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            /*---------- Hooks ----------*/
            recipeImageView = itemView.findViewById(R.id.latest_imageView);
            recipeTitleTextView = itemView.findViewById(R.id.latest_textView);
            readMoreButton = itemView.findViewById(R.id.latest_button);

            /*---------- Event Listeners ----------*/
            readMoreButton.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            Intent recipeIntent = new Intent(context, RecipeActivity.class);
            recipeIntent.putExtra("recipe", latestRecipes.get(getBindingAdapterPosition()));
            context.startActivity(recipeIntent);
        }


    }


}
