package com.jwn.blogapp.Data;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jwn.blogapp.Model.Blog;
import com.jwn.blogapp.R;
import com.squareup.picasso.Picasso;

import java.util.Date;
import java.util.List;



public class BlogRecyclerAdapter extends RecyclerView.Adapter<BlogRecyclerAdapter.ViewHolder>{

    private static final String TAG = BlogRecyclerAdapter.class.getSimpleName();

    private Context context;
    private List<Blog> blogList;
    //Constructor
    public BlogRecyclerAdapter(Context context, List<Blog> blogList) {
        this.context = context;
        this.blogList = blogList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_row, parent, false);

        return new ViewHolder(view, context);
    }

    //bind together the views & data
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {



        Blog blog = blogList.get(position);
        String imageUrl = null;

        //Log.d(TAG, "blog value is " + blog);


        //set up the widgets created inside ViewHolder
        holder.title.setText(blog.getTitle());
        holder.desc.setText(blog.getDesc());
        //holder.timestamp.setText(blog.getTimestamp());

        // format the time
        java.text.DateFormat dateFormat = java.text.DateFormat.getDateInstance();
        String formattedDate = dateFormat.format(new Date(Long.valueOf(blog.getTimestamp())).getTime());

        holder.timestamp.setText(formattedDate);

       imageUrl = blog.getImage();

        //Log.d(TAG, "imageUrl value is " + imageUrl);


        //TODO: Use Picasso to load image
        Picasso.get().load(imageUrl).into(holder.image);

    }//end public void onBindViewHolder



    @Override
    public int getItemCount() {

        return blogList.size();
    }




    public class ViewHolder extends RecyclerView.ViewHolder {

        // instantiate the widgets we just created into post_row.xml
        public TextView title, desc, timestamp;
        public ImageView image;
        String userid;

        public ViewHolder(@NonNull View view, Context ctx) {
            super(view);

            context = ctx;
            title =view.findViewById(R.id.postTitleList);
            desc = view.findViewById(R.id.postTextList);
            image = view.findViewById(R.id.postImageList);
            timestamp = view.findViewById(R.id.timestampList);

            userid = null;

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // go to the next activity
                }

            });

        }//end public ViewHolder


    }//end class ViewHolder extends RecyclerView.ViewHolder



}// end class BlogRecyclerAdapter extends RecyclerView.Adapter<BlogRecyclerAdapter.ViewHolder>
