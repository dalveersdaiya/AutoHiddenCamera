package com.ardityawahyu.autohiddencamera;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.androidhiddencamera.CameraConfig;
import com.androidhiddencamera.config.CameraFacing;

import java.io.File;

public class MainActivity extends AppCompatActivity {
    private String albumName;
    private BroadcastReceiver receiver;
    private int cameraFacing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final EditText textInterval = (EditText)findViewById(R.id.textInterval);
        final EditText textHost = (EditText)findViewById(R.id.textHost);
        final EditText textUsername = (EditText)findViewById(R.id.textUsername);
        final EditText textPassword = (EditText)findViewById(R.id.textPassword);
        final EditText textAlbumName = (EditText)findViewById(R.id.textAlbumName);
        final CheckBox checkUpload = (CheckBox) findViewById(R.id.checkUpload);
        final CheckBox checkAutoUpload = (CheckBox) findViewById(R.id.checkAutoDelete);
        final Button buttonStart = (Button) findViewById(R.id.buttonStart);
        final RadioGroup rgCamera = (RadioGroup) findViewById(R.id.rgCamera);

        //ask permission at first
        if (!(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, ConfigFinal.requestRequiredPermissions);
        }

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Toast.makeText(MainActivity.this, "Camera Stopped!", Toast.LENGTH_SHORT).show();
                buttonStart.setText("start");
            }
        };

        if(!CameraService.isInstanceCreated()) {
            buttonStart.setText("start");
        } else {
            buttonStart.setText("stop");
        }

        buttonStart.setOnClickListener(new View.OnClickListener() {
        @Override
            public void onClick(View view) {
                if(!CameraService.isInstanceCreated()) {
                    //create directory first
                    albumName = textAlbumName.getText().toString().trim();
                    if (albumName.trim().equals("") || albumName == null) {
                        albumName = textAlbumName.getHint().toString();
                    }
                    File saveImage = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + File.separator + albumName);
                    if (!saveImage.exists()) {
                        saveImage.mkdir();
                    }

                    //get camera facing
                    switch (rgCamera.getCheckedRadioButtonId()) {
                        case R.id.radioBackCamera:
                            cameraFacing = CameraFacing.REAR_FACING_CAMERA;
                            break;
                        case R.id.radioFrontCamera:
                            cameraFacing = CameraFacing.FRONT_FACING_CAMERA;
                            break;
                        default:
                            cameraFacing = CameraFacing.FRONT_FACING_CAMERA;
                    }

                    Config config = new Config();
                    config.setCameraFacing(cameraFacing);
                    config.setInterval(Integer.parseInt(textInterval.getText().toString()));
                    config.setHost(textHost.getText().toString());
                    config.setUsername(textUsername.getText().toString());
                    config.setPassword(textPassword.getText().toString());
                    config.setAlbumName(albumName);
                    config.setIsUpload(checkUpload.isChecked() ? 1 : 0);
                    config.setIsDeleteAfterUpload(checkAutoUpload.isChecked() ? 1 : 0);

                    Bundle bundle = new Bundle();
                    bundle.putParcelable("config", config);

                    /*Intent i = new Intent(MainActivity.this, CameraActivity.class);
                    i.putExtra("Config", config);
                    startActivity(i);*/

                    Intent i = new Intent(MainActivity.this, CameraService.class);
                    i.putExtras(bundle);
                    startService(i);
                    Toast.makeText(MainActivity.this, "Camera Started!", Toast.LENGTH_SHORT).show();
                    buttonStart.setText("stop");
                } else {
                    stopService(new Intent(MainActivity.this, CameraService.class));
                    Toast.makeText(MainActivity.this, "Camera Stopped!", Toast.LENGTH_SHORT).show();
                    buttonStart.setText("start");
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == ConfigFinal.requestRequiredPermissions) {
            if(grantResults.length > 0) {
                for (int i = 0; i < grantResults.length; i++) {
                    if(grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(this, "Give the permission, please", Toast.LENGTH_LONG).show();
                    }
                }
            }
//            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
//                Toast.makeText(this, "Give the permission, please", Toast.LENGTH_LONG).show();
//                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(this).registerReceiver((receiver),
                new IntentFilter(ConfigFinal.broadcastStop)
        );
    }

    @Override
    protected void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
    }
}
