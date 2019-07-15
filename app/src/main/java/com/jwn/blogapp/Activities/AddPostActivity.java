package com.jwn.blogapp.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.jwn.blogapp.R;

import java.util.HashMap;
import java.util.Map;

public class AddPostActivity extends AppCompatActivity {

    private static final String TAG = AddPostActivity.class.getSimpleName();


    private ImageButton mPostImage;
    private EditText mPostTitle, mPostDesc;
    private Button mSubmitButton;
    private StorageReference mStorage;
    private DatabaseReference mPostDatabase;
    private FirebaseDatabase mDatabase;

    private FirebaseUser mUser;
    private FirebaseAuth mAuth;
    private ProgressDialog mProgress;
    private Uri mImageUri;
    private static final int GALLERY_CODE = 1;
    StorageReference  filepath;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);

        mProgress = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        mStorage = FirebaseStorage.getInstance().getReference();

        mPostDatabase = FirebaseDatabase.getInstance().getReference().child("MBlog");

        mPostImage = findViewById(R.id.imageButton);
        mPostTitle = findViewById(R.id.postTitleEt);
        mPostDesc = findViewById(R.id.descriptionEt);
        mSubmitButton = findViewById(R.id.submitPost);


        mPostImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                //get all types of images
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent,GALLERY_CODE);

            }
        });


        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // posting to the database
                startPosting();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_CODE && resultCode == RESULT_OK) {
            mImageUri = data.getData();

            mPostImage.setImageURI(mImageUri);

        }

        //Log.d(TAG, "mImageUri in AddPostActivity is: " + mImageUri);
    }



    private void startPosting(){

        final String titleVal = mPostTitle.getText().toString().trim();
        final String descVal = mPostDesc.getText().toString().trim();

        if(!TextUtils.isEmpty(titleVal) && !TextUtils.isEmpty(descVal) && mImageUri !=null) {
            //start uploading
            mProgress.show();

            mStorage = FirebaseStorage.getInstance().getReference();


            filepath = mStorage.child("MBlog_images").child(mImageUri.getLastPathSegment());
            filepath.putFile(mImageUri).continueWithTask(new Continuation<UploadTask.TaskSnapshot
                    , Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {

                    if (!task.isSuccessful()) {

                        throw  task.getException();

                    }

                    return filepath.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {

                    if (task.isSuccessful()) {

                        Uri downloadUri = task.getResult();


                        DatabaseReference newPost = mPostDatabase.push();


                        Map<String, String> dataToSave = new HashMap<>();
                        dataToSave.put("title", titleVal);
                        dataToSave.put("desc", descVal);
                        dataToSave.put("image", downloadUri.toString());
                        dataToSave.put("timestamp", String.valueOf(java.lang.System.currentTimeMillis()));
                        dataToSave.put("userid", mUser.getUid());

                        newPost.setValue(dataToSave);
                        mProgress.dismiss();

                        startActivity(new Intent(AddPostActivity.this, PostListActivity.class));
                    }else{
                        Toast.makeText(AddPostActivity.this, "Upload failure: " + task.getException()
                                        .getMessage(), Toast.LENGTH_SHORT).show();
                    }

                }
            });


        }

    }// end start Posting





}//end class AddPostActivity extends AppCompatActivity
