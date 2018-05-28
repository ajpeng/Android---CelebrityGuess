package com.example.alex.guessthecelebrity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    ArrayList<String> celebURLs = new ArrayList<String>();
    ArrayList<String> celebNames = new ArrayList<String>();
    ImageView celebImageView;
    int chosenCeleb = 0;
    int locationOfCorrectAnswer = 0;
    String[] answers = new String[4];
    Button answerBtn0;
    Button answerBtn1;
    Button answerBtn2;
    Button answerBtn3;

    public class ImageDownloader extends AsyncTask<String, Void, Bitmap>{

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
            catch(MalformedURLException e){
                e.printStackTrace();
            }
            catch(Exception e){
                e.printStackTrace();
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

        celebImageView = findViewById(R.id.celebImageView);
        //Setting button views
        answerBtn0 = (Button) findViewById(R.id.answerBtn0);
        answerBtn1 = (Button) findViewById(R.id.answerBtn1);
        answerBtn2 = (Button) findViewById(R.id.answerBtn2);
        answerBtn3 = (Button) findViewById(R.id.answerBtn3);

        DownloadTask task = new DownloadTask();

        try{
            result = task.execute("http://www.posh24.se/kandisar").get();
            // parsing http content and filtering with regex
            String[] splitResult = result.split("<div class=\"sidebarContainer\">");
            // reading image src location
            Pattern p = Pattern.compile("<img src=\"(.*?)\"");
            Matcher m = p.matcher(splitResult[0]);

            while(m.find()) {
                celebURLs.add(m.group(1));
            }

             p = Pattern.compile("alt=\"(.*?)\"");
             m = p.matcher(splitResult[0]);

            while(m.find()) {
                celebNames.add(m.group(1));
            }

            generateNewQuestion();



        } catch(Exception e) {
            e.printStackTrace();
        }

        //Log.i("Result", result);
    }

    public void generateNewQuestion(){
        Random random = new Random();
        chosenCeleb = random.nextInt(celebURLs.size());

        ImageDownloader imgTask = new ImageDownloader();
        Bitmap celebImg;

        try {
            celebImg = imgTask.execute(celebURLs.get(chosenCeleb)).get();
            celebImageView.setImageBitmap(celebImg);
            locationOfCorrectAnswer = random.nextInt(4);

            int incorrectAnswerLocation;

            for(int i=0 ; i < 4 ; i++){

                if( i == locationOfCorrectAnswer){

                    answers[i] = celebNames.get(chosenCeleb);

                } else {
                    incorrectAnswerLocation = random.nextInt(celebURLs.size());
                    while(incorrectAnswerLocation == chosenCeleb){
                        incorrectAnswerLocation = random.nextInt(celebURLs.size());
                    }
                    answers[i] = celebNames.get(incorrectAnswerLocation);
                }
            }

            answerBtn0.setText(answers[0]);
            answerBtn1.setText(answers[1]);
            answerBtn2.setText(answers[2]);
            answerBtn3.setText(answers[3]);


        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }


        //<img src=
        //<div class="sidebarContainer">
    }

    public void celebChosen(View view){
        if(view.getTag().toString().equals(Integer.toString(locationOfCorrectAnswer))){
            Toast.makeText(getApplicationContext(), "Correct!" ,Toast.LENGTH_LONG).show();
            Log.i("Correct" ,"Correct");
        } else {
            Toast.makeText(getApplicationContext(), "Wrong! It was " + celebNames.get(chosenCeleb) , Toast.LENGTH_LONG).show();

            Log.i("INCORRECT" ,"INCORRECT");
        }
        System.out.println("  " + chosenCeleb);

        generateNewQuestion();
     //   System.out.println(view.getTag().toString());
        //Log.i("celebChosen" , "A" );
    }
}
