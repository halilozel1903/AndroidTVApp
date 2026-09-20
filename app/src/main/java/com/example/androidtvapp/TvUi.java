package com.example.androidtvapp;

import android.app.Activity;

import androidx.core.splashscreen.SplashScreen;
import androidx.core.view.WindowCompat;

/**
 * Small TV-friendly startup helpers (splash screen, window insets).
 */
public final class TvUi {

    private TvUi() {
    }

    public static void applyActivityStartup(Activity activity) {
        SplashScreen.installSplashScreen(activity);
        WindowCompat.setDecorFitsSystemWindows(activity.getWindow(), false);
    }
}
