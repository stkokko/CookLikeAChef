package com.example.recipeapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.recipeapp.R;
import com.example.recipeapp.RecipeActivity;
import com.example.recipeapp.model.Recipe;


import java.util.ArrayList;

public class RecipeCategoryRecyclerViewAdapter extends RecyclerView.Adapter<RecipeCategoryRecyclerViewAdapter.ViewHolder> {

    /*----- Variables -----*/
    private final Context context;
    private final ArrayList<Recipe> recipes;
    private final RequestOptions options;

    /*----- Constructor -----*/
    public RecipeCategoryRecyclerViewAdapter(Context context, ArrayList<Recipe> recipes) {
        this.context = context;
        this.recipes = recipes;

        options = new RequestOptions().centerCrop().placeholder(R.drawable.progress_animation).error(R.drawable.ic_warning);

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.category_recipe_column, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        /*----- Init Variables -----*/
        Recipe recipe = recipes.get(position);
        holder.recipeTextView.setText(String.format("%s\n\n%s", recipe.getName(), recipe.getSteps()));
        Glide.with(context).load(recipe.getImageURL()).apply(options).into(holder.recipeImageView);

    }

    @Override
    public int getItemCount() {
        return recipes.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        /*----- XML Element Variables -----*/
        public CardView recipeCardView;
        public ImageView recipeImageView;
        public TextView recipeTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            /*---------- Hooks ----------*/
            recipeCardView = itemView.findViewById(R.id.recipe_cardView);
            recipeImageView = itemView.findViewById(R.id.recipe_imageView);
            recipeTextView = itemView.findViewById(R.id.recipe_textView);

            /*---------- Event Listeners ----------*/
            recipeCardView.setOnClickListener(v -> {
                Intent recipeIntent = new Intent(context, RecipeActivity.class);
                recipeIntent.putExtra("recipe", recipes.get(getBindingAdapterPosition()));
                (context).startActivity(recipeIntent);
            });
        }


    }


}
