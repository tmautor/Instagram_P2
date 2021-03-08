package projects.instagram_codepath;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.parse.ui.widget.ParseImageView;

import java.util.List;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.ViewHolder>
{
    private Context context;
    private List<Post> posts;

    public PostsAdapter(Context c, List<Post> p)
    {
        this.context = c;
        this.posts = p;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View v = LayoutInflater.from(context).inflate(R.layout.layout_post, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position)
    {
        Post p = posts.get(position);
        holder.bind(p);
    }

    @Override
    public int getItemCount()
    {
        return posts.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder
    {
        private TextView post_author;
        private ParseImageView post_image;
        private TextView post_description;

        public ViewHolder(@NonNull View itemView)
        {
            super(itemView);

            post_author = itemView.findViewById(R.id.POST_TV_USERNAME);
            post_image = itemView.findViewById(R.id.POST_IV_IMAGE);
            post_description = itemView.findViewById(R.id.POST_TV_DESCRIPTION);
        }

        public void bind(Post p)
        {
            post_author.setText(p.getUser().getUsername());
            post_description.setText(p.getDescription());

            if(p.getImage() != null)
            {
                post_image.setParseFile(p.getImage());
                post_image.loadInBackground();
            }
        }
    }
}
