package com.example.diceroller;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.Random;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    public final Random randomVariable = new Random();
    private ImageView rollDice;
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private final String TAG = "MainActivity";
    private final double shakeLimit = 2.5;
    private GridView diceList;
    private int noOfDice = 1;
    private CustomDiceAdapter diceAdapter;
    private boolean rollDicePaused = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rollDice = findViewById(R.id.roll_dice_button);
        //diceImage = findViewById(R.id.dice_image);

        diceList = findViewById(R.id.dice_list_view);
        diceAdapter = new CustomDiceAdapter();
        diceList.setAdapter(diceAdapter);

        // initialize sensorManager and sensor
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(MainActivity.this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);

        initializeButtonClick();
        initializeAddRemoveButtons();
    }

    private void initializeButtonClick() {
        rollDice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rollTheDice();
            }
        });
    }

    private void initializeAddRemoveButtons() {
        ImageView addDice = findViewById(R.id.add_dice);
        ImageView removeDice = findViewById(R.id.remove_dice);
        addDice.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (noOfDice < 6) {
                    rollDicePaused = true;
                    noOfDice = noOfDice + 1;
                    diceAdapter.updateAdapter();
                } else {
                    Toast.makeText(getApplicationContext(), "Maximum 6 dice can be added", Toast.LENGTH_SHORT).show();
                }
            }
        });
        removeDice.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (noOfDice != 1) {
                    rollDicePaused = true;
                    noOfDice = noOfDice - 1;
                    diceAdapter.updateAdapter();
                } else {
                    Toast.makeText(getApplicationContext(), "Cannot roll with no dice", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // this method plays the dice sound
    private void playDiceSound() {
        MediaPlayer diceSound;
        diceSound = MediaPlayer.create(getApplicationContext(), R.raw.dice_rolling_sound);
        diceSound.start();
    }

    // method to create vibration on device
    private void vibrateDevice() {
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            vibrator.vibrate(100);
        }
    }

    private void rollTheDice() {
        rollDicePaused = false;
        diceAdapter.updateAdapter();
        playDiceSound();
        vibrateDevice();
    }

    public int randomDiceNumber() {
        return randomVariable.nextInt(6) + 1;
    }

    // abstract method implementation for SensorEventListener
    @Override
    public void onSensorChanged(SensorEvent event) {
        double valueX = event.values[0];
        double valueY = event.values[1];
        double valueZ = event.values[2];

        double shake = (float) Math.sqrt(valueX * valueX + valueY * valueY + valueZ * valueZ) - SensorManager.GRAVITY_EARTH;
        shake = Math.sqrt(shake * shake);

        if (shake > shakeLimit) {
            rollTheDice();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    // Custom Dice adapter
    public class CustomDiceAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            Log.d(TAG, "noOfDice: " + noOfDice);
            return noOfDice;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        // this method is used to set the dice roll animation
        public void addAnimation(ImageView image) {
            final ImageView imageView = image;
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
                        imageView.setImageResource(resId);
                    }
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            };
            anim.setAnimationListener(animationListener);
            imageView.startAnimation(anim);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Log.d(TAG, "getView: position: " + position);
            convertView = getLayoutInflater().inflate(R.layout.custom_dice_layout, null);
            final ImageView image = convertView.findViewById(R.id.custom_view_dice);
            int value = randomDiceNumber();
            int resId = getResources().getIdentifier("dice" + value, "drawable", "com.example.diceroller");
            image.setImageResource(resId);
            if (!rollDicePaused) {
                addAnimation(image);
            }
            return convertView;
        }

        public void updateAdapter() {
            //and call notifyDataSetChanged
            notifyDataSetChanged();
        }
    }

}
