package com.example.orderfoodandroidsever.viewholder;

import android.view.ContextMenu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.orderfoodandroidsever.Common.Common;
import com.example.orderfoodandroidsever.Interface.ItemClickListener;
import com.example.orderfoodandroidsever.R;

public class FoodViewHolder extends RecyclerView.ViewHolder implements
        View.OnClickListener,
        View.OnCreateContextMenuListener {
    public TextView food_name;
    public ImageView food_image, fav_image, share_image;
    private ItemClickListener itemClickListener;

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public FoodViewHolder(@NonNull View itemView) {
        super(itemView);
        food_name = (TextView) itemView.findViewById(R.id.food_name);
        food_image = (ImageView) itemView.findViewById(R.id.food_imgage);
        itemView.setOnCreateContextMenuListener(this);
        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        itemClickListener.onClick(view, getAdapterPosition(), false);
    }

    @Override
    public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
        contextMenu.setHeaderTitle("Chọn Hành Động");
        contextMenu.add(0, 0, getAdapterPosition(), Common.UPDATE);
        contextMenu.add(0, 0, getAdapterPosition(), Common.DELETE);

    }
}
