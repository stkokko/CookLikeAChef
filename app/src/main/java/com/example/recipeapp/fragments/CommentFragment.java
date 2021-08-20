package com.example.recipeapp.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.recipeapp.R;
import com.example.recipeapp.RecipeActivity;
import com.example.recipeapp.adapter.CommentRecyclerViewAdapter;
import com.example.recipeapp.data.RecipeBankFirebase;
import com.example.recipeapp.data.RecipeFirebaseAsyncResponse;
import com.example.recipeapp.data.SharedPreferencesLanguage;
import com.example.recipeapp.model.Comment;
import com.example.recipeapp.model.Recipe;
import com.example.recipeapp.ui.LoadingDialog;
import com.example.recipeapp.util.LanguageUtils;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;

import static android.content.Context.MODE_PRIVATE;

public class CommentFragment extends Fragment implements View.OnClickListener {

    /*----- XML Element Variables -----*/
    private RecyclerView recyclerView;
    private EditText commentEditText;

    /*----- Database Variables -----*/
    private DatabaseReference mDatabase;
    private FirebaseUser user;

    /*----- Variables -----*/
    private String language;
    private LoadingDialog loadingDialog;
    private Comment deletedComment;
    private Recipe recipe;
    private CommentRecyclerViewAdapter commentRecyclerViewAdapter;
    private ArrayList<Comment> commentsArrayList;


    /*----- Swipe Variables -----*/
    /*
        Deleting comment when user swipes right.
     */
    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

            int position = viewHolder.getBindingAdapterPosition();
            deletedComment = commentsArrayList.get(position);

            if (user.getUid().equals(deletedComment.getAuthor())) {

                deleteUserComment(deletedComment, user.getUid(), position);
                Snackbar.make(recyclerView, "Comment deleted", Snackbar.LENGTH_LONG)
                        .setAction("Undo", v -> saveComment(deletedComment)).show();
            } else {
                commentRecyclerViewAdapter.notifyItemChanged(position);
            }
        }
    };

    private void deleteUserComment(final Comment deletedComment, final String uid, final int position) {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase = mDatabase.child("Recipes").child(language).child(recipe.getName()).child("comments");

        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot ds : snapshot.getChildren()) {
                    String author = ds.child("userId").getValue(String.class);
                    String commentText = ds.child("comment").getValue(String.class);
                    Log.d("DELETE_COMMENT", "onDataChange: " + " Author: " + ds.getKey());
                    if (author != null && author.equals(uid) && Objects.equals(commentText, deletedComment.getComment())) {
                        Objects.requireNonNull(((RecipeActivity) getActivity()).bottomNavigationView.getBadge(R.id.commentsNavIcon)).setNumber(recipe.getComment().size() - 1);
                        commentsArrayList.remove(position);
                        commentRecyclerViewAdapter.notifyDataSetChanged();
                        mDatabase.child(Objects.requireNonNull(ds.getKey())).removeValue();
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.comments_fragment, container, false);

        /*----- Init Variables -----*/
        loadingDialog = new LoadingDialog(getContext());
        loadingDialog.startLoadingDialog();
        RecipeBankFirebase recipeBankFirebase = new RecipeBankFirebase();

        /*----- Get Selected Language -----*/
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences(LanguageUtils.LANGUAGE_ID, MODE_PRIVATE);
        SharedPreferencesLanguage sharedPreferencesLanguage = new SharedPreferencesLanguage(sharedPreferences);
        language = sharedPreferencesLanguage.getLanguage();

        /*----- Getting Extras -----*/
        Intent intent = requireActivity().getIntent();
        recipe = (Recipe) intent.getSerializableExtra("recipe");

        /*----- Hooks -----*/
        recyclerView = view.findViewById(R.id.comments_recyclerView);
        ImageView sendComment = view.findViewById(R.id.send_comment_imageView);
        commentEditText = view.findViewById(R.id.comment_editText);
        Toolbar toolbar = view.findViewById(R.id.comment_toolbar);

        /*---------- Set Up Toolbar ----------*/
        ((AppCompatActivity) requireActivity()).setSupportActionBar(toolbar);
        Objects.requireNonNull(((AppCompatActivity) requireActivity()).getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        toolbar.setTitle(recipe.getName().trim());
        toolbar.setNavigationIcon(R.drawable.return_white_icon);

        /*----- Init Variables -----*/
        FirebaseAuth auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();


        recipeBankFirebase.getComments(new RecipeFirebaseAsyncResponse() {
            @Override
            public void processFinishedRecipeList(ArrayList<Recipe> recipes) {
            }

            @Override
            public void processFinishedCommentList(ArrayList<Comment> comments) {

                commentsArrayList = comments;
                recipe.setComment(comments);

                commentRecyclerViewAdapter = new CommentRecyclerViewAdapter(recipe.getComment(), getContext(), user.getUid());
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                recyclerView.setHasFixedSize(true);
                recyclerView.setAdapter(commentRecyclerViewAdapter);

                ItemTouchHelper touchHelper = new ItemTouchHelper(simpleCallback);
                touchHelper.attachToRecyclerView(recyclerView);

                loadingDialog.dismissDialog();

            }
        }, recipe.getName(), language);


        /*----------- Event Listeners -----------*/
        sendComment.setOnClickListener(this);
        toolbar.setNavigationOnClickListener(v -> requireActivity().onBackPressed());

        return view;
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.send_comment_imageView) {
            saveComment(null);
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private void saveComment(final Comment comment) {

        mDatabase = FirebaseDatabase.getInstance().getReference();

        if (comment != null) {
            HashMap<String, Object> comments = new HashMap<>();
            HashMap<String, Object> commentMap = new HashMap<>();
            commentMap.put("userId", comment.getAuthor());
            commentMap.put("comment", comment.getComment());
            commentMap.put("date", comment.getDate());

            String id = comment.getAuthor() + "_" + comment.getDate();
            comments.put(id, commentMap);

            mDatabase.child("Recipes").child(language).child(recipe.getName()).child("comments").updateChildren(comments)
                    .addOnSuccessListener(aVoid -> {
                        Objects.requireNonNull(((RecipeActivity) getActivity()).bottomNavigationView.getBadge(R.id.commentsNavIcon)).setNumber(recipe.getComment().size() + 1);
                        recipe.getComment().add(comment);
                        commentRecyclerViewAdapter.notifyDataSetChanged();
                    });

            return;
        }

        if (!TextUtils.isEmpty(commentEditText.getText().toString())) {

            final String commentText = commentEditText.getText().toString();
            final String userId = user.getUid();
            final String commentDate = String.valueOf(new Timestamp(new Date()).getSeconds());
            commentEditText.setText("");

            HashMap<String, Object> comments = new HashMap<>();
            HashMap<String, Object> commentMap = new HashMap<>();
            commentMap.put("userId", userId);
            commentMap.put("comment", commentText);
            commentMap.put("date", commentDate);

            String id = userId + "_" + commentDate;
            comments.put(id, commentMap);

            mDatabase.child("Recipes").child(language).child(recipe.getName()).child("comments").updateChildren(comments)
                    .addOnSuccessListener(aVoid -> {
                        Objects.requireNonNull(((RecipeActivity) getActivity()).bottomNavigationView.getBadge(R.id.commentsNavIcon)).setNumber(recipe.getComment().size() + 1);
                        recipe.getComment().add(new Comment(userId, commentText, commentDate));
                        commentRecyclerViewAdapter.notifyDataSetChanged();
                    });
        }

    }
}
