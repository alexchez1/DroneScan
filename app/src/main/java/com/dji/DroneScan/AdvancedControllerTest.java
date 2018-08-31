package com.dji.DroneScan;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;

import dji.common.error.DJIError;
import dji.common.flightcontroller.ObstacleDetectionSector;
import dji.common.flightcontroller.VisionDetectionState;
import dji.common.flightcontroller.virtualstick.FlightControlData;
import dji.common.flightcontroller.virtualstick.FlightCoordinateSystem;
import dji.common.flightcontroller.virtualstick.RollPitchControlMode;
import dji.common.flightcontroller.virtualstick.VerticalControlMode;
import dji.common.flightcontroller.virtualstick.YawControlMode;
import dji.common.util.CommonCallbacks;
import dji.sdk.flightcontroller.FlightAssistant;
import dji.sdk.flightcontroller.FlightController;
import dji.sdk.products.Aircraft;

public class AdvancedControllerTest extends AppCompatActivity {

    private FlightController flightController;
    private FlightAssistant flightAssistant;
    DecimalFormat df = new DecimalFormat();

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advanced_controller_test);

        // Get all instances
        Aircraft aircraft = new Aircraft(null);
        flightController = aircraft.getFlightController();
        flightAssistant = flightController.getFlightAssistant();

        // Set advanced Mode enabled
        flightController.setVirtualStickModeEnabled(true, null);
        flightController.setVirtualStickAdvancedModeEnabled(true);

        // Get all UI elements
        final Switch switchCollisionAvoid, switchVisionAssist;
        switchCollisionAvoid = findViewById(R.id.switchCollisionAvoid);
        switchVisionAssist = findViewById(R.id.switchVisionAssist);
        Button btnYawLeft = findViewById(R.id.btnYawLeft);
        Button btnYawRight = findViewById(R.id.btnYawRight);
        Button btnForward = findViewById(R.id.btnForward);
        Button btnLeft = findViewById(R.id.btnLeft);
        Button btnRight = findViewById(R.id.btnRight);
        Button btnBack = findViewById(R.id.btnBack);
        Button btnDown = findViewById(R.id.btnDown);
        Button btnUp = findViewById(R.id.btnUp);
        Button btnTakeOff = findViewById(R.id.btnTakeOff);
        Button btnLand = findViewById(R.id.btnLand);
        Button btnF1 = findViewById(R.id.btnF1);
        Button btnF2 = findViewById(R.id.btnF2);
        Button btnF3 = findViewById(R.id.btnF3);
        Button btnF4 = findViewById(R.id.btnF4);
        Button btnF5 = findViewById(R.id.btnF5);
        TextView valSen1, valSen2, valSen3, valSen4;
        valSen1 = findViewById(R.id.valSen1);
        valSen2 = findViewById(R.id.valSen2);
        valSen3 = findViewById(R.id.valSen3);
        valSen4 = findViewById(R.id.valSen4);
        final TextView senArray[] = new TextView[]{valSen1, valSen2, valSen3, valSen4};
        df.setMaximumFractionDigits(2);

        //Aircraft setting
        flightController.setVerticalControlMode(VerticalControlMode.VELOCITY);
        flightController.setYawControlMode(YawControlMode.ANGULAR_VELOCITY);
        flightController.setRollPitchControlMode(RollPitchControlMode.VELOCITY);
        flightController.setRollPitchCoordinateSystem(FlightCoordinateSystem.BODY);

        // Take off button
        btnTakeOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                flightController.startTakeoff(null);
            }
        });

        // Landing button
        btnLand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                flightController.startLanding(null);
            }
        });

        // Yaw Left button
        btnYawLeft.setOnTouchListener(new View.OnTouchListener() {
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
                    flightController.sendVirtualStickFlightControlData(new FlightControlData(0, 0, -10, 0), null);
                    mHandler.postDelayed(this, 100);
                }
            };
        });

        // Yaw right button
        btnYawRight.setOnTouchListener(new View.OnTouchListener() {
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
                    flightController.sendVirtualStickFlightControlData(new FlightControlData(0, 0, 10, 0), null);
                    mHandler.postDelayed(this, 100);
                }
            };
        });

        // Forward button
        btnForward.setOnTouchListener(new View.OnTouchListener() {
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
                    flightController.sendVirtualStickFlightControlData(new FlightControlData(0, 1, 0, 0), null);
                    mHandler.postDelayed(this, 100);
                }
            };
        });

        // Back button
        btnBack.setOnTouchListener(new View.OnTouchListener() {
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
                    flightController.sendVirtualStickFlightControlData(new FlightControlData(0, -1, 0, 0), null);
                    mHandler.postDelayed(this, 100);
                }
            };
        });

        // Left button
        btnLeft.setOnTouchListener(new View.OnTouchListener() {
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

        // Right button
        btnRight.setOnTouchListener(new View.OnTouchListener() {
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

        // Up button
        btnUp.setOnTouchListener(new View.OnTouchListener() {
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

        // Down button
        btnDown.setOnTouchListener(new View.OnTouchListener() {
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

        // 1s button
        btnF1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String[] arr = new String[]{"takeoff", "0.5up", "13forward", "7left", "190yawR", "align"};
                NavigationExecutor ne = new NavigationExecutor(arr, 1f);
                ne.runCommands();

            }
        });

        // 2s button
        btnF2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new CountDownTimer(15100, 100) {
                    @Override
                    public void onTick(long l) {
                        flightController.sendVirtualStickFlightControlData(new FlightControlData((float) 0.5, 0, 0, 0), null);
                    }

                    @Override
                    public void onFinish() {
                        flightController.sendVirtualStickFlightControlData(new FlightControlData(0, 0, 0, 0), null);
                    }
                }.start();
            }
        });

        // 3s button
        btnF3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new CountDownTimer(1100, 100) {
                    @Override
                    public void onTick(long l) {
                        flightController.sendVirtualStickFlightControlData(new FlightControlData(0, 0, 90, 0), null);
                    }

                    @Override
                    public void onFinish() {
                        flightController.sendVirtualStickFlightControlData(new FlightControlData(0, 0, 0, 0), null);
                    }
                }.start();
            }
        });

        // 4s button
        btnF4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new CountDownTimer(2100, 100) {
                    @Override
                    public void onTick(long l) {
                        flightController.sendVirtualStickFlightControlData(new FlightControlData(0, 0, 90, 0), null);
                    }

                    @Override
                    public void onFinish() {
                        flightController.sendVirtualStickFlightControlData(new FlightControlData(0, 0, 0, 0), null);
                    }
                }.start();
            }
        });

        // 5s button
        btnF5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new CountDownTimer(3100, 100) {
                    @Override
                    public void onTick(long l) {
                        flightController.sendVirtualStickFlightControlData(new FlightControlData(0, 0, 90, 0), null);
                    }

                    @Override
                    public void onFinish() {
                        flightController.sendVirtualStickFlightControlData(new FlightControlData(0, 0, 0, 0), null);
                    }
                }.start();
            }
        });

        // Switch control for collision Avoid
        switchCollisionAvoid.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    flightAssistant.setCollisionAvoidanceEnabled(true, new CommonCallbacks.CompletionCallback() {
                        @Override
                        public void onResult(DJIError djiError) {
                            flightAssistant.getCollisionAvoidanceEnabled(new CommonCallbacks.CompletionCallbackWith<Boolean>() {
                                @Override
                                public void onSuccess(Boolean aBoolean) {
                                    if (!aBoolean) {
                                        switchCollisionAvoid.setChecked(false);
                                    } else {
                                        Toast.makeText(getApplicationContext(), "Collision avoidance enabled", Toast.LENGTH_LONG).show();
                                    }
                                }

                                @Override
                                public void onFailure(DJIError djiError) {
                                }
                            });
                        }
                    });
                } else {
                    flightAssistant.setCollisionAvoidanceEnabled(false, new CommonCallbacks.CompletionCallback() {
                        @Override
                        public void onResult(DJIError djiError) {
                            flightAssistant.getCollisionAvoidanceEnabled(new CommonCallbacks.CompletionCallbackWith<Boolean>() {
                                @Override
                                public void onSuccess(Boolean aBoolean) {
                                    if (aBoolean) {
                                        switchCollisionAvoid.setChecked(true);
                                    } else {
                                        Toast.makeText(getApplicationContext(), "Collision avoidance disabled", Toast.LENGTH_LONG).show();
                                    }
                                }

                                @Override
                                public void onFailure(DJIError djiError) {
                                }
                            });
                        }
                    });
                }
            }
        });

        // Switch control for Vision assistant
        switchVisionAssist.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    flightAssistant.setVisionAssistedPositioningEnabled(true, new CommonCallbacks.CompletionCallback() {
                        @Override
                        public void onResult(DJIError djiError) {
                            flightAssistant.getVisionAssistedPositioningEnabled(new CommonCallbacks.CompletionCallbackWith<Boolean>() {
                                @Override
                                public void onSuccess(Boolean aBoolean) {
                                    if (!aBoolean) {
                                        switchVisionAssist.setChecked(false);
                                    } else {
                                        Toast.makeText(getApplicationContext(), "Vision assistance enabled", Toast.LENGTH_LONG).show();
                                    }
                                }

                                @Override
                                public void onFailure(DJIError djiError) {
                                }
                            });
                        }
                    });
                } else {
                    flightAssistant.setVisionAssistedPositioningEnabled(false, new CommonCallbacks.CompletionCallback() {
                        @Override
                        public void onResult(DJIError djiError) {
                            flightAssistant.getVisionAssistedPositioningEnabled(new CommonCallbacks.CompletionCallbackWith<Boolean>() {
                                @Override
                                public void onSuccess(Boolean aBoolean) {
                                    if (aBoolean) {
                                        switchVisionAssist.setChecked(true);
                                    } else {
                                        Toast.makeText(getApplicationContext(), "Vision assistance disabled", Toast.LENGTH_LONG).show();
                                    }
                                }

                                @Override
                                public void onFailure(DJIError djiError) {
                                }
                            });
                        }
                    });
                }
            }
        });

        // Sensor callback to measure distance for obstacles
        flightAssistant.setVisionDetectionStateUpdatedCallback(new VisionDetectionState.Callback() {
            @Override
            public void onUpdate(@NonNull VisionDetectionState visionDetectionState) {

                ObstacleDetectionSector[] obstacleDetectionSectors = visionDetectionState.getDetectionSectors();

                for (int i = 0; i < obstacleDetectionSectors.length; i++) {
                    float dist = obstacleDetectionSectors[i].getObstacleDistanceInMeters();
                    senArray[i].setText(df.format(dist) + "");
                }
            }
        });

    }
}
