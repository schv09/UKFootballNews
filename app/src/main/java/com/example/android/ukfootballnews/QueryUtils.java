package com.example.android.ukfootballnews;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Helper methods related to requesting and receiving news data from The Guardian API.
 */
public final class QueryUtils {

    /**
     * Tag for the log messages
     */
    private static final String LOG_TAG = QueryUtils.class.getSimpleName();

    /** HTTP request response code for the current request. */
    private static int mResponseCode;

    /**
     * Create a private constructor because no one should ever create a {@link QueryUtils} object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name QueryUtils (and an object instance of QueryUtils is not needed).
     */
    private QueryUtils() {
    }

    /**
     * Query the USGS data set and return a list of {@link NewsArticle} objects.
     */
    public static List<NewsArticle> fetchNewsData(String url) {

        Log.i(LOG_TAG, "fetchNewsData(): starting to fetch data");

        // Create URL object
        URL queryURL = createURL(url);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponseString = "";

        try {
            jsonResponseString = makeHttpRequest(queryURL);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem closing input stream. ", e);
        }

        // Extract relevant fields from the JSON response and create a list of {@link NewsArticle}s
        List<NewsArticle> articles = extractArticles(jsonResponseString);

        // Return the list of {@link NewsArticle}s
        return articles;
    }

    /**
     * Return a list of {@link NewsArticle} objects that has been built up from
     * parsing a JSON response.
     */
    private static List<NewsArticle> extractArticles(String jsonResponseString) {

        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(jsonResponseString)) {
            return null;
        }

        // Create an empty ArrayList that we can start adding articles to
        List<NewsArticle> articles = new ArrayList<>();

        // Try to parse the JSON response string. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {

            // Create a JSONObject from the JSON response string
            JSONObject rootJsonObject = new JSONObject(jsonResponseString);

            // Extract JSONObject with the key "response".
            JSONObject responseJsonObject = rootJsonObject.getJSONObject("response");

            // Extract the JSONArray associated with the key called "results",
            // which represents a list of articles.
            JSONArray listOfArticles = responseJsonObject.getJSONArray("results");

            // For each article in the list of articles, create a {@link NewsArticle} object
            for (int i = 0; i < listOfArticles.length(); i++) {

                // Get a single article at position i within the list of articles
                JSONObject articleJsonObject = listOfArticles.getJSONObject(i);

                // Extract the value for the key called "webTitle"
                String webTitle = articleJsonObject.getString("webTitle");

                // Extract the value for the key called "webUrl"
                String webUrl = articleJsonObject.getString("webUrl");

                // Extract the JSONObject for the key called "fields"
                JSONObject otherFields = articleJsonObject.getJSONObject("fields");

                // Extract the value for the key called "thumbnail"
                String thumbnailUrl = otherFields.getString("thumbnail");

                // Create a new {@link NewsArticle} object with the webTitle, webUrl and
                // thumbnailUrl from the JSON response.
                NewsArticle article = new NewsArticle(webTitle, webUrl, thumbnailUrl);

                // Add the new {@link NewsArticle} to the list of articles.
                articles.add(article);
            }

        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e(LOG_TAG, "Problem parsing the news article JSON results", e);
        }
        // Return the list of articles
        return articles;
    }

    /**
     * This method creates  URL object from a given string
     *
     * @param url the string representation of a URL
     * @return URL object
     */
    private static URL createURL(String url) {

        URL queryURL = null;
        try {
            queryURL = new URL(url);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error creating URL. ", e);
        }
        return queryURL;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    private static String makeHttpRequest(URL url) throws IOException {

        String jsonResponseString = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponseString;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;

        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            int responseCode = urlConnection.getResponseCode();

            mResponseCode = responseCode;

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (responseCode == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponseString = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the news article JSON results. ", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                // Closing the input stream could throw an IOException, which is why
                // the makeHttpRequest(URL url) method signature specifies than an IOException
                // could be thrown.
                inputStream.close();
            }
        }
        return jsonResponseString;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();

        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            String line = bufferedReader.readLine();

            while (line != null) {
                output.append(line);
                line = bufferedReader.readLine();
            }
        }
        return output.toString();
    }

    /**
     * Returns the HTTP request response code for a this request.
     * @return the HTTP response code for this request
     */
    public static int getHttpRequestResponseCode() {
        return mResponseCode;
    }
}
