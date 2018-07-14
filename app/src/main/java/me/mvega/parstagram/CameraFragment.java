package me.mvega.parstagram;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;

import me.mvega.parstagram.model.Post;

public class CameraFragment extends Fragment {

    private OnItemSelectedListener listener;

    // The onCreateView method is called when Fragment should create its View object hierarchy,
    // either dynamically or via XML layout inflation.
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        // Defines the xml file for the fragment
        return inflater.inflate(R.layout.fragment_camera, parent, false);
    }

    // This event is triggered soon after onCreateView().
    // Any view setup should occur here.  E.g., view lookups and attaching view listeners.
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // Setup any handles to view objects here
        // EditText etFoo = (EditText) view.findViewById(R.id.etFoo);

        final ImageView ivPreview = (ImageView) view.findViewById(R.id.ivPreview);
        final EditText etCaption = (EditText) view.findViewById(R.id.etCaption);
        Button btnPost = (Button) view.findViewById(R.id.btnPost);

        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String description = etCaption.getText().toString();
                final ParseUser user = ParseUser.getCurrentUser();

                //Convert bitmap to byte array
                Bitmap bitmap = ((BitmapDrawable) ivPreview.getDrawable()).getBitmap();
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
                byte[] bitmapData = bos.toByteArray();

                final Post post = new Post();
                post.setDescription(description);
                post.setImage(new ParseFile("post.jpg", bitmapData));
                post.setUser(user);

                post.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            listener.onPostSelected();
                        } else {
                            e.printStackTrace();
                        }
                    }
                });

            }
        });
    }

    public static CameraFragment newInstance() {
        CameraFragment fragmentCamera = new CameraFragment();
        fragmentCamera.setArguments(new Bundle());
        return fragmentCamera;
    }

    public interface OnItemSelectedListener {
        void onPostSelected();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnItemSelectedListener) {
            listener = (OnItemSelectedListener) context;
        } else {
            throw new ClassCastException(context.toString()
                    + " must implement CameraFragment.OnItemSelectedListener");
        }
    }
}
