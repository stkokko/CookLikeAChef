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

    private Context context;
    private ArrayList<Recipe> recipes;
    private RequestOptions options;

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

        Recipe recipe = recipes.get(position);
        holder.recipeTextView.setText(String.format("%s\n\n\n%s", recipe.getName(), recipe.getSteps()));
        Glide.with(context).load(recipe.getImageURL()).apply(options).into(holder.recipeImageView);

    }

    @Override
    public int getItemCount() {
        return recipes.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public CardView recipeCardView;
        public ImageView recipeImageView;
        public TextView recipeTextView;
        public TextView readMoreTextView;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            recipeCardView = itemView.findViewById(R.id.recipe_cardView);
            recipeImageView = itemView.findViewById(R.id.recipe_imageView);
            recipeTextView = itemView.findViewById(R.id.recipe_textView);
            readMoreTextView = itemView.findViewById(R.id.read_more_TextView);

            readMoreTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent recipeIntent = new Intent(context, RecipeActivity.class);
                    recipeIntent.putExtra("recipe", recipes.get(getAdapterPosition()));
                    (context).startActivity(recipeIntent);
                }
            });
        }


    }


}
