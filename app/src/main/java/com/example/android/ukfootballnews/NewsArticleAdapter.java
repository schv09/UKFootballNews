package com.example.android.ukfootballnews;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Custom ArrayAdapter to provide NewsArticle list item views.
 */

public class NewsArticleAdapter extends ArrayAdapter<NewsArticle> {

    /**
     * Constructs a new {@link NewsArticleAdapter}.
     *
     * @param context of the app
     * @param articles is the list of articles, which is the data source of the adapter
     */
    public NewsArticleAdapter (Context context, List articles){
        super(context, 0, articles);
    }

    /**
     * Returns a list item view that displays information about the article at the given position
     * in the list of articles.
     */
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        // Check if there is an existing list item view (called convertView) that we can reuse,
        // otherwise, if convertView is null, then inflate a new list item layout.
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_item, parent, false);
        }

        NewsArticle currentArticle = getItem(position);

        // Find image view for thumbnail
        ImageView articleThumbnailImageView = (ImageView) listItemView.findViewById(R.id.article_image);

        // Get URL that leads to thumbnail
        String thumbnailUrl = currentArticle.getThumbnailUrl();

        // Load image from internet and set it into image view
        Picasso.with(getContext()).load(thumbnailUrl).into(articleThumbnailImageView);

        TextView titleTextView = (TextView) listItemView.findViewById(R.id.title_text_view);
        titleTextView.setText(currentArticle.getTitle());

        TextView sectionTextView = (TextView) listItemView.findViewById(R.id.section_text_view);
        sectionTextView.setText(NewsArticle.getSection());

        return listItemView;
    }
}
