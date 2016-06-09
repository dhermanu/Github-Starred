package com.textme.dhermanu.githubstarred;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.textme.dhermanu.githubstarred.adapters.RepoAdapter;
import com.textme.dhermanu.githubstarred.api.GithubAPI;
import com.textme.dhermanu.githubstarred.models.Repo;
import com.textme.dhermanu.githubstarred.models.RepoList;

import java.text.SimpleDateFormat;
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
public class MainActivityFragment extends Fragment {

    private GithubAPI githubAPI;
    private ArrayList<Repo> repoListsaved = null;
    private RecyclerView rvRepos;
    private RepoAdapter repoAdapter;
    private String SAVEDINSTANCE_REPO = "save_repo";
    public String EXTRA_DATA = "SEND_REPO_INFO";
    public String queryFormat;

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.fragment_main, container, false);

        queryFormat = getCurrentDate();

        rvRepos = (RecyclerView) rootview.findViewById(R.id.recycle_repo_list);
        rvRepos.setLayoutManager(new LinearLayoutManager(getActivity()));

        if(!checkConnection()){
            if(repoListsaved != null){
                repoAdapter = new RepoAdapter(repoListsaved, getContext());
                rvRepos.setAdapter(repoAdapter);
            }
            else{
                Toast.makeText(getContext(), "No Internet Connection",Toast.LENGTH_SHORT).show();
            }
        }

        else{
            if(savedInstanceState != null){
                repoListsaved = savedInstanceState.getParcelableArrayList(SAVEDINSTANCE_REPO);
                if(repoListsaved != null){
                    repoAdapter = new RepoAdapter(repoListsaved, getContext());
                    rvRepos.setAdapter(repoAdapter);
                }

                else if(!checkConnection())
                    Toast.makeText(getContext(), "No Internet Connection",Toast.LENGTH_SHORT).show();

                else if(checkConnection())
                    updateList(queryFormat);
            }
            else
                updateList(queryFormat);
        }
        return rootview;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(repoListsaved != null){
            outState.putParcelableArrayList(SAVEDINSTANCE_REPO, repoListsaved);
        }
    }

    private void updateList(String date){
        final String BASE_URL = "https://api.github.com/search/";
        final String QUERY_PARAM = date;
        final String SORT_PARAM = "stars";
        final String ORDER_PARAM = "desc";

        Toast.makeText(getContext(), "NETWORK OPERATION",  Toast.LENGTH_SHORT).show();

        Gson gson =  new GsonBuilder().create();
        Retrofit retrofit = new Retrofit.Builder().baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        githubAPI = retrofit.create(GithubAPI.class);
        Call<RepoList> repoListCall = githubAPI.getRepos(QUERY_PARAM,SORT_PARAM,ORDER_PARAM);

        repoListCall.enqueue(new Callback<RepoList>(){
            @Override
            public void onResponse(Call<RepoList> call, Response<RepoList> response) {
                List<Repo> repoList = response.body().getItems();
                repoAdapter = new RepoAdapter(repoList, getContext());
                rvRepos.setAdapter(repoAdapter);

                repoListsaved = new ArrayList<>();
                for(Repo repo : repoList){
                    repoListsaved.add(repo);
                }
            }

            @Override
            public void onFailure(Call<RepoList> call, Throwable t) {

            }
        });

    }

    private String getReadableDateString(long time){
        // Because the API returns a unix timestamp (measured in seconds),
        // it must be converted to milliseconds in order to be converted to valid date.
        SimpleDateFormat shortenedDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return shortenedDateFormat.format(time);
    }

    private String getCurrentDate(){
        Time dayTime = new Time();
        dayTime.setToNow();

        // we start at the day returned by local time. Otherwise this is a mess.
        int julianStartDay = Time.getJulianDay(System.currentTimeMillis(), dayTime.gmtoff);
        dayTime = new Time();
        long dateTime;

        // Cheating to convert this to UTC time, which is what we want anyhow
        dateTime = dayTime.setJulianDay(julianStartDay-7);
        String day = getReadableDateString(dateTime);
        String dateFormat = "created:" + day;

        return dateFormat;
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
