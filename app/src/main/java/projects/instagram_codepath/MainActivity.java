package projects.instagram_codepath;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.File;
import java.util.List;

public class MainActivity extends AppCompatActivity
{
    public static final String TAG = "MainActivity";
    public final static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1;

    public String photoFileName = "photo.jpg";
    File photoFile;

    ImageView post_picture;
    TextView post_username;
    EditText post_description;
    Button post_take_picture;
    Button post_submit;
    Button post_logout;

    String username;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        post_picture = findViewById(R.id.POST_IV_PICTURE);
        post_username = findViewById(R.id.POST_TV_USERNAME);
        post_description = findViewById(R.id.POST_ET_DESCRIPTION);
        post_take_picture = findViewById(R.id.POST_BUTTON_PICTURE);
        post_submit = findViewById(R.id.POST_BUTTON_SUBMIT);
        post_logout = findViewById(R.id.POST_BUTTON_LOGOUT);

        username = ParseUser.getCurrentUser().getUsername();
        post_username.setText("User: " + username);

        post_take_picture.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                launchCamera();
            }
        });

        post_submit.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String description = post_description.getText().toString();

                if(description.isEmpty())
                {
                    Toast.makeText(MainActivity.this, "Sorry, the description cannot be empty.", Toast.LENGTH_SHORT).show();
                    return;
                }
                else if(photoFile == null || post_picture.getDrawable() == null)
                {
                    Toast.makeText(MainActivity.this, "Sorry, you have not taken a picture for this post.", Toast.LENGTH_SHORT).show();
                    return;
                }
                else
                {
                    ParseUser user = ParseUser.getCurrentUser();

                    savePost(description, user, photoFile);
                }
            }
        });

        post_logout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                ParseUser.logOut();
                goTo_loginActivity();
            }
        });

        //getPosts();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE)
        {
            if (resultCode == RESULT_OK) {
                // by this point we have the camera photo on disk
                Bitmap takenImage = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
                // RESIZE BITMAP, see section below
                Uri takenPhotoUri = Uri.fromFile(getPhotoFileUri(photoFileName));
                // by this point we have the camera photo on disk
                Bitmap rawTakenImage = BitmapFactory.decodeFile(takenPhotoUri.getPath());
                // See BitmapScaler.java: https://gist.github.com/nesquena/3885707fd3773c09f1bb
                Bitmap resizedBitmap = BitmapScaler.scaleToFitHeight(rawTakenImage, 500);
                // Load the taken image into a preview
                post_picture.setImageBitmap(resizedBitmap);
            }
            else
            {
                // Result was a failure
                Toast.makeText(this, "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void goTo_loginActivity()
    {
        Intent i = new Intent(this, LoginActivity.class);
        startActivity(i);
        finish();
    }

    private File getPhotoFileUri(String fileName)
    {
        // Get safe storage directory for photos
        // Use `getExternalFilesDir` on Context to access package-specific directories.
        // This way, we don't need to request external read/write runtime permissions.
        File mediaStorageDir = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), TAG);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs())
        {
            Log.d(TAG, "failed to create directory");
        }

        // Return the file target for the photo based on filename
        return new File(mediaStorageDir.getPath() + File.separator + fileName);
    }

    private void launchCamera()
    {
        // create Intent to take a picture and return control to the calling application
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Create a File reference for future access
        photoFile = getPhotoFileUri(photoFileName);

        // wrap File object into a content provider
        // required for API >= 24
        // See https://guides.codepath.com/android/Sharing-Content-with-Intents#sharing-files-with-api-24-or-higher
        Uri fileProvider = FileProvider.getUriForFile(MainActivity.this, "com.codepath.fileprovider", photoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

        // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
        // So as long as the result is not null, it's safe to use the intent.
        if (intent.resolveActivity(getPackageManager()) != null)
        {
            // Start the image capture intent to take photo
            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }
    }

    private void savePost(String d, ParseUser u, File photoFile)
    {
        Post p = new Post();

        p.setDescription(d);
        p.setUser(u);
        p.setImage(new ParseFile(photoFile));

        p.saveInBackground(new SaveCallback()
        {
            @Override
            public void done(ParseException e)
            {
                if(e != null)
                {
                    Log.e(TAG, "Unable to save new post.", e);
                    Toast.makeText(MainActivity.this, "Unable save new post due to : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    return;
                }
                else
                {
                    Toast.makeText(MainActivity.this, "Post saved successfully.", Toast.LENGTH_SHORT).show();
                    post_description.setText("");
                    post_picture.setImageResource(0);
                }
            }
        });
    }

    private void getPosts()
    {
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.include(Post.KEY_USER);

        query.findInBackground(new FindCallback<Post>()
        {
            @Override
            public void done(List<Post> objects, ParseException e)
            {
                if(e != null)
                {
                    Log.e(TAG, "Unable to load posts.", e);
                    Toast.makeText(MainActivity.this, "Unable to load posts due to : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    return;
                }
                else
                {
                    Toast.makeText(MainActivity.this, "Post loading successful.", Toast.LENGTH_SHORT).show();

                    for(Post post : objects)
                    {
                        Log.i(TAG, "Post: " + post.getDescription() + ", username: " + post.getUser().getUsername());
                    }
                }
            }
        });
    }
}