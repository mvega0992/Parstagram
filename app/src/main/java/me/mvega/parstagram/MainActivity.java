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
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import me.mvega.parstagram.model.Post;

public class MainActivity extends AppCompatActivity
        implements ProfileFragment.OnItemSelectedListener,
        CameraFragment.OnItemSelectedListener,
        HomeFragment.HomeFragmentListener,
        DetailsFragment.OnItemSelectedListener {

    private HomeFragment home = HomeFragment.newInstance();
    private CameraFragment camera = CameraFragment.newInstance();
    private ProfileFragment profile = ProfileFragment.newInstance();
    private DetailsFragment details = DetailsFragment.newInstance();

    final FragmentManager fragmentManager = getSupportFragmentManager();
    BottomNavigationView bottomNavigationView;
    FragmentTransaction fragmentTransaction;
    FrameLayout frameLayout;

    ParseUser user = ParseUser.getCurrentUser();

    public final String APP_TAG = "Parstagram";
    public final static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1034;
    public String photoFileName = "photo.jpg";
    File photoFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        frameLayout = (FrameLayout) findViewById(R.id.frameLayout);

        showHome();

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.toolbar);
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
    }

    public void showProfile() {
        profile = ProfileFragment.newInstance();
        replaceFragment(profile);
    }

    public void showCamera() {
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
        home = HomeFragment.newInstance();
        replaceFragment(home);
    }

    // Returns the File for a photo stored on disk given the fileName
    public File getPhotoFileUri(String fileName) {
        // Get safe storage directory for photos
        // Use `getExternalFilesDir` on Context to access package-specific directories.
        // This way, we don't need to request external read/write runtime permissions.
        File mediaStorageDir = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), APP_TAG);

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
        if (requestCode == PICK_IMAGE) {
            try {
                InputStream inputStream = this.getContentResolver().openInputStream(data.getData());
                Bitmap takenImage = BitmapFactory.decodeStream(inputStream);
                Bitmap resizedBitmap = BitmapScaler.scaleToFitWidth(takenImage, 400);
                // Load the taken image into a preview
                ImageView ivProfile = (ImageView) findViewById(R.id.ivProfile);
                ivProfile.setImageBitmap(resizedBitmap);

                //Convert bitmap to byte array
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
                byte[] bitmapData = bos.toByteArray();
                final ParseFile parseFile = new ParseFile("profile.jpg", bitmapData);

                try {
                    bos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                parseFile.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            user.put("image", parseFile);
                            user.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if (e != null) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        } else {
                            e.printStackTrace();
                        }
                    }
                });
            } catch (FileNotFoundException e) {
                e.printStackTrace();
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

    @Override
    public void onPostSelected() {
        showHome();
        bottomNavigationView.setSelectedItemId(R.id.button_home);
    }

    public void sendPostToMainActivity(Post post) {
        details = DetailsFragment.newInstance();
        details.post = post;
        replaceFragment(details);
    }

    public void onBackSelected() {
        replaceFragment(home);
    }

    public static final int PICK_IMAGE = 1;

    public void loadPicture() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
    }
}