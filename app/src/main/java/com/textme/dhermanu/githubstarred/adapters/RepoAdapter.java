package com.textme.dhermanu.githubstarred.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.textme.dhermanu.githubstarred.R;
import com.textme.dhermanu.githubstarred.models.Owner;
import com.textme.dhermanu.githubstarred.models.Repo;

import java.util.List;

/**
 * Created by dhermanu on 6/5/16.
 */
public class RepoAdapter extends RecyclerView.Adapter<RepoAdapter.ViewHolder>{

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public TextView repoName, userName, description, lang, type;
        public ImageView repoImage;
        public ViewHolder(View itemView) {
            super(itemView);
            repoName = (TextView) itemView.findViewById(R.id.repo_name);
            userName = (TextView) itemView.findViewById(R.id.user_name);
            description = (TextView) itemView.findViewById(R.id.description);
            lang = (TextView) itemView.findViewById(R.id.language);
            type = (TextView) itemView.findViewById(R.id.type);

            repoImage = (ImageView) itemView.findViewById(R.id.repoImage);

        }

        @Override
        public void onClick(View view) {

        }
    }

    public List<Repo> mRepos;
    public Context mContext;

    public RepoAdapter(List<Repo> repos, Context context){
        mRepos = repos;
        mContext = context;
    }

    @Override
    public RepoAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        //inflate the costum layout
        View repoView = inflater.inflate(R.layout.list_item_repo, parent , false);

        //return a new holder instance
        ViewHolder viewHolder = new ViewHolder(repoView);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RepoAdapter.ViewHolder holder, int position) {
        Repo repo = mRepos.get(position);
        TextView repoName = holder.repoName;
        TextView userName = holder.userName;
        TextView description = holder.description;
        TextView lang = holder.lang;
        TextView type = holder.type;
        ImageView repoImage = holder.repoImage;
        //Context context = ViewHolder.getContext();

        Owner owner = repo.getOwner();

        repoName.setText(repo.getName());
        userName.setText(owner.getLogin());
        description.setText(repo.getDescription());
        lang.setText(repo.getLanguage());
        type.setText(owner.getType());

        Picasso
                .with(mContext)
                .load(owner.getAvatarUrl())
                .fit()
                .into(repoImage);


    }

    @Override
    public int getItemCount() {
        if(mRepos != null)
            return mRepos.size();

        return 0;
    }
}
