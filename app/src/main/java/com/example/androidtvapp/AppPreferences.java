package com.example.androidtvapp;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

/**
 * User-tunable sample settings (Leanback preferences screen).
 */
public final class AppPreferences {

    public static final String KEY_NETWORK_POSTER_FALLBACK = "network_poster_fallback";
    public static final String KEY_BACKGROUND_UPDATE_DELAY_MS = "background_update_delay_ms";
    public static final String KEY_CATALOG_REFRESH_PENDING = "catalog_refresh_pending";

    private AppPreferences() {
    }

    public static SharedPreferences prefs(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
    }

    public static boolean isNetworkPosterFallbackEnabled(Context context) {
        return prefs(context).getBoolean(KEY_NETWORK_POSTER_FALLBACK, true);
    }

    public static int getBackgroundUpdateDelayMs(Context context) {
        String value = prefs(context).getString(KEY_BACKGROUND_UPDATE_DELAY_MS, "500");
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return 500;
        }
    }

    public static void markCatalogRefreshPending(Context context) {
        prefs(context).edit().putBoolean(KEY_CATALOG_REFRESH_PENDING, true).apply();
    }

    public static boolean consumeCatalogRefreshPending(Context context) {
        SharedPreferences prefs = prefs(context);
        if (!prefs.getBoolean(KEY_CATALOG_REFRESH_PENDING, false)) {
            return false;
        }
        prefs.edit().putBoolean(KEY_CATALOG_REFRESH_PENDING, false).apply();
        return true;
    }
}
