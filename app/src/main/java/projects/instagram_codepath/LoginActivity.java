package projects.instagram_codepath;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class LoginActivity extends AppCompatActivity
{
    public static final String TAG = "LoginActivity";

    ImageView login_logo;
    EditText login_username;
    EditText login_password;
    Button login_submit;
    Button login_signup;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if(ParseUser.getCurrentUser() != null)
        {
            goTo_mainActivity();
        }

        login_logo = findViewById(R.id.LOGIN_IV_LOGO);
        login_username = findViewById(R.id.LOGIN_ET_USERNAME);
        login_password = findViewById(R.id.LOGIN_ET_PASSWORD);
        login_submit = findViewById(R.id.LOGIN_BUTTON_SUBMIT);
        login_signup = findViewById(R.id.LOGIN_BUTTON_SIGNUP);

        login_submit.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String username = login_username.getText().toString();
                String password = login_password.getText().toString();

                login_attempt(username, password);
            }
        });

        login_signup.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String username = login_username.getText().toString();
                String password = login_password.getText().toString();

                signup_attempt(username, password);
            }
        });
    }

    private void signup_attempt(String u, String p)
    {
        ParseUser user = new ParseUser();
        // Set core properties
        user.setUsername(u);
        user.setPassword(p);
        user.setEmail(u + "@gmail.com");
        // Invoke signUpInBackground
        user.signUpInBackground(new SignUpCallback() {
            public void done(ParseException e) {
                if(e != null)
                {
                    Log.e(TAG, "Unable create user.", e);
                    Toast.makeText(LoginActivity.this, "Unable to sign up due to: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    return;
                }
                else
                {
                    Toast.makeText(LoginActivity.this, "Sign up successful.", Toast.LENGTH_SHORT).show();
                    login_attempt(u, p);
                }
            }
        });
    }

    private void login_attempt(String u, String p)
    {
        if(u.isEmpty())
        {
            Toast.makeText(LoginActivity.this, "Sorry, the username field cannot be empty.", Toast.LENGTH_SHORT).show();
        }
        else if(p.isEmpty())
        {
            Toast.makeText(LoginActivity.this, "Sorry, the password field cannot be empty.", Toast.LENGTH_SHORT).show();
        }
        else
        {
            Log.i(TAG, "Login attempt with: " + u + ", " + p);
            ParseUser.logInInBackground(u, p, new LogInCallback()
            {
                @Override
                public void done(ParseUser user, ParseException e)
                {
                    if(e != null)
                    {
                        Log.e(TAG, "Unable log in user.", e);
                        Toast.makeText(LoginActivity.this, "Login failed due to: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    else
                    {
                        Toast.makeText(LoginActivity.this, "Login successful.", Toast.LENGTH_SHORT).show();
                        goTo_mainActivity();
                    }
                }
            });
        }
    }

    private void goTo_mainActivity()
    {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        finish();
    }
}