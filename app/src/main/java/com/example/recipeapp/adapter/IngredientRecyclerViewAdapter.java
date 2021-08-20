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

    /*----- Variables -----*/
    private final Context context;
    private final ArrayList<String> ingredients;
    private final boolean[] isSelected;
    private final OnIngredientListener onIngredientListener;
    public ArrayList<String> selectedIngredients;

    /*----- Constructor -----*/
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

        /*----- Init Variables -----*/
        holder.ingredientTextView.setText(ingredients.get(position));
        holder.ingredientCheckbox.setChecked(isSelected[position]);

    }

    @Override
    public int getItemCount() {
        return ingredients.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        /*----- XML Element Variables -----*/
        public TextView ingredientTextView;
        public CheckBox ingredientCheckbox;


        OnIngredientListener onIngredientListener;

        public ViewHolder(@NonNull View itemView, final OnIngredientListener onIngredientListener) {
            super(itemView);

            /*---------- Hooks ----------*/
            ingredientTextView = itemView.findViewById(R.id.ingredient_filter_textView);
            ingredientCheckbox = itemView.findViewById(R.id.ingredient_checkbox);

            /*---------- Event Listeners ----------*/
            this.onIngredientListener = onIngredientListener;
            ingredientCheckbox.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (!ingredientCheckbox.isChecked()) {
                selectedIngredients.remove(ingredientTextView.getText().toString());
                isSelected[getBindingAdapterPosition()] = false;
            } else {
                selectedIngredients.add(ingredientTextView.getText().toString());
                isSelected[getBindingAdapterPosition()] = true;
            }
            onIngredientListener.onIngredientClick(getBindingAdapterPosition());
        }
    }

    public interface OnIngredientListener {
        void onIngredientClick(int position);
    }
}
