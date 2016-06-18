package com.textme.dhermanu.githubstarred.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.textme.dhermanu.githubstarred.R;
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
    public String NO_INTERNET = "No Internet Connection";
    public String queryFormat;

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
                Toast.makeText(getContext(), NO_INTERNET,Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(getContext(), NO_INTERNET,Toast.LENGTH_SHORT).show();
            }
        }


        return rootview;
    }

    @Override
    public void onStart() {
        super.onStart();
        queryFormat = getCurrentDate();
        if(checkConnection()){
            Toast.makeText(getContext(), "FETCHING NEW",Toast.LENGTH_SHORT).show();
            updateList(queryFormat);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String date = prefs.getString("key", "7");

        int weekDay = Integer.parseInt(date);
        Time dayTime = new Time();
        dayTime.setToNow();

        // we start at the day returned by local time. Otherwise this is a mess.
        int julianStartDay = Time.getJulianDay(System.currentTimeMillis(), dayTime.gmtoff);
        dayTime = new Time();
        long dateTime;

        // Cheating to convert this to UTC time, which is what we want anyhow
        dateTime = dayTime.setJulianDay(julianStartDay-weekDay);
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