package com.itay.hw21;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Button btn_leaderBoard;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseApp.initializeApp(this);

        DBManager.getInstance().fetchScores(scoreList -> {
            for(Score s : scoreList) {
                System.out.println(s);
            }
        });
        btn_leaderBoard = findViewById(R.id.btn_leaderboard);
        btn_leaderBoard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,score_leade.class);
                startActivity(intent);
            }
        });
        findViewById(R.id.start_btn).setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this,GameActivity.class);
            startActivity(intent);
    });
    }
}