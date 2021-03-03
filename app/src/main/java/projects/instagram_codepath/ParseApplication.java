package projects.instagram_codepath;

import android.app.Application;
import com.parse.Parse;
import com.parse.ParseObject;

public class ParseApplication extends Application
{
    // Initializes Parse SDK as soon as the application is created
    @Override
    public void onCreate() {
        super.onCreate();

        ParseObject.registerSubclass(Post.class);

        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("FDf80Li39xkh1YvrqpXOSTZASg6jfBXcHQsobm0z")
                .clientKey("sCHBhxmHr2lhMHtSpQ2nUiMjJzRwdSaJbpUBS3gg")
                .server("https://parseapi.back4app.com")
                .build()
        );
    }
}
