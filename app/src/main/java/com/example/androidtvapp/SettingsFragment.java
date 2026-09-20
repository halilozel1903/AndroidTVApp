package com.example.androidtvapp;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.leanback.preference.LeanbackPreferenceFragmentCompat;
import androidx.preference.Preference;
import androidx.preference.PreferenceManager;

public class SettingsFragment extends LeanbackPreferenceFragmentCompat
        implements SharedPreferences.OnSharedPreferenceChangeListener {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        PreferenceManager.setDefaultValues(requireContext(), R.xml.preferences, false);
        setPreferencesFromResource(R.xml.preferences, rootKey);

        Preference versionPreference = findPreference("app_version");
        if (versionPreference != null) {
            versionPreference.setSummary(
                    getString(R.string.settings_version_summary, BuildConfig.VERSION_NAME));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        PreferenceManager.getDefaultSharedPreferences(requireContext())
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        PreferenceManager.getDefaultSharedPreferences(requireContext())
                .unregisterOnSharedPreferenceChangeListener(this);
        super.onPause();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (AppPreferences.KEY_NETWORK_POSTER_FALLBACK.equals(key)) {
            MovieCatalog.reload(requireContext());
            AppPreferences.markCatalogRefreshPending(requireContext());
        }
    }
}
