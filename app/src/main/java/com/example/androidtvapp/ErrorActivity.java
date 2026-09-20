package com.example.androidtvapp;

import android.app.Activity;
import android.os.Bundle;

public class ErrorActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        TvUi.applyActivityStartup(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        testError();
    }

    private void testError() {
        ErrorFragment mErrorFragment = new ErrorFragment();
        getFragmentManager().beginTransaction().add(R.id.main_browse_fragment, mErrorFragment).commit();
    }
}
