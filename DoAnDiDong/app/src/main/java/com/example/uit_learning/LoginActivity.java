package com.example.uit_learning;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    Button btnLogin;

    TextView btnRegister,btnForgetPassword;

    TextInputLayout textEmail, textPassword;

    FirebaseAuth firebaseAuth;

    ProgressDialog progressDialog;

    float v= 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        btnLogin  =findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);
        btnForgetPassword = findViewById(R.id.btnForgetPassword);

        textEmail = findViewById(R.id.textEmailDN);
        textPassword = findViewById(R.id.textPasswordDN);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Logging in...");

        firebaseAuth = FirebaseAuth.getInstance();

        //Set animation______________________________________________________
        textEmail.setTranslationX(800);
        textPassword.setTranslationX(800);
        btnForgetPassword.setTranslationX(800);
        btnLogin.setTranslationX(800);
        btnRegister.setTranslationX(800);

        textEmail.setAlpha(v);
        textPassword.setAlpha(v);
        btnForgetPassword.setAlpha(v);
        btnLogin.setAlpha(v);
        btnRegister.setAlpha(v);

        textEmail.animate().translationX(0).alpha(1).setDuration(1000).setStartDelay(300).start();
        textPassword.animate().translationX(0).alpha(1).setDuration(1000).setStartDelay(400).start();
        btnForgetPassword.animate().translationX(0).alpha(1).setDuration(1000).setStartDelay(500).start();
        btnLogin.animate().translationX(0).alpha(1).setDuration(1000).setStartDelay(600).start();
        btnRegister.animate().translationX(0).alpha(1).setDuration(1000).setStartDelay(700).start();
        //____________________________________________________________________

        btnRegister.setOnClickListener((view)->{
            Intent intent = new Intent(LoginActivity.this,RegisterActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
        });

        btnLogin.setOnClickListener((view)->{

            textEmail.setErrorEnabled(false);
            textPassword.setErrorEnabled(false);

            String email = textEmail.getEditText().getText().toString().trim();
            String password  =textPassword.getEditText().getText().toString().trim();

            if (TextUtils.isEmpty(email))
            {
                textEmail.setError("Please enter your email");
                textEmail.setFocusable(true);
                textEmail.setErrorIconDrawable(null);
                return;
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches())
            {
                textEmail.setError("Invalid email");
                textEmail.setFocusable(true);
                textEmail.setErrorIconDrawable(null);
                return;
            }

            if (TextUtils.isEmpty(password))
            {
                textPassword.setError("Please enter your password");
                textPassword.setFocusable(true);
                textPassword.setErrorIconDrawable(null);
                return;
            }

            if (password.length() < 6)
            {
                textPassword.setError("Password length at least 6 characters");
                textPassword.setFocusable(true);
                textPassword.setErrorIconDrawable(null);
                return;
            }

            progressDialog.show();

            firebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful())
                    {
                        progressDialog.dismiss();
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

//                        if (user.isEmailVerified())
//                        {
//                            Intent intent  =new Intent(LoginActivity.this,LoadActivity.class);
//                            startActivity(intent);
//                            finish();
//                        }
//                        else
//                        {
//                            user.sendEmailVerification();
//                            Toast.makeText(LoginActivity.this,"Kiểm tra mail để xác thực cho lần đầu đăng nhập",Toast.LENGTH_LONG).show();
//                        }
                        startActivity(new Intent(LoginActivity.this, DashboardActivity.class));
                        overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
                        finish();
                    }
                    else
                    {
                        progressDialog.dismiss();
                        Toast.makeText(LoginActivity.this,"Tài khoản hoặc mật khẩu không chính xác!",Toast.LENGTH_LONG).show();
                    }
                }
            });
        });

        btnForgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this,ForgetPasswordActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
//                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
//                builder.setTitle("Reset password");
//
//                LinearLayout linearLayout = new LinearLayout(LoginActivity.this);
//
//                EditText emailEt = new EditText(LoginActivity.this);
//                emailEt.setHint("Email");
//                emailEt.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
//                emailEt.setMinEms(16);
//
//                linearLayout.addView(emailEt);
//                linearLayout.setPadding(10,10,10,10);
//
//                builder.setView(linearLayout);
//
//                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        String email = emailEt.getText().toString().trim();
//
//                        progressDialog.setMessage("Sending mail...");
//                        progressDialog.show();
//
//                        firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
//                            @Override
//                            public void onComplete(@NonNull Task<Void> task) {
//                                progressDialog.dismiss();
//                                if (task.isSuccessful())
//                                {
//                                    Toast.makeText(LoginActivity.this,"Email sent",Toast.LENGTH_SHORT).show();
//                                }
//                                else
//                                {
//                                    Toast.makeText(LoginActivity.this,"Fail..",Toast.LENGTH_SHORT).show();
//                                }
//                            }
//                        }).addOnFailureListener(new OnFailureListener() {
//                            @Override
//                            public void onFailure(@NonNull Exception e) {
//                                progressDialog.dismiss();
//                                Toast.makeText(LoginActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
//                            }
//                        });
//                    }
//                });
//
//                builder.create().show();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        textEmail.setErrorEnabled(false);
        textPassword.setErrorEnabled(false);

//        textEmail.setTranslationX(-800);
//        textPassword.setTranslationX(-800);
//        btnForgetPassword.setTranslationX(-800);
//        btnLogin.setTranslationX(-800);
//        btnRegister.setTranslationX(-800);
//
//        textEmail.setAlpha(v);
//        textPassword.setAlpha(v);
//        btnForgetPassword.setAlpha(v);
//        btnLogin.setAlpha(v);
//        btnRegister.setAlpha(v);
//
//        textEmail.animate().translationX(0).alpha(1).setDuration(1000).setStartDelay(300).start();
//        textPassword.animate().translationX(0).alpha(1).setDuration(1000).setStartDelay(400).start();
//        btnForgetPassword.animate().translationX(0).alpha(1).setDuration(1000).setStartDelay(500).start();
//        btnLogin.animate().translationX(0).alpha(1).setDuration(1000).setStartDelay(600).start();
//        btnRegister.animate().translationX(0).alpha(1).setDuration(1000).setStartDelay(700).start();
    }
}