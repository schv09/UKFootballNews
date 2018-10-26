package com.example.android.ukfootballnews;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class NewsActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<List<NewsArticle>> {

    public static final String LOG_TAG = NewsActivity.class.getName();

    /** Url for news data from The Guardian data set. This url will make a query for the keyword
     * "football", limited to the section "football", and present the data from newest to oldest.
     */
    private static final String THE_GUARDIAN_REQUEST_URL = "https://content.guardianapis.com/search?q=football&format=json&section=football&show-fields=thumbnail&order-by=newest&api-key=test";

    /**
     * Constant value for the news article loader ID.
     */
    private static final int ARTICLE_LOADER_ID = 1;

    /** Loading progress indicator */
    private ProgressBar mProgressIndicator;

    /** Adapter for the list of news articles */
    private NewsArticleAdapter mAdapter;

    /** TextView that is displayed when the list is empty */
    private TextView mEmptyTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.i(LOG_TAG, "Activity's onCreate(): Activity is being created");

        setContentView(R.layout.activity_news);

        // Find a reference to the {@link ListView} in the layout
        ListView articleListView = (ListView) findViewById(R.id.list);

        //Find and set empty view for the list view
        mEmptyTextView = (TextView) findViewById(R.id.empty_list_text_view);
        articleListView.setEmptyView(mEmptyTextView);

        // Create a new {@link ArrayAdapter} of earthquakes
        mAdapter = new NewsArticleAdapter(this, new ArrayList<NewsArticle>());

        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
        articleListView.setAdapter(mAdapter);

        // Set an item click listener on the ListView, which sends an intent to a web browser
        // to open a website with the complete news article.
        articleListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Find the current earthquake that was clicked on
                NewsArticle currentArticle = mAdapter.getItem(position);

                // Convert the String URL into a URI object (to pass into the Intent constructor)
                Uri articleUri = Uri.parse(currentArticle.getInfoUrl());

                // Create a new intent to view the article URI
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, articleUri);

                //Check if activity can be started, if so start it
                if (websiteIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(websiteIntent);
                }
            }
        });

        mProgressIndicator = (ProgressBar) findViewById(R.id.progress_indicator);

        // Get a reference to the ConnectivityManager to check state of network connectivity
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        // Get details on the currently active default data network
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        //Get connection status
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        //If there is a network connection, fetch the data
        if (isConnected) {

            // Get a proper loader manager and initialize the loader. Pass in the int ID constant defined above and pass in null for
            // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
            // because this activity implements the LoaderCallbacks interface).
            getLoaderManager().initLoader(ARTICLE_LOADER_ID, null, this);
            Log.i(LOG_TAG, "initLoader(): Loader 0 was initialized");
        } else {
            // Otherwise, display error
            // First, hide loading indicator so error message will be visible
            mProgressIndicator.setVisibility(View.GONE);

            // Update empty state with no connection error message
            mEmptyTextView.setText(R.string.no_internet);
        }
    }

    @Override
    public Loader<List<NewsArticle>> onCreateLoader(int id, Bundle args) {
        Log.i(LOG_TAG, "onCreateLoader(): New Loader object is about to be created.");

        // Create a new loader for the given URL
        return new NewsArticleLoader(this, THE_GUARDIAN_REQUEST_URL);
    }

    @Override
    public void onLoadFinished(Loader<List<NewsArticle>> loader, List<NewsArticle> articles) {
        // Hide loading indicator because the data has been loaded
        mProgressIndicator.setVisibility(View.GONE);

        // Clear the adapter of previous news data
        mAdapter.clear();

        // Check if there was an error response code. If so, change text on empty text view to show this
        // and exit early.
        if (QueryUtils.getHttpRequestResponseCode() != 200) {
            mEmptyTextView.setText(R.string.bad_response_code);
            Log.i(LOG_TAG, "Response code: " + QueryUtils.getHttpRequestResponseCode());
            Log.e(LOG_TAG, "onLoadFinished(): Bad response code, exiting early.");
            return;
        }

        // If there is a valid list of {@link NewsArticle}s, then add them to the adapter's
        // data set. This will trigger the ListView to update.
        if (articles != null && !articles.isEmpty()) {
            mAdapter.addAll(articles);
            Log.i(LOG_TAG, "onLoadFinished(): Data was received and assigned to Adapter.");
        } else {
            // Otherwise, change the text on the empty text view to "no news articles found".
            mEmptyTextView.setText(R.string.no_news_found);
            Log.i(LOG_TAG, "onLoadFinished(): No Data was received (Empty or null results).");
        }
    }

    @Override
    public void onLoaderReset(Loader<List<NewsArticle>> loader) {
        // Remove references to Loader data because it won't be available anymore.
        mAdapter.addAll(new ArrayList<NewsArticle>());

        Log.i(LOG_TAG, "onLoaderReset(): Activity is being popped from back stack. Data won't be available anymore. Removing references from Loader data.");
    }
}
