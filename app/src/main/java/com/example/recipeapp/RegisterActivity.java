package com.example.recipeapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.recipeapp.data.SharedPreferencesLanguage;
import com.example.recipeapp.helper.LocaleHelper;
import com.example.recipeapp.model.User;
import com.example.recipeapp.ui.LoadingDialog;
import com.example.recipeapp.util.LanguageUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener, TextView.OnEditorActionListener {

    /*------------- XML Element Variables -------------*/
    private TextInputEditText emailEditText;
    private TextInputEditText passwordEditText;
    private TextInputEditText confirmPasswordEditText;
    private TextInputLayout emailLayout;
    private TextInputLayout passwordLayout;
    private TextInputLayout confirmPasswordLayout;
    private TextView errorMessageTextView;
    private ImageView languageImageView;
    private TextView loginTextView;
    private Button registerButton;
    private TextView msgTextView;

    /*------------- Variables -------------*/
    private User userToRegister;
    private LoadingDialog loadingDialog;
    private String language;
    private SharedPreferencesLanguage sharedPreferencesLanguage;
    private boolean notValidPassword;
    private boolean passwordsDiffer;

    /*------------- Database Variables -------------*/
    private FirebaseAuth auth;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        auth = null;
        sharedPreferencesLanguage = null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*------------- Get Selected Language -------------*/
        SharedPreferences sharedPreferences = getSharedPreferences(LanguageUtils.LANGUAGE_ID, MODE_PRIVATE);
        sharedPreferencesLanguage = new SharedPreferencesLanguage(sharedPreferences);
        language = sharedPreferencesLanguage.getLanguage();

        /*------------- Setting Up Language -------------*/
        if (language.equalsIgnoreCase(LanguageUtils.ENGLISH)) {
            LocaleHelper.setLocale(this, "en");
        } else {
            LocaleHelper.setLocale(this, "el");
        }

        setContentView(R.layout.activity_register);

        /*------------- Init Variables -------------*/
        userToRegister = new User();
        auth = FirebaseAuth.getInstance();
        loadingDialog = new LoadingDialog(this);
        notValidPassword = false;
        passwordsDiffer = false;

        /*------------- Hooks -------------*/
        loginTextView = findViewById(R.id.loginTextView);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText);
        errorMessageTextView = findViewById(R.id.errorMessageTextView);
        languageImageView = findViewById(R.id.language);
        registerButton = findViewById(R.id.registerButton);
        msgTextView = findViewById(R.id.msg_textView);
        emailLayout = findViewById(R.id.emailLayout);
        passwordLayout = findViewById(R.id.passwordLayout);
        confirmPasswordLayout = findViewById(R.id.confirmPasswordLayout);

        /*------------- Setting Up Language Image -------------*/
        if (language.equalsIgnoreCase(LanguageUtils.ENGLISH)) {
            languageImageView.setImageResource(R.drawable.united_kingdom);
        } else {
            languageImageView.setImageResource(R.drawable.greece);
        }

        /*------------- Event Listeners -------------*/
        loginTextView.setOnClickListener(this);
        registerButton.setOnClickListener(this);
        languageImageView.setOnClickListener(this);
        emailEditText.setOnEditorActionListener(this);
        passwordEditText.setOnEditorActionListener(this);
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        //IME_ACTION: actionNext onClick
        if (actionId == EditorInfo.IME_ACTION_NEXT && v.getId() == R.id.emailEditText) {
            passwordEditText.requestFocus();
        } else {
            confirmPasswordEditText.requestFocus();
        }
        return true;
    }


    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.loginTextView) {
            Intent loginIntent = new Intent(RegisterActivity.this, LogInActivity.class);
            startActivity(loginIntent);
            finish();
        } else if (v.getId() == R.id.registerButton) {
            initUser();
            if (isEmailValid() && isPasswordValid()) {
                loadingDialog.startLoadingDialog();
                registerUser(userToRegister.getEmail(), userToRegister.getPassword());
                loadingDialog.dismissDialog();
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
        passwordLayout.setHint(getResources().getString(R.string.password));
        confirmPasswordLayout.setHint(getResources().getString(R.string.confirm_password));
    }

    /*---------- Checking if the email is valid ----------*/
    private boolean isEmailValid() {
        String email = Objects.requireNonNull(emailEditText.getText()).toString();
        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailLayout.setError(getResources().getString(R.string.enter_valid_email));
            return false;
        } else {
            emailLayout.setError(null);
            return true;
        }
    }

    /*---------- Checking if password is valid ----------*/
    private boolean isPasswordValid() {
        String password = Objects.requireNonNull(passwordEditText.getText()).toString();
        String confirmPassword = Objects.requireNonNull(confirmPasswordEditText.getText()).toString();
        boolean validLength = true;
        boolean validEquality = true;

        if (password.length() < 6) {
            passwordLayout.setError(getResources().getString(R.string.error_message_password_length));
            validLength = false;
        } else {
            passwordLayout.setError(null);
        }

        if (!password.equals(confirmPassword)) {
            confirmPasswordLayout.setError(getResources().getString(R.string.error_message_password_confirm));
            validEquality = false;
        } else {
            confirmPasswordLayout.setError(null);
        }

        return (validLength && validEquality);
    }

    /*---------- User tries to register ----------*/
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
                    errorMessageTextView.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    /*---------- User tries to Log In ----------*/
    private void initUser() {
        userToRegister.setEmail(Objects.requireNonNull(emailEditText.getText()).toString());
        userToRegister.setPassword(Objects.requireNonNull(passwordEditText.getText()).toString());
    }
}