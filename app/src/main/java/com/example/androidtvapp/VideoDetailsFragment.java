package com.example.androidtvapp;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;

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

import android.util.Log;

import com.squareup.picasso.Picasso;

import java.io.IOException;

public class VideoDetailsFragment extends DetailsFragment {

    private static final String TAG = VideoDetailsFragment.class.getSimpleName();
    private static final int DETAIL_THUMB_WIDTH = 274;
    private static final int DETAIL_THUMB_HEIGHT = 274;
    private static final String MOVIE = "Movie";

    private CustomFullWidthDetailsOverviewRowPresenter customFullWidthDetailsOverviewRowPresenter;

    private Movie mSelectedMovie;
    private DetailsRowBuilderTask mDetailsRowBuilderTask;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        customFullWidthDetailsOverviewRowPresenter = new CustomFullWidthDetailsOverviewRowPresenter(new DetailsDescriptionPresenter());

        PicassoBackgroundManager mPicassoBackgroundManager = new PicassoBackgroundManager(getActivity());
        mSelectedMovie = (Movie) getActivity().getIntent().getSerializableExtra(MOVIE);

        mDetailsRowBuilderTask = (DetailsRowBuilderTask) new DetailsRowBuilderTask().execute(mSelectedMovie);
        mPicassoBackgroundManager.updateBackgroundWithDelay(mSelectedMovie.getCardImageUrl());
    }

    @Override
    public void onStop() {
        mDetailsRowBuilderTask.cancel(true);
        super.onStop();
    }

    @SuppressLint("StaticFieldLeak")
    private class DetailsRowBuilderTask extends AsyncTask<Movie, Integer, DetailsOverviewRow> {
        @Override
        protected DetailsOverviewRow doInBackground(Movie... params) {
            DetailsOverviewRow row = new DetailsOverviewRow(mSelectedMovie);
            try {
                Bitmap poster = Picasso.get()
                        .load(mSelectedMovie.getCardImageUrl())
                        .resize(Utils.convertDpToPixel(getActivity().getApplicationContext(), DETAIL_THUMB_WIDTH),
                                Utils.convertDpToPixel(getActivity().getApplicationContext(), DETAIL_THUMB_HEIGHT))
                        .centerCrop()
                        .get();
                row.setImageBitmap(getActivity(), poster);
            } catch (IOException e) {
                Log.w(TAG, e.toString());
            }
            return row;
        }

        @Override
        protected void onPostExecute(DetailsOverviewRow row) {
            /* 1st row: DetailsOverviewRow */
            SparseArrayObjectAdapter sparseArrayObjectAdapter = new SparseArrayObjectAdapter();
            for (int i = 0; i < 10; i++) {

                if (mSelectedMovie.getTitle().equals("Sherlock")) {
                    if (i == 1) {
                        sparseArrayObjectAdapter.set(1, new Action(1, "Sherlock", "Season 1"));
                    } else if (i == 2) {
                        sparseArrayObjectAdapter.set(2, new Action(2, "Sherlock", "Season 2"));
                    } else if (i == 3) {
                        sparseArrayObjectAdapter.set(3, new Action(3, "Sherlock", "Season 3"));
                    } else if (i == 4) {
                        sparseArrayObjectAdapter.set(4, new Action(4, "Sherlock", "Season 4"));
                    }
                }
                if (mSelectedMovie.getTitle().equals("Stranger Things")) {
                    if (i == 1) {
                        sparseArrayObjectAdapter.set(1, new Action(1, "Stranger Things", "Season 1"));
                    } else if (i == 2) {
                        sparseArrayObjectAdapter.set(2, new Action(2, "Stranger Things", "Season 2"));
                    } else if (i == 3) {
                        sparseArrayObjectAdapter.set(3, new Action(3, "Stranger Things", "Season 3"));
                    }
                }
            }
            row.setActionsAdapter(sparseArrayObjectAdapter);

            /* 2nd row: ListRow */
            ArrayObjectAdapter listRowAdapter = new ArrayObjectAdapter(new CardPresenter());
            for (int i = 0; i < 10; i++) {
                Movie movie = new Movie();
                if (i == 0) {
                    movie.setCardImageUrl("https://pbs.twimg.com/media/C2Dv8DcVEAAC5B_.jpg");
                    movie.setTitle("Sherlock");
                    movie.setStudio("BBC One");
                } else if (i == 1) {
                    movie.setCardImageUrl("https://frpnet.net/wp-content/uploads/2022/05/kapak.jpg");
                    movie.setTitle("Stranger Things");
                    movie.setStudio("Netflix");
                } else if (i == 2) {
                    movie.setCardImageUrl("https://occ-0-784-1380.1.nflxso.net/dnm/api/v6/E8vDc_W8CLv7-yMQu8KMEC7Rrr8/AAAABQkofIypds_rD7yInykgr059MHPbl0gpMiFZ4mgL5Vuu2JRxvs-7fzdvN97DD0Ir2126xeFWMucX9IZJVmH33Hv1WfUJ4GB86SVY.jpg");
                    movie.setTitle("Black Mirror");
                    movie.setStudio("Netflix");
                } else if (i == 3) {
                    movie.setCardImageUrl("https://m.media-amazon.com/images/M/MV5BMjMzNjUxMDk0MF5BMl5BanBnXkFtZTgwMjY1OTgyNDM@._V1_.jpg");
                    movie.setTitle("Dark");
                    movie.setStudio("Netflix");
                }
                listRowAdapter.add(movie);
            }
            HeaderItem headerItem = new HeaderItem(0, "Related Videos");

            ClassPresenterSelector classPresenterSelector = new ClassPresenterSelector();
            customFullWidthDetailsOverviewRowPresenter.setInitialState(FullWidthDetailsOverviewRowPresenter.STATE_SMALL);

            classPresenterSelector.addClassPresenter(DetailsOverviewRow.class, customFullWidthDetailsOverviewRowPresenter);
            classPresenterSelector.addClassPresenter(ListRow.class, new ListRowPresenter());

            ArrayObjectAdapter adapter = new ArrayObjectAdapter(classPresenterSelector);
            /* 1st row */
            adapter.add(row);
            /* 2nd row */
            adapter.add(new ListRow(headerItem, listRowAdapter));
            /* 3rd row */
            //adapter.add(new ListRow(headerItem, listRowAdapter));
            setAdapter(adapter);
        }
    }
}
