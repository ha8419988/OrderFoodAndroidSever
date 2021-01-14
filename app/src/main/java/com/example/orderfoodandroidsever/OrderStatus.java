package com.example.orderfoodandroidsever;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.orderfoodandroidsever.Common.Common;
import com.example.orderfoodandroidsever.Interface.ItemClickListener;
import com.example.orderfoodandroidsever.model.Category;
import com.example.orderfoodandroidsever.model.MyResponse;
import com.example.orderfoodandroidsever.model.Notification;
import com.example.orderfoodandroidsever.model.Request;
import com.example.orderfoodandroidsever.model.Sender;
import com.example.orderfoodandroidsever.model.Token;
import com.example.orderfoodandroidsever.remote.APIService;
import com.example.orderfoodandroidsever.viewholder.FoodViewHolder;
import com.example.orderfoodandroidsever.viewholder.OrderViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.jaredrummler.materialspinner.MaterialSpinner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderStatus extends AppCompatActivity {
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    FirebaseDatabase db;
    FirebaseRecyclerAdapter<Request, OrderViewHolder> adapter;
    DatabaseReference requests;
    MaterialSpinner spinner, shipper_Spiner;
    APIService mService;
    String phoneShip;
    String phoneOder;
    String timeStamp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_status);
        db = FirebaseDatabase.getInstance();
        requests = db.getReference("Requests");

        recyclerView = findViewById(R.id.listOrder);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        //service
        mService = Common.getFCMService();

        loadOrder();
    }

    private void loadOrder() {
        FirebaseRecyclerOptions<Request> showorder = new FirebaseRecyclerOptions.Builder<Request>()
                .setQuery(requests, Request.class)
                .build();
        adapter = new FirebaseRecyclerAdapter<Request, OrderViewHolder>(showorder) {
            @Override
            protected void onBindViewHolder(@NonNull OrderViewHolder viewHolder, final int position, @NonNull final Request model) {
                viewHolder.txtOrderId.setText("Mã Đơn:" + adapter.getRef(position).getKey());
                viewHolder.txtOrderStatus.setText(Common.convertCodeToStatus(model.getStatus()));
                viewHolder.txtOrderPhone.setText(model.getPhone());
                viewHolder.txtOrderAddress.setText(model.getAddress());
                timeStamp =  model.getTimeStamp();
                viewHolder.txtOrderDate.setText("Thời Gian Đặt Hàng: " + Common.getDate(Long.parseLong(adapter.getRef(position).getKey())));
                phoneOder = model.getPhone();

                //bắt sự kiện cho Button
                viewHolder.btn_edit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showUpdateDialog(adapter.getRef(position).getKey(), adapter.getItem(position));
                    }
                });
                viewHolder.btn_delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        deleteOrder(adapter.getRef(position).getKey());
                    }
                });

                viewHolder.btn_detail.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(OrderStatus.this, OrderDetail.class);
                        Common.current_request = model;
                        intent.putExtra("OrderId", adapter.getRef(position).getKey());
                        startActivity(intent);

                    }
                });

                viewHolder.btn_direction.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(OrderStatus.this, TrackingOrder.class);
                        Common.current_request = model;
                        Log.d("hoannnn", "onClick: " + phoneShip);
                        startActivity(intent);
                    }
                });
            }

            @NonNull
            @Override
            public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.order_layout, parent, false);
                return new OrderViewHolder(itemView);
            }
        };
        adapter.startListening();
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);
    }


    private void deleteOrder(String key) {
        requests.child(key).removeValue();
        adapter.notifyDataSetChanged();

    }


    private void showUpdateDialog(String key, final Request item) {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(OrderStatus.this);
        alertDialog.setTitle("Sửa đơn đặt");
        alertDialog.setMessage("Hãy chọn trạng thái");

        LayoutInflater inflater = this.getLayoutInflater();
        final View view = inflater.inflate(R.layout.order_update, null);

        spinner = (MaterialSpinner) view.findViewById(R.id.spinner_order);
        shipper_Spiner = (MaterialSpinner) view.findViewById(R.id.spinner_shipper);

        spinner.setItems("Đã Đặt Hàng", "Đang Giao", "Đang Chuyển Hàng");

        //load all shipper phone to spiner
        final List<String> shipperPhoneList = new ArrayList<>();
        FirebaseDatabase.getInstance().getReference("Shippers")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot shipperSnashot : dataSnapshot.getChildren())
                            shipperPhoneList.add(shipperSnashot.getKey());
                        shipper_Spiner.setItems(shipperPhoneList);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        alertDialog.setView(view);

        final String localKey = key;
        alertDialog.setPositiveButton("Đồng ý", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                item.setStatus(String.valueOf(spinner.getSelectedIndex()));

                if (item.getStatus().equals("2")) {
                    FirebaseDatabase.getInstance().getReference("OrdersNeedShip").
                            child(shipper_Spiner.getItems().get(shipper_Spiner.getSelectedIndex()).toString()).child(localKey)
                            .setValue(item);
                    adapter.notifyDataSetChanged();
                    requests.child(localKey).setValue(item);
                    sendOrderStatusToUser(localKey, item);
                    sendOrderStatusToShipper(shipper_Spiner.getItems().get(shipper_Spiner.getSelectedIndex()).toString(), item);
                    Log.d("hoannnnnn", "onClick: "+shipper_Spiner.getItems().get(shipper_Spiner.getSelectedIndex()).toString());

                    phoneShip = ""+shipper_Spiner.getItems().get(shipper_Spiner.getSelectedIndex()).toString();
                    DatabaseReference phoneShipRef = FirebaseDatabase.getInstance().getReference(phoneOder);
                    HashMap<String,String> hashMap = new HashMap<>();
                    hashMap.put("phoneShip", phoneShip);
                    phoneShipRef.child(timeStamp).setValue(hashMap);
                } else {
                    adapter.notifyDataSetChanged();
                    requests.child(localKey).setValue(item);
                    sendOrderStatusToUser(localKey, item);
                }
            }
        });
        alertDialog.setNegativeButton("Không", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        alertDialog.show();

    }

    private void sendOrderStatusToShipper(final String shipperPhone, Request item) {
        DatabaseReference tokens = db.getReference("Tokens");
        tokens.child(shipperPhone)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        Token token = dataSnapshot.getValue(Token.class);
                        Notification notification = new Notification("", "Có Đơn Hàng Mới Cần Ship #:" + shipperPhone);
                        Sender content = new Sender(token.getToken(), notification);
                        mService.sendNotification(content).enqueue(new Callback<MyResponse>() {
                            @Override
                            public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                if (response.code() == 200) {
                                    if (response.body().success == 1) {
                                        Toast.makeText(OrderStatus.this, "Đã Gửi Tới Shipper", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(OrderStatus.this, "Lỗi!!!",
                                                Toast.LENGTH_LONG).show();

                                    }
                                }
                            }

                            @Override
                            public void onFailure(Call<MyResponse> call, Throwable t) {
                                Log.e("ERROR", t.getMessage());
                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void sendOrderStatusToUser(final String key, final Request item) {
        DatabaseReference tokens = db.getReference("Tokens");
        tokens.orderByKey().equalTo(item.getPhone())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                            Token token = postSnapshot.getValue(Token.class);
                            Notification notification = new Notification("", "Đơn Hàng Của Bạn #:" + key + " Đã Được Cập Nhập");
                            Sender content = new Sender(token.getToken(), notification);
                            mService.sendNotification(content).enqueue(new Callback<MyResponse>() {
                                @Override
                                public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                    if (response.code() == 200) {
                                        if (response.body().success == 1) {
                                            Toast.makeText(OrderStatus.this, "Đơn Hàng Vừa Được Cập Nhập!", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(OrderStatus.this, "Đơn Hàng Vừa Được Cập Nhập Nhưng Lỗi Để Gửi Thông Báo",
                                                    Toast.LENGTH_LONG).show();

                                        }
                                    }
                                }

                                @Override
                                public void onFailure(Call<MyResponse> call, Throwable t) {
                                    Log.e("ERROR", t.getMessage());
                                }
                            });

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }
}
