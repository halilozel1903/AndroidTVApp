/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package com.example.androidtvapp;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v17.leanback.app.BrowseFragment;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.HeaderItem;
import android.support.v17.leanback.widget.ListRow;
import android.support.v17.leanback.widget.ListRowPresenter;

import android.support.v17.leanback.widget.OnItemViewClickedListener;
import android.support.v17.leanback.widget.OnItemViewSelectedListener;
import android.support.v17.leanback.widget.Presenter;

import android.support.v17.leanback.widget.Row;
import android.support.v17.leanback.widget.RowPresenter;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.TextView;





public class MainFragment extends BrowseFragment {


    private static final String TAG = MainFragment.class.getSimpleName();

    private ArrayObjectAdapter mRowsAdapter;

    private static final int GRID_ITEM_WIDTH = 300;
    private static final int GRID_ITEM_HEIGHT = 200;

    private static PicassoBackgroundManager picassoBackgroundManager = null;
    String description="";

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Log.i(TAG, "onActivityCreated");
        super.onActivityCreated(savedInstanceState);

        setupUIElements();

        loadRows();

        setupEventListeners();

        picassoBackgroundManager = new PicassoBackgroundManager(getActivity());
    }

    private void setupEventListeners() {
        setOnItemViewSelectedListener(new ItemViewSelectedListener());
        setOnItemViewClickedListener(new ItemViewClickedListener());
    }

    private final class ItemViewClickedListener implements OnItemViewClickedListener {
        @Override
        public void onItemClicked(Presenter.ViewHolder itemViewHolder, Object item,
                                  RowPresenter.ViewHolder rowViewHolder, Row row) {
            // each time the item is clicked, code inside here will be executed.
            if (item instanceof Movie) {
                Movie movie = (Movie) item;
                Log.d(TAG, "Item: " + item.toString());
                Intent intent = new Intent(getActivity(), DetailsActivity.class);
                intent.putExtra(DetailsActivity.MOVIE, movie);

                getActivity().startActivity(intent);
            } else if (item instanceof String){
                if (item == "ErrorFragment") {
                    Intent intent = new Intent(getActivity(), ErrorActivity.class);
                    startActivity(intent);
                }
            }
        }
    }

    private final class ItemViewSelectedListener implements OnItemViewSelectedListener {
        @Override
        public void onItemSelected(Presenter.ViewHolder itemViewHolder, Object item,
                                   RowPresenter.ViewHolder rowViewHolder, Row row) {
            // each time the item is selected, code inside here will be executed.
            if (item instanceof String) {                    // GridItemPresenter
                picassoBackgroundManager.updateBackgroundWithDelay("https://assets.gqindia.com/photos/5cdc0cd5540043c64b3c4049/16:9/w_1920,c_limit/netflix-shows-amazon-prime-series-best-shows-on-hotstar-premium-sonlyliv1.jpg");
            } else if (item instanceof Movie) {              // CardPresenter
                picassoBackgroundManager.updateBackgroundWithDelay(((Movie) item).getCardImageUrl());
            }
        }
    }

    private void setupUIElements() {
        // setBadgeDrawable(getActivity().getResources().getDrawable(R.drawable.videos_by_google_banner));
        setTitle("Hello Android TV!"); // Badge, when set, takes precedent
        // over title
        setHeadersState(HEADERS_ENABLED);
        setHeadersTransitionOnBackEnabled(true);

        // set fastLane (or headers) background color
        setBrandColor(getResources().getColor(R.color.fastlane_background));
        // set search icon color
        setSearchAffordanceColor(getResources().getColor(R.color.search_opaque));
    }

    private void loadRows() {
        mRowsAdapter = new ArrayObjectAdapter(new ListRowPresenter());

        /* GridItemPresenter */
        HeaderItem gridItemPresenterHeader = new HeaderItem(0, "Movies");

        GridItemPresenter mGridPresenter = new GridItemPresenter();
        ArrayObjectAdapter gridRowAdapter = new ArrayObjectAdapter(mGridPresenter);
        gridRowAdapter.add("ErrorFragment");
        gridRowAdapter.add("New Movies");
        gridRowAdapter.add("Old Movies");
        mRowsAdapter.add(new ListRow(gridItemPresenterHeader, gridRowAdapter));

        /* CardPresenter */
        HeaderItem cardPresenterHeader = new HeaderItem(1, "Series");
        CardPresenter cardPresenter = new CardPresenter();
        ArrayObjectAdapter cardRowAdapter = new ArrayObjectAdapter(cardPresenter);

        for(int i=0; i<10; i++) {
            Movie movie = new Movie();

            if(i==0) {
                movie.setCardImageUrl("https://wallpapercave.com/wp/wp2720749.jpg");
                movie.setTitle("Sherlock");
                movie.setStudio("BBC One");
                description = "In this updated take on Sir Arthur Conan Doyle's beloved mystery tales, the eccentric sleuth prowls the streets of modern London in search of clues.";

            }else if(i==1) {
                movie.setCardImageUrl("https://wallpapercave.com/wp/wp1839580.jpg");
                movie.setTitle("Stranger Things");
                movie.setStudio("Netflix");
                description = "This sci-fi anthology series explores a twisted, high-tech near-future where humanity's greatest innovations and darkest instincts collide.";

            }else if (i == 2) {
                movie.setCardImageUrl("https://wallpapercave.com/wp/wp4261117.jpg");
                movie.setTitle("Black Mirror");
                movie.setStudio("Netflix");
                description = "When a young boy vanishes, a small town uncovers a mystery involving secret experiments, terrifying supernatural forces and one strange little girl.";
            }else if (i==3){
                movie.setCardImageUrl("https://wallpapercave.com/wp/wp4056398.png");
                movie.setTitle("Dark");
                movie.setStudio("Netflix");
                description = "A missing child sets four families on a frantic hunt for answers as they unearth a mind-bending mystery that spans three generations.";
            }


             else {
                movie.setCardImageUrl("https://store-images.s-microsoft.com/image/apps.55787.9007199266246365.687a10a8-4c4a-4a47-8ec5-a95f70d8852d.12c2c5b7-0d97-4635-9b57-8a35da0f4c79?mode=scale&q=90&h=1080&w=1920");
                movie.setTitle("Saat Teknoloji");
                movie.setStudio("Saat Teknoloji");
                description = "New Series and Movies are coming soon";
            }


            movie.setDescription(description);
            cardRowAdapter.add(movie);
        }

        mRowsAdapter.add(new ListRow(cardPresenterHeader, cardRowAdapter));

        /* Set */
        setAdapter(mRowsAdapter);
    }


    private class GridItemPresenter extends Presenter {
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent) {
            TextView view = new TextView(parent.getContext());
            view.setLayoutParams(new ViewGroup.LayoutParams(GRID_ITEM_WIDTH, GRID_ITEM_HEIGHT));
            view.setFocusable(true);
            view.setFocusableInTouchMode(true);
            view.setBackgroundColor(getResources().getColor(R.color.default_background));
            view.setTextColor(Color.WHITE);
            view.setGravity(Gravity.CENTER);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, Object item) {
            ((TextView) viewHolder.view).setText((String) item);
        }

        @Override
        public void onUnbindViewHolder(ViewHolder viewHolder) {

        }
    }


}