package com.textme.dhermanu.githubstarred;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.picasso.Picasso;
import com.textme.dhermanu.githubstarred.adapters.CollabAdapter;
import com.textme.dhermanu.githubstarred.api.GithubAPI;
import com.textme.dhermanu.githubstarred.models.Collaborator;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment {

    private GithubAPI githubAPI;
    private CollabAdapter collabAdapter;
    private ImageView userImage;
    private RecyclerView rvCollab;

    public DetailActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootview = inflater.inflate(R.layout.fragment_detail, container, false);
        Intent intent  = getActivity().getIntent();
        Bundle args = intent.getExtras();

        Toolbar toolbar = (Toolbar) rootview.findViewById(R.id.toolbar);

        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String name = args.getString(getResources().getString(R.string.EXTRA_DATA));
        String ownerLogin = args.getString(getResources().getString(R.string.EXTRA_OWNER));
        String avatarUrl = args.getString(getResources().getString(R.string.EXTRA_AVATAR));

        userImage = (ImageView) rootview.findViewById(R.id.imageBanner);
        rvCollab = (RecyclerView) rootview.findViewById(R.id.recycle_collab_list);
        rvCollab.setLayoutManager(new LinearLayoutManager(getActivity()));

        CollapsingToolbarLayout collapsingToolbarLayout
                = (CollapsingToolbarLayout) rootview.findViewById(R.id.collapsingtoolbar);

        final CollapsingToolbarLayout templayout = collapsingToolbarLayout;

        //set layout
        collapsingToolbarLayout = templayout;
        collapsingToolbarLayout.setTitle("Banner title");

        //https://api.github.com/repos/googlecreativelab/anypixel/contributors

        Log.v("HELO", ownerLogin);

        updateCollabList(ownerLogin, name);

        Picasso
                .with(getContext())
                .load(avatarUrl)
                .fit()
                .into(userImage);

        return rootview;
    }

    private void updateCollabList(String login, String name){

        final String BASE_URL = "https://api.github.com/repos/";

        Gson gson =  new GsonBuilder().create();
        Retrofit retrofit = new Retrofit.Builder().baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        githubAPI = retrofit.create(GithubAPI.class);
        Call<List<Collaborator>> collaboratorCall = githubAPI.getCollab(login, name);
        collaboratorCall.enqueue(new Callback<List<Collaborator>>() {
            @Override
            public void onResponse(Call<List<Collaborator>> call, Response<List<Collaborator>> response) {
                List<Collaborator> collaborators = response.body();
                collabAdapter = new CollabAdapter(collaborators, getContext());
                rvCollab.setAdapter(collabAdapter);
            }

            @Override
            public void onFailure(Call<List<Collaborator>> call, Throwable t) {

            }
        });
    }
}
