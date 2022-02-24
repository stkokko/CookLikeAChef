package com.example.recipeapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.recipeapp.R;
import com.example.recipeapp.RecipeActivity;
import com.example.recipeapp.model.Recipe;


import java.util.ArrayList;

public class RecipeCategoryViewPager2Adapter extends RecyclerView.Adapter<RecipeCategoryViewPager2Adapter.ViewHolder> {

    /*----- Variables -----*/
    private final Context context;
    private final ArrayList<Recipe> recipes;
    private final RequestOptions options;

    /*----- Constructor -----*/
    public RecipeCategoryViewPager2Adapter(Context context, ArrayList<Recipe> recipes) {
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
        Glide.with(context).load(recipe.getImageURL()).apply(options).into(holder.recipeImageView);

    }

    @Override
    public int getItemCount() {
        return recipes.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        /*----- XML Element Variables -----*/
        public ImageView recipeImageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            /*---------- Hooks ----------*/
            recipeImageView = itemView.findViewById(R.id.viewPagerItem_imageView);

            /*---------- Event Listeners ----------*/
            recipeImageView.setOnClickListener(v -> {
                Intent recipeIntent = new Intent(context, RecipeActivity.class);
                recipeIntent.putExtra("recipe", recipes.get(getBindingAdapterPosition()));
                (context).startActivity(recipeIntent);
            });
        }


    }


}
