package com.ardityawahyu.autohiddencamera;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.androidhiddencamera.CameraConfig;
import com.androidhiddencamera.CameraError;
import com.androidhiddencamera.HiddenCameraService;
import com.androidhiddencamera.HiddenCameraUtils;
import com.androidhiddencamera.config.CameraImageFormat;
import com.androidhiddencamera.config.CameraResolution;
import com.androidhiddencamera.config.CameraRotation;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by ardityawahyu on 12/13/17.
 */

public class CameraService extends HiddenCameraService {
    private CameraConfig cameraConfig;
    private static CameraService instance = null;

    public static boolean isInstanceCreated() {
        return instance != null;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        instance = this;
        Bundle bundle = intent.getExtras();
        Config config = bundle.getParcelable("config");
        int cameraFacing = config.getCameraFacing();
        String albumName = config.getAlbumName();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            if (HiddenCameraUtils.canOverDrawOtherApps(this)) {
                cameraConfig = new CameraConfig()
                        .getBuilder(this)
                        .setCameraFacing(cameraFacing)
                        .setCameraResolution(CameraResolution.HIGH_RESOLUTION)
                        .setImageFormat(CameraImageFormat.FORMAT_JPEG)
                        .setImageRotation(CameraRotation.ROTATION_90)
                        .setImageFile(new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) +
                                File.separator + albumName +
                                File.separator + "IMG_" + simpleDateFormat.format(new Date()) + ".jpeg"))
                        .build();
                startCamera(cameraConfig);
                new android.os.Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        takePicture();
                    }
                }, 2000);
            } else {
                HiddenCameraUtils.openDrawOverPermissionSetting(this);
            }
        } else {
            Toast.makeText(this, "Camera permission not available", Toast.LENGTH_SHORT).show();
        }

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        instance = null;
        stopCamera();
        sendBroadcast(new Intent(ConfigFinal.broadcastStop));
    }

    @Override
    public void onImageCapture(@NonNull File imageFile) {
        Toast.makeText(this, "Image captured", Toast.LENGTH_LONG).show();
        Log.d("Image capture", imageFile.length() + "");
    }

    @Override
    public void onCameraError(int errorCode) {
        switch (errorCode) {
            case CameraError.ERROR_CAMERA_OPEN_FAILED:
                //Camera open failed. Probably because another application
                //is using the camera
                Toast.makeText(this, "Cannot open camera.", Toast.LENGTH_LONG).show();
                break;
            case CameraError.ERROR_IMAGE_WRITE_FAILED:
                //Image write failed. Please check if you have provided WRITE_EXTERNAL_STORAGE permission
                Toast.makeText(this, "Cannot write image captured by camera.", Toast.LENGTH_LONG).show();
                break;
            case CameraError.ERROR_CAMERA_PERMISSION_NOT_AVAILABLE:
                //camera permission is not available
                //Ask for the camera permission before initializing it.
                Toast.makeText(this, "Camera permission not available.", Toast.LENGTH_LONG).show();
                break;
            case CameraError.ERROR_DOES_NOT_HAVE_OVERDRAW_PERMISSION:
                //Display information dialog to the user with steps to grant "Draw over other app"
                //permission for the app.
                HiddenCameraUtils.openDrawOverPermissionSetting(this);
                break;
            case CameraError.ERROR_DOES_NOT_HAVE_FRONT_CAMERA:
                Toast.makeText(this, "Your device does not have front camera.", Toast.LENGTH_LONG).show();
                break;
        }

        stopSelf();
    }
}
