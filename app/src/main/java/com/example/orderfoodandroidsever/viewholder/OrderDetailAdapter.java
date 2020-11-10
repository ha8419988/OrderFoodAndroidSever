package com.example.orderfoodandroidsever.viewholder;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.orderfoodandroidsever.R;
import com.example.orderfoodandroidsever.model.Order;

import java.util.List;

class MyViewHolder extends RecyclerView.ViewHolder {
    public TextView name, quantity, price, discount;

    public MyViewHolder(@NonNull View itemView) {
        super(itemView);
        name = itemView.findViewById(R.id.product_name);
        quantity = itemView.findViewById(R.id.price);
        price = itemView.findViewById(R.id.quantity);
        discount = itemView.findViewById(R.id.discount);
    }
}

public class OrderDetailAdapter extends RecyclerView.Adapter<MyViewHolder> {
    List<Order> myOrders;

    public OrderDetailAdapter(List<Order> myOrders) {
        this.myOrders = myOrders;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.detail_don_dat_hang, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Order order = myOrders.get(position);

        holder.name.setText(String.format("Name : %s", order.getProductName()));
        holder.quantity.setText(String.format("Số Lượng : %s", order.getQuantity()));
        holder.price.setText(String.format("Giá : %s", order.getPrice()));
        holder.discount.setText(String.format("Giảm Giá: %s", order.getDiscount()));

    }

    @Override
    public int getItemCount() {
        return myOrders.size();
    }
}
