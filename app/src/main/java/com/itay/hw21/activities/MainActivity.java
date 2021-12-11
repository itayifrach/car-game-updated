package com.itay.hw21.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.FirebaseApp;
import com.itay.hw21.R;

public class MainActivity extends AppCompatActivity {

    private Button btn_leaderBoard;
    private Button btn_Sensors_Mode;
   public static final String GAME_MODE = "GAME_MODE";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseApp.initializeApp(this);
//Ask Permission
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this,new String[] {Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},0);
        }
        //Buttons init
        btn_leaderBoard = findViewById(R.id.btn_leaderboard);
        btn_Sensors_Mode=findViewById(R.id.sensors_Mode);
        btn_leaderBoard.setOnClickListener(new View.OnClickListener() {
           //All activity switches
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,score_leade.class);
                startActivity(intent);
            }
        });
        findViewById(R.id.start_btn).setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this,GameActivity.class);
            intent.putExtra(GAME_MODE,"ordinary");
            startActivity(intent);
    });
        findViewById(R.id.sensors_Mode).setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this,GameActivity.class);
            intent.putExtra(GAME_MODE,"Sensors");
            startActivity(intent);
        });
    }
}