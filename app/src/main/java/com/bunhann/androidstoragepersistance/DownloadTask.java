package com.bunhann.androidstoragepersistance;

import android.content.Context;
import android.content.ContextWrapper;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.widget.Button;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class DownloadTask {

    private static final String TAG = "Download Task";
    private Context context;
    private Button buttonText;
    private String downloadUrl = "", downloadFileName = "";

    public DownloadTask(Context context, Button buttonText, String downloadUrl) {
        this.context = context;
        this.buttonText = buttonText;
        this.downloadUrl = downloadUrl;

        try {
            downloadFileName = new File(new URL(downloadUrl).getPath()).getName();//Create file name by picking download file name from URL
        } catch (MalformedURLException e) {
            e.printStackTrace();

        }
        Log.e(TAG, downloadFileName);

        //Start Downloading Task
        new DownloadingTask().execute();
    }

    private class DownloadingTask extends AsyncTask<Void, Void, Void> {

        File internalStorage = null;
        File outputFile = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            buttonText.setEnabled(false);
            buttonText.setText("Download Started");//Set Button Text when download started
        }

        @Override
        protected void onPostExecute(Void result) {
            try {
                if (outputFile != null) {
                    buttonText.setEnabled(true);
                    buttonText.setText("Download");//If Download completed then change button text
                } else {
                    buttonText.setText("Failed!");//If download failed change button text
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            buttonText.setEnabled(true);
                            buttonText.setText("Download Again?");//Change button text again after 3sec
                        }
                    }, 3000);

                    Log.e(TAG, "Download Failed");

                }
            } catch (Exception e) {
                e.printStackTrace();

                //Change button text if exception occurs
                buttonText.setText("Failed!");
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        buttonText.setEnabled(true);
                        buttonText.setText("Download Again?");
                    }
                }, 3000);
                Log.e(TAG, "Download Failed with Exception - " + e.getLocalizedMessage());

            }


            super.onPostExecute(result);
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            try {
                URL url = new URL(downloadUrl);//Create Download URl
                HttpURLConnection c = (HttpURLConnection) url.openConnection();//Open Url Connection
                c.setRequestMethod("GET");//Set Request Method to "GET" since we are grtting data
                c.connect();//connect the URL Connection

                //If Connection response is not OK then show Logs
                if (c.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    Log.e(TAG, "Server returned HTTP " + c.getResponseCode()
                            + " " + c.getResponseMessage());

                }

                ContextWrapper cw = new ContextWrapper(context.getApplicationContext());
                internalStorage = cw.getDir("private_video", Context.MODE_PRIVATE);


                //If File is not present create directory
                if (!internalStorage.exists()) {
                    internalStorage.mkdir();
                    Log.e(TAG, "Directory Created.");
                }

                outputFile = new File(internalStorage, downloadFileName);//Create Output file in Main File

                //Create New File if not present
                if (!outputFile.exists()) {
                    outputFile.createNewFile();
                    Log.e(TAG, "File Created");
                }

                FileOutputStream fos = new FileOutputStream(outputFile);//Get OutputStream for NewFile Location

                InputStream is = c.getInputStream();//Get InputStream for connection

                byte[] buffer = new byte[1024];//Set buffer type
                int len1 = 0;//init length
                while ((len1 = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, len1);//Write new file
                }

                //Close all connection after doing task
                fos.close();
                is.close();

            } catch (Exception e) {

                //Read exception if something went wrong
                e.printStackTrace();
                outputFile = null;
                Log.e(TAG, "Download Error Exception " + e.getMessage());
            }

            return null;
        }
    }
}
