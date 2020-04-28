package com.example.earthquake;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private EarthquakeAdapter mAdapter;
    private TextView mEmpty;
    private View mProgress;
    private static final String REQUEST_URL = "https://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson&starttime=2014-01-01&endtime=2014-01-02";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mAdapter = new EarthquakeAdapter(new ArrayList<Earthquake>());
        mRecyclerView.setAdapter(mAdapter);

        mEmpty=findViewById(R.id.empty_view);
        mProgress=findViewById(R.id.loading_spinner);

        ConnectivityManager connectivityManager=(ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo=connectivityManager.getActiveNetworkInfo();
        if(networkInfo!=null&&networkInfo.isConnected()){
            EarthquakeAsyncTask task=new EarthquakeAsyncTask();
            task.execute(REQUEST_URL);
        }
        else{
            mProgress.setVisibility(View.GONE);
            mEmpty.setText("No internet connection");
            mEmpty.setVisibility(View.VISIBLE);
        }
    }
    private class EarthquakeAsyncTask extends AsyncTask<String,Void, List<Earthquake>>{

        @Override
        protected List<Earthquake> doInBackground(String... urls) {
            if(urls!=null&&urls.length>0){
                List<Earthquake> results=NetworkingUtilities.fetchEarthquakeData(urls[0]);
                return results;
            }
            return null;
        }
        public void onPostExecute(List<Earthquake> earthquakes){
            mProgress.setVisibility(View.GONE);
            if(earthquakes!=null&&earthquakes.size()>0){
                mEmpty.setVisibility(View.GONE);
                mRecyclerView.setVisibility(View.VISIBLE);
                mAdapter.setData(earthquakes);
                mAdapter.notifyDataSetChanged();
            }
            else{
                mRecyclerView.setVisibility(View.GONE);
                mEmpty.setText("No earthquakes found");
                mEmpty.setVisibility(View.VISIBLE);
            }
        }
    }
}