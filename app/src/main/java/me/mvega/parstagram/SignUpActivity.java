package me.mvega.parstagram;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class SignUpActivity extends AppCompatActivity {

    private EditText usernameInput;
    private EditText passwordInput;
    private EditText emailInput;
    private EditText handleInput;
    private Button signUpBtn;
    private TextView tvLogin;
    TextInputLayout textInputLayout;

//    AnimationDrawable animationDrawable;
    RelativeLayout relativeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int mainView = R.layout.activity_sign_up; // TODO R.layout.activity_sign_up_colorful for colorful background

        setContentView(mainView);

        relativeLayout = (RelativeLayout) findViewById(R.id.relativeLayout);

//        if (mainView == R.layout.activity_main) {
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

        textInputLayout.setHintEnabled(false);

        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String username = usernameInput.getText().toString();
                final String password = passwordInput.getText().toString();
                final String email = emailInput.getText().toString();
                final String handle = handleInput.getText().toString();

                signUp(username, password, email, handle);
            }
        });

        tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
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

    private void signUp(String username, String password, String email, String handle) {
        // Create the ParseUser
        ParseUser user = new ParseUser();
        // Set core properties
        user.setUsername(username);
        user.setPassword(password);
        user.setEmail(email);
        // Set custom properties
        user.put("handle", handle);
        // Invoke signUpInBackground
        user.signUpInBackground(new SignUpCallback() {
            public void done(ParseException e) {
                if (e == null) {
                    Log.d("SignUpActivity", "Sign Up successful!");
                    final Intent intent = new Intent(SignUpActivity.this, HomeActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Log.e("SignUpActivity", "Sign Up Failure.");
                    e.printStackTrace();
                }
            }
        });
    }
}
