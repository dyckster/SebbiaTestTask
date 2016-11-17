package com.example.dyckster.sebbiatesttask.ui.adapters;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.dyckster.sebbiatesttask.R;
import com.example.dyckster.sebbiatesttask.model.Category;
import com.example.dyckster.sebbiatesttask.ui.NewsListFragment;
import com.example.dyckster.sebbiatesttask.ui.OnFragmentChange;

import java.util.ArrayList;
import java.util.List;


public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.ViewHolder> {

    private List<Category> categories;
    private OnFragmentChange onFragmentChangeListener;


    public CategoriesAdapter(ArrayList<Category> categories, OnFragmentChange onFragmentChangeListener) {
        this.categories = categories;
        this.onFragmentChangeListener = onFragmentChangeListener;
    }

    @Override
    public CategoriesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(CategoriesAdapter.ViewHolder holder, int position) {
        final Category category = categories.get(position);
        holder.categoryTitle.setText(category.getName());
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onFragmentChangeListener.onFragmentChange(new NewsListFragment().newInstance(category.getId()));
            }
        });

    }


    @Override
    public int getItemCount() {
        return categories.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView categoryTitle;
        private CardView cardView;

        public ViewHolder(View itemView) {
            super(itemView);
            cardView = (CardView) itemView.findViewById(R.id.category_card);
            categoryTitle = (TextView) itemView.findViewById(R.id.category_title);
        }
    }
}
