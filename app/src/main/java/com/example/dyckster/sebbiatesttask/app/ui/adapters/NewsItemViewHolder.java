package com.example.dyckster.sebbiatesttask.app.ui.adapters;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.dyckster.sebbiatesttask.R;
import com.example.dyckster.sebbiatesttask.app.model.news.CompactNews;
import com.example.dyckster.sebbiatesttask.app.ui.MainActivity;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by dombaev_yury on 26.12.16.
 */

public class NewsItemViewHolder extends ListItemsViewHolder<CompactNews> implements View.OnClickListener {

    @Bind(R.id.title_listed_item)
    TextView titleText;
    @Bind(R.id.listed_news_card)
    ViewGroup rootView;
    @Bind(R.id.description_listed_news)
    TextView shortDesc;
    @Bind(R.id.text_listed_news_date)
    TextView newsDate;

    private long newsId;

    public NewsItemViewHolder(View itemView) {
        super(itemView);
        itemView.setOnClickListener(this);
        ButterKnife.bind(this, itemView);
    }

    @Override
    public void bindData(CompactNews element) {
        newsId = element.getNewsId();
        titleText.setText(element.getTitle());
        shortDesc.setText(element.getShortDesc());
        newsDate.setText(element.getDate());
    }

    @Override
    public void onClick(View v) {
        MainActivity.FragmentType fragmentType = MainActivity.FragmentType.NEWS_DETAILS;
        fragmentType.setId(newsId);
        ((MainActivity)v.getContext()).setCurrentScreen(fragmentType,true);
    }
}
