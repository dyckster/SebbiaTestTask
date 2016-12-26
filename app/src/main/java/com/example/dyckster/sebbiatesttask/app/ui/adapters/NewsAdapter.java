//package com.example.dyckster.sebbiatesttask.app.ui.adapters;
//
//import android.support.v7.widget.CardView;
//import android.support.v7.widget.RecyclerView;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.TextView;
//
//import com.example.dyckster.sebbiatesttask.R;
//import com.example.dyckster.sebbiatesttask.app.model.news.News;
//import com.example.dyckster.sebbiatesttask.utils.TimeUtil;
//import com.example.dyckster.sebbiatesttask.app.ui.OnFragmentChange;
//
//import java.util.ArrayList;
//import java.util.List;
//
//
//public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.ViewHolder> {
//
//    private List<News> newsListItems;
//    private OnFragmentChange onFragmentChangeListener;
//
//    public NewsAdapter(ArrayList<News> newsListItems, OnFragmentChange onFragmentChangeListener) {
//        this.newsListItems = newsListItems;
//        this.onFragmentChangeListener = onFragmentChangeListener;
//    }
//
//    @Override
//    public NewsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_listed_news, parent, false);
//        return new ViewHolder(v);
//    }
//
//    @Override
//    public void onBindViewHolder(final NewsAdapter.ViewHolder holder, int position) {
//        final News newsListItem = newsListItems.get(position);
//        holder.newsTitle.setText(newsListItem.getTitle());
//        holder.newsDescription.setText(newsListItem.getShortDescription());
//        holder.dateTextView.setText(TimeUtil.getFormattedDate(newsListItem.getDate()));
//        holder.cardView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                onFragmentChangeListener.onFragmentChange(new NewsDetailsFragment().newInstance(newsListItem.getNewsId(), newsListItem.getTitle(), newsListItem.getShortDescription(), holder.dateTextView.getText().toString()));
//            }
//        });
//
//    }
//
//
//    @Override
//    public int getItemCount() {
//        return newsListItems.size();
//    }
//
//    class ViewHolder extends RecyclerView.ViewHolder {
//        private TextView newsTitle;
//        private TextView newsDescription;
//        private CardView cardView;
//        private TextView dateTextView;
//
//        public ViewHolder(View itemView) {
//            super(itemView);
//            dateTextView = (TextView) itemView.findViewById(R.id.text_listed_news_date);
//            cardView = (CardView) itemView.findViewById(R.id.listed_news_card);
//            newsTitle = (TextView) itemView.findViewById(R.id.title_listed_item);
//            newsDescription = (TextView) itemView.findViewById(R.id.description_listed_news);
//        }
//    }
//
//    public void addItems(List<News> items) {
//        newsListItems.addAll(items);
//        this.notifyDataSetChanged();
//    }
//}
