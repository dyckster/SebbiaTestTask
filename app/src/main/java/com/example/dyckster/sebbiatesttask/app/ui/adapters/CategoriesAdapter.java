package com.example.dyckster.sebbiatesttask.app.ui.adapters;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.dyckster.sebbiatesttask.R;
import com.example.dyckster.sebbiatesttask.app.model.news.Category;
import com.example.dyckster.sebbiatesttask.app.ui.MainActivity;
import com.example.dyckster.sebbiatesttask.app.ui.OnFragmentChange;

import java.util.ArrayList;
import java.util.List;


public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.ViewHolder> {

    private List<Category> categories;
    private OnFragmentChange onFragmentChangeListener;
    private long categoryId;

    public CategoriesAdapter(List<Category> categories, OnFragmentChange onFragmentChangeListener) {
        this.categories = categories;
        this.onFragmentChangeListener = onFragmentChangeListener;
    }

    public CategoriesAdapter(List<Category> categories) {
        this.categories = categories;
    }

    @Override
    public CategoriesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(CategoriesAdapter.ViewHolder holder, final int position) {
        final Category category = categories.get(position);
        categoryId = category.getCategoryId();
        holder.categoryTitle.setText(category.getName());
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.FragmentType fragmentType = MainActivity.FragmentType.NEWS_LIST;
                fragmentType.setId(categories.get(position).getCategoryId());
                // TODO: 27.12.16 check context instance
                ((MainActivity)view.getContext()).setCurrentScreen(fragmentType,true);
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

    public void swapData(List<Category> categories) {
        this.categories = categories;
        notifyDataSetChanged();
    }
}
