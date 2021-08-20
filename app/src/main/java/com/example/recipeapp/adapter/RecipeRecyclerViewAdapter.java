package com.example.recipeapp.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Filter;
import android.widget.Filterable;
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
import java.util.Collection;
import java.util.List;

public class RecipeRecyclerViewAdapter extends RecyclerView.Adapter<RecipeRecyclerViewAdapter.ViewHolder> implements Filterable {

    /*----- Variables -----*/
    private final Context context;
    private final List<Recipe> recipes;
    private final List<Recipe> recipesAll;
    private final RequestOptions options;


    /*----- Constructor -----*/
    public RecipeRecyclerViewAdapter(Context context, List<Recipe> recipes) {
        this.context = context;
        this.recipes = recipes;
        this.recipesAll = new ArrayList<>(recipes);

        options = new RequestOptions().centerCrop().placeholder(R.drawable.progress_animation).error(R.drawable.ic_warning);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.recipe_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeRecyclerViewAdapter.ViewHolder holder, int position) {


        /*----- Init Variables -----*/
        holder.recipeCardView.setAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_transition_animation));

        Recipe recipe = recipes.get(position);
        holder.recipeTitleTextView.setText(recipe.getName());
        Glide.with(context).load(recipe.getImageURL()).apply(options).into(holder.recipeImageView);
        holder.recipeImageView.setAlpha(0.5f);

    }

    @Override
    public int getItemCount() {
        return recipes.size();
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            List<Recipe> filteredList = new ArrayList<>();
            if (constraint.toString().isEmpty()) {
                filteredList.addAll(recipesAll);
            } else {
                for (Recipe recipe : recipesAll) {
                    if (recipe.getName().toLowerCase().contains(constraint.toString().toLowerCase())) {
                        filteredList.add(recipe);
                    }
                }
            }

            FilterResults filterResults = new FilterResults();
            filterResults.values = filteredList;


            return filterResults;
        }

        @SuppressLint("NotifyDataSetChanged")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {

            recipes.clear();
            recipes.addAll((Collection<? extends Recipe>) results.values);
            notifyDataSetChanged();

        }
    };


    public class ViewHolder extends RecyclerView.ViewHolder implements AdapterView.OnClickListener {

        /*----- XML Element Variables -----*/
        public ImageView recipeImageView;
        public TextView recipeTitleTextView;
        public CardView recipeCardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            /*---------- Hooks ----------*/
            recipeImageView = itemView.findViewById(R.id.recipe_imageView);
            recipeTitleTextView = itemView.findViewById(R.id.recipe_title_TextView);
            recipeCardView = itemView.findViewById(R.id.recipe_cardView);
            /*---------- Event Listeners ----------*/
            recipeImageView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Intent recipeIntent = new Intent(context, RecipeActivity.class);
            recipeIntent.putExtra("recipe", recipes.get(getBindingAdapterPosition()));
            context.startActivity(recipeIntent);

        }
    }


}
