package projects.instagram_codepath;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import projects.instagram_codepath.main_fragments.ComposeFragment;
import projects.instagram_codepath.main_fragments.PostsFragment;
import projects.instagram_codepath.main_fragments.ProfileFragment;

public class MainActivity extends AppCompatActivity
{
    public static final String TAG = "MainActivity";

    private final FragmentManager fm = getSupportFragmentManager();
    private final Fragment home = new PostsFragment();
    private final Fragment compose = new ComposeFragment();
    private final Fragment profile = new ProfileFragment();

    private BottomNavigationView bottom_menu;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottom_menu = findViewById(R.id.MAIN_NAVIGATION);
        final int home_action = R.id.MENU_ACTION_HOME;
        final int compose_action = R.id.MENU_ACTION_COMPOSE;
        final int profile_action = R.id.MENU_ACTION_PROFILE;

        bottom_menu.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener()
        {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item)
            {
                Fragment fragment;

                switch (item.getItemId())
                {
                    case home_action:
                        fragment = home;
                        break;
                    case compose_action:
                        fragment = compose;
                        break;
                    case profile_action:
                        fragment = profile;
                        break;
                    default:
                        throw new IllegalStateException("Unexpected menu item: " + item.getItemId());
                }

                fm.beginTransaction().replace(R.id.MAIN_FRAME_FRAGMENT, fragment).commit();

                return true;
            }
        });

        // Set default selection
        bottom_menu.setSelectedItemId(R.id.MENU_ACTION_HOME);

        //getPosts();
    }
}