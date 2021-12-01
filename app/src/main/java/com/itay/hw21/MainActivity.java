package com.itay.hw21;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.start_btn).setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this,GameActivity.class);
            startActivity(intent);
    });
    }
}