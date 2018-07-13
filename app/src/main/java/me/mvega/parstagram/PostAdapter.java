package me.mvega.parstagram;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.parse.ParseFile;
import com.parse.ParseUser;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import me.mvega.parstagram.model.Post;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder>{

    private AdapterListener listener;
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
        holder.tvCaption.setText(Html.fromHtml("<b>" + user.getUsername() + "</b> " + post.getDescription()));
        Date createdAt = post.getTimestamp();
        SimpleDateFormat df = new SimpleDateFormat("MMMM d");
        holder.tvTimestamp.setText(df.format(createdAt));

        Log.d("PostAdapter", Boolean.toString(post != null));
        ParseFile picture = post.getImage();
        if(picture != null) {
            String imageUrl = picture.getUrl();
            Glide.with(context).load(imageUrl).into(holder.ivPost);
        } else holder.ivPost.setImageResource(R.drawable.image_placeholder);

        Log.d("PostAdapter", Boolean.toString(user != null));
        ParseFile profileImage = user.getParseFile("image");
        if(profileImage != null) {
            String imageUrl = profileImage.getUrl();
            Glide.with(context).load(imageUrl).into(holder.ivProfile);
        } else holder.ivProfile.setImageResource(R.drawable.profile_image);
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
        public ImageView ivProfile;

        public ViewHolder(View itemView) {
            super(itemView);

            // perform findViewById lookups
            ivPost = (ImageView) itemView.findViewById(R.id.ivPost);
            tvUsername = (TextView) itemView.findViewById(R.id.tvUsername);
            tvCaption = (TextView) itemView.findViewById(R.id.tvCaption);
            tvTimestamp = (TextView) itemView.findViewById(R.id.tvTimestamp);
            ivProfile = (ImageView) itemView.findViewById(R.id.ivProfile);

            // add this as the itemView's OnClickListener
            itemView.setOnClickListener(this);
        }

        // when the user clicks on a row, show DetailsFragment for the selected post
        @Override
        public void onClick(View v) {
            // gets item position
            int position = getAdapterPosition();
            // make sure the position is valid, i.e. actually exists in the view
            if (position != RecyclerView.NO_POSITION) {
                // get the post at the position, this won't work if the class is static
                Post post = mPosts.get(position);

                Log.d("PostAdapter", "Post for Details fragment obtained.");

                listener.sendPostToHomeFragment(post);
            }
        }
    }

    // set the listener. Must be called from the fragment
    public void setListener(AdapterListener listener) {
        this.listener = listener;
    }

    public interface AdapterListener {
        void sendPostToHomeFragment(Post post);
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
