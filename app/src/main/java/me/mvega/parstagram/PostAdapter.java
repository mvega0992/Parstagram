package me.mvega.parstagram;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.parse.ParseFile;
import com.parse.ParseUser;

import java.util.List;

import me.mvega.parstagram.model.Post;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder>{

    private List<Post> mPosts;
    Context context;

    // pass in the Posts array in the constructor
    public PostAdapter(List<Post> posts) {
        mPosts = posts;
    }

    // for each row, inflate the layout and cache references into ViewHolder
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View postView = inflater.inflate(R.layout.item_post, parent, false);
        ViewHolder viewHolder = new ViewHolder(postView);
        return viewHolder;
    }

    // bind the values on the position of the element
    public void onBindViewHolder(ViewHolder holder, int position) {
        // get the data according to position
        Post post = mPosts.get(position);
        ParseUser user = post.getUser();

        // populate the view according to this data
        holder.tvUsername.setText(user.getUsername());
        holder.tvCaption.setText(post.getDescription());
        holder.tvTimestamp.setText(post.getTimestamp().toString());
        post.getImage();

        ParseFile thumbnail = post.getParseFile("image");
        if(thumbnail != null) {
            String imageUrl = thumbnail.getUrl();
            Glide.with(context).load(imageUrl).into(holder.ivPost);
        } else holder.ivPost.setImageResource(R.drawable.image_placeholder);
    }

    @Override
    public int getItemCount() {
        return mPosts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
//        public ImageView ivProfileImage;
        public ImageView ivPost;
        public TextView tvUsername;
        public TextView tvCaption;
        public TextView tvTimestamp;

        public ViewHolder(View itemView) {
            super(itemView);

            // perform findViewById lookups
            ivPost = (ImageView) itemView.findViewById(R.id.ivPost);
            tvUsername = (TextView) itemView.findViewById(R.id.tvUsername);
            tvCaption = (TextView) itemView.findViewById(R.id.tvCaption);
            tvTimestamp = (TextView) itemView.findViewById(R.id.tvTimestamp);

            // add this as the itemView's OnClickListener
            itemView.setOnClickListener(this);
        }

        // when the user clicks on a row, show DetailsActivity for the selected post
        @Override
        public void onClick(View v) {
            // gets item position
            int position = getAdapterPosition();
            // make sure the position is valid, i.e. actually exists in the view
            if (position != RecyclerView.NO_POSITION) {
                // get the movie at the position, this won't work if the class is static
                Post post = mPosts.get(position);

                // TODO Post Details Fragment
//                // create intent for the new activity
//                Intent intent = new Intent(context, DetailsActivity.class);
//                // serialize the movie using parceler, use its short name as a key
//                intent.putExtra(Post.class.getSimpleName(), Parcels.wrap(post));
//                // show the activity
//                context.startActivity(intent);
            }
        }
    }

    // Clean all elements of the recycler
    public void clear() {
        mPosts.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<Post> list) {
        mPosts.addAll(list);
        notifyDataSetChanged();
    }
}
