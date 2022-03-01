package com.opsc7311.healthandfitness19013888;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


public class LoginActivity extends AppCompatActivity {

     EditText Email, Password;
     Button SignUpBtn, LoginBtn;
     ProgressBar ProgressBar;
     FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Email       = findViewById(R.id.email_edt_text);
        Password    = findViewById(R.id.pass_edt_text);
        ProgressBar = findViewById(R.id.progressBar2);
        LoginBtn    = findViewById(R.id.login_btn);
        SignUpBtn   = findViewById(R.id.signUp_btn);
        fAuth       = FirebaseAuth.getInstance();

        LoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = Email.getText().toString().trim();
                String password = Password.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    Email.setError("Email is Required.");
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Password.setError("Password is Required.");
                    return;
                }

                if (password.length() < 6) {
                    Password.setError("Password must be at least 6 characters long.");
                    return;
                }

                ProgressBar.setVisibility(View.VISIBLE);

                //authenticate user

                fAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful())
                        {
                            Toast.makeText(LoginActivity.this, "Login Successful.", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            ProgressBar.setVisibility(View.INVISIBLE);
                        }
                        else
                        {
                            Toast.makeText(LoginActivity.this, "Error, Login Failed." + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            ProgressBar.setVisibility(View.INVISIBLE);
                        }
                    }
                });


            }
        });

        SignUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), SignupActivity.class));
            }
        });

    }

}