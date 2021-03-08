package projects.instagram_codepath.main_fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

import projects.instagram_codepath.Post;
import projects.instagram_codepath.PostsAdapter;
import projects.instagram_codepath.R;

public class PostsFragment extends Fragment
{
    public static final String TAG = "PostsFragment";
    public static final int POST_LIMIT = 20;

    protected SwipeRefreshLayout srl_posts;
    protected RecyclerView rv_posts;
    protected PostsAdapter rv_posts_adapter;
    protected List<Post> posts;

    public PostsFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        return inflate_view(inflater, container);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        init_components(view);
        posts = new ArrayList<Post>();
        rv_posts_adapter = new PostsAdapter(getContext(), posts);

        rv_posts.setAdapter(rv_posts_adapter);
        rv_posts.setLayoutManager(new LinearLayoutManager(getContext()));

        srl_posts.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener()
        {
            @Override
            public void onRefresh()
            {
                getPosts();
                srl_posts.setRefreshing(false);
            }
        });

        srl_posts.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        getPosts();
    }

    protected View inflate_view(LayoutInflater i, ViewGroup c)
    {
        return i.inflate(R.layout.fragment_posts, c, false);
    }

    protected void init_components(View v)
    {
        srl_posts = v.findViewById(R.id.TIMELINE_SRC);
        rv_posts = v.findViewById(R.id.POSTS_RV_POSTS);
    }

    protected void clear_posts()
    {
        posts.clear();
        rv_posts_adapter.notifyDataSetChanged();
    }

    protected void populate_posts(List<Post> p)
    {
        posts.addAll(p);
        rv_posts_adapter.notifyDataSetChanged();
    }

    protected void set_query_filter(ParseQuery<Post> q)
    {
        q.setLimit(POST_LIMIT);
        q.addDescendingOrder(Post.KEY_CREATED_AT);
    }

    protected void getPosts()
    {
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.include(Post.KEY_USER);
        set_query_filter(query);

        query.findInBackground(new FindCallback<Post>()
        {
            @Override
            public void done(List<Post> objects, ParseException e)
            {
                if(e != null)
                {
                    Log.e(TAG, "Unable to load posts.", e);
                    Toast.makeText(getContext(), "Unable to load posts due to : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    return;
                }
                else
                {
                    Toast.makeText(getContext(), "Post loading successful.", Toast.LENGTH_SHORT).show();

                    for(Post post : objects)
                    {
                        Log.i(TAG, "Post: " + post.getDescription() + ", username: " + post.getUser().getUsername());
                    }

                    clear_posts();
                    populate_posts(objects);
                }
            }
        });
    }
}