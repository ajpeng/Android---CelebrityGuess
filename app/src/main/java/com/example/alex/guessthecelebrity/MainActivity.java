package com.example.alex.guessthecelebrity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    ArrayList<String> celebURLs = new ArrayList<String>();
    ArrayList<String> celebNames = new ArrayList<String>();
    int chosenCelebs = 0;

    public class ImageDownload extends AsyncTask<String, Void, Bitmap>{

        @Override
        protected Bitmap doInBackground(String... urls) {
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                InputStream inputStream = connection.getInputStream();
                Bitmap myBitMap = BitmapFactory.decodeStream(inputStream);

                return myBitMap;

            }
            catch(Exception e){

            }
            return null;
        }
    }

    public class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls){
            String result = "";
            URL url;
            HttpURLConnection urlConnection = null;

            try{
                //accessing String... varargs urls
                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();

                InputStream inputStream = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(inputStream);

                int data = reader.read();

                //while data stream is not empty continue reading
                while(data != -1 ){
                    char currentChar = (char) data;
                    result += currentChar;
                    // replaces current char w/ the next char in the stream
                    data = reader.read();
                }

                return result;

            } catch(Exception e) {
                e.printStackTrace();
            }

            //Log.i("URL", urls[0]);
            return null;
        }
    }




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String result = null;

        DownloadTask task = new DownloadTask();

        try{
            result = task.execute("http://www.posh24.se/kandisar").get();
            // parsing http content and filtering with regex
            String[] splitResult = result.split("<div class=\"sidebarContainer\">");
            // reading image src location
            Pattern p = Pattern.compile("src=\"*(.*?)\"");
            Matcher m = p.matcher(splitResult[0]);

            while(m.find()) {
                celebURLs.add(m.group(1));
            }

             p = Pattern.compile("src=\"*(.*?)\"");
             m = p.matcher(splitResult[0]);

            while(m.find()) {
                celebNames.add(m.group(1));
            }

            Random random = new Random();
            chosenCelebs = random.nextInt(celebURLs.size());

            //<img src=
            //<div class="sidebarContainer">

        } catch(Exception e) {
            e.printStackTrace();
        }

        //Log.i("Result", result);
    }

    public void celebChosen(View view){

    }
}
