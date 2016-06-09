package com.textme.dhermanu.githubstarred;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.textme.dhermanu.githubstarred.callbacks.CallbackTablet;
import com.textme.dhermanu.githubstarred.models.Owner;
import com.textme.dhermanu.githubstarred.models.Repo;

public class MainActivity extends AppCompatActivity implements CallbackTablet{

    private boolean mTwoPane;
    private static final String DETAILFRAGMENT_TAG = "DFTAG";
    public final static String EXTRA_DATA =
            "com.example.dhermanu.popularmoviesi.EXTRA_DATA";
    public final static String EXTRA_OWNER =
            "com.example.dhermanu.popularmoviesi.EXTRA_OWNER";
    public final static String EXTRA_AVATAR =
            "com.example.dhermanu.popularmoviesi.EXTRA_AVATAR";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (findViewById(R.id.repo_detail_container) != null) {
            mTwoPane = true;
        }

        else {
            mTwoPane = false;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
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
    public void onItemSelected(Repo repo) {
        Owner owner = repo.getOwner();
        if(mTwoPane){

            Bundle extras = new Bundle();
            extras.putString(EXTRA_DATA, repo.getName());
            extras.putString(EXTRA_OWNER, owner.getLogin());
            extras.putString(EXTRA_AVATAR, owner.getAvatarUrl());

            Log.v("Movie Title is", repo.getName());

            DetailActivityFragment fragment = new DetailActivityFragment();
            fragment.setArguments(extras);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.repo_detail_container, fragment, DETAILFRAGMENT_TAG)
                    .commit();
        }

        else{
            Intent intent = new Intent(this, DetailActivity.class);
            Bundle extras = new Bundle();
            extras.putString(EXTRA_DATA, repo.getName());
            extras.putString(EXTRA_OWNER, owner.getLogin());
            extras.putString(EXTRA_AVATAR, owner.getAvatarUrl());
            intent.putExtras(extras);
            startActivity(intent);
        }
    }

}
