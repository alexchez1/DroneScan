package com.dji.DroneScan;

import android.annotation.SuppressLint;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import dji.common.flightcontroller.virtualstick.FlightControlData;
import dji.common.flightcontroller.virtualstick.FlightCoordinateSystem;
import dji.common.flightcontroller.virtualstick.RollPitchControlMode;
import dji.common.flightcontroller.virtualstick.VerticalControlMode;
import dji.sdk.flightcontroller.FlightController;
import dji.sdk.products.Aircraft;

public class ControllerActivity extends AppCompatActivity {
    private FlightController flightController;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_controlle_activity);

        // Get instance of connected aircraft
        Aircraft aircraft = new Aircraft(null);

        // Get flight controller
        flightController = aircraft.getFlightController();

        // Enable virtual sticks and change flight control
        flightController.setVirtualStickModeEnabled(true, null);
        flightController.setRollPitchControlMode(RollPitchControlMode.VELOCITY);
        flightController.setVerticalControlMode(VerticalControlMode.VELOCITY);
        flightController.setRollPitchCoordinateSystem(FlightCoordinateSystem.BODY);

        // Find land button and set LAND action on click
        Button btnLand = findViewById(R.id.btnLand);
        btnLand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                flightController.startLanding(null);
                flightController.turnOffMotors(null);
            }
        });

        // Find take off button and set TAKE OFF action on click
        Button btnTakeOff = findViewById(R.id.btnTakeOff);
        btnTakeOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                flightController.startTakeoff(null);
            }
        });

        // Settings for LEFT button
        Button btnGoLeft = findViewById(R.id.btnGoLeft);
        btnGoLeft.setOnTouchListener(new View.OnTouchListener() {
            private Handler mHandler;

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if (mHandler != null) return true;
                        mHandler = new Handler();
                        mHandler.postDelayed(mAction, 100);
                        break;
                    case MotionEvent.ACTION_UP:
                        if (mHandler == null) return true;
                        mHandler.removeCallbacks(mAction);
                        mHandler = null;
                        break;
                }
                return false;
            }

            Runnable mAction = new Runnable() {
                @Override
                public void run() {
                    flightController.sendVirtualStickFlightControlData(new FlightControlData(-1, 0, 0, 0), null);
                    mHandler.postDelayed(this, 100);
                }
            };
        });

        // Settings for RIGHT button
        Button btnGoRight = findViewById(R.id.btnGoRight);
        btnGoRight.setOnTouchListener(new View.OnTouchListener() {
            private Handler mHandler;

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if (mHandler != null) return true;
                        mHandler = new Handler();
                        mHandler.postDelayed(mAction, 100);
                        break;
                    case MotionEvent.ACTION_UP:
                        if (mHandler == null) return true;
                        mHandler.removeCallbacks(mAction);
                        mHandler = null;
                        break;
                }
                return false;
            }

            Runnable mAction = new Runnable() {
                @Override
                public void run() {
                    flightController.sendVirtualStickFlightControlData(new FlightControlData(1, 0, 0, 0), null);
                    mHandler.postDelayed(this, 100);
                }
            };
        });

        // Settings for UP button
        Button btnGoUp = findViewById(R.id.btnGoUp);
        btnGoUp.setOnTouchListener(new View.OnTouchListener() {

            private Handler mHandler;

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if (mHandler != null) return true;
                        mHandler = new Handler();
                        mHandler.postDelayed(mAction, 100);
                        break;
                    case MotionEvent.ACTION_UP:
                        if (mHandler == null) return true;
                        mHandler.removeCallbacks(mAction);
                        mHandler = null;
                        break;
                }
                return false;
            }

            Runnable mAction = new Runnable() {
                @Override
                public void run() {
                    flightController.sendVirtualStickFlightControlData(new FlightControlData(0, 0, 0, 1), null);
                    mHandler.postDelayed(this, 100);
                }
            };
        });

        // Settings for DOWN button
        Button btnGoDown = findViewById(R.id.btnGoDown);
        btnGoDown.setOnTouchListener(new View.OnTouchListener() {

            private Handler mHandler;

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if (mHandler != null) return true;
                        mHandler = new Handler();
                        mHandler.postDelayed(mAction, 100);
                        break;
                    case MotionEvent.ACTION_UP:
                        if (mHandler == null) return true;
                        mHandler.removeCallbacks(mAction);
                        mHandler = null;
                        break;
                }
                return false;
            }

            Runnable mAction = new Runnable() {
                @Override
                public void run() {
                    flightController.sendVirtualStickFlightControlData(new FlightControlData(0, 0, 0, -1), null);
                    mHandler.postDelayed(this, 100);
                }
            };
        });

        // Button that will accelerate drone UP for 2 seconds
        Button btn2secUp = findViewById(R.id.btn2secUp);
        btn2secUp.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                new CountDownTimer(2100, 100) {
                    @Override
                    public void onTick(long l) {
                        flightController.sendVirtualStickFlightControlData(new FlightControlData(0, 0, 0, 1), null);
                    }

                    @Override
                    public void onFinish() {
                        flightController.sendVirtualStickFlightControlData(new FlightControlData(0, 0, 0, 0), null);
                    }
                }.start();
            }
        });

        // Button that will accelerate drone RIGHT for 2 seconds
        Button btn2secRight = findViewById(R.id.btn2secRight);
        btn2secRight.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                new CountDownTimer(1100, 100) {
                    @Override
                    public void onTick(long l) {
                        flightController.sendVirtualStickFlightControlData(new FlightControlData((float)0.5, 0, 0, 0), null);
                    }

                    @Override
                    public void onFinish() {
                        flightController.sendVirtualStickFlightControlData(new FlightControlData(0, 0, 0, 0), null);
                    }
                }.start();
            }
        });

        // Button that will accelerate drone RIGHT for 4 seconds
        Button btn4secRight = findViewById(R.id.btn4secRight);
        btn4secRight.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                new CountDownTimer(2100, 100) {
                    @Override
                    public void onTick(long l) {
                        flightController.sendVirtualStickFlightControlData(new FlightControlData((float)0.5, 0, 0, 0), null);
                    }

                    @Override
                    public void onFinish() {
                        flightController.sendVirtualStickFlightControlData(new FlightControlData(0, 0, 0, 0), null);
                    }
                }.start();
            }
        });
    }
}
