package com.example.recipeapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.recipeapp.data.RecipeBankFirebase;
import com.example.recipeapp.data.SharedPreferencesLanguage;
import com.example.recipeapp.helper.LocaleHelper;
import com.example.recipeapp.model.User;
import com.example.recipeapp.ui.LoadingDialog;
import com.example.recipeapp.util.LanguageUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class LogInActivity extends AppCompatActivity implements View.OnClickListener {

    /*----- XML Element Variables -----*/
    private TextView errorMessage;
    private TextInputEditText emailEditText;
    private TextInputEditText passwordEditText;
    private TextInputLayout emailLayout;
    private TextInputLayout passwordLayout;
    private TextView msgTextView;
    private ImageView languageImageView;
    private TextView registerTextView;
    private Button loginButton;
    private TextView forgotPasswordTextView;

    /*----- Variables -----*/
    private LoadingDialog loadingDialog;
    private User userToConnect;
    private String language;
    private SharedPreferencesLanguage sharedPreferencesLanguage;

    /*----- Database Variables -----*/
    private FirebaseAuth auth;
    private RecipeBankFirebase recipeBankFirebase;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        auth = null;
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        /*----- Get Selected Language -----*/
        SharedPreferences sharedPreferences = getSharedPreferences(LanguageUtils.LANGUAGE_ID, MODE_PRIVATE);
        sharedPreferencesLanguage = new SharedPreferencesLanguage(sharedPreferences);
        language = sharedPreferencesLanguage.getLanguage();

        /*----- Init Variables -----*/
        recipeBankFirebase = new RecipeBankFirebase();
        auth = FirebaseAuth.getInstance();
        loadingDialog = new LoadingDialog(this);

        /*----- Setting Up Language -----*/
        if (language.equalsIgnoreCase(LanguageUtils.ENGLISH)) {
            LocaleHelper.setLocale(this, "en");
        } else {
            LocaleHelper.setLocale(this, "el");
        }

        setContentView(R.layout.activity_login);

        /*---------- Hooks ----------*/
        registerTextView = findViewById(R.id.registerTextView);
        errorMessage = findViewById(R.id.errorMessageTextView);
        msgTextView = findViewById(R.id.msg_textView);
        loginButton = findViewById(R.id.loginButton);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        languageImageView = findViewById(R.id.language);
        forgotPasswordTextView = findViewById(R.id.forgot_password_textView);
        emailLayout = findViewById(R.id.emailLayout);
        passwordLayout = findViewById(R.id.passwordLayout);

        /*---------- Setting Up Language Image ----------*/
        if (language.equalsIgnoreCase(LanguageUtils.ENGLISH)) {
            languageImageView.setImageResource(R.drawable.united_kingdom);
        } else {
            languageImageView.setImageResource(R.drawable.greece);
        }

        /*---------- Event Listeners ----------*/
        registerTextView.setOnClickListener(this);
        loginButton.setOnClickListener(this);
        languageImageView.setOnClickListener(this);
        forgotPasswordTextView.setOnClickListener(this);

        userToConnect = new User();

    }

    @Override
    protected void onResume() {
        super.onResume();
        /*---------- Checking Internet Connection ----------*/
        if (isConnected(this)) {
            showCustomDialog();
        } else {/*---------- Checking If User Is Already Logged In ----------*/
            if (recipeBankFirebase.getCurrentUser() != null) {
                Intent homeIntent = new Intent(LogInActivity.this, HomeActivity.class);
                startActivity(homeIntent);
                finish();
            }
        }
    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.registerTextView) {
            Intent registerIntent = new Intent(LogInActivity.this, RegisterActivity.class);
            startActivity(registerIntent);
            finish();
        } else if (v.getId() == R.id.loginButton) {
            initUser();
            errorMessage.setVisibility(View.INVISIBLE);
            if (isEmailValid() && isPasswordValid()) {
                loadingDialog.startLoadingDialog();
                loginUser(userToConnect.getEmail(), userToConnect.getPassword());
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
        } else if (v.getId() == R.id.forgot_password_textView) {

            final EditText resetEmail = new EditText(v.getContext());
            final AlertDialog.Builder passwordResetDialog = new AlertDialog.Builder(v.getContext());
            passwordResetDialog.setTitle(R.string.reset_password_text);
            passwordResetDialog.setMessage(R.string.reset_password_message);
            passwordResetDialog.setView(resetEmail);
            passwordResetDialog.setCancelable(false);

            passwordResetDialog.setPositiveButton(R.string.send_text, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    String email = resetEmail.getText().toString();
                    if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                        Toast.makeText(LogInActivity.this, getResources().getString(R.string.enter_valid_email), Toast.LENGTH_LONG)
                                .show();
                    } else {
                        auth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(LogInActivity.this, R.string.email_succ_text, Toast.LENGTH_LONG)
                                            .show();
                                } else {
                                    Toast.makeText(LogInActivity.this, R.string.email_fail_text, Toast.LENGTH_LONG)
                                            .show();
                                }
                            }
                        });
                    }
                }
            }).setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });


            passwordResetDialog.create().show();

        }

    }//end of onClick

    public void updateTextStrings() {
        registerTextView.setText(R.string.string_register);
        errorMessage.setText(R.string.error_message_login);
        msgTextView.setText(R.string.new_to_appname);
        loginButton.setText(R.string.login_button_text);
        forgotPasswordTextView.setText(R.string.forgot_password_text);
        passwordLayout.setHint(getResources().getString(R.string.password));
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
        if (password.isEmpty()) {
            passwordLayout.setError(getResources().getString(R.string.enter_password));
            return false;
        } else {
            passwordLayout.setError(null);
            return true;
        }
    }

    /*---------- User tries to Log In ----------*/
    private void loginUser(String email, String password) {
        auth.signInWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                loadingDialog.dismissDialog();
                Intent mainScreenIntent = new Intent(LogInActivity.this, HomeActivity.class);
                startActivity(mainScreenIntent);
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                loadingDialog.dismissDialog();
                errorMessage.setVisibility(View.VISIBLE);
            }
        });

    }

    /*---------- Initialize User ----------*/
    public void initUser() {
        userToConnect.setEmail(Objects.requireNonNull(emailEditText.getText()).toString());
        userToConnect.setPassword(Objects.requireNonNull(passwordEditText.getText()).toString());
    }

    /*---------- Checking Internet Access ----------*/
    private boolean isConnected(LogInActivity logInActivity) {

        ConnectivityManager connectivityManager = (ConnectivityManager) logInActivity.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo wifiConn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            NetworkInfo mobileConn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            return (wifiConn == null || !wifiConn.isConnected()) && (mobileConn == null || !mobileConn.isConnected());
        }
        return true;

    }

    /*---------- Asking User To Connect To The Internet Dialog ----------*/
    private void showCustomDialog() {

        final AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogTheme);
        builder.setMessage("Δεν υπάρχει σύνδεση στο διαδίκτυο")
                .setCancelable(false)
                .setPositiveButton("Συνδεση", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                    }
                })
                .setNegativeButton("Εξοδος", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();

    }

}