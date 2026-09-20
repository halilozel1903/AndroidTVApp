package com.example.androidtvapp;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.leanback.app.DetailsFragment;
import androidx.leanback.widget.Action;
import androidx.leanback.widget.ArrayObjectAdapter;
import androidx.leanback.widget.ClassPresenterSelector;
import androidx.leanback.widget.DetailsOverviewRow;
import androidx.leanback.widget.FullWidthDetailsOverviewRowPresenter;
import androidx.leanback.widget.HeaderItem;
import androidx.leanback.widget.ListRow;
import androidx.leanback.widget.ListRowPresenter;
import androidx.leanback.widget.SparseArrayObjectAdapter;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class VideoDetailsFragment extends DetailsFragment {

    private static final String TAG = VideoDetailsFragment.class.getSimpleName();
    private static final int DETAIL_THUMB_WIDTH = 274;
    private static final int DETAIL_THUMB_HEIGHT = 274;
    private static final String MOVIE = "Movie";
    private static final int MAX_RELATED_ITEMS = 4;

    private final ExecutorService mBackgroundExecutor = Executors.newSingleThreadExecutor();
    private final Handler mMainHandler = new Handler(Looper.getMainLooper());

    private CustomFullWidthDetailsOverviewRowPresenter customFullWidthDetailsOverviewRowPresenter;

    private Movie mSelectedMovie;
    private MovieCatalog mMovieCatalog;
    private Future<?> mDetailsRowFuture;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        customFullWidthDetailsOverviewRowPresenter = new CustomFullWidthDetailsOverviewRowPresenter(new DetailsDescriptionPresenter());

        Activity activity = getActivity();
        PicassoBackgroundManager mPicassoBackgroundManager = new PicassoBackgroundManager(activity);
        mSelectedMovie = (Movie) activity.getIntent().getSerializableExtra(MOVIE);
        mMovieCatalog = MovieCatalog.getInstance(activity);

        startDetailsRowLoad();
        mPicassoBackgroundManager.updateBackgroundWithDelay(mSelectedMovie.getCardImageUrl());
    }

    @Override
    public void onStop() {
        cancelDetailsRowLoad();
        super.onStop();
    }

    private void cancelDetailsRowLoad() {
        if (mDetailsRowFuture != null) {
            mDetailsRowFuture.cancel(true);
            mDetailsRowFuture = null;
        }
    }

    private void startDetailsRowLoad() {
        cancelDetailsRowLoad();
        final Activity activity = getActivity();
        if (activity == null) {
            return;
        }
        final android.content.Context appContext = activity.getApplicationContext();
        mDetailsRowFuture = mBackgroundExecutor.submit(() -> {
            if (Thread.currentThread().isInterrupted()) {
                return;
            }
            Bitmap poster = null;
            try {
                poster = Picasso.get()
                        .load(mSelectedMovie.getCardImageUrl())
                        .resize(Utils.convertDpToPixel(appContext, DETAIL_THUMB_WIDTH),
                                Utils.convertDpToPixel(appContext, DETAIL_THUMB_HEIGHT))
                        .centerCrop()
                        .get();
            } catch (IOException e) {
                Log.w(TAG, e.toString());
            }
            final Bitmap loadedPoster = poster;
            mMainHandler.post(() -> {
                Activity host = getActivity();
                if (host == null || host.isFinishing()) {
                    return;
                }
                DetailsOverviewRow row = new DetailsOverviewRow(mSelectedMovie);
                if (loadedPoster != null) {
                    row.setImageBitmap(host, loadedPoster);
                }
                bindRows(row);
            });
        });
    }

    private void bindRows(DetailsOverviewRow row) {
        SparseArrayObjectAdapter sparseArrayObjectAdapter = new SparseArrayObjectAdapter();
        List<String> seasons = mMovieCatalog.getSeasonLabels(mSelectedMovie);
        for (int i = 0; i < seasons.size(); i++) {
            int actionId = i + 1;
            sparseArrayObjectAdapter.set(actionId, new Action(actionId, mSelectedMovie.getTitle(), seasons.get(i)));
        }
        row.setActionsAdapter(sparseArrayObjectAdapter);

        ArrayObjectAdapter listRowAdapter = new ArrayObjectAdapter(new CardPresenter());
        for (Movie movie : mMovieCatalog.getRelatedSeries(mSelectedMovie, MAX_RELATED_ITEMS)) {
            listRowAdapter.add(movie);
        }
        HeaderItem headerItem = new HeaderItem(0, "Related Videos");

        ClassPresenterSelector classPresenterSelector = new ClassPresenterSelector();
        customFullWidthDetailsOverviewRowPresenter.setInitialState(FullWidthDetailsOverviewRowPresenter.STATE_SMALL);

        classPresenterSelector.addClassPresenter(DetailsOverviewRow.class, customFullWidthDetailsOverviewRowPresenter);
        classPresenterSelector.addClassPresenter(ListRow.class, new ListRowPresenter());

        ArrayObjectAdapter adapter = new ArrayObjectAdapter(classPresenterSelector);
        adapter.add(row);
        adapter.add(new ListRow(headerItem, listRowAdapter));
        setAdapter(adapter);
    }

    @Override
    public void onDestroy() {
        cancelDetailsRowLoad();
        mBackgroundExecutor.shutdownNow();
        super.onDestroy();
    }
}
