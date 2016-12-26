package com.example.dyckster.sebbiatesttask.app.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.activeandroid.sebbia.Model;

/**
 * Created by Andrey Antonenko on 24.05.2016.
 */
public abstract class ListItemsViewHolder<E extends Model> extends RecyclerView.ViewHolder {
    public ListItemsViewHolder(View itemView) {
        super(itemView);
    }
    public abstract void bindData(E element);
}
