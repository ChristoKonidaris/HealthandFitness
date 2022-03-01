package com.opsc7311.healthandfitness19013888;

import androidx.annotation.NonNull;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth fAuth;
    private FirebaseDatabase database;
    private User user;
    private DatabaseReference reference;

    EditText fullName, weight, height, goalWeight, goalCalorie;
    Button SubmitBtn;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fullName    = findViewById(R.id.fullName);
        weight      = findViewById(R.id.weight);
        height      = findViewById(R.id.height);
        SubmitBtn   = findViewById(R.id.submit);
        goalWeight  = findViewById(R.id.weightGoal);
        goalCalorie = findViewById(R.id.CalorieIntake);
        progressBar = findViewById(R.id.progressBar3);

        fAuth = FirebaseAuth.getInstance();

        String userUid = fAuth.getUid();

        database = FirebaseDatabase.getInstance();
        reference = database.getReference("Users/" + userUid).child("userInformation");

        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                user = snapshot.getValue(User.class);
                if(user != null)
                {
                    fullName.setText(user.fullName);

                    if(user.setting.equals("metric")) {

                        weight.setText(Double.toString(user.weight));
                        height.setText(Double.toString(user.height));
                        goalWeight.setText(Double.toString(user.goalWeight));
                    }else
                    {
                        weight.setText(Double.toString(user.getImperialWeight()));
                        height.setText(Double.toString(user.getImperialHeight()));
                        goalWeight.setText(Double.toString(user.getImperialGoalWeight()));
                    }

                        goalCalorie.setText(Double.toString(user.goalCalorie));
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        reference.addValueEventListener(valueEventListener);

        //initialize and assign variable
        BottomNavigationView navigationView = findViewById(R.id.navView);

        //set home selected
        navigationView.setSelectedItemId(R.id.myProfile);

        //perform item selected listener
        navigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                switch (menuItem.getItemId()) {
                    case R.id.barGraph:
                        startActivity(new Intent(getApplicationContext(), BarGraphActivity.class));
                        overridePendingTransition(0, 0);
                        finish();
                        return true;
                    case R.id.myProfile:
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.settings:
                        startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.weightGoals:
                        startActivity(new Intent(getApplicationContext(), WeightActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.meals:
                        startActivity(new Intent(getApplicationContext(), MealActivity.class));
                        overridePendingTransition(0, 0);
                        return true;

                }
                return false;
            }
        });


        SubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                progressBar.setVisibility(View.VISIBLE);

                String FullName = fullName.getText().toString();
                double Height = Double.parseDouble(height.getText().toString());
                double Weight = Double.parseDouble(weight.getText().toString());
                double goalweight = Double.parseDouble((goalWeight.getText().toString()));
                double goalcalorie = Double.parseDouble((goalCalorie.getText().toString()));

                user.fullName = FullName;
                user.height = Height;
                user.weight = Weight;
                user.goalWeight = goalweight;
                user.goalCalorie = goalcalorie;


                reference.setValue(user);
                progressBar.setVisibility(View.INVISIBLE);

                Toast.makeText(MainActivity.this, "Your information has been updated.", Toast.LENGTH_SHORT).show();
            }
        });

    }


}