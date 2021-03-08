package projects.instagram_codepath.main_fragments;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.parse.ParseQuery;
import com.parse.ParseUser;

import projects.instagram_codepath.LoginActivity;
import projects.instagram_codepath.Post;
import projects.instagram_codepath.R;

public class ProfileFragment extends PostsFragment
{
    private TextView tv_username;
    private TextView tv_email;
    private Button button_logout;

    @Override
    protected View inflate_view(LayoutInflater i, ViewGroup c)
    {
        return i.inflate(R.layout.fragment_profile, c, false);
    }

    @Override
    protected void init_components(View v)
    {
        srl_posts = v.findViewById(R.id.PROFILE_SRC);
        rv_posts = v.findViewById(R.id.PROFILE_RV_POSTS);
        tv_username = v.findViewById(R.id.PROFILE_TV_USERNAME);
        tv_email = v.findViewById(R.id.PROFILE_TV_EMAIL);
        button_logout = v.findViewById(R.id.PROFILE_BUTTON_LOGOUT);

        tv_username.setText("User: " + ParseUser.getCurrentUser().getUsername());
        tv_email.setText("Email: " + ParseUser.getCurrentUser().getEmail());

        button_logout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                ParseUser.logOut();
                goTo_loginActivity();
            }
        });
    }

    @Override
    protected void set_query_filter(ParseQuery<Post> q)
    {
        q.setLimit(POST_LIMIT);
        q.whereEqualTo(Post.KEY_USER, ParseUser.getCurrentUser());
        q.addDescendingOrder(Post.KEY_CREATED_AT);
    }

    public void goTo_loginActivity()
    {
        Intent i = new Intent(getContext(), LoginActivity.class);
        startActivity(i);
        getActivity().finish();
    }
}
