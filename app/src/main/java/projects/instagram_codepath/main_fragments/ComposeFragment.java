package projects.instagram_codepath.main_fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import projects.instagram_codepath.BitmapScaler;
import projects.instagram_codepath.LoginActivity;
import projects.instagram_codepath.Post;
import projects.instagram_codepath.R;

import static android.app.Activity.RESULT_OK;

public class ComposeFragment extends Fragment
{
    public static final String TAG = "ComposeFragment";
    public static final String PHOTO_FILENAME = "photo.jpg";
    public final static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1;

    private ImageView post_picture;
    private TextView post_username;
    private EditText post_description;
    private Button post_take_picture;
    private Button post_submit;

    private File photoFile;

    public ComposeFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_compose, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        post_picture = view.findViewById(R.id.POST_IV_PICTURE);
        post_username = view.findViewById(R.id.POST_TV_USERNAME);
        post_description = view.findViewById(R.id.POST_ET_DESCRIPTION);
        post_take_picture = view.findViewById(R.id.POST_BUTTON_PICTURE);
        post_submit = view.findViewById(R.id.POST_BUTTON_SUBMIT);

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
                    Toast.makeText(getContext(), "Sorry, the description cannot be empty.", Toast.LENGTH_SHORT).show();
                    return;
                }
                else if(photoFile == null || post_picture.getDrawable() == null)
                {
                    Toast.makeText(getContext(), "Sorry, you have not taken a picture for this post.", Toast.LENGTH_SHORT).show();
                    return;
                }
                else
                {
                    ParseUser user = ParseUser.getCurrentUser();

                    savePost(description, user, photoFile);
                }
            }
        });
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
                Uri takenPhotoUri = Uri.fromFile(getPhotoFileUri(PHOTO_FILENAME));
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
                Toast.makeText(getContext(), "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private File getPhotoFileUri(String fileName)
    {
        // Get safe storage directory for photos
        // Use `getExternalFilesDir` on Context to access package-specific directories.
        // This way, we don't need to request external read/write runtime permissions.
        File mediaStorageDir = new File(getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), TAG);

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
        photoFile = getPhotoFileUri(PHOTO_FILENAME);

        // wrap File object into a content provider
        // required for API >= 24
        // See https://guides.codepath.com/android/Sharing-Content-with-Intents#sharing-files-with-api-24-or-higher
        Uri fileProvider = FileProvider.getUriForFile(getContext(), "com.codepath.fileprovider", photoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

        // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
        // So as long as the result is not null, it's safe to use the intent.
        if (intent.resolveActivity(getContext().getPackageManager()) != null)
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
                    Toast.makeText(getContext(), "Unable save new post due to : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    return;
                }
                else
                {
                    Toast.makeText(getContext(), "Post saved successfully.", Toast.LENGTH_SHORT).show();
                    post_description.setText("");
                    post_picture.setImageResource(0);
                }
            }
        });
    }
}