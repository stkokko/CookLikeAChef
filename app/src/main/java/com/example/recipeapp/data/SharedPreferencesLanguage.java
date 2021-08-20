package com.example.recipeapp.data;

import android.content.SharedPreferences;

import com.example.recipeapp.util.LanguageUtils;

public class SharedPreferencesLanguage {

    private String language;
    private final SharedPreferences sharedPreferences;

    public SharedPreferencesLanguage(SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
        language = LanguageUtils.ENGLISH;
    }

    public String getLanguage() {
        language = sharedPreferences.getString("language", LanguageUtils.ENGLISH);
        return language;
    }

    public void setLanguage(String language) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("language", language);
        editor.apply();
    }
}
