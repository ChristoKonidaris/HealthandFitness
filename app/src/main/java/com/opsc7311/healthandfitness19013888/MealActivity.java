package com.opsc7311.healthandfitness19013888;

        import androidx.annotation.NonNull;
        import androidx.annotation.Nullable;
        import androidx.appcompat.app.AppCompatActivity;
        import androidx.core.app.ActivityCompat;
        import androidx.core.content.ContextCompat;
        import androidx.core.content.FileProvider;

        import android.Manifest;
        import android.app.Activity;
        import android.content.ContentResolver;
        import android.content.Intent;
        import android.content.pm.PackageManager;
        import android.net.Uri;
        import android.os.Bundle;
        import android.os.Environment;
        import android.provider.MediaStore;
        import android.util.Log;
        import android.view.MenuItem;
        import android.view.View;
        import android.webkit.MimeTypeMap;
        import android.widget.Button;
        import android.widget.EditText;
        import android.widget.ImageView;
        import android.widget.ProgressBar;
        import android.widget.TextView;
        import android.widget.Toast;

        import com.google.android.gms.tasks.OnFailureListener;
        import com.google.android.gms.tasks.OnSuccessListener;
        import com.google.android.gms.tasks.Task;
        import com.google.android.material.bottomnavigation.BottomNavigationView;
        import com.google.firebase.auth.FirebaseAuth;
        import com.google.firebase.database.DatabaseReference;
        import com.google.firebase.database.FirebaseDatabase;
        import com.google.firebase.storage.FirebaseStorage;
        import com.google.firebase.storage.StorageReference;
        import com.google.firebase.storage.StorageTask;
        import com.google.firebase.storage.UploadTask;
        import com.squareup.picasso.Picasso;

        import java.io.File;
        import java.io.IOException;
        import java.text.SimpleDateFormat;
        import java.util.Date;
        import java.util.UUID;

public class MealActivity extends AppCompatActivity {

    public static final int CAMERA_REQUEST_CODE = 102;
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int CAMERA_PERM_CODE = 101;

    String currentPhotoPath;
    StorageReference storageReference;

    Button mButtonCamera;
    Button mButtonChooseImage;
    Button mButtonUpload;
    TextView mTextViewShowUploads;
    EditText mEditTextFileName;
    ImageView mImageView;
    ProgressBar mProgressBar;

    private Uri mImageUri;

    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;


    private StorageTask mUploadTask;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meal);

        mButtonCamera = findViewById(R.id.button_camera);
        mButtonChooseImage = findViewById(R.id.button_choose);
        mButtonUpload = findViewById(R.id.button_upload);
        mTextViewShowUploads = findViewById(R.id.text_view_show_uploads);
        mEditTextFileName = findViewById(R.id.edit_text_file_name);
        mImageView = findViewById(R.id.image_view);
        mProgressBar = findViewById(R.id.image_progress);

        mStorageRef = FirebaseStorage.getInstance().getReference("uploads/");
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("Users/" + FirebaseAuth.getInstance().getUid() + "/uploads");

        storageReference = FirebaseStorage.getInstance().getReference();

        mButtonCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                askCameraPermission();
                Toast.makeText(MealActivity.this, "Camera Opening", Toast.LENGTH_SHORT).show();
            }
        });


        mButtonChooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

        mButtonUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    uploadFile();
            }
        });

        mTextViewShowUploads.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImagesActivity();

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

    private void openFileChooser() {
        /*Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);*/

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                File f = new File(currentPhotoPath);
                mImageView.setImageURI(Uri.fromFile(f));
                Log.d("TAG", "Absolute Url of image is " + Uri.fromFile(f));

                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                Uri contentUri = Uri.fromFile(f);
                mediaScanIntent.setData(contentUri);
                this.sendBroadcast(mediaScanIntent);



            }
        }

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            mImageUri = data.getData();
            Picasso.get().load(mImageUri).into(mImageView);
        }


    }


    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }



    private void openImagesActivity() {
        Intent intent = new Intent(this, MealImagesActivity.class);
        startActivity(intent);
    }

    private void askCameraPermission() {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String [] {Manifest.permission.CAMERA}, CAMERA_PERM_CODE);
        }else
        {
            dispatchTakePictureIntent();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == CAMERA_PERM_CODE)
        {
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                dispatchTakePictureIntent();
            }else
            {
                Toast.makeText(this, "Camera Permission is Required to Use Camera", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        //File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }


    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE);
            }
        }
    }

    private void uploadFile() {

        if (mImageUri != null) {
            mProgressBar.setVisibility(View.VISIBLE);

            //Log.d("TAG", "failed1"); //use if testing...don't need this line.
            StorageReference ref = mStorageRef.child(UUID.randomUUID().toString());

            mUploadTask = ref.putFile(mImageUri);

            // Register observers to listen for when the download is done or if it fails
            mUploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Toast.makeText(MealActivity.this, exception.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    mProgressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(MealActivity.this, "Image Uploaded", Toast.LENGTH_LONG).show();
                    //Log.d("TAG", "failed2");
                    // Get a URL to the uploaded content

                    //String userUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

                    Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                    while (!urlTask.isSuccessful());
                    Uri downloadUrl = urlTask.getResult();

                    Log.d("TAG", "onSuccess: firebase download url: " + downloadUrl.toString()); //use if testing...don't need this line.
                    UploadMeal upload = new UploadMeal(mEditTextFileName.getText().toString().trim(), downloadUrl.toString());

                    //String uploadId = mDatabaseRef.push().getKey();

                    mDatabaseRef.push().setValue(upload);
                }
            });
        }else
        {
            Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show();
        }

    }



}


