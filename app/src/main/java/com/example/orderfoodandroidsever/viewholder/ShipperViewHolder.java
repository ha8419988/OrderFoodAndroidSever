package com.example.orderfoodandroidsever.viewholder;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.orderfoodandroidsever.Interface.ItemClickListener;
import com.example.orderfoodandroidsever.R;

public class ShipperViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public TextView shipper_name, shipper_phone;
    public Button btn_Edit, btn_Remove;
    private ItemClickListener itemClickListener;

    public ShipperViewHolder(@NonNull View itemView) {
        super(itemView);
        shipper_name = itemView.findViewById(R.id.shipper_name_);
        shipper_phone = itemView.findViewById(R.id.shipper_phone);
        btn_Edit = itemView.findViewById(R.id.btn_edit_shipper);
        btn_Remove = itemView.findViewById(R.id.btn_delete_shipper);
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View v) {
        itemClickListener.onClick(v, getAdapterPosition(), false);
    }
}
