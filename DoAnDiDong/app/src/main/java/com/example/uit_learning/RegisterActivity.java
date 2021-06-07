package com.example.uit_learning;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    Button btnLoginActivity, btnRegister;
    ProgressDialog progressDialog;
    TextInputLayout textEmail, textFullName, textPassword, textConPassword;

    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        //Ẩn thanh actionBar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        //kết thúc

        btnLoginActivity = findViewById(R.id.btnLoginActivity);
        btnRegister = findViewById(R.id.btnRegister);

        textEmail = findViewById(R.id.textEmailDK);
        textFullName = findViewById(R.id.textFullNameDK);
        textPassword = findViewById(R.id.textPasswordDK);
        textConPassword = findViewById(R.id.textConPasswordDK);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Registering User");

        firebaseAuth = FirebaseAuth.getInstance();

        btnLoginActivity.setOnClickListener((view)->{
            Intent intent = new Intent(RegisterActivity.this,LoginActivity.class);
            startActivity(intent);
            finish();
        });

        btnRegister.setOnClickListener((view)->{

            String email = textEmail.getEditText().getText().toString().trim();
            String fullname = textFullName.getEditText().getText().toString().trim();
            String password = textPassword.getEditText().getText().toString().trim();
            String conpassword = textConPassword.getEditText().getText().toString().trim();

            if (TextUtils.isEmpty(fullname))
            {
                textFullName.setError("Bắt buộc nhập tên");
                return;
            }

            if (TextUtils.isEmpty(email))
            {
                textEmail.setError("Bắt buộc nhập email");
                return;
            }

            if (TextUtils.isEmpty(password))
            {
                textPassword.setError("Bắt buộc nhập mật khẩu");
                return;
            }

            if (password.length() < 6)
            {
                textPassword.setError("Mật khẩu phải có ít nhất 6 kí tự");
                return;
            }

            if (TextUtils.isEmpty(conpassword))
            {
                textConPassword.setError("Bắt buộc nhập xác nhận mật khẩu");
                return;
            }

            progressDialog.show();

            firebaseAuth.createUserWithEmailAndPassword(email,password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful())
                            {
                                progressDialog.dismiss();
                                FirebaseUser user = firebaseAuth.getCurrentUser();

                                HashMap<Object,String> hashMap = new HashMap<>();
                                hashMap.put("email",email);
                                hashMap.put("name",fullname);
                                hashMap.put("image","");
                                hashMap.put("cover","");
                                hashMap.put("uid",user.getUid());

                                FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                                DatabaseReference reference = firebaseDatabase.getReference("Users");
                                reference.child(user.getUid()).setValue(hashMap);

                                Toast.makeText(RegisterActivity.this,"Register Successful",Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(RegisterActivity.this,LoginActivity.class));
                                finish();;
                            }
                            else
                            {
                                Toast.makeText(RegisterActivity.this, "Lỗi",Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        });
    }
}