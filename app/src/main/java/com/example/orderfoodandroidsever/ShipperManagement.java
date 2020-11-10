package com.example.orderfoodandroidsever;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.orderfoodandroidsever.Common.Common;
import com.example.orderfoodandroidsever.model.Shipper;
import com.example.orderfoodandroidsever.viewholder.ShipperViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.HashMap;
import java.util.Map;

public class ShipperManagement extends AppCompatActivity {
    FloatingActionButton fab_add;
    FirebaseDatabase database;
    DatabaseReference shippers;

    public RecyclerView recyclerView;
    public RecyclerView.LayoutManager layoutManager;
    FirebaseRecyclerAdapter<Shipper, ShipperViewHolder> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shipper_management);
        fab_add = findViewById(R.id.fab_add);
        fab_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateShipper();
            }

        });

        recyclerView = (RecyclerView) findViewById(R.id.recycler_shipper);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        //Firebase
        database = FirebaseDatabase.getInstance();
        shippers = database.getReference("Shippers");
        //load all shipper
        loadAllShipper();
    }

    private void loadAllShipper() {
        FirebaseRecyclerOptions<Shipper> allShipper = new FirebaseRecyclerOptions.Builder<Shipper>()
                .setQuery(shippers, Shipper.class)
                .build();
        adapter = new FirebaseRecyclerAdapter<Shipper, ShipperViewHolder>(allShipper) {
            @Override
            protected void onBindViewHolder(@NonNull ShipperViewHolder shipperViewHolder, final int i, @NonNull final Shipper shipper) {
                shipperViewHolder.shipper_phone.setText(shipper.getPhone());
                shipperViewHolder.shipper_name.setText(shipper.getName());
                shipperViewHolder.btn_Edit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        EditShipperDialog(adapter.getRef(i).getKey(), shipper);
                    }
                });
                shipperViewHolder.btn_Remove.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DeleteShipper(adapter.getRef(i).getKey());
                    }
                });
            }

            @NonNull
            @Override
            public ShipperViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.shipper_layout, parent, false);
                return new ShipperViewHolder(itemView);

            }

        };
        adapter.startListening();
        recyclerView.setAdapter(adapter);
    }

    private void DeleteShipper(String key) {
        shippers.child(key)
                .removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(ShipperManagement.this, "Xóa Thành Công", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ShipperManagement.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        adapter.notifyDataSetChanged();
    }

    private void EditShipperDialog(String key, Shipper model) {
        AlertDialog.Builder cr_shipper_dialog = new AlertDialog.Builder(ShipperManagement.this);
        cr_shipper_dialog.setTitle("Cập Nhập Tài Khoản Shipper");

        LayoutInflater inflater = this.getLayoutInflater();
        View view = inflater.inflate(R.layout.create_shipper, null);
        final MaterialEditText edtName = (MaterialEditText) view.findViewById(R.id.edt_name_Shipper);
        final MaterialEditText edtPhone = (MaterialEditText) view.findViewById(R.id.edt_phone_shipper);
        final MaterialEditText edtPassword = (MaterialEditText) view.findViewById(R.id.edt_pass_shipper);

        //set data
        edtName.setText(model.getName());
        edtPhone.setText(model.getPhone());
        edtPassword.setText(model.getPassword());

        cr_shipper_dialog.setView(view);

        cr_shipper_dialog.setPositiveButton("Cập Nhập", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

                Map<String, Object> update = new HashMap<>();
                update.put("name", edtName.getText().toString());
                update.put("phone", edtPhone.getText().toString());
                update.put("password", edtPassword.getText().toString());

                shippers.child(edtPhone.getText().toString())
                        .updateChildren(update)
                         .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(ShipperManagement.this, "Cập Nhập Thành Công", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ShipperManagement.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
        cr_shipper_dialog.setNegativeButton("Không", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        cr_shipper_dialog.show();
    }

    private void CreateShipper() {
        AlertDialog.Builder cr_shipper_dialog = new AlertDialog.Builder(ShipperManagement.this);
        cr_shipper_dialog.setTitle("Tạo Tài Khoản Shipper");
        LayoutInflater inflater = this.getLayoutInflater();
        View view = inflater.inflate(R.layout.create_shipper, null);
        final MaterialEditText edtName = (MaterialEditText) view.findViewById(R.id.edt_name_Shipper);
        final MaterialEditText edtPhone = (MaterialEditText) view.findViewById(R.id.edt_phone_shipper);
        final MaterialEditText edtPassword = (MaterialEditText) view.findViewById(R.id.edt_pass_shipper);
        cr_shipper_dialog.setView(view);
        cr_shipper_dialog.setIcon(R.drawable.ic_baseline_local_shipping_24);
        cr_shipper_dialog.setPositiveButton("Tạo", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

                Shipper shipper = new Shipper();
                shipper.setName(edtName.getText().toString());
                shipper.setPhone(edtPhone.getText().toString());
                shipper.setPassword(edtPassword.getText().toString());


                shippers.child(edtPhone.getText().toString())
                        .setValue(shipper)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(ShipperManagement.this, "Tạo Tài Khoản Thành Công", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ShipperManagement.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
        cr_shipper_dialog.setNegativeButton("Không", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        cr_shipper_dialog.show();
    }

}