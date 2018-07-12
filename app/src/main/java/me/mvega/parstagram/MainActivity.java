package me.mvega.parstagram;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.File;
import java.util.List;

import me.mvega.parstagram.model.Post;

public class MainActivity extends AppCompatActivity implements ProfileFragment.OnItemSelectedListener {

    private static final String imagePath = "/sdcard/DCIM/Camera/IMG_20180708_151117.jpg";

    private HomeFragment home = HomeFragment.newInstance();
    private CameraFragment camera = CameraFragment.newInstance();
    private ProfileFragment profile = ProfileFragment.newInstance();
    final FragmentManager fragmentManager = getSupportFragmentManager();
    FragmentTransaction fragmentTransaction;
    FrameLayout frameLayout;

    public final String APP_TAG = "Parstagram";
    public final static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1034;
    public String photoFileName = "photo.jpg";
    File photoFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        frameLayout = (FrameLayout) findViewById(R.id.frameLayout);


        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.toolbar);
        // handle navigation selection
        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.button_home:
                                showHome();
                                return true;
                            case R.id.button_camera:
                                showCamera();
                                return true;
                            case R.id.button_profile:
                                showProfile();
                                return true;
                        }
                        return false;
                    }
                });


//        createButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                final String description = descriptionInput.getText().toString();
//                final ParseUser user = ParseUser.getCurrentUser();
//
//                final File file = new File(imagePath);
//                final ParseFile parseFile = new ParseFile(file);
//                parseFile.saveInBackground(new SaveCallback() {
//                    @Override
//                    public void done(ParseException e) {
//                        if (e == null) {
//                            createPost(description, parseFile, user);
//                        }
//                        else {
//                            e.printStackTrace();
//                        }
//                    }
//                });
//            }
//        });
//
//        refreshButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                loadTopPosts();
//            }
//        });


    }

    private void createPost(String description, ParseFile imageFile, ParseUser user) {
        final Post post = new Post();
        post.setDescription(description);
        post.setImage(imageFile);
        post.setUser(user);

        post.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Log.d("MainActivity", "Create post success!");
                } else {
                    Log.e("MainActivity", "Create post FAILED!");
                    e.printStackTrace();
                }
            }
        });
    }

    private void loadTopPosts() {
        final Post.Query postQuery = new Post.Query();
        postQuery.getTop().withUser();

        postQuery.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> objects, ParseException e) {
                if (e == null) {
                    for (int i = 0; i < objects.size(); ++i) {
                        Log.d("MainActivity", "Post[" + i + "] = "
                                + objects.get(i).getDescription()
                                + "\nusername = " + objects.get(i).getUser().getUsername());
                    }
                } else {
                    e.printStackTrace();
                }
            }
        });
    }

    public void showProfile() {
        Log.d("MainActivity", "Click on Profile button recorded!");
        profile = ProfileFragment.newInstance();
        replaceFragment(profile);
    }

    public void showCamera() {
        Log.d("MainActivity", "Click on Camera button recorded!");
        camera = CameraFragment.newInstance();
        replaceFragment(camera);

        // create Intent to take a picture and return control to the calling application
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Create a File reference to access to future access
        photoFile = getPhotoFileUri(photoFileName);

        // wrap File object into a content provider
        // required for API >= 24
        // See https://guides.codepath.com/android/Sharing-Content-with-Intents#sharing-files-with-api-24-or-higher
        Uri fileProvider = FileProvider.getUriForFile(MainActivity.this, "com.codepath.fileprovider", photoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

        // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
        // So as long as the result is not null, it's safe to use the intent.
        if (intent.resolveActivity(getPackageManager()) != null) {
            // Start the image capture intent to take photo
            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }
    }

    public void showHome() {
        Log.d("MainActivity", "Click on Home button recorded!");
        home = HomeFragment.newInstance();
        replaceFragment(home);
    }

    // Returns the File for a photo stored on disk given the fileName
    public File getPhotoFileUri(String fileName) {
        // Get safe storage directory for photos
        // Use `getExternalFilesDir` on Context to access package-specific directories.
        // This way, we don't need to request external read/write runtime permissions.
        File mediaStorageDir = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), APP_TAG);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
            Log.d(APP_TAG, "failed to create directory");
        }

        // Return the file target for the photo based on filename
        File file = new File(mediaStorageDir.getPath() + File.separator + fileName);

        return file;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // by this point we have the camera photo on disk
                Bitmap takenImage = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
                Bitmap resizedBitmap = BitmapScaler.scaleToFitWidth(takenImage, 400);
                // Load the taken image into a preview
                ImageView ivPreview = (ImageView) findViewById(R.id.ivPreview);
                ivPreview.setImageBitmap(resizedBitmap);
            } else { // Result was a failure
                Toast.makeText(this, "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void replaceFragment(Fragment f) {
        // Begin the transaction
        fragmentTransaction = fragmentManager.beginTransaction();
        // Replace the contents of the container with the new fragment and complete the changes added above
        fragmentTransaction.replace(R.id.frameLayout, f).commit();
    }

    @Override
    public void onLogOutSelected() {
        startActivity(new Intent(MainActivity.this, LoginActivity.class));
        finish();
    }
}