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
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    Button name1Btn, name2Btn, name3Btn, name4Btn;
    ImageView image;
    private  static ArrayList<Bitmap> images;
    private  static ArrayList<String> names;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        images = new ArrayList<>();
        names = new ArrayList<>();

        name1Btn = findViewById(R.id.button1);
        name2Btn = findViewById(R.id.button2);
        name3Btn = findViewById(R.id.button3);
        name4Btn = findViewById(R.id.button4);
        image = findViewById(R.id.imageView);

        DownloadImages task = new DownloadImages();
        task.execute("http://www.posh24.se/kandisar");
    }

    protected void buttonClick(View view){

        Log.d("bntClick", view.getTag().toString());

    }

    private static class DownloadImages extends AsyncTask<String, Void, Bitmap>{

        @Override
        protected Bitmap doInBackground(String... urls) {

            ArrayList<String> imageUrl = new ArrayList<>();

            String s = "";

            try {
                URL url = new URL(urls[0]);

                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                connection.connect();

                InputStream inputStream = connection.getInputStream();

                InputStreamReader reader = new InputStreamReader(inputStream);


                int data = reader.read();

                while (data != -1){
                    char current = (char) data;

                    s += current;

                    data = reader.read();
                }

                Pattern p = Pattern.compile("img src=\"(.*?)\"");
                Matcher m = p.matcher(s);



                while (m.find()){
                    imageUrl.add(m.group(1));
                }

                p = Pattern.compile("alt=\"(.*?)\"");
                m = p.matcher(s);

                while (m.find()){
                    names.add(m.group(1));
                }


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }


            return null;
        }
    }

}
