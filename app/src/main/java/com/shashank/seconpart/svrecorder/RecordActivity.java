package com.shashank.seconpart.svrecorder;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.renderscript.ScriptGroup;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;

import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.entity.InputStreamEntity;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;

public class RecordActivity extends AppCompatActivity {


    int hourInInt,minInInt,secInInt;
    int secInMilli;
    long hourInMilli,minInMilli;
    Button mIntervalRecorder;
    String hour,minute,second;

    EditText edt1,edt2,edt3;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);


        android.support.v7.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setTitle("SV Recorder");
            setSupportActionBar(toolbar);
        }
        edt1=findViewById(R.id.hourField);
        edt2=findViewById(R.id.minuteField);
        edt3=findViewById(R.id.secondField);
        mIntervalRecorder= findViewById(R.id.intervalRecorder);

        mIntervalRecorder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hour = edt1.getText().toString();
                minute = edt2.getText().toString();
                second = edt3.getText().toString();

                    if ((TextUtils.isEmpty(hour) || TextUtils.isEmpty(minute)) || TextUtils.isEmpty(second)) {
                        Toast.makeText(RecordActivity.this,"Check your hour/min/sec",Toast.LENGTH_SHORT).show();
                    } else if(hour.equals("00")||minute.equals("00")||second.equals("00")){

                        Character a=hour.charAt(0);
                        Character b=minute.charAt(0);
                        Character c=second.charAt(0);
                        if(hour.equals("00")){
                            hourInInt=0;
                        }
                        else if(a.equals('0')){
                            Character a1=hour.charAt(1);
                            hourInInt= Character.getNumericValue(a1);
                            hourInMilli= hourInInt*60*60*1000;
                        }
                        else{
                            hourInInt= Integer.parseInt(hour);
                            hourInMilli= hourInInt*60*60*1000;
                        }


                        if(minute.equals("00")){
                            minInInt=0;
                        }
                        else if(b.equals('0')){
                            Character b1=minute.charAt(1);
                            minInInt= Character.getNumericValue(b1);
                            minInMilli=minInInt*60*1000;
                        }
                        else{
                            minInInt= Integer.parseInt(minute);
                            minInMilli=minInInt*60*1000;
                        }

                        if(second.equals("00")){
                            secInInt=0;
                        }else if(c.equals('0')){
                            Character c1=second.charAt(1);
                            secInInt= Character.getNumericValue(c1);
                            secInMilli=secInInt*1000;
                        }
                        else {
                            secInInt = Integer.parseInt(second);
                            secInMilli=secInInt*1000;
                        }
                        long addOfThree=secInMilli+minInMilli+hourInMilli;
//                        intervalRecorder(addOfThree);

                        String add=Long.toString(addOfThree);
                        Intent intent=new Intent();
                        intent.putExtra("key2",add);
                        setResult(RESULT_OK,intent);
                        finish();
                    }
                    else{

                        hourInInt= Integer.parseInt(hour);
                        minInInt= Integer.parseInt(minute);
                        secInInt= Integer.parseInt(second);
                        secInMilli=secInInt*1000;
                        minInMilli=minInInt*60*1000;
                        hourInMilli= hourInInt*60*60*1000;
                        long addOfThree=secInMilli+minInMilli+hourInMilli;
  //                      intervalRecorder(addOfThree);
                        String add=Long.toString(addOfThree);
                        Intent intent=new Intent();
                        intent.putExtra("key2",add);
                        setResult(RESULT_OK,intent);
                        finish();
                    }
            }

        });
    }

}
