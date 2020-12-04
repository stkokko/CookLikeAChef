package com.example.recipeapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.recipeapp.R;

import java.util.ArrayList;

public class IngredientRecyclerViewAdapter extends RecyclerView.Adapter<IngredientRecyclerViewAdapter.ViewHolder> {

    private Context context;
    private ArrayList<String> ingredients;
    private boolean[] isSelected;
    private OnIngredientListener onIngredientListener;
    public ArrayList<String> selectedIngredients;

    public IngredientRecyclerViewAdapter(Context context, ArrayList<String> ingredients, OnIngredientListener onIngredientListener) {
        this.context = context;
        this.ingredients = ingredients;
        this.onIngredientListener = onIngredientListener;
        selectedIngredients = new ArrayList<>();
        isSelected = new boolean[ingredients.size()];
    }

    public ArrayList<String> getSelectedIngredients() {
        return selectedIngredients;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.filter_ingredients_row, parent, false);
        return new ViewHolder(view, onIngredientListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.ingredientTextView.setText(ingredients.get(position));
        if (isSelected[position])
            holder.ingredientCheckbox.setChecked(true);
        else
            holder.ingredientCheckbox.setChecked(false);

    }

    @Override
    public int getItemCount() {
        return ingredients.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView ingredientTextView;
        public CheckBox ingredientCheckbox;
        OnIngredientListener onIngredientListener;

        public ViewHolder(@NonNull View itemView, final OnIngredientListener onIngredientListener) {
            super(itemView);

            ingredientTextView = itemView.findViewById(R.id.ingredient_filter_textView);
            ingredientCheckbox = itemView.findViewById(R.id.ingredient_checkbox);
            this.onIngredientListener = onIngredientListener;
            ingredientCheckbox.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (!ingredientCheckbox.isChecked()) {
                selectedIngredients.remove(ingredientTextView.getText().toString());
                isSelected[getAdapterPosition()] = false;
            } else {
                selectedIngredients.add(ingredientTextView.getText().toString());
                isSelected[getAdapterPosition()] = true;
            }
            onIngredientListener.onIngredientClick(getAdapterPosition());
        }
    }

    public interface OnIngredientListener {
        void onIngredientClick(int position);
    }
}
