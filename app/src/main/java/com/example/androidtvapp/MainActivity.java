package com.example.androidtvapp;

import android.app.Activity;
import android.os.Bundle;

public class MainActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        TvUi.applyActivityStartup(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (AppPreferences.consumeCatalogRefreshPending(this)) {
            MainFragment fragment = (MainFragment) getFragmentManager()
                    .findFragmentById(R.id.main_browse_fragment);
            if (fragment != null) {
                fragment.refreshCatalogRows();
            }
        }
    }
}
