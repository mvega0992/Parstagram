package me.mvega.parstagram;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseObject;

import me.mvega.parstagram.model.Post;

public class ParseApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        ParseObject.registerSubclass(Post.class);

        final Parse.Configuration configuration = new Parse.Configuration.Builder(this)
                .applicationId("crystal-geyser")
                .clientKey("bottled-at-the-source")
                .server("http://mvega0992-fbu-instagram.herokuapp.com/parse")
                .build();

        Parse.initialize(configuration);
    }
}
