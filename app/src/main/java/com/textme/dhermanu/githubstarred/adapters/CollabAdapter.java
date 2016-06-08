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
import com.textme.dhermanu.githubstarred.models.Collaborator;

import java.util.List;

/**
 * Created by dhermanu on 6/6/16.
 */
public class CollabAdapter extends RecyclerView.Adapter<CollabAdapter.ViewHolder> {

    public static class ViewHolder extends RecyclerView.ViewHolder{

        public TextView userName, profileLink;
        public ImageView collabName;
        public ViewHolder(View itemView) {
            super(itemView);
            userName = (TextView) itemView.findViewById(R.id.user_name);
            profileLink = (TextView) itemView.findViewById(R.id.profile_link);
            collabName = (ImageView) itemView.findViewById(R.id.collab_name);
        }
    }

    public List<Collaborator> mCollabs;
    public Context mContext;

    public CollabAdapter(List<Collaborator> collaborators, Context context){
        mCollabs = collaborators;
        mContext = context;
    }

    @Override
    public CollabAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View collabView = inflater.inflate(R.layout.list_item_collab, parent , false);

        //return a new holder instance
        ViewHolder viewHolder = new ViewHolder(collabView);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Collaborator collaborator = mCollabs.get(position);
        TextView userName = holder.userName;
        TextView profileLink = holder.profileLink;
        ImageView collabImage = holder.collabName;

        userName.setText(collaborator.getLogin());
        profileLink.setText(collaborator.getHtmlUrl());
        Picasso
                .with(mContext)
                .load(collaborator.getAvatarUrl())
                .fit()
                .into(collabImage);
    }

    @Override
    public int getItemCount() {
        if(mCollabs != null)
            return mCollabs.size();

        return 0;
    }


}
