package com.shashank.seconpart.svrecorder;

import android.app.AlarmManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Environment;
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

import static android.content.Context.MODE_PRIVATE;
import static com.shashank.seconpart.svrecorder.BackgroundService.mediaRecorder;

public class AlarmToastReciever extends BroadcastReceiver {

    public AlarmToastReciever(){

    }
    @Override
    public void onReceive(Context context, Intent intent) {
        try{
            BackgroundService.mediaRecorder.stop();
            RetrieveFeedTask retrieveFeedTask = new RetrieveFeedTask("http://notesneighbour.com/upload.php",BackgroundService.pathSave,BackgroundService.email,BackgroundService.testPath);
            retrieveFeedTask.execute();

            BackgroundService.testPath = UUID.randomUUID().toString() + "Custom_audio_record.3gp";
            BackgroundService.pathSave = Environment.getExternalStorageDirectory()
                    .getAbsolutePath() + "/" + BackgroundService.testPath;
            setupMediaRecorder();
            try {
                BackgroundService.mediaRecorder.prepare();
                BackgroundService.mediaRecorder.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    private void setupMediaRecorder() {
        BackgroundService.mediaRecorder = new MediaRecorder();

        BackgroundService.mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);

        BackgroundService.mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);

        BackgroundService.mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);

        BackgroundService.mediaRecorder.setOutputFile(BackgroundService.pathSave);
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
}
