package com.example.orderfoodandroidsever.viewholder;

import android.view.ContextMenu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.example.orderfoodandroidsever.R;

public class OrderViewHolder extends RecyclerView.ViewHolder {
    public TextView txtOrderId, txtOrderStatus, txtOrderPhone, txtOrderAddress, txtOrderDate;
    public Button btn_edit, btn_delete, btn_detail, btn_direction;


    public OrderViewHolder(@NonNull View itemView) {
        super(itemView);
        txtOrderId = (TextView) itemView.findViewById(R.id.order_id);
        txtOrderStatus = (TextView) itemView.findViewById(R.id.order_status);
        txtOrderPhone = (TextView) itemView.findViewById(R.id.order_phone);
        txtOrderAddress = (TextView) itemView.findViewById(R.id.order_address);
        txtOrderDate = (TextView) itemView.findViewById(R.id.order_date);
        btn_edit = itemView.findViewById(R.id.btn_edit);
        btn_delete = itemView.findViewById(R.id.btn_delete);
        btn_detail = itemView.findViewById(R.id.btn_detail);
        btn_direction = itemView.findViewById(R.id.btn_direction);

    }


}
