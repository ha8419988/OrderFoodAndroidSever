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


public class MenuViewHolder extends RecyclerView.ViewHolder
        implements View.OnClickListener,
        View.OnCreateContextMenuListener {
    public TextView tvMenuName;
    public ImageView imgMenu;
    private ItemClickListener itemClickListener;

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public MenuViewHolder(@NonNull View itemView) {
        super(itemView);
        tvMenuName = (TextView) itemView.findViewById(R.id.menu_name);
        imgMenu = (ImageView) itemView.findViewById(R.id.menu_imgage);
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
