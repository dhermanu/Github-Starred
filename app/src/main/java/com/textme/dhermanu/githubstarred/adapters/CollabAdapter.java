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
import com.textme.dhermanu.githubstarred.models.Contributor;

import java.util.List;

/**
 * Created by dhermanu on 6/6/16.
 */
public class CollabAdapter extends RecyclerView.Adapter<CollabAdapter.ViewHolder> {

    public static class ViewHolder extends RecyclerView.ViewHolder{

        public TextView userName, profileLink, commits;
        public ImageView contributorImage;
        public ViewHolder(View itemView) {
            super(itemView);
            userName = (TextView) itemView.findViewById(R.id.user_name);
            profileLink = (TextView) itemView.findViewById(R.id.profile_link);
            commits = (TextView) itemView.findViewById(R.id.commits);
            contributorImage = (ImageView) itemView.findViewById(R.id.contributor_image);
        }
    }

    public List<Contributor> mContributors;
    public Context mContext;

    public CollabAdapter(List<Contributor> contributors, Context context){
        mContributors = contributors;
        mContext = context;
    }

    @Override
    public CollabAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View collabView = inflater.inflate(R.layout.list_item_contributor, parent , false);

        //return a new holder instance
        ViewHolder viewHolder = new ViewHolder(collabView);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Contributor contributor = mContributors.get(position);
        TextView userName = holder.userName;
        TextView profileLink = holder.profileLink;
        ImageView contributorImage = holder.contributorImage;
        TextView commits = holder.commits;

        userName.setText(contributor.getLogin());
        profileLink.setText(contributor.getHtmlUrl());
         commits.setText(Integer.toString(contributor.getContributions()) + " commit(s)");

        Picasso
                .with(mContext)
                .load(contributor.getAvatarUrl())
                .fit()
                .into(contributorImage);
    }

    @Override
    public int getItemCount() {
        if(mContributors != null)
            return mContributors.size();

        return 0;
    }


}
