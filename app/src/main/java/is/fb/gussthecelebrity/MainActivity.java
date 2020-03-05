package is.fb.gussthecelebrity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

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
    ArrayList<String> celebUrls=new ArrayList<String>();
    ArrayList<String> celebNames=new ArrayList<String>();
    int chosCleb=0;
   ImageView imageView;
   int locationOfCorectAns=0;
   String[] answers=new String[4];
   Button button1,button2,button3,button4;
   Pattern p;
   Matcher m;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView=findViewById(R.id.image);
        button1=findViewById(R.id.button1);
        button2=findViewById(R.id.button2);
        button3=findViewById(R.id.button3);
        button4=findViewById(R.id.button4);
        DownloadTask task=new DownloadTask();
        String result="";
        try {
            result=task.execute("http://www.posh24.se/kandisar").get();
            Log.i("urls",result);
                String[] splitResult = result.split("<div class=\"sidebarContainer\">");
                 p = Pattern.compile("<img src=\"(.*?)\"");
                 m = p.matcher(splitResult[0]);
                while (m.find()) {
                    celebUrls.add(m.group(1));
                }
                p = Pattern.compile("alt=\"(.*?)\"");
                m = p.matcher(splitResult[0]);
                while (m.find()) {
                    celebNames.add(m.group(1));
                }
            creatNewQuestion();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
    public void creatNewQuestion(){
        Random random = new Random();
        chosCleb = random.nextInt(celebUrls.size());
        ImageDownloader imageDownloader = new ImageDownloader();
        Bitmap clebImage;
        try {
            clebImage = imageDownloader.execute(celebUrls.get(chosCleb)).get();
            locationOfCorectAns=random.nextInt(4);
            int incorect;
            for(int i=0;i<4;i++){
                if(i==locationOfCorectAns){
                    answers[i]=celebNames.get(chosCleb);
                }
                else {
                    incorect=random.nextInt(celebUrls.size());
                    while (incorect==chosCleb)
                    {
                        incorect=random.nextInt(celebUrls.size());
                    }
                    answers[i]=celebNames.get(incorect);
                }
            }
            imageView.setImageBitmap(clebImage);
            button1.setText(answers[0]);
            button2.setText(answers[1]);
            button3.setText(answers[2]);
            button4.setText(answers[3]);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    public void celebChosen(View view){
        if(view.getTag().toString().equals(Integer.toString(locationOfCorectAns))){
            Toast.makeText(this,"correct",Toast.LENGTH_LONG).show();
        }
        else {
            Toast.makeText(this,"incorrect it was "+celebNames.get(chosCleb),Toast.LENGTH_LONG).show();
        }
        creatNewQuestion();


    }
    public class DownloadTask extends AsyncTask<String,Void,String>{

        @Override
        protected String doInBackground(String... urls) {
             String result="";
            URL url;
            HttpURLConnection urlConnection=null;
            try {
                url=new URL(urls[0]);
                urlConnection=(HttpURLConnection)url.openConnection();
                InputStream in=urlConnection.getInputStream();
                InputStreamReader reader=new InputStreamReader(in);
                int data=reader.read();
                while (data!=-1){
                    char current=(char)data;
                    result+=current;
                    data=reader.read();
                }
                return result;
            }catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }
    }
    public class ImageDownloader extends AsyncTask<String,Void, Bitmap>{

        @Override
        protected Bitmap doInBackground(String... urls) {
            try {
                URL url=new URL(urls[0]);
                HttpURLConnection connection=(HttpURLConnection)url.openConnection();
                connection.connect();
                InputStream stream=connection.getInputStream();
                Bitmap bitmap= BitmapFactory.decodeStream(stream);
                return bitmap;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
