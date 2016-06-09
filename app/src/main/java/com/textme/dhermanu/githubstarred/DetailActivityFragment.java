package com.textme.dhermanu.githubstarred;

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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.picasso.Picasso;
import com.textme.dhermanu.githubstarred.adapters.CollabAdapter;
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
    private CollabAdapter contributorAdapter;
    private ImageView userImage;
    private RecyclerView rvContributor;
    private String SAVEDINSTANCE_CONTRIBUTOR = "save_collabs";

    private ArrayList<Contributor> ContributorListsaved = null;


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
        rvContributor = (RecyclerView) rootview.findViewById(R.id.recycle_collab_list);
        rvContributor.setLayoutManager(new LinearLayoutManager(getActivity()));

        CollapsingToolbarLayout collapsingToolbarLayout
                = (CollapsingToolbarLayout) rootview.findViewById(R.id.collapsingtoolbar);

        final CollapsingToolbarLayout templayout = collapsingToolbarLayout;

        //set layout
        collapsingToolbarLayout = templayout;
        collapsingToolbarLayout.setTitle(name + " Contributors");

        //https://api.github.com/repos/googlecreativelab/anypixel/contributors

        if(!checkConnection())
            Toast.makeText(getContext(), "No Internet Connection",Toast.LENGTH_SHORT).show();

        else{
            if(savedInstanceState != null){
                ContributorListsaved = savedInstanceState.getParcelableArrayList(SAVEDINSTANCE_CONTRIBUTOR);
                if(ContributorListsaved != null){
                    contributorAdapter = new CollabAdapter(ContributorListsaved, getContext());
                    rvContributor.setAdapter(contributorAdapter);
                }

                else if(!checkConnection())
                    Toast.makeText(getContext(), "No Internet Connection",Toast.LENGTH_SHORT).show();

                else if(checkConnection())
                    updateContributorList(ownerLogin, name);
            }
            else
                updateContributorList(ownerLogin, name);
        }

        updateContributorList(ownerLogin, name);

        Picasso
                .with(getContext())
                .load(avatarUrl)
                .fit()
                .into(userImage);

        return rootview;
    }

    private void updateContributorList(String login, String name){

        final String BASE_URL = "https://api.github.com/repos/";

        Toast.makeText(getContext(), "NETWORK OPERATION",  Toast.LENGTH_SHORT).show();

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
                contributorAdapter = new CollabAdapter(contributors, getContext());
                rvContributor.setAdapter(contributorAdapter);

                for(Contributor collab : contributors){
                    ContributorListsaved.add(collab);
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
