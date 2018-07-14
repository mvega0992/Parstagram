package me.mvega.parstagram;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class SignUpActivity extends AppCompatActivity {

    private EditText usernameInput;
    private EditText passwordInput;
    private EditText emailInput;
    private EditText handleInput;
    private Button signUpBtn;
    private TextView tvLogin;
    private ImageView ivProfile;
    public ParseFile file;
    TextInputLayout textInputLayout;

    AnimationDrawable animationDrawable;
    RelativeLayout relativeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int mainView = R.layout.activity_sign_up; // TODO R.layout.activity_sign_up_colorful for colorful background
        setContentView(mainView);

        relativeLayout = (RelativeLayout) findViewById(R.id.relativeLayout);

//        if (mainView == R.layout.activity_sign_up_colorful) {
//            animationDrawable = (AnimationDrawable) relativeLayout.getBackground();
//            animationDrawable.setEnterFadeDuration(2000);
//            animationDrawable.setExitFadeDuration(5000);
//        }

        usernameInput = findViewById(R.id.username_et);
        passwordInput = findViewById(R.id.password_et);
        emailInput = findViewById(R.id.email_et);
        handleInput = findViewById(R.id.handle_et);

        signUpBtn = findViewById(R.id.sign_up_btn);
        textInputLayout = findViewById(R.id.password_input_layout);
        tvLogin = findViewById(R.id.tvLogin);
        ivProfile = findViewById(R.id.ivProfile);

        ivProfile.setImageResource(R.drawable.profile_image);

        textInputLayout.setHintEnabled(false);

        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String username = usernameInput.getText().toString();
                final String password = passwordInput.getText().toString();
                final String email = emailInput.getText().toString();
                final String handle = handleInput.getText().toString();

                signUp(username, password, email, handle, file);
            }
        });

        tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        ivProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadPicture();
            }
        });
    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//        if (animationDrawable != null && !animationDrawable.isRunning()) {
//            animationDrawable.start();
//        }
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        if (animationDrawable != null && !animationDrawable.isRunning()) {
//            animationDrawable.stop();
//        }
//    }

    private void signUp(String username, String password, String email, String handle, ParseFile image) {
        // Create the ParseUser
        ParseUser user = new ParseUser();
        // Set core properties
        user.setUsername(username);
        user.setPassword(password);
        user.setEmail(email);
        // Set custom properties
        user.put("handle", handle);
        user.put("image", image);
        // Invoke signUpInBackground
        user.signUpInBackground(new SignUpCallback() {
            public void done(ParseException e) {
                if (e == null) {
                    Log.d("SignUpActivity", "Sign Up successful!");
                    final Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Log.e("SignUpActivity", "Sign Up Failure.");
                    Toast.makeText(SignUpActivity.this, "Sign up failed.", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }
        });
    }

    public static final int PICK_IMAGE = 1;

    public void loadPicture() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
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
                file = new ParseFile("profile.jpg", bitmapData);

                try {
                    bos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                file.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            Log.d("SignUpActivity", "Profile image save successful!");
                        } else {
                            Log.e("SignUpActivity", "Profile image save failed.");
                            e.printStackTrace();
                        }
                    }
                });

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

}
