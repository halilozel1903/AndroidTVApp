package com.example.androidtvapp;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Loads browse and detail sample content from {@code assets/catalog.json}.
 * Poster URLs prefer bundled assets ({@code file:///android_asset/...}); when an asset is
 * missing, {@link #resolveImageUrl(JSONObject)} uses the documented {@code fallbackUrl}.
 */
public final class MovieCatalog {

    private static final String TAG = MovieCatalog.class.getSimpleName();
    private static final String CATALOG_ASSET = "catalog.json";

    private static MovieCatalog sInstance;

    private final JSONObject mRoot;
    private final List<Movie> mSeries;

    private MovieCatalog(JSONObject root, List<Movie> series) {
        mRoot = root;
        mSeries = series;
    }

    public static synchronized MovieCatalog getInstance(Context context) {
        if (sInstance == null) {
            sInstance = load(context.getApplicationContext());
        }
        return sInstance;
    }

    public List<Movie> getSeries() {
        return Collections.unmodifiableList(mSeries);
    }

    public Movie findByTitle(String title) {
        for (Movie movie : mSeries) {
            if (movie.getTitle().equals(title)) {
                return movie;
            }
        }
        return null;
    }

    public List<Movie> getRelatedSeries(Movie current, int maxItems) {
        List<Movie> related = new ArrayList<>();
        for (Movie movie : mSeries) {
            if (current != null && movie.getId() == current.getId()) {
                continue;
            }
            related.add(copyMovie(movie));
            if (related.size() >= maxItems) {
                break;
            }
        }
        return related;
    }

    public List<String> getSeasonLabels(Movie movie) {
        if (movie == null) {
            return Collections.emptyList();
        }
        try {
            JSONObject entry = findSeriesEntry(movie.getTitle());
            if (entry == null) {
                return Collections.emptyList();
            }
            JSONArray seasons = entry.optJSONArray("seasons");
            if (seasons == null || seasons.length() == 0) {
                return Collections.emptyList();
            }
            List<String> labels = new ArrayList<>(seasons.length());
            for (int i = 0; i < seasons.length(); i++) {
                labels.add(seasons.getString(i));
            }
            return labels;
        } catch (JSONException e) {
            Log.w(TAG, "Failed to read seasons for " + movie.getTitle(), e);
            return Collections.emptyList();
        }
    }

    public String getBrowseGridBackgroundUrl(Context context) {
        try {
            JSONObject background = mRoot.getJSONObject("browseGridBackground");
            return resolveImageUrl(context, background);
        } catch (JSONException e) {
            Log.w(TAG, "Missing browseGridBackground in catalog", e);
            return null;
        }
    }

    public static String resolveImageUrl(Context context, JSONObject imageSpec) {
        if (imageSpec == null) {
            return null;
        }
        String assetPath = imageSpec.optString("assetPath", null);
        if (assetPath != null && !assetPath.isEmpty() && assetExists(context.getAssets(), assetPath)) {
            return "file:///android_asset/" + assetPath;
        }
        return imageSpec.optString("fallbackUrl", null);
    }

    private static boolean assetExists(AssetManager assets, String path) {
        try (InputStream in = assets.open(path)) {
            return in != null;
        } catch (IOException e) {
            return false;
        }
    }

    private JSONObject findSeriesEntry(String title) throws JSONException {
        JSONArray series = mRoot.getJSONArray("series");
        for (int i = 0; i < series.length(); i++) {
            JSONObject entry = series.getJSONObject(i);
            if (title.equals(entry.getString("title"))) {
                return entry;
            }
        }
        return null;
    }

    private static MovieCatalog load(Context context) {
        try (InputStream in = context.getAssets().open(CATALOG_ASSET)) {
            byte[] buffer = new byte[in.available()];
            int read = in.read(buffer);
            String json = new String(buffer, 0, read, StandardCharsets.UTF_8);
            JSONObject root = new JSONObject(new JSONTokener(json));
            List<Movie> movies = parseSeries(context, root.getJSONArray("series"));
            return new MovieCatalog(root, movies);
        } catch (IOException | JSONException e) {
            Log.e(TAG, "Failed to load " + CATALOG_ASSET + ", using empty catalog", e);
            try {
                return new MovieCatalog(new JSONObject("{}"), Collections.emptyList());
            } catch (JSONException ignored) {
                return new MovieCatalog(new JSONObject(), Collections.emptyList());
            }
        }
    }

    private static List<Movie> parseSeries(Context context, JSONArray series) throws JSONException {
        List<Movie> movies = new ArrayList<>(series.length());
        for (int i = 0; i < series.length(); i++) {
            JSONObject entry = series.getJSONObject(i);
            Movie movie = new Movie();
            movie.setId(entry.getLong("id"));
            movie.setTitle(entry.getString("title"));
            movie.setStudio(entry.getString("studio"));
            movie.setDescription(entry.getString("description"));
            movie.setCardImageUrl(resolveImageUrl(context, entry.getJSONObject("poster")));
            movies.add(movie);
        }
        return movies;
    }

    private static Movie copyMovie(Movie source) {
        Movie copy = new Movie();
        copy.setId(source.getId());
        copy.setTitle(source.getTitle());
        copy.setStudio(source.getStudio());
        copy.setDescription(source.getDescription());
        copy.setCardImageUrl(source.getCardImageUrl());
        return copy;
    }
}
