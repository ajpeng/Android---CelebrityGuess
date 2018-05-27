package com.example.alex.guessthecelebrity;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends AppCompatActivity {

    public class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls){
            Log.i("URL", urls[0]);
            return null;
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String result = null;

        DownloadTask task = new DownloadTask();
        try{result = task.execute("https://www.google.com").get();
        } catch(Exception e) {
            e.printStackTrace();
        }

        Log.i("Result", result);
    }
}
