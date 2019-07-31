package com.shashank.seconpart.svrecorder;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.StorageReference;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;

public class Record2Activity extends AppCompatActivity {

    Button btnStart,btnSettings;
    String email;
    int val1=0;
    boolean bo;
    int g=0;
    int c=0;
    int k=0;
    MediaRecorder mediaRecorder;
    String pathSave = "";
    String testPath;


    final int REQUEST_PERMISSION_CODE = 1000;
    public static long val2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record2);

        android.support.v7.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setTitle("SV Recorder");
            setSupportActionBar(toolbar);
        }
        SharedPreferences sharedPreferences = getSharedPreferences("MYPREF", MODE_PRIVATE);
        String data= sharedPreferences.getString("Key","No save Data");
        this.email=data;
        if (checkPermissionFromDevice())
            requestPermissions();
        Toast.makeText(Record2Activity.this,email,Toast.LENGTH_LONG).show();
        btnStart= findViewById(R.id.RecordingButton);
        btnSettings= findViewById(R.id.CustomRecorder);
        btnStart.setEnabled(true);
        btnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1= new Intent(Record2Activity.this,RecordActivity.class);
                startActivityForResult(intent1,200);
            }
        });
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkPermissionFromDevice()) {
                    Record2Activity.val2=3600000;
                    Intent it =new Intent(Record2Activity.this,BackgroundService.class);
                    startService(it);
                    btnStart.setEnabled(false);
                } else {
                    requestPermissions();
                }
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 200) {
            if (resultCode == RESULT_OK) {
                String val1 = data.getStringExtra("key2");
                Record2Activity.val2= Long.parseLong(val1);
                btnStart= findViewById(R.id.RecordingButton);
                btnStart.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (checkPermissionFromDevice()) {
                            Intent it =new Intent(Record2Activity.this,BackgroundService.class);
                            startService(it);
                            btnStart.setEnabled(false);
                        } else {
                            requestPermissions();
                        }
                    }
                });
            }
        }
    }
    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.PROCESS_OUTGOING_CALLS,
                Manifest.permission.CALL_PHONE,
        }, REQUEST_PERMISSION_CODE);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {
            case REQUEST_PERMISSION_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
            break;
        }
    }
    private boolean checkPermissionFromDevice() {
        int write_external_storage_result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int record_audio_result = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);
        int call_phone_hide=ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE);
        int call_phone_hide2=ContextCompat.checkSelfPermission(this, Manifest.permission.PROCESS_OUTGOING_CALLS);

        return write_external_storage_result == PackageManager.PERMISSION_GRANTED &&
                record_audio_result == PackageManager.PERMISSION_GRANTED &&
                call_phone_hide == PackageManager.PERMISSION_GRANTED&&
                call_phone_hide2 == PackageManager.PERMISSION_GRANTED;
    }

}
