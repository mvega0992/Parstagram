package me.mvega.parstagram;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.parse.ParseFile;
import com.parse.ParseUser;

import java.text.SimpleDateFormat;
import java.util.Date;

import me.mvega.parstagram.model.Post;

public class DetailsFragment extends Fragment {

    private OnItemSelectedListener listener;
    private ImageButton btnBack;
    private ImageView ivPost;
    private TextView tvCaption;
    private TextView tvUsername;
    private TextView tvTimestamp;
    private ImageView ivProfile;
    public Post post;

    // The onCreateView method is called when Fragment should create its View object hierarchy,
    // either dynamically or via XML layout inflation.
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        // Defines the xml file for the fragment
        return inflater.inflate(R.layout.fragment_details, parent, false);
    }

    // This event is triggered soon after onCreateView().
    // Any view setup should occur here.  E.g., view lookups and attaching view listeners.
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // Setup any handles to view objects here
        // EditText etFoo = (EditText) view.findViewById(R.id.etFoo);
        ivPost = (ImageView) view.findViewById(R.id.ivPost);
        tvCaption = (TextView) view.findViewById(R.id.tvCaption);
        tvUsername = (TextView) view.findViewById(R.id.tvUsername);
        tvTimestamp = (TextView) view.findViewById(R.id.tvTimestamp);
        btnBack = (ImageButton) view.findViewById(R.id.btnBack);
        ivProfile = (ImageView) view.findViewById(R.id.ivProfile);

        ParseUser user = post.getUser();

        // populate the view according to this data
        tvUsername.setText(user.getUsername());
        tvCaption.setText(Html.fromHtml("<b>" + user.getUsername() + "</b> " + post.getDescription()));
        Date createdAt = post.getTimestamp();
        SimpleDateFormat df = new SimpleDateFormat("MMMM d");
        tvTimestamp.setText(createdAt.toString());

        ParseFile picture = post.getImage();
        if (picture != null) {
            String imageUrl = picture.getUrl();
            Glide.with(getContext()).load(imageUrl).into(ivPost);
        } else ivPost.setImageResource(R.drawable.image_placeholder);

        ParseFile profileImage = user.getParseFile("image");
        if (profileImage != null) {
            String imageUrl = profileImage.getUrl();
            Glide.with(getContext()).load(imageUrl).into(ivProfile);
        } else ivProfile.setImageResource(R.drawable.profile_image);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onBackSelected();
            }
        });
    }

    public static DetailsFragment newInstance() {
        DetailsFragment fragmentDetails = new DetailsFragment();
        Bundle args = new Bundle();
        fragmentDetails.setArguments(args);
        return fragmentDetails;
    }

    public interface OnItemSelectedListener {
        void onBackSelected();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnItemSelectedListener) {
            listener = (OnItemSelectedListener) context;
        } else {
            throw new ClassCastException(context.toString()
                    + " must implement DetailsFragment.OnItemSelectedListener");
        }
    }
}
