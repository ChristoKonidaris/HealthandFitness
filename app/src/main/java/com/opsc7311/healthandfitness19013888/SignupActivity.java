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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class SignupActivity extends AppCompatActivity {

    EditText Email, Password;
    Button SignUpBtn, LoginBtn;
    FirebaseAuth fAuth;
    ProgressBar ProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        Email           = findViewById(R.id.email_edt_text);
        Password        = findViewById(R.id.pass_edt_text);
        SignUpBtn       = findViewById(R.id.signup_btn);
        LoginBtn        = findViewById(R.id.login_btn);

        fAuth           = FirebaseAuth.getInstance();
        ProgressBar     = findViewById(R.id.progressBar);


        SignUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = Email.getText().toString().trim();
                String password = Password.getText().toString().trim();

                if(TextUtils.isEmpty(email))
                {
                    Email.setError("Email is Required.");
                    return;
                }

                if(TextUtils.isEmpty(password))
                {
                    Password.setError("Password is Required.");
                    return;
                }

                if(password.length() < 6)
                {
                    Password.setError("Password must be at least 6 characters long.");
                    return;
                }

                ProgressBar.setVisibility(View.VISIBLE);

                //register the user in firebase

                fAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful())
                        {
                            ProgressBar.setVisibility(View.INVISIBLE);
                            Toast.makeText(SignupActivity.this, "Your account has been created.", Toast.LENGTH_SHORT).show();


                            fAuth = FirebaseAuth.getInstance();
                            String userID = fAuth.getCurrentUser().getUid();


                            FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                            DatabaseReference databaseReference = firebaseDatabase.getReference("Users/" + userID).child("userInformation");

                            databaseReference.setValue(new User("", 0, 0, 0,0,"metric"));

                            startActivity(new Intent(getApplicationContext(), MainActivity.class));


                        }
                        else
                        {
                            Toast.makeText(SignupActivity.this, "Error! Sign up Failed." + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            ProgressBar.setVisibility(View.INVISIBLE);
                        }


                    }
                });



            }
        });



        LoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                finish();
            }
        });

    }



}