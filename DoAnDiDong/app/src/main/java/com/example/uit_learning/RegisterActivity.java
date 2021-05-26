package com.example.uit_learning;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

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
import com.google.firebase.database.FirebaseDatabase;

import model.User;

public class RegisterActivity extends AppCompatActivity {

    Button btnLoginActivity, btnRegister;

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

            firebaseAuth.createUserWithEmailAndPassword(email,password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful())
                            {
                                User user = new User(fullname,email);
                                FirebaseDatabase.getInstance().getReference("users")
                                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful())
                                        {
                                            //Toast.makeText(RegisterActivity.this,"Đăng kí thành công",Toast.LENGTH_LONG).show();
                                            Intent intent = new Intent(RegisterActivity.this,SuccessRegisterActivity.class);
                                            startActivity(intent);
                                            finish();
                                        }
                                        else
                                        {
                                            Toast.makeText(RegisterActivity.this,"Đăng kí thất bại",Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });
                            }
                            else
                            {

                            }
                        }
                    });
        });
    }
}