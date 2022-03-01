package com.opsc7311.healthandfitness19013888;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class WeightActivity extends AppCompatActivity {

    private weightTracker WeightTracker;
    private FirebaseAuth fAuth;
    private FirebaseDatabase database;
    private DatabaseReference reference;
    private DatabaseReference reference2;
    private User user;


    private FirebaseAuth.AuthStateListener authListener;


    EditText date, weightLog;
    Button SubmitBtn;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weight);


        date      = findViewById(R.id.Date);
        weightLog      = findViewById(R.id.weightLog);
        SubmitBtn   = findViewById(R.id.submit);
        listView = findViewById(R.id.list);

        fAuth = FirebaseAuth.getInstance();

        String userUid = fAuth.getUid();

        database = FirebaseDatabase.getInstance();
        reference2 = database.getReference("Users/" + userUid).child("userInformation");
        reference2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                user = snapshot.getValue(User.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        reference = database.getReference("Users/" + userUid).child("weightLog");

        ValueEventListener valueEventListener = new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                List<String> weightTrackerList = new ArrayList<>();



                for(DataSnapshot snap : snapshot.getChildren()){
                    weightTracker wt = snap.getValue(weightTracker.class);
                    Log.d("tag", wt.date);



                    if(user.setting.equals(("metric"))){
                        weightTrackerList.add("On " + wt.date + " your weight was: " + wt.weightLog + "kg");
                    }else{
                        weightTrackerList.add("On " + wt.date + " your weight was: " + wt.weightLog * 2.205 + " pounds");
                    }

                }

                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(WeightActivity.this, android.R.layout.simple_list_item_1, weightTrackerList);

                listView.setAdapter(arrayAdapter);

            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

        reference.addValueEventListener(valueEventListener);

        //initialize and assign variable
        BottomNavigationView navigationView = findViewById(R.id.navView);

        //set home selected
        navigationView.setSelectedItemId(R.id.weightGoals);

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

                //progressBar.setVisibility(View.VISIBLE);

                double WeightLog = Double.parseDouble(weightLog.getText().toString());
                String Date = date.getText().toString();
                weightTracker wt = new weightTracker();
                wt.weightLog = WeightLog;
                wt.date = Date;
                reference.push().setValue(wt);

                Toast.makeText(WeightActivity.this, "Your weight log has been added..", Toast.LENGTH_SHORT).show();

                //progressBar.setVisibility(View.INVISIBLE);
            }
        });

    }

}