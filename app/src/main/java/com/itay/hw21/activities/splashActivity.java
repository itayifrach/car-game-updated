package com.itay.hw21.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.itay.hw21.R;

public class splashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        Thread thread= new Thread(){
            @Override
            public void run() {
                try {
                    //Time that splash screen will show on screen
                    sleep(4000);
                }
                catch(Exception e) {
                    e.printStackTrace();
                }
                finally {
                    Intent mainIntent= new Intent(splashActivity.this,MainActivity.class);
                    startActivity(mainIntent);

                }
            }
        };thread.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}









//itay ifrah