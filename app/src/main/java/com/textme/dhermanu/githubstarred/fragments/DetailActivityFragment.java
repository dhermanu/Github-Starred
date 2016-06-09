package com.textme.dhermanu.githubstarred.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.picasso.Picasso;
import com.textme.dhermanu.githubstarred.activities.MainActivity;
import com.textme.dhermanu.githubstarred.R;
import com.textme.dhermanu.githubstarred.adapters.ContributorAdapter;
import com.textme.dhermanu.githubstarred.api.GithubAPI;
import com.textme.dhermanu.githubstarred.models.Contributor;

import java.util.ArrayList;
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
    private ContributorAdapter contributorAdapter;
    private ImageView userImage;
    private RecyclerView rvContributor;
    private String SAVEDINSTANCE_CONTRIBUTOR = "save_collabs";
    public String NO_INTERNET = "No Internet Connection";

    private ArrayList<Contributor> ContributorListsaved = null;


    public DetailActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootview = inflater.inflate(R.layout.fragment_detail, container, false);

        Intent intent  = getActivity().getIntent();
        Bundle args;

        // checks for tablet
        if(rootview.findViewById(R.id.tablet_layout) != null){
            args = intent.getExtras();
            Toolbar toolbar = (Toolbar) rootview.findViewById(R.id.toolbar);

            ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
            ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        else{
            args = getArguments();
        }

        String name = args.getString(MainActivity.EXTRA_DATA);
        String ownerLogin = args.getString(MainActivity.EXTRA_OWNER);
        String avatarUrl = args.getString(MainActivity.EXTRA_AVATAR);
        String title = name + " Contributors";

        userImage = (ImageView) rootview.findViewById(R.id.imageBanner);
        rvContributor = (RecyclerView) rootview.findViewById(R.id.recycle_collab_list);
        rvContributor.setLayoutManager(new LinearLayoutManager(getActivity()));

        CollapsingToolbarLayout collapsingToolbarLayout
                = (CollapsingToolbarLayout) rootview.findViewById(R.id.collapsingtoolbar);

        collapsingToolbarLayout.setTitle(title);

        if(!checkConnection())
            Toast.makeText(getContext(), NO_INTERNET ,Toast.LENGTH_SHORT).show();

        else{
            if(savedInstanceState != null){
                ContributorListsaved = savedInstanceState.getParcelableArrayList(SAVEDINSTANCE_CONTRIBUTOR);
                if(ContributorListsaved != null){
                    contributorAdapter = new ContributorAdapter(ContributorListsaved, getContext());
                    rvContributor.setAdapter(contributorAdapter);
                }

                else if(!checkConnection())
                    Toast.makeText(getContext(), NO_INTERNET,Toast.LENGTH_SHORT).show();

                else if(checkConnection())
                    updateContributorList(ownerLogin, name);
            }
            else
                updateContributorList(ownerLogin, name);
        }

        Picasso
                .with(getContext())
                .load(avatarUrl)
                .fit()
                .into(userImage);

        return rootview;
    }

    private void updateContributorList(String login, String name){

        Log.v("HELO", login);
        Log.v("HELO", name);

        final String BASE_URL = "https://api.github.com/repos/";

        Toast.makeText(getContext(), "NETWORK OPERTION",  Toast.LENGTH_SHORT).show();

        Gson gson =  new GsonBuilder().create();
        Retrofit retrofit = new Retrofit.Builder().baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        githubAPI = retrofit.create(GithubAPI.class);
        Call<List<Contributor>> collaboratorCall = githubAPI.getCollab(login, name);
        collaboratorCall.enqueue(new Callback<List<Contributor>>() {
            @Override
            public void onResponse(Call<List<Contributor>> call, Response<List<Contributor>> response) {
                List<Contributor> contributors = response.body();
                ContributorListsaved = new ArrayList<>();
                contributorAdapter = new ContributorAdapter(contributors, getContext());
                rvContributor.setAdapter(contributorAdapter);

                for(Contributor contributor : contributors){
                    ContributorListsaved.add(contributor);
                }

            }

            @Override
            public void onFailure(Call<List<Contributor>> call, Throwable t) {

            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(ContributorListsaved != null){
            outState.putParcelableArrayList(SAVEDINSTANCE_CONTRIBUTOR, ContributorListsaved);
        }
    }

    public boolean checkConnection(){
        ConnectivityManager cm =
                (ConnectivityManager)getContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        return isConnected;
    }
}
