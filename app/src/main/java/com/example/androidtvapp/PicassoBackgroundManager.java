package com.example.androidtvapp;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import androidx.leanback.app.BackgroundManager;
import android.util.DisplayMetrics;
import android.util.Log;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Timer;
import java.util.TimerTask;

public class PicassoBackgroundManager {

    private static final String TAG = PicassoBackgroundManager.class.getSimpleName();

    private static Drawable mDefaultBackground;
    // Handler attached with main thread
    private final Handler mHandler = new Handler(Looper.getMainLooper());

    private final Activity mActivity;
    private final DisplayMetrics mMetrics;
    private URI mBackgroundURI;
    private final PicassoBackgroundManagerTarget mBackgroundTarget;

    // null when no UpdateBackgroundTask is running.
    Timer mBackgroundTimer;

    public PicassoBackgroundManager (Activity activity) {
        mActivity = activity;
        int DEFAULT_BACKGROUND_RES_ID = R.drawable.default_background;
        mDefaultBackground = activity.getDrawable(DEFAULT_BACKGROUND_RES_ID);
        BackgroundManager mBackgroundManager = BackgroundManager.getInstance(activity);
        mBackgroundManager.attach(activity.getWindow());
        mBackgroundTarget = new PicassoBackgroundManagerTarget(mBackgroundManager);
        mMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(mMetrics);
    }

    /**
     * if UpdateBackgroundTask is already running, cancel this task and start new task.
     */
    private void startBackgroundTimer() {
        if (mBackgroundTimer != null) {
            mBackgroundTimer.cancel();
        }
        mBackgroundTimer = new Timer();
        /* set delay time to reduce too much background image loading process */
        int BACKGROUND_UPDATE_DELAY = 500;
        mBackgroundTimer.schedule(new UpdateBackgroundTask(), BACKGROUND_UPDATE_DELAY);
    }


    private class UpdateBackgroundTask extends TimerTask {
        @Override
        public void run() {
            /* Here is TimerTask thread, not UI thread */
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    /* Here is main (UI) thread */
                    if (mBackgroundURI != null) {
                        updateBackground(mBackgroundURI);
                    }
                }
            });
        }
    }

    public void updateBackgroundWithDelay(String url) {
        try {
            URI uri = new URI(url);
            updateBackgroundWithDelay(uri);
        } catch (URISyntaxException e) {
            /* skip updating background */
            Log.e(TAG, e.toString());
        }
    }

    /**
     * updateBackground with delay
     * delay time is measured in other Timer task thread.
     * @param uri
     */
    public void updateBackgroundWithDelay(URI uri) {
        mBackgroundURI = uri;
        startBackgroundTimer();
    }

    private void updateBackground(URI uri) {
        try {
            Picasso.with(mActivity)
                    .load(uri.toString())
                    .resize(mMetrics.widthPixels, mMetrics.heightPixels)
                    .centerCrop()
                    .error(mDefaultBackground)
                    .into(mBackgroundTarget);
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
    }

    /**
     * Copied from AOSP sample code.
     * Inner class
     * Picasso target for updating default_background images
     */
    public static class PicassoBackgroundManagerTarget implements Target {
        BackgroundManager mBackgroundManager;

        public PicassoBackgroundManagerTarget(BackgroundManager backgroundManager) {
            this.mBackgroundManager = backgroundManager;
        }

        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom loadedFrom) {
            this.mBackgroundManager.setBitmap(bitmap);
        }

        @Override
        public void onBitmapFailed(Drawable drawable) {
            this.mBackgroundManager.setDrawable(drawable);
        }

        @Override
        public void onPrepareLoad(Drawable drawable) {
            // Do nothing, default_background manager has its own transitions
        }

        @Override
        public boolean equals(Object o) {
            if (this == o)
                return true;
            if (o == null || getClass() != o.getClass())
                return false;

            PicassoBackgroundManagerTarget that = (PicassoBackgroundManagerTarget) o;

            return mBackgroundManager.equals(that.mBackgroundManager);
        }

        @Override
        public int hashCode() {
            return mBackgroundManager.hashCode();
        }
    }
}
