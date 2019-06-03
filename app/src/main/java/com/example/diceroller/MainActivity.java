package com.example.diceroller;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.Random;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    public final Random randomVariable = new Random();
    private Button rollDice;
    private ImageView diceImage;

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private final String TAG = "MainActivity";
    private final double shakeLimit = 2.5;
    private MediaPlayer diceSound;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rollDice = findViewById(R.id.roll_dice_button);
        diceImage = findViewById(R.id.dice_image);
        diceSound = MediaPlayer.create(getApplicationContext(), R.raw.dice_rolling_sound);

        // initialize semsorManager and sensor
        Log.d(TAG, "onCreate: started");
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(MainActivity.this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        Log.d(TAG, "onCreate: sensor initialized");

        rollDice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                final Animation anim = AnimationUtils.loadAnimation(MainActivity.this, R.anim.dice_roll_animation);
//                final Animation.AnimationListener animationListener = new Animation.AnimationListener() {
//                    @Override
//                    public void onAnimationStart(Animation animation) {
//
//                    }
//
//                    @Override
//                    public void onAnimationEnd(Animation animation) {
//                        int value = randomDiceNumber();
//                        int resId = getResources().getIdentifier("dice" + value, "drawable", "com.example.diceroller");
//                        if (animation == anim) {
//                            diceImage.setImageResource(resId);
//                        }
//                    }
//
//                    @Override
//                    public void onAnimationRepeat(Animation animation) {
//
//                    }
//                };
//                anim.setAnimationListener(animationListener);
//                diceImage.startAnimation(anim);
                rollTheDice();
            }
        });
    }

    private void rollTheDice(){
//        diceSound.stop();
//        diceSound.reset();
//        diceSound.release();
        diceSound.start();
        final Animation anim = AnimationUtils.loadAnimation(MainActivity.this, R.anim.dice_roll_animation);
        final Animation.AnimationListener animationListener = new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                int value = randomDiceNumber();
                int resId = getResources().getIdentifier("dice" + value, "drawable", "com.example.diceroller");
                if (animation == anim) {
                    diceImage.setImageResource(resId);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        };
        anim.setAnimationListener(animationListener);
        diceImage.startAnimation(anim);
    }

    public int randomDiceNumber() {
        return randomVariable.nextInt(6) + 1;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        double valueX = event.values[0];
        double valueY = event.values[1];
        double valueZ = event.values[2];

        double shake = (float) Math.sqrt(valueX * valueX + valueY * valueY + valueZ * valueZ) - SensorManager.GRAVITY_EARTH;
        shake = Math.sqrt(shake * shake);
        Log.d(TAG, "shake: " + shake);

        if (shake > shakeLimit) {
            Toast.makeText(getApplicationContext(), "Shake detected", Toast.LENGTH_SHORT).show();
            //diceSound.stop();
            rollTheDice();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
