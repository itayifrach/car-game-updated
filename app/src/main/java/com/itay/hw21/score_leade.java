package com.itay.hw21;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

public class score_leade extends AppCompatActivity {

    private RecyclerView scoreRv;
    private ScoreBoardAdapter scoreRvAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score_leade);
        scoreRv = findViewById(R.id.scoreRv);

    }
}