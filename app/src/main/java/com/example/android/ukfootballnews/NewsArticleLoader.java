package com.example.android.ukfootballnews;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Custom AsyncTaskLoader that fetches news data in the background.
 */

public class NewsArticleLoader extends AsyncTaskLoader <List<NewsArticle>>{

    /** Tag for the log messages */
    private static final String LOG_TAG = NewsArticleLoader.class.getSimpleName();

    /** Query url */
    private String mUrl;

    /**
     * Constructs a new {@link NewsArticleLoader}.
     *
     * @param context of the activity
     * @param url to load data from
     */
    public NewsArticleLoader(Context context, String url) {
        super(context);
        mUrl = url;
        Log.i(LOG_TAG, "NewsArticleLoader constructor called: new Loader created.");
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
        Log.i(LOG_TAG, "onStartLoading() --> forceLoad()");
    }

    @Override
    public List<NewsArticle> loadInBackground() {
        Log.i(LOG_TAG, "loadInBackground(): loading data in background");

        // If the query url is empty, return an empty array list. Otherwise, fetch data
        // using this url
        if (TextUtils.isEmpty(mUrl)) {
            return new ArrayList<NewsArticle>();
        } else {
            return QueryUtils.fetchNewsData(mUrl);
        }
    }
}
