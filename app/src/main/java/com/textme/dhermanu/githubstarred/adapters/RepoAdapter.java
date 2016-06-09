package com.textme.dhermanu.githubstarred.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.textme.dhermanu.githubstarred.DetailActivity;
import com.textme.dhermanu.githubstarred.R;
import com.textme.dhermanu.githubstarred.api.ItemClickListener;
import com.textme.dhermanu.githubstarred.models.Owner;
import com.textme.dhermanu.githubstarred.models.Repo;

import java.util.List;

/**
 * Created by dhermanu on 6/5/16.
 */
public class RepoAdapter extends RecyclerView.Adapter<RepoAdapter.ViewHolder>{

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public TextView repoName, description, lang, stargazers;
        public ImageView repoImage;
        private ItemClickListener itemClickListener;
        public ViewHolder(View itemView) {
            super(itemView);
            repoName = (TextView) itemView.findViewById(R.id.repo_name);
            description = (TextView) itemView.findViewById(R.id.description);
            lang = (TextView) itemView.findViewById(R.id.language);
            stargazers = (TextView) itemView.findViewById(R.id.starred);
            repoImage = (ImageView) itemView.findViewById(R.id.repoImage);
            itemView.setOnClickListener(this);
        }

        public void setClickListener(ItemClickListener itemClickListener){
            this.itemClickListener = itemClickListener;
        }
        @Override
        public void onClick(View view) {
            itemClickListener.onClick(view, getPosition(), false);
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
        final Repo repo = mRepos.get(position);
        TextView repoName = holder.repoName;
        TextView description = holder.description;
        TextView lang = holder.lang;
        TextView stargazers = holder.stargazers;
        ImageView repoImage = holder.repoImage;

        final Owner owner = repo.getOwner();

        repoName.setText(repo.getFullName());
        description.setText(repo.getDescription());
        stargazers.setText(Integer.toString(repo.getStargazersCount()));

        if(repo.getLanguage() != null){
            lang.setText("Language: " + repo.getLanguage());
        }

        else
            lang.setText("Language: " + "Not specified");

        Picasso
                .with(mContext)
                .load(owner.getAvatarUrl())
                .fit()
                .into(repoImage);

        holder.setClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position, boolean isLongClick) {
                Intent intent = new Intent(mContext, DetailActivity.class);
                Bundle extras = new Bundle();

                extras.putString
                        (mContext.getResources().getString(R.string.EXTRA_DATA), repo.getName());
                extras.putString
                        (mContext.getResources().getString(R.string.EXTRA_OWNER), owner.getLogin());
                extras.putString
                        (mContext.getResources().getString(R.string.EXTRA_AVATAR), owner.getAvatarUrl());

                intent.putExtras(extras);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        if(mRepos != null)
            return mRepos.size();

        return 0;
    }
}
