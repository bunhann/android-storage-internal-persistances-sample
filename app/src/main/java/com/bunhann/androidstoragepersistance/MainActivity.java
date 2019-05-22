package com.bunhann.androidstoragepersistance;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private Button btnDownload, btnPlay;
    private EditText edURL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnDownload = (Button) findViewById(R.id.btnDownload);
        btnPlay = (Button) findViewById(R.id.btnViewFile);
        edURL = (EditText) findViewById(R.id.edURL);

        if (isConnectingToInternet()){
            btnDownload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String url = edURL.getText().toString();
                    if (url.length()>0) {
                        new DownloadTask(v.getContext(), btnDownload, url);
                    }

                }
            });
        }

        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ContextWrapper cw = new ContextWrapper(getApplicationContext());
                File internalStorage = cw.getDir("private_video", Context.MODE_PRIVATE);

                Intent intent = new Intent(Intent.ACTION_VIEW);

                try {
                    String downloadName = new File(new URL(edURL.getText().toString()).getPath()).getName();

                    File videoFile = new File (internalStorage, downloadName);
                    videoFile.setReadable(true, false);

                    intent.setDataAndType(Uri.fromFile(videoFile), "video/mp4");
                    startActivity(intent);

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this, "Unable to open! " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private boolean isConnectingToInternet() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager
                .getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected())
            return true;
        else
            return false;
    }

}
