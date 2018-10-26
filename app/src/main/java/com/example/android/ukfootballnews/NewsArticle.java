package com.example.android.ukfootballnews;

/**
 * Class that represents a NewsArticle object.
 */

public class NewsArticle {
    /** Title of this article */
    private String mTitle;

    /** Name of section of this article (All articles will belong to the Football section
     * based on the selected query url).
     */
    private static final String section = "Section: Football" ;

    /** URL that leads to complete article */
    private String mInfoUrl;

    /** URL that leads to a thumbnail for this article */
    private String mThumbnailUrl;

    /**
     * Constructor for a new NewsArticle object
     * @param title Title of this article
     * @param url URL leading to complete article
     * @param smallThumbnailUrl URL leading to thumbnail
     */
    public NewsArticle(String title,
                       String url, String smallThumbnailUrl) {
        mTitle = title;
        mInfoUrl = url;
        mThumbnailUrl = smallThumbnailUrl;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getInfoUrl() {
        return mInfoUrl;
    }

    public String getThumbnailUrl() {
        return mThumbnailUrl;
    }

    public static String getSection() {
        return section;
    }
}
