package com.itay.hw21.activities;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.itay.hw21.R;
import com.itay.hw21.Utils;
import com.itay.hw21.adapters.ScoreBoardAdapter;
import com.itay.hw21.database.DBManager;
import com.itay.hw21.models.Coordinate;
import com.itay.hw21.models.OnItemClickListener;
import com.itay.hw21.models.OnScoresResponse;
import com.itay.hw21.models.Score;

import java.util.List;

public class score_leade extends FragmentActivity implements OnScoresResponse, OnItemClickListener, OnMapReadyCallback {

    private RecyclerView scoreRv;
    private ScoreBoardAdapter scoreRvAdapter;
    private GoogleMap map;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score_leade);
        scoreRv = findViewById(R.id.scoreRv);
        scoreRv.setLayoutManager(new LinearLayoutManager(this));
        // Get a handle to the fragment and register the callback.

        SupportMapFragment mapFragment = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map);
        if(mapFragment!=null)
        mapFragment.getMapAsync(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        DBManager.getInstance().fetchScores(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void consumeScores(List<Score> scoreList) {
        scoreRvAdapter = new ScoreBoardAdapter(scoreList,this);
        scoreList.sort((o1, o2) -> Long.compare(o2.getTimeLasted(),o1.getTimeLasted()));
        scoreRv.setAdapter(scoreRvAdapter);
    }

    private void moveCamera(Score score) {
        Log.d("Method","moveCamera, coordinte: " + score.getCoordinate());
        LatLng position = new LatLng(score.getCoordinate().getLatitude(),
                score.getCoordinate().getLongtitude());
        map.addMarker(new MarkerOptions()
                .position(position)
                .title(Utils.getTimeString(score.getTimeLasted())));
        map.moveCamera(CameraUpdateFactory.
                newLatLngZoom(position,1));
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Coordinate coord = new Coordinate(5.20,7.2);
        map = googleMap;
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(coord.getLatitude(),coord.getLongtitude()),0));
    }

    @Override
    public void Clicked(Score score) {
        // other logic..
        moveCamera(score);
    }
}