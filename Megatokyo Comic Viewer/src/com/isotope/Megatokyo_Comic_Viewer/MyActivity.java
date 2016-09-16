package com.isotope.Megatokyo_Comic_Viewer;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import uk.co.senab.photoview.PhotoViewAttacher;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Random;

public class MyActivity extends Activity {
    final Context context = this;
    int currentcomic;
    int latestComic;
    ImageDownloader loadthisURL = new ImageDownloader();
    SharedPreferences Megatokyo_data;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Megatokyo_data = getSharedPreferences("First_File", 0);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        //FirstImageDownloader startup = new FirstImageDownloader();
        //startup.execute();
        if (checkNet()){
        try{
            Document doc = Jsoup.connect("http://megatokyo.com/").get();
            Elements spans = doc.select("span#strip-bl");
            Element mySpan = spans.first();
            Element image = mySpan.child(0);
            final String cakeString= image.attr("src");
            String shortenedString = cakeString.substring(7,11);
            currentcomic = Integer.parseInt(shortenedString);
            latestComic = currentcomic;
            ImageDownloader startup = new ImageDownloader();
            startup.execute(currentcomic);
            final Button first = (Button) findViewById(R.id.first);
            final Button prev = (Button) findViewById(R.id.prev);
            final Button rand = (Button) findViewById(R.id.rand);
            final Button next = (Button) findViewById(R.id.next);
            final Button last = (Button) findViewById(R.id.last);
            first.setEnabled(false);
            prev.setEnabled(false);
            rand.setEnabled(false);
            next.setEnabled(false);
            last.setEnabled(false);

        }catch(Exception e){
            Log.e("Image", "Failed to load image", e);
        }
        }else{
                nopeNet();
            }
        buttonlistener();
    }


    public void buttonlistener() {
        final Button first = (Button) findViewById(R.id.first);
        final Button prev = (Button) findViewById(R.id.prev);
        final Button rand = (Button) findViewById(R.id.rand);
        final Button next = (Button) findViewById(R.id.next);
        final Button last = (Button) findViewById(R.id.last);
        final ImageDownloader loadcomic = new ImageDownloader();
        first.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkNet()) {
                    currentcomic = 1;
                    prev.setEnabled(false);
                    rand.setEnabled(false);
                    next.setEnabled(false);
                    last.setEnabled(false);
                    ImageDownloader loadAncomic = new ImageDownloader();
                    loadAncomic.execute(currentcomic);
                }else{
                    nopeNet();
                  //  rand.setText("cake");
                }
            }
        });
        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkNet()) {
                    currentcomic--;
                    first.setEnabled(false);
                    prev.setEnabled(false);
                    rand.setEnabled(false);
                    next.setEnabled(false);
                    last.setEnabled(false);
                    ImageDownloader loadAncomic = new ImageDownloader();
                    loadAncomic.execute(currentcomic);
                }else{
                    nopeNet();
                 //  rand.setText("cake");
                }
            }
        });
        rand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkNet()) {
                    Random randComic = new Random();
                    currentcomic = randComic.nextInt(latestComic);
                    first.setEnabled(false);
                    prev.setEnabled(false);
                    rand.setEnabled(false);
                    next.setEnabled(false);
                    last.setEnabled(false);
                    ImageDownloader loadAcomic = new ImageDownloader();
                loadAcomic.execute(currentcomic);
                }else{
                   nopeNet();
                   // rand.setText("cake");
                }
            }
        });
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkNet()) {
                    currentcomic++;
                    first.setEnabled(false);
                    prev.setEnabled(false);
                    rand.setEnabled(false);
                    next.setEnabled(false);
                    last.setEnabled(false);
                    ImageDownloader loadAncomic = new ImageDownloader();
                    loadAncomic.execute(currentcomic);
                }else{
                    nopeNet();
                 //   rand.setText("cake");
                }
            }
        });
        last.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkNet()) {
                    currentcomic = latestComic;
                     first.setEnabled(false);
                    prev.setEnabled(false);
                    rand.setEnabled(false);
                    next.setEnabled(false);
                    last.setEnabled(false);
                    ImageDownloader loadAncomic = new ImageDownloader();
                    loadAncomic.execute(currentcomic);
                }else{
                   nopeNet();
                  //  rand.setText("cake");
                }
            }
        });
    }

    public boolean checkNet() {
        ConnectivityManager cn=(ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo nf=cn.getActiveNetworkInfo();
        if(nf == null)
        {
            Button rand = (Button) findViewById(R.id.rand);
            //rand.setText("null");
            return false;
        } else if(nf.isConnected()){
            return true;
        }
        else
        {
            return false;
        }
    }

    class ImageDownloader
            extends AsyncTask<Integer, Integer, String> {
        protected void onPreExecute(){
            launchDialog();
        }

        @Override
        protected String doInBackground(Integer... params) {
            //TODO Auto-generated method stub
            try{
                int number = params[0];
                Document doc = Jsoup.connect("http://megatokyo.com/strip/"+ number).get();
                Elements titleDivs = doc.select("div#comic");  //start of title getter
                Element myDivs = titleDivs.first();
                Element myDiv = myDivs.child(0);
                Element theDiv = myDiv.child(1);
                String title = theDiv.ownText();    //end title getter
                return title;
            }catch(Exception e){
                Log.e("Image", "Failed to load image", e);

            }
            return null;
        }
        protected void onProgressUpdate(Integer... params){

        }
        protected void onPostExecute(String title){
           TextView durrrr = (TextView) findViewById(R.id.textView);
            durrrr.setText(title);
           ImageDownloaderCont tim = new ImageDownloaderCont();
            tim.execute(currentcomic);
        }


        protected void onCancelled(){
            closeDialog();
            enablebuttons();
            oopsDialog();
        }
    }
    class ImageDownloaderCont
            extends AsyncTask<Integer, Integer, Bitmap> {
        protected void onPreExecute(){
            //launchDialog();
        }

        @Override
        protected Bitmap doInBackground(Integer... params) {
            //TODO Auto-generated method stub
            try{
                int number = params[0];
                Document doc = Jsoup.connect("http://megatokyo.com/strip/"+ number).get();
                Elements spans = doc.select("span#strip-bl");
                Element mySpan = spans.first();
                Element image = mySpan.child(0);
                final String srcString = image.attr("src");;
                URL url = new URL("http://megatokyo.com/"+ srcString);
                HttpURLConnection httpCon =
                        (HttpURLConnection)url.openConnection();
                if(httpCon.getResponseCode() != 200)
                    throw new Exception("Failed to connect");
                InputStream is = httpCon.getInputStream();
                return BitmapFactory.decodeStream(is);
            }catch(Exception e){
                Log.e("Image", "Failed to load image", e);
            }
            return null;
        }
        protected void onProgressUpdate(Integer... params){

        }
        protected void onPostExecute(Bitmap img){
            ImageView iv = (ImageView) findViewById(R.id.imageView);
            if(iv!=null && img!=null){
                iv.setImageBitmap(img);
                new PhotoViewAttacher(iv);
            }
            closeDialog();
            enablebuttons();
        }


        protected void onCancelled(){
            closeDialog();
            enablebuttons();
            oopsDialog();
        }
    }
    public void setComicTitle(String title){
        TextView titleText = (TextView) findViewById(R.id.textView);
        titleText.setText(title);
    }
    ProgressDialog ringProgressDialog;
    public void launchDialog() {
        ringProgressDialog = ProgressDialog.show(MyActivity.this, "Please wait ...", "Loading Comic...", true);
        ringProgressDialog.setCancelable(false);
    }

    public void closeDialog() {
        ringProgressDialog.dismiss();
    }
    public void enablebuttons() {
        final Button first = (Button) findViewById(R.id.first);
        final Button prev = (Button) findViewById(R.id.prev);
        final Button rand = (Button) findViewById(R.id.rand);
        final Button next = (Button) findViewById(R.id.next);
        final Button last = (Button) findViewById(R.id.last);
        first.setEnabled(true);
        prev.setEnabled(true);
        rand.setEnabled(true);
        next.setEnabled(true);
        last.setEnabled(true);
        doAThing();
    }
    public void doAThing() {
        final Button first = (Button) findViewById(R.id.first);
        final Button prev = (Button) findViewById(R.id.prev);
        final Button next = (Button) findViewById(R.id.next);
        final Button last = (Button) findViewById(R.id.last);
        if (currentcomic < 2){
            first.setEnabled(false);
            prev.setEnabled(false);
        } else if(currentcomic > (latestComic -1)){
            next.setEnabled(false);
            last.setEnabled(false);
        }
    }
    public void nopeNet() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                context);
        alertDialogBuilder.setTitle("Network Not Available");
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("Settings", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                    }
                })
                .setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
    public void searchDialog() {
        final EditText jumpCode = new EditText(this);
        jumpCode.setInputType(2);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                context);
        alertDialogBuilder.setTitle("Jump to Comic");
        alertDialogBuilder
                .setView(jumpCode)
                .setCancelable(false)
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        ImageDownloader hurrDeDerp = new ImageDownloader();
                        int pants = Integer.parseInt(jumpCode.getText().toString());
                        if (pants <= latestComic) {
                            currentcomic = pants;
                            hurrDeDerp.execute(currentcomic);
                              }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
    public void oopsDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                context);
        alertDialogBuilder.setTitle("An Error Occured");
        alertDialogBuilder
                .setCancelable(true)
                .setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        ImageDownloader hurrDeDerp = new ImageDownloader();
                        hurrDeDerp.execute(currentcomic);
                    }
                }
                )
                
                .setNegativeButton("Close", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
    public void aboutPage() {
        Intent goAbout = new Intent(this, aboutPage.class);
        startActivity(goAbout);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.layout.menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
           case R.id.search:
                searchDialog();
                 return true;
            case R.id.menu_list:
               aboutPage();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}

