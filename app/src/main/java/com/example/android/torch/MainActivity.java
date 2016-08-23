package com.example.android.torch;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

public class MainActivity extends Activity {

    ImageButton btnSwitch;

    private Camera camera;
    private boolean isFlashOn;
    private boolean hasFlash;
    Parameters params;
    MediaPlayer mp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // flash switch button
        btnSwitch = (ImageButton) findViewById(R.id.btnSwitch);


        // First check if device is supporting flashlight or not
        hasFlash = getApplicationContext().getPackageManager()
                .hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);

        if (!hasFlash) {
            // device doesn't support flash
            // Show alert message and close the application
            AlertDialog alert = new AlertDialog.Builder(MainActivity.this)
                    .create();
            alert.setTitle(getString(R.string.error_title));
            alert.setMessage(getString(R.string.no_flash_error));
            alert.setButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    // closing the application
                    finish();
                }
            });
            alert.show();
            return;
        }

        // get the camera
        getCamera();

        // displaying button image
        toggleButtonImage();


        // Switch button click event to toggle flash on/off
        btnSwitch.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (isFlashOn) {
                    // turn off flash
                    turnOffFlash();
                } else {
                    // turn on flash
                    turnOnFlash();
                }
            }
        });
    }


    // Get the camera
    private void getCamera() {
        Log.v("getCamera()", "In the method to get camera");
        if (camera == null) {
            Log.v("getCamera()", "camera == null");
            try {
                Log.v("getCamera()", "try camera = Camera.open()");
                camera = Camera.open();
                Log.v("getCamera()", "now camera = "+camera);
                params = camera.getParameters();
            } catch (RuntimeException e) {
                Log.e("Camera Error. Failed to Open. Error: ", e.getMessage());
            }
        }
    }


    // Turning On flash
    private void turnOnFlash() {
        Log.v("turnOnFlash()", "You're in the turnOnFlash method");
        if (!isFlashOn) {
            Log.v("turnOnFlash()", "!isFlashOn");
            if (camera == null || params == null) {
                Log.v("turnOnFlash()", "camera or params == null so return");
                Log.v("turnOnFlash()", "camera ="+camera+"\nparams ="+params);
                return;
            }
            Log.v("turnOnFlash()", "Flash is off and everything is good. turn on flash");
            // play sound
            playSound();

            params = camera.getParameters();
            params.setFlashMode(Parameters.FLASH_MODE_TORCH);
            camera.setParameters(params);
            camera.startPreview();
            isFlashOn = true;

            Log.v("turnOnFlash()", "Change image on screen");
            // changing button/switch image
            toggleButtonImage();
        }

    }


    // Turning Off flash
    private void turnOffFlash() {
        Log.v("turnOffFlash()", "You're in the turnOffFlash method");
        if (isFlashOn) {
            Log.v("turnOffFlash()", "isFlashOn");
            if (camera == null || params == null) {
                Log.v("turnOffFlash()", "camera or params == null so return");
                return;
            }
            Log.v("turnOffFlash()", "Flash is on and everything is good. turn off flash");
            // play sound
            playSound();

            params = camera.getParameters();
            params.setFlashMode(Parameters.FLASH_MODE_OFF);
            camera.setParameters(params);
            camera.stopPreview();
            isFlashOn = false;

            Log.v("turnOffFlash()", "Change image on screen");
            // changing button/switch image
            toggleButtonImage();
        }
    }


    // Playing sound
    // will play button toggle sound on flash on / off
    private void playSound(){
        if(isFlashOn){
            mp = MediaPlayer.create(MainActivity.this, R.raw.off);
        }else{
            mp = MediaPlayer.create(MainActivity.this, R.raw.on);
        }
        mp.setOnCompletionListener(new OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer mp) {
                // TODO Auto-generated method stub
                mp.release();
            }
        });
        mp.start();
    }

    /*
     * Toggle switch button images
     * changing image states to on / off
     * */
    private void toggleButtonImage(){
        if(isFlashOn){
            btnSwitch.setImageResource(R.drawable.on);
        }else{
            btnSwitch.setImageResource(R.drawable.off);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();

        // on pause turn off the flash
        turnOffFlash();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // on resume turn on the flash
        if(hasFlash)
            turnOnFlash();
    }

    @Override
    protected void onStart() {
        super.onStart();

        // on starting the app get the camera params
        getCamera();
    }

    @Override
    protected void onStop() {
        super.onStop();

        // on stop release the camera
        if (camera != null) {
            camera.release();
            camera = null;
        }
    }

}
