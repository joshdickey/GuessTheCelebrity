package com.warpgatetechnologies.guessthecelebrity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.IOException;
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

    Button name1Btn, name2Btn, name3Btn, name4Btn;
    ImageView image;
    private  static ArrayList<Bitmap> images;
    private  static ArrayList<String> names;
    int count;
    String result;
    private static ArrayList<String> mImageUrl;
    private int mChosenCeleb = 0;
    private int mCorrectButtonChoice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        images = new ArrayList<>();
        names = new ArrayList<>();
        mImageUrl = new ArrayList<>();
        String[] answers = new String[4];

        name1Btn = findViewById(R.id.button1);
        name2Btn = findViewById(R.id.button2);
        name3Btn = findViewById(R.id.button3);
        name4Btn = findViewById(R.id.button4);
        image = findViewById(R.id.imageView);


        try {
            DownloadWebpage task = new DownloadWebpage();
            result = task.execute("http://www.posh24.se/kandisar").get();

            String[] splitResult = result.split("<div class=\"sidebarContainer\">");

            Pattern p = Pattern.compile("img src=\"(.*?)\"");
            Matcher m = p.matcher(splitResult[0]);

            while (m.find()){
                mImageUrl.add(m.group(1));
            }

            p = Pattern.compile("alt=\"(.*?)\"");
            m = p.matcher(splitResult[0]);

            while (m.find()){
                names.add(m.group(1));
            }


            Random random = new Random();
            mChosenCeleb = random.nextInt(mImageUrl.size());
            mCorrectButtonChoice = random.nextInt(4);

            DownloadImage imageTask = new DownloadImage();

            Bitmap myBitmap;
            myBitmap = imageTask.execute(mImageUrl.get(mChosenCeleb)).get();

            if (myBitmap != null) {
                image.setImageBitmap(myBitmap);

                for (int i = 0; i < 4; i++) {
                    if (i == mCorrectButtonChoice){
                        answers[i] = names.get(mChosenCeleb);
                    }else{
                        answers[i] = names.get(random.nextInt(mImageUrl.size()));
                    }

                }

            }


        } catch (Exception e) {
            e.printStackTrace();
        }


        name1Btn.setText(answers[0]);
        name2Btn.setText(answers[1]);
        name3Btn.setText(answers[2]);
        name4Btn.setText(answers[3]);



    }

    protected void buttonClick(View view){


//        name1Btn.setText(names.get(count));
//        image.setImageBitmap(images.get(count));
        Log.d("bntClick", view.getTag().toString());
        count++;

    }

    private static class DownloadImage extends AsyncTask<String, Void, Bitmap>{

        @Override
        protected Bitmap doInBackground(String... urls) {

            URL url;
            HttpURLConnection connection;
            InputStream inputStream;

            try {
                url = new URL(urls[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                inputStream = connection.getInputStream();

                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

                return bitmap;

            } catch (IOException e) {
                        e.printStackTrace();
            }
            return null;
        }
    }

    private static class DownloadWebpage extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {


            URL url;
            HttpURLConnection connection = null;
            InputStream inputStream;
            String s = "";
            InputStreamReader reader;

            //load the webpage
            try {
                 url = new URL(urls[0]);

                connection = (HttpURLConnection) url.openConnection();

                inputStream = connection.getInputStream();

                reader = new InputStreamReader(inputStream);

                int data = reader.read();

                while (data != -1){
                    char current = (char) data;

                    s += current;

                    data = reader.read();
                }


//                Pattern pName = Pattern.compile("alt=\"(.*?)\"");
//                Matcher mName = pName.matcher(s);

                //parse out the image urls
//                while (m.find()){
//                    imageUrl.add(m.group(1));
//
//                    try {
//                        url = new URL(m.group(1));
//                        connection = (HttpURLConnection) url.openConnection();
//                        connection.connect();
//
//                        inputStream = connection.getInputStream();
//
//                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
//                        images.add(bitmap);
//
//                        //parse out the celb names
//                        while (mName.find()){
//                            names.add(mName.group(1));
//                        }
//
//                    } catch (IOException e) {
//                        e.printStackTrace();
////
//                }
//                }

                return s;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }


            return null;
        }
    }

}
