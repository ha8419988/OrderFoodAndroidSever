package com.example.orderfoodandroidsever;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.orderfoodandroidsever.Common.Common;
import com.example.orderfoodandroidsever.Interface.ItemClickListener;
import com.example.orderfoodandroidsever.model.Category;
import com.example.orderfoodandroidsever.model.Food;
import com.example.orderfoodandroidsever.viewholder.FoodViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.google.firebase.database.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;

import java.util.UUID;

public class FoodList extends AppCompatActivity {
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    RelativeLayout rootLayout;
    FirebaseDatabase database;
    DatabaseReference foodlist;
    String categoryId = "";

    FirebaseRecyclerAdapter<Food, FoodViewHolder> adapter;
    FloatingActionButton fab;
    MaterialEditText edt_name_newfood, edt_price, edt_des, edt_discount;
    Button btn_select1, btn_upload1;
    Food newFood;
    Uri saveUri;
    FirebaseStorage storage;
    StorageReference storageReference;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_list);

        //Init Firebase
        database = FirebaseDatabase.getInstance();
        foodlist = database.getReference("Foods");
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        recyclerView = findViewById(R.id.recycler_food);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        //fab
        rootLayout = findViewById(R.id.rootLayout);
        fab = findViewById(R.id.fab1);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddNewFood();
            }
        });

        //get Intent
        if (getIntent() != null)
            categoryId = getIntent().getStringExtra("CategoryId");

        if (!categoryId.isEmpty() && categoryId != null) {
            loadListFood(categoryId);
        }


    }

    private void showAddNewFood() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(FoodList.this);
        alertDialog.setTitle("Thêm mới tài liệu");
        alertDialog.setMessage("Hãy điền đầy đủ thông tin");

        LayoutInflater inflater = this.getLayoutInflater();
        View add_document_layout = inflater.inflate(R.layout.add_new_food, null);
        edt_name_newfood = add_document_layout.findViewById(R.id.edt_name_newfood);
        edt_des = add_document_layout.findViewById(R.id.edt_des_newfood);
        edt_price = add_document_layout.findViewById(R.id.edt_price_newfood);
        edt_discount = add_document_layout.findViewById(R.id.edt_discount_newfood);
        btn_select1 = add_document_layout.findViewById(R.id.btnSelect1);
        btn_upload1 = add_document_layout.findViewById(R.id.btnUpLoad1);
        btn_select1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });
        btn_upload1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                upLoad();
            }
        });
        alertDialog.setView(add_document_layout);
        alertDialog.setIcon(R.drawable.shopping_cart1_24);

        alertDialog.setPositiveButton("CÓ", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                if (newFood != null) {
                    foodlist.push().setValue(newFood);
                    Snackbar.make(rootLayout, "Mục mới " + newFood.getName() + "đã thêm xong", Snackbar.LENGTH_SHORT).show();
                }

            }
        });
        alertDialog.setNegativeButton("Không", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialog.show();

    }

    private void upLoad() {
        {
            if (saveUri != null) {
                final ProgressDialog mDialog = new ProgressDialog(this);
                mDialog.setMessage("Đang Upload..");
                mDialog.show();

                String imageName = UUID.randomUUID().toString();
                final StorageReference imageFolder = storageReference.child("image/" + imageName);
                imageFolder.putFile(saveUri)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                mDialog.dismiss();
                                Toast.makeText(FoodList.this, "Đã tải xong", Toast.LENGTH_SHORT).show();
                                imageFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        //set value for newCategory
                                        //viet gia tri nhap vao Firebase
                                        newFood = new Food();
                                        newFood.setName(edt_name_newfood.getText().toString());
                                        newFood.setDescription(edt_des.getText().toString());
                                        newFood.setPrice(edt_price.getText().toString());
                                        newFood.setDiscount(edt_discount.getText().toString());
                                        newFood.setMenuId(categoryId);
                                        newFood.setImage(uri.toString());
                                    }
                                });


                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        mDialog.dismiss();
                        Toast.makeText(FoodList.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                        mDialog.setMessage("Đã Upload" + (int) progress + "%");
                    }
                });
            }
        }
    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        //nhan ket qua la hinh anh da dc chon
        startActivityForResult(Intent.createChooser(intent, "Chọn Ảnh "), Common.PICK_IMAGE_REQUEST);

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Common.PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            saveUri = data.getData();
            btn_select1.setText("Ảnh đã được chọn!");
        }
    }


    private void loadListFood(String categoryId) {
        Query listFoodByCategoryId = foodlist.orderByChild("menuId").equalTo(categoryId);
        FirebaseRecyclerOptions<Food> showFood = new FirebaseRecyclerOptions.Builder<Food>()
                .setQuery(listFoodByCategoryId, Food.class)
                .build();
        adapter = new FirebaseRecyclerAdapter<Food, FoodViewHolder>(showFood) {
            @Override
            protected void onBindViewHolder(@NonNull FoodViewHolder viewHolder, int i, @NonNull Food model) {
                viewHolder.food_name.setText(model.getName());
                Picasso.get().load(model.getImage()).into(viewHolder.food_image);
                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int posittion, boolean isLongClick) {

                    }
                });
            }

            @NonNull
            @Override
            public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.food_item, parent, false);
                return new FoodViewHolder(itemView);
            }
        };
        adapter.startListening();
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);
    }



    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        if (item.getTitle().equals(Common.UPDATE)) {
            UpdateFood(adapter.getRef(item.getOrder()).getKey(), adapter.getItem(item.getOrder()));
        } else if (item.getTitle().equals(Common.DELETE)) {
            DeleteFood(adapter.getRef(item.getOrder()).getKey());

        }
        return super.onContextItemSelected(item);
    }

    private void DeleteFood(String key) {
        foodlist.child(key).removeValue();
    }

    private void UpdateFood(final String key, final Food item) {
        //copy nguyen dialogAddnewFood
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(FoodList.this);
        alertDialog.setTitle("Cập Nhập");
        alertDialog.setMessage("Hãy điền đầy đủ thông tin");

        LayoutInflater inflater = this.getLayoutInflater();
        View add_document_layout = inflater.inflate(R.layout.add_new_food, null);
        edt_name_newfood = add_document_layout.findViewById(R.id.edt_name_newfood);
        edt_des = add_document_layout.findViewById(R.id.edt_des_newfood);
        edt_price = add_document_layout.findViewById(R.id.edt_price_newfood);
        edt_discount = add_document_layout.findViewById(R.id.edt_discount_newfood);
        //set giá trị cho view
        edt_name_newfood.setText(item.getName());
        edt_discount.setText(item.getDiscount());
        edt_price.setText(item.getPrice());
        edt_des.setText(item.getDescription());

        btn_select1 = add_document_layout.findViewById(R.id.btnSelect1);
        btn_upload1 = add_document_layout.findViewById(R.id.btnUpLoad1);
        btn_select1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });
        btn_upload1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UploadUpdate(item);
            }
        });
        alertDialog.setView(add_document_layout);
        alertDialog.setIcon(R.drawable.shopping_cart1_24);

        alertDialog.setPositiveButton("CÓ", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

                //update Infor
                item.setName(edt_name_newfood.getText().toString());
                item.setDiscount(edt_discount.getText().toString());
                item.setPrice(edt_price.getText().toString());
                item.setDescription(edt_des.getText().toString());

                foodlist.child(key).setValue(item);
                Snackbar.make(rootLayout, "Mục " + item.getName() + "đã sửa xong", Snackbar.LENGTH_SHORT).show();


            }
        });
        alertDialog.setNegativeButton("Không", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialog.show();

    }

    private void UploadUpdate(final Food item) {
        if (saveUri != null) {
            final ProgressDialog mDialog = new ProgressDialog(this);
            mDialog.setMessage("Đang tải ....");
            mDialog.show();

            String imageName = UUID.randomUUID().toString();
            final StorageReference imageFolder = storageReference.child("image/" + imageName);
            imageFolder.putFile(saveUri).
                    addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            mDialog.dismiss();
                            Toast.makeText(FoodList.this, "Đã tải xong", Toast.LENGTH_SHORT).show();
                            imageFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    item.setImage(uri.toString());
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    mDialog.dismiss();
                    Toast.makeText(FoodList.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                    mDialog.setMessage("Đã tải xong" + progress + "%");
                }
            });
        }
    }


}