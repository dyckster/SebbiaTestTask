package com.example.dyckster.sebbiatesttask.model;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.dyckster.sebbiatesttask.R;
import com.example.dyckster.sebbiatesttask.tools.TimeUtil;
import com.example.dyckster.sebbiatesttask.ui.NewsDetailsFragment;
import com.example.dyckster.sebbiatesttask.ui.OnFragmentChange;

import java.util.ArrayList;
import java.util.List;


public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.ViewHolder> {

    private List<NewsListItem> newsListItems;
    private OnFragmentChange listener;

    public NewsAdapter(ArrayList<NewsListItem> newsListItems, OnFragmentChange listener) {
        this.newsListItems = newsListItems;
        this.listener = listener;
    }

    @Override
    public NewsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_listed_news, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final NewsAdapter.ViewHolder holder, int position) {
        final NewsListItem newsListItem = newsListItems.get(position);
        holder.newsTitle.setText(newsListItem.getTitle());
        holder.newsDescription.setText(newsListItem.getShortDescription());
        holder.dateTextView.setText(TimeUtil.getFormattedDate(newsListItem.getDate()));
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onFragmentChange(new NewsDetailsFragment().newInstance(newsListItem.getId(), newsListItem.getTitle(), newsListItem.getShortDescription(), holder.dateTextView.getText().toString()));
            }
        });

    }


    @Override
    public int getItemCount() {
        return newsListItems.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView newsTitle;
        private TextView newsDescription;
        private CardView cardView;
        private TextView dateTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            dateTextView = (TextView) itemView.findViewById(R.id.text_listed_news_date);
            cardView = (CardView) itemView.findViewById(R.id.listed_news_card);
            newsTitle = (TextView) itemView.findViewById(R.id.title_listed_item);
            newsDescription = (TextView) itemView.findViewById(R.id.description_listed_news);
        }
    }
}
