package com.itay.hw21;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class GameActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView car;
    private ImageView[] dynamites = new ImageView[6];
    private ImageView life_1,life_2,life_3;
    private ImageButton arrow_left;
    private ImageButton arrow_right;
    private float offset_x,offset_y;
    private int currentLane = 3;
    private int lives=3;
    private Timer dynamite_timer,game_timer = new Timer();
    private Random rand = new Random();
    private int[] dynamites_lane = {0,0,0,0,0,0};
    private boolean canMove = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_activity);
        findViews();
        hideSystemUI();
        arrow_right.setOnClickListener(this);
        arrow_left.setOnClickListener(this);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        offset_x = displayMetrics.widthPixels/6f;
        offset_y = displayMetrics.heightPixels + car.getHeight();

    }

    private void startDynamiteTimers() {
        dynamite_timer = new Timer();
       showMessage("Get ready dynamites will be fired soon!");
        dynamite_timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                int random_lane = rand.nextInt(dynamites.length);
                dynamites_lane[random_lane] = 1;
                ImageView random_dynamite = dynamites[random_lane];

                runOnUiThread(() ->  {
                    random_dynamite.setY(-200);
                    random_dynamite.setVisibility(View.VISIBLE);
                    random_dynamite.animate()
                        .y(offset_y)
                        .setUpdateListener(animation -> {
                            checkHit(random_lane,random_dynamite);
                        })
                        .setDuration(4000)
                        .start();
                });

            }
        },2000,2000);

    }

    private void checkHit(int lane,ImageView dynamite) {
        int[] car_location = new int[2];
        int[] dynamite_location = new int[2];
        car.getLocationOnScreen(car_location);
        dynamite.getLocationOnScreen(dynamite_location);
        if(dynamite_location[1] >= offset_y) {
            dynamite.setVisibility(View.INVISIBLE);
            dynamite.setY(-200f);
            dynamites_lane[lane] = 0;
        }else if(lane == currentLane) {
           Log.d("Checkhit","There was an hit!" + Math.abs(car_location[1] - dynamite_location[1]));

            if(Math.abs(car_location[1] - dynamite_location[1]) < 5) {
            car.setImageResource(R.drawable.explosion);
                MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.sound_explosion);
                mediaPlayer.start();
            for(int i = 0; i < dynamites.length; i++) {
                dynamites_lane[i] = 0;
                dynamites[i].setVisibility(View.INVISIBLE);
                dynamites[i].setY(-200f);
            }
            canMove = false;
            dynamite_timer.cancel();
            if(lives ==0) {
               endGame();
               return;
            }else if(lives == 3) {
                life_3.setImageResource(R.drawable.heart_unfilled);
            }else if(lives == 2) {
                life_2.setImageResource(R.drawable.heart_unfilled);
            }else if(lives == 1) {
                life_1.setImageResource(R.drawable.heart_unfilled);
            }
            showMessage("Exploded! lives left: " + --lives);
            game_timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    runOnUiThread(() -> car.setImageResource(R.drawable.car));
                    canMove=true;
                    startDynamiteTimers();
                }
            },3000);

           }
       }
    }

    private void endGame() {
        showMessage("All lives ran out,you lasted: ...");
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                finish();
            }
        },3000);
    }

    private void showMessage(String msg) {
      runOnUiThread(() -> Toast.makeText(this,msg,Toast.LENGTH_SHORT).show());
    }

    private void findViews() {
        car=findViewById(R.id.car);
        for (int d_index = 0; d_index < 6; d_index++) {
            switch (d_index) {
                case 0:
                    dynamites[d_index] = findViewById(R.id.dynamite_1);
                    break;
                case 1:
                    dynamites[d_index] = findViewById(R.id.dynamite_2);
                    break;
                case 2:
                    dynamites[d_index] = findViewById(R.id.dynamite_3);
                    break;
                case 3:
                    dynamites[d_index] = findViewById(R.id.dynamite_4);
                    break;
                case 4:
                    dynamites[d_index] = findViewById(R.id.dynamite_5);
                    break;
                case 5:
                    dynamites[d_index] = findViewById(R.id.dynamite_6);
                    break;
            }
            dynamites[d_index].setVisibility(View.INVISIBLE);
        }
        arrow_left=findViewById(R.id.arrow_left);
        arrow_right=findViewById(R.id.arrow_right);
        life_1=findViewById(R.id.life_1);
        life_2=findViewById(R.id.life_2);
        life_3=findViewById(R.id.life_3);

    }
    public void hideSystemUI() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        startDynamiteTimers();
    }

    @Override
    protected void onPause() {
        super.onPause();
        dynamite_timer.cancel();
    }

    @Override
    protected void onResume() {
        super.onResume();
        startDynamiteTimers();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dynamite_timer.cancel();
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.arrow_right) {
            DirectionAction action = DirectionAction.RIGHT;
            move(action);
        }else if(v.getId() == R.id.arrow_left) {
            DirectionAction action = DirectionAction.LEFT;
            move(action);
        }
    }

    private void move(DirectionAction directionAction) {
       if(directionAction == DirectionAction.RIGHT) {
            if(currentLane == 5 || !canMove) return;
            car.animate().xBy(offset_x)
                    .setListener(onCarMoveListener())
                    .setDuration(100).start();
            currentLane++;
       }else {
           if(currentLane == 0 || !canMove) return;
           car.animate().xBy(-offset_x)
                   .setListener(onCarMoveListener())
                   .setDuration(100).start();
            currentLane--;
       }
    }



    private Animator.AnimatorListener onCarMoveListener() {
        return new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                canMove = false;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                canMove = true;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                canMove = true;
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
                canMove = false;
            }
        };
    }
}
