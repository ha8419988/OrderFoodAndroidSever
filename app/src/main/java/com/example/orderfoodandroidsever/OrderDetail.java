package com.example.orderfoodandroidsever;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.TextView;

import com.example.orderfoodandroidsever.Common.Common;
import com.example.orderfoodandroidsever.viewholder.OrderDetailAdapter;

public class OrderDetail extends AppCompatActivity {
    TextView order_id, order_phone, order_address, order_total;
    String order_id_value = "";
    RecyclerView list_order_detail;
    RecyclerView.LayoutManager layoutManager;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        order_id = (TextView) findViewById(R.id.order_id1);
        order_phone = (TextView) findViewById(R.id.order_phone1);
        order_address = (TextView) findViewById(R.id.order_address1);
        order_total = (TextView) findViewById(R.id.order_total);
        list_order_detail = findViewById(R.id.list_detail_order);
        list_order_detail.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        list_order_detail.setLayoutManager(layoutManager);
        if (getIntent() != null) {
            order_id_value = getIntent().getStringExtra("OrderId");
        }
        //set giá trị
        order_id.setText("Mã Đơn: "+order_id_value);
        order_phone.setText(Common.current_request.getPhone());
        order_total.setText(Common.current_request.getTotal());
        order_address.setText(Common.current_request.getAddress());
        OrderDetailAdapter adapter = new OrderDetailAdapter(Common.current_request.getFoods());
        adapter.notifyDataSetChanged();
        list_order_detail.setAdapter(adapter);
    }
}