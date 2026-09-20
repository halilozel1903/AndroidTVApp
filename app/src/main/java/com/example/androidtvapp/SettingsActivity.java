package com.example.androidtvapp;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        TvUi.applyActivityStartup(this);
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(android.R.id.content, new SettingsFragment());
            transaction.commit();
        }
    }
}
