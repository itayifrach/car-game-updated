package com.itay.hw21.activities;

import static com.itay.hw21.activities.MainActivity.GAME_MODE;

import android.Manifest;
import android.animation.Animator;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.TriggerEvent;
import android.hardware.TriggerEventListener;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.itay.hw21.R;
import com.itay.hw21.Utils;
import com.itay.hw21.database.DBManager;
import com.itay.hw21.models.Coordinate;
import com.itay.hw21.models.DirectionAction;
import com.itay.hw21.models.Score;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class GameActivity extends AppCompatActivity implements View.OnClickListener, LocationListener, SensorEventListener {

    private ImageView car;
    private ImageView[] dynamites = new ImageView[6];
    private ImageView life_1, life_2, life_3;
    private ImageButton arrow_left;
    private ImageButton arrow_right;
    private MediaPlayer mediaPlayer, mediaPlayer1;
    private TextView timerTv;


    // the current lane the car is in
    private int currentLane = 3;
    // the current amount of lives
    private int lives = 3;
    // the length of the song?
    private int length;
    // timers
    private Timer dynamite_timer, game_timer = new Timer(), stopwatch = new Timer();
    private Random rand = new Random();
    // indicators for the dynamite lanes
    private int[] dynamites_lane = {0, 0, 0, 0, 0, 0};
    private boolean canMove = true;
    // car and dynamite move-by
    private float offset_x, offset_y;
    //location
    private Long startTime = null;
    private LocationManager locationManager;
    private Coordinate locationCoordinate = new Coordinate(0, 0);
    //sensors
    private SensorManager sensorManager;
    private Sensor sensor;
    private TriggerEventListener triggerEventListener;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_activity);
        findViews();
        hideSystemUI();
        // Declare move-by
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        offset_x = displayMetrics.widthPixels / 6f;
        offset_y = displayMetrics.heightPixels + car.getHeight();
        //Music
        mediaPlayer = MediaPlayer.create(this, R.raw.background_music);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();
        //location
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        //sensor
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        //Game Mode
        if (getIntent() != null) {
            Intent intent = getIntent();
            String mode = intent.getStringExtra(GAME_MODE);
            if (mode.equals("Sensors")) {

                // hide arrows
                arrow_right.setVisibility(View.INVISIBLE);
                arrow_left.setVisibility(View.INVISIBLE);

            } else {
                arrow_right.setOnClickListener(this);
                arrow_left.setOnClickListener(this);
            }
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
    }

    private void startDynamiteTimers() {
        dynamite_timer = new Timer();
        showMessage("Get ready dynamites will be fired soon!");

        dynamite_timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                int random_lane = rand.nextInt(dynamites.length);
                // executes once at game start
                if (startTime == null) {
                    startTime = System.currentTimeMillis();
                    stopwatch.scheduleAtFixedRate(new TimerTask() {
                        @Override
                        public void run() {
                            String timeString = Utils.getTimeString(getTimeElapsed());
                            runOnUiThread(() -> {
                                timerTv.setText(timeString);
                            });

                        }
                    }, 1, 1);
                }
                if (dynamites_lane[random_lane] == 1) {
                    return;
                }
                dynamites_lane[random_lane] = 1;
                ImageView random_dynamite = dynamites[random_lane];

                runOnUiThread(() -> {
                    random_dynamite.setY(-200);
                    random_dynamite.setVisibility(View.VISIBLE);
                    random_dynamite.animate()
                            .y(offset_y)
                            .setUpdateListener(animation -> {
                                checkHit(random_lane, random_dynamite);
                            })
                            .setDuration(4000)
                            .start();
                });

            }
        }, 2000, 2000);
    }

    private Long getTimeElapsed() {
        long timeElapsed = System.currentTimeMillis() - startTime;
        return timeElapsed;
    }


    private void checkHit(int lane, ImageView dynamite) {
        int[] car_location = new int[2];
        int[] dynamite_location = new int[2];
        car.getLocationOnScreen(car_location);
        dynamite.getLocationOnScreen(dynamite_location);
        if (dynamite_location[1] >= offset_y) {
            dynamite.setVisibility(View.INVISIBLE);
            dynamite.setY(-200f);
            dynamites_lane[lane] = 0;
        } else if (lane == currentLane) {

            if (Math.abs(car_location[1] - dynamite_location[1]) < 20) {
                car.setImageResource(R.drawable.explosion);
                mediaPlayer.pause();
                length = mediaPlayer.getCurrentPosition();
                mediaPlayer1 = MediaPlayer.create(this, R.raw.sound_explosion);
                mediaPlayer1.start();
                for (int i = 0; i < dynamites.length; i++) {
                    dynamites_lane[i] = 0;
                    dynamites[i].setVisibility(View.INVISIBLE);
                    dynamites[i].setY(-200f);
                }
                canMove = false;
                dynamite_timer.cancel();
                if (lives == 0) {
                    endGame();
                    return;
                } else if (lives == 3) {
                    life_3.setImageResource(R.drawable.heart_unfilled);
                } else if (lives == 2) {
                    life_2.setImageResource(R.drawable.heart_unfilled);
                } else if (lives == 1) {
                    life_1.setImageResource(R.drawable.heart_unfilled);
                }
                showMessage("Exploded! lives left: " + --lives);
                game_timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        runOnUiThread(() -> car.setImageResource(R.drawable.car));
                        canMove = true;
                        mediaPlayer.seekTo(length);
                        mediaPlayer.start();
                        mediaPlayer.start();
                        startDynamiteTimers();
                    }
                }, 3000);

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
        }, 3000);

        Score score = new Score(getTimeElapsed(), locationCoordinate);
        DBManager.getInstance().addNewScore(score, unused -> Log.d("addNew Score", "Successfuly added new score")
                , e -> {
                    Log.d("addNew Score", e.getMessage());
                });
    }

    private void showMessage(String msg) {
        runOnUiThread(() -> Toast.makeText(this, msg, Toast.LENGTH_SHORT).show());
    }

    private void findViews() {
        car = findViewById(R.id.car);
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
        arrow_left = findViewById(R.id.arrow_left);
        arrow_right = findViewById(R.id.arrow_right);
        life_1 = findViewById(R.id.life_1);
        life_2 = findViewById(R.id.life_2);
        life_3 = findViewById(R.id.life_3);

        timerTv = findViewById(R.id.currenScore_TV);


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
        if (v.getId() == R.id.arrow_right) {
            DirectionAction action = DirectionAction.RIGHT;
            move(action);
        } else if (v.getId() == R.id.arrow_left) {
            DirectionAction action = DirectionAction.LEFT;
            move(action);
        }
    }

    private void move(DirectionAction directionAction) {
        if (directionAction == DirectionAction.RIGHT) {
            if (currentLane == 5 || !canMove) return;
            car.animate().xBy(offset_x)
                    .setListener(onCarMoveListener())
                    .setDuration(100).start();
            currentLane++;
        } else {
            if (currentLane == 0 || !canMove) return;
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

    @Override
    public void onLocationChanged(@NonNull Location location) {
        double latitude = location.getLatitude();
        double longtitue = location.getLongitude();
        this.locationCoordinate = new Coordinate(longtitue, latitude);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {






    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}