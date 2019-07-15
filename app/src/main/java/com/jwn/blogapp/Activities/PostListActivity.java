package com.jwn.blogapp.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.jwn.blogapp.Data.BlogRecyclerAdapter;
import com.jwn.blogapp.Model.Blog;
import com.jwn.blogapp.R;

import java.util.ArrayList;
import java.util.List;

public class PostListActivity extends AppCompatActivity {
    private DatabaseReference mDatabaseReference;
    private RecyclerView recyclerView;
    private BlogRecyclerAdapter blogRecyclerAdapter;
    private List<Blog> blogList;
    private FirebaseDatabase mDatabase;
    private FirebaseUser mUser;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_list);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();


        mDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mDatabase.getReference().child("MBlog");
        mDatabaseReference.keepSynced(true);

        blogList = new ArrayList<>();

        recyclerView = findViewById(R.id.recyclerView);

        LinearLayoutManager manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);
        //recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recyclerView.setHasFixedSize(true);

        blogRecyclerAdapter = new BlogRecyclerAdapter(PostListActivity.this, blogList);

        recyclerView.setAdapter(blogRecyclerAdapter);

    }
/*onCreateOptionsMenu &  onOptionsItemSelected allow menu to be inflated so we can see it */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_add:

                if (mUser != null && mAuth != null) {

                    startActivity(new Intent(PostListActivity.this,  AddPostActivity.class));
                    //clear the previous activity so the Activities don't start stacking up
                    finish();
                }

             break;

            case R.id.action_signout:

                if (mUser != null && mAuth != null) {
                    mAuth.signOut();

                    startActivity(new Intent(PostListActivity.this,  MainActivity.class));
                    //clear the previous activity so the Activities don't start stacking up
                    finish();
                }
             break;
        }// end switch

        return super.onOptionsItemSelected(item);
    }// end boolean onOptionsItemSelected

    @Override
    protected void onStart() {
        super.onStart();

        mDatabaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                // map objects to Blog class (Blog.java)
                 Blog blog = dataSnapshot.getValue(Blog.class);
                // add the objects to blogList
                blogList.add(blog);

                blogRecyclerAdapter = new BlogRecyclerAdapter(PostListActivity.this, blogList);
                recyclerView.setAdapter(blogRecyclerAdapter);
                blogRecyclerAdapter.notifyDataSetChanged();

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}//end class PostListActivity extends AppCompatActivity
