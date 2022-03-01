package com.opsc7311.healthandfitness19013888;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Cartesian;
import com.anychart.core.cartesian.series.Column;
import com.anychart.core.gauge.pointers.Bar;
import com.anychart.enums.Anchor;
import com.anychart.enums.Position;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class BarGraphActivity extends AppCompatActivity {

    private FirebaseDatabase database;
    private DatabaseReference reference;
    private DatabaseReference reference2;
    private FirebaseAuth fAuth;
    private User user;

    AnyChartView barChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bar_graph);

        barChart = findViewById(R.id.barChart);
        database = FirebaseDatabase.getInstance();
        fAuth = FirebaseAuth.getInstance();

        String userUid = fAuth.getUid();
        reference = database.getReference("Users/" + userUid).child("weightLog");

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

        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                ArrayList<DataEntry> weight = new ArrayList<>();

                if(user.setting.equals(("metric"))){

                for(DataSnapshot snap : snapshot.getChildren()) {
                    weightTracker wt = snap.getValue(weightTracker.class);
                    weight.add(new ValueDataEntry(wt.date, wt.weightLog));
                }

                }else{

                    for(DataSnapshot snap : snapshot.getChildren()) {
                        weightTracker wt = snap.getValue(weightTracker.class);
                        weight.add(new ValueDataEntry(wt.date, wt.weightLog * 2.205));
                    }
                }

                Cartesian cartesian = AnyChart.column();

                if(user.setting.equals(("metric"))){

                    cartesian.yAxis(0).title("Weight (kg)");
                }else{

                    cartesian.yAxis(0).title("Weight (pounds)");
                }
                cartesian.animation(true);
                cartesian.data(weight);
                cartesian.xAxis(0).title("Date");
                cartesian.title("Weight vs Date Bar Graph");
                barChart.setChart(cartesian);
                barChart.animate();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

        reference.addValueEventListener(valueEventListener);

        //initialize and assign variable
        BottomNavigationView navigationView = findViewById(R.id.navView);

        //set home selected
        navigationView.setSelectedItemId(R.id.barGraph);

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

    }
}