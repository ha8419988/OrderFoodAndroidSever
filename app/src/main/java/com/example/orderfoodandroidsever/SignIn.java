package com.example.orderfoodandroidsever;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import com.example.orderfoodandroidsever.Common.Common;
import com.example.orderfoodandroidsever.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.Executor;

public class SignIn extends AppCompatActivity {
    EditText edt_sdt, edt_password;
    Button btn_signIn1;
    FirebaseDatabase db;
    DatabaseReference reference;
    private AppCompatImageView appCompatImageView;
    private Executor executor;
    private BiometricPrompt biometricPrompt;
    private BiometricPrompt.PromptInfo promptInfo;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);
        edt_sdt = findViewById(R.id.edt_sdt);
        edt_password = findViewById(R.id.edt_password);
        btn_signIn1 = findViewById(R.id.btn_signin1);
        appCompatImageView = findViewById(R.id.img_fingerprint);
        //Firebase
        db = FirebaseDatabase.getInstance();
        reference = db.getReference("User");
        btn_signIn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signInUser(edt_sdt.getText().toString(), edt_password.getText().toString());
            }
        });
        init();
    }

    private void init() {
        executor = ContextCompat.getMainExecutor(this);
        biometricPrompt = new BiometricPrompt(this,
                executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode,
                                              @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                Toast.makeText(getApplicationContext(),
                        "Authentication error: " + errString, Toast.LENGTH_SHORT)
                        .show();
            }

            @Override
            public void onAuthenticationSucceeded(
                    @NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                Intent intent = new Intent(SignIn.this, Home.class);
                Common.current_user = Common.loadData(getApplicationContext());
                startActivity(intent);
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                Toast.makeText(getApplicationContext(), "Authentication failed",
                        Toast.LENGTH_SHORT)
                        .show();
            }
        });

        promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Biometric login for my app")
                .setSubtitle("Log in using your biometric credential")
                .setNegativeButtonText("Use account password")
                .build();

        appCompatImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                {
                    if (Common.getSetting(getApplicationContext())) {
                        if (Common.loadData(getApplicationContext()) != null) {
                            biometricPrompt.authenticate(promptInfo);
                        }
                    } else {
                        Toast.makeText(SignIn.this,
                                getString(R.string.ban_can_bat_face_id), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private void signInUser(String phone, String password) {
        final ProgressDialog mDialog = new ProgressDialog(SignIn.this);
        mDialog.setMessage("Vui lòng đợi...");
        mDialog.show();
        final String localPhone = phone;
        final String localPassword = password;
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(localPhone).exists()) {
                    mDialog.dismiss();
                    User user = snapshot.child(localPhone).getValue(User.class);
                    user.setPhone(localPhone);
                    if (Boolean.parseBoolean(user.getIsStaff()))//if staff=true
                    {
                        if (user.getPassword().equals(localPassword)) {
                            //login OK
                            Intent login = new Intent(SignIn.this, Home.class);
                            Common.current_user = user;
                            startActivity(login);
                        } else
                            Toast.makeText(SignIn.this, "Mật Khẩu Sai", Toast.LENGTH_SHORT).show();
                    } else
                        Toast.makeText(SignIn.this, "Xin đăng nhập bằng tài khoản được cấp quyền", Toast.LENGTH_SHORT).show();
                } else {
                    mDialog.dismiss();
                    Toast.makeText(SignIn.this, "Tài Khoản Không Tồn Tại", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
