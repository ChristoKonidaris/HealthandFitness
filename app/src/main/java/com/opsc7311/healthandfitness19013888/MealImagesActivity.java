package com.opsc7311.healthandfitness19013888;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class MealImagesActivity extends AppCompatActivity implements ImageAdapter.OnItemCLickListener {

    private RecyclerView mRecyclerView;
    private ImageAdapter mAdapter;

    private FirebaseStorage mStorage;
    private DatabaseReference mDatabaseRef;
    private ValueEventListener mDBListener;

    private List<UploadMeal> mUploads;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meal_images);

        mRecyclerView = findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mUploads = new ArrayList<>();

        mAdapter = new ImageAdapter(MealImagesActivity.this, mUploads);
        mRecyclerView.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(MealImagesActivity.this);

        mStorage = FirebaseStorage.getInstance();

        mDatabaseRef = FirebaseDatabase.getInstance().getReference("Users/" + FirebaseAuth.getInstance().getUid()).child("uploads");

        mDBListener = mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mUploads.clear();
                //get images from database and upload
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    UploadMeal upload = postSnapshot.getValue(UploadMeal.class);
                    upload.setmKey(postSnapshot.getKey());
                    mUploads.add(upload);
                }

                mAdapter.notifyDataSetChanged();

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(MealImagesActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
        //initialize and assign variable
        BottomNavigationView navigationView = findViewById(R.id.navView);

        //set home selected
        navigationView.setSelectedItemId(R.id.meals);

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

    @Override
    public void onItemClick(int position) {
        Toast.makeText(this, "This meal is at position: " + position, Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onDeleteClick(int position) {
        UploadMeal selectedItem = mUploads.get(position);
        final String selectedKey = selectedItem.getmKey();

        StorageReference imageRef = mStorage.getReferenceFromUrl(selectedItem.getImageUrl());
        imageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                mDatabaseRef.child(selectedKey).removeValue();
                Toast.makeText(MealImagesActivity.this, "Item has been deleted", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDatabaseRef.removeEventListener(mDBListener);
    }
}