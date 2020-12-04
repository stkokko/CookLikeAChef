package com.example.recipeapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.recipeapp.data.SharedPreferencesLanguage;
import com.example.recipeapp.helper.LocaleHelper;
import com.example.recipeapp.model.User;
import com.example.recipeapp.ui.LoadingDialog;
import com.example.recipeapp.util.LanguageUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText registerEmailEditText;
    private EditText passwordEditText;
    private EditText confirmEditText;
    private TextView errorMessageTextView;
    private ImageView languageImageView;
    private TextView loginTextView;
    private Button registerButton;
    private TextView msgTextView;

    private User userToRegister;
    private String language;
    private SharedPreferencesLanguage sharedPreferencesLanguage;
    private boolean notValidPassword;
    private boolean passwordsDiffer;

    private FirebaseAuth auth;
    private LoadingDialog loadingDialog;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        auth = null;
        sharedPreferencesLanguage = null;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences sharedPreferences = getSharedPreferences(LanguageUtils.LANGUAGE_ID, MODE_PRIVATE);
        sharedPreferencesLanguage = new SharedPreferencesLanguage(sharedPreferences);
        language = sharedPreferencesLanguage.getLanguage();

        //Setting Language
        if (language.equalsIgnoreCase(LanguageUtils.ENGLISH)) {
            LocaleHelper.setLocale(this, "en");
        } else {
            LocaleHelper.setLocale(this, "el");
        }

        setContentView(R.layout.activity_register);

        userToRegister = new User();
        auth = FirebaseAuth.getInstance();
        loadingDialog = new LoadingDialog(this);
        //emailExists = false;
        notValidPassword = false;
        passwordsDiffer = false;

        loginTextView = findViewById(R.id.loginTextView);
        registerEmailEditText = findViewById(R.id.registerEmailEditText);
        passwordEditText = findViewById(R.id.registerPasswordEditText);
        confirmEditText = findViewById(R.id.registerConfirmPasswordEditText);
        errorMessageTextView = findViewById(R.id.errorMessageTextView);
        languageImageView = findViewById(R.id.language);
        registerButton = findViewById(R.id.registerButton);
        msgTextView = findViewById(R.id.msg_textView);

        if (language.equalsIgnoreCase(LanguageUtils.ENGLISH)) {
            languageImageView.setImageResource(R.drawable.united_kingdom);
        } else {
            languageImageView.setImageResource(R.drawable.greece);
        }


        loginTextView.setOnClickListener(this);
        registerButton.setOnClickListener(this);
        languageImageView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.loginTextView) {
            Intent loginIntent = new Intent(RegisterActivity.this, LogInActivity.class);
            startActivity(loginIntent);
            finish();
        } else if (v.getId() == R.id.registerButton) {
            initUser();

            if (checkPassword()) {
                loadingDialog.startLoadingDialog();
                registerUser(userToRegister.getEmail(), userToRegister.getPassword());
            }

        } else if (v.getId() == R.id.language) {
            if (language.equalsIgnoreCase(LanguageUtils.ENGLISH)) {
                languageImageView.setImageResource(R.drawable.greece);
                language = LanguageUtils.GREEK;

                LocaleHelper.setLocale(this, "el");

            } else {
                languageImageView.setImageResource(R.drawable.united_kingdom);
                language = LanguageUtils.ENGLISH;

                LocaleHelper.setLocale(this, "en");

            }
            updateTextStrings();
            sharedPreferencesLanguage.setLanguage(language);
        }

    }//end of onClick

    public void updateTextStrings() {

        if (notValidPassword) errorMessageTextView.setText(R.string.error_message_password_length);
        else if (passwordsDiffer)
            errorMessageTextView.setText(R.string.error_message_password_confirm);
        else errorMessageTextView.setText(R.string.error_message_email_exists);

        loginTextView.setText(R.string.login_button_text);
        msgTextView.setText(R.string.register_textView);
        registerButton.setText(R.string.string_register);
        passwordEditText.setHint(R.string.password);
        confirmEditText.setHint(R.string.confirm_password);
    }

    private void registerUser(final String email, String password) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    loadingDialog.dismissDialog();
                    Intent mainScreenIntent = new Intent(RegisterActivity.this, HomeActivity.class);
                    startActivity(mainScreenIntent);
                    finish();
                } else {
                    errorMessageTextView.setText(R.string.error_message_email_exists);
                    //emailExists = true;
                    errorMessageTextView.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private boolean checkPassword() {

        String password = passwordEditText.getText().toString();
        if (password.length() < 6) {
            errorMessageTextView.setText(R.string.error_message_password_length);
            notValidPassword = true;
            errorMessageTextView.setVisibility(View.VISIBLE);
            return false;
        } else if (!password.equals(confirmEditText.getText().toString())) {
            errorMessageTextView.setText(R.string.error_message_password_confirm);
            passwordsDiffer = true;
            errorMessageTextView.setVisibility(View.VISIBLE);
            return false;
        }
        notValidPassword = false;
        passwordsDiffer = false;
        return true;
    }

    private void initUser() {
        userToRegister.setEmail(registerEmailEditText.getText().toString());
        userToRegister.setPassword(passwordEditText.getText().toString());
    }
}