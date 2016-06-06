package com.textme.dhermanu.githubstarred.api;

import com.textme.dhermanu.githubstarred.models.RepoList;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by dhermanu on 6/5/16.
 */
public interface GithubAPI {
    @GET("repositories?" )
    Call<RepoList> getRepos
            (@Query("q") String query, @Query("sort") String sort, @Query("order") String order);

}
