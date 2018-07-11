package me.mvega.parstagram;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.List;

import me.mvega.parstagram.model.Post;

public class MainActivity extends AppCompatActivity {

    private static final String imagePath = "/sdcard/DCIM/Camera/IMG_20180708_151117.jpg";
    private ImageButton homeButton;
    private ImageButton captureButton;
    private ImageButton profileButton;

    private HomeFragment home;
    private CameraFragment camera;
    private ProfileFragment profile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        if (savedInstanceState == null) {
//            home = HomeFragment.newInstance("foo");
//            camera = CameraFragment.newInstance("bar");
//            profile = ProfileFragment.newInstance("baz");
//        }

        homeButton = (ImageButton) findViewById(R.id.button_home);
        captureButton = (ImageButton) findViewById(R.id.button_camera);
        profileButton = (ImageButton) findViewById(R.id.button_profile);

        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logOut();
            }
        });

        captureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                capture();
            }
        });

        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showHome();
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

    public void logOut() {
        ParseUser.logOut();
        Log.d("MainActivity", "Log out successful!");
        startActivity(new Intent(MainActivity.this, LoginActivity.class));
        finish();
    }

    public void capture() {
        Log.d("MainActivity", "Click on Capture button recorded!");
        // TODO
    }

    public void showHome() {
        Log.d("MainActivity", "Click on Home button recorded!");
        // TODO
    }
}
