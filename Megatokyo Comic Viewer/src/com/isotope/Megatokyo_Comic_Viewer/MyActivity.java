package com.isotope.Megatokyo_Comic_Viewer;

import android.app.*;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.support.v4.util.LruCache;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import com.startapp.android.publish.StartAppSDK;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import uk.co.senab.photoview.PhotoViewAttacher;


import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;
import java.util.Random;


public class MyActivity extends Activity {
    final Context context = this;
    int currentcomic;
    int latestComic;
    String comicString;
    String totalTitle;
    ImageDownloader loadthisURL = new ImageDownloader();
    SharedPreferences Megatokyo_data;
    public static String filename = "first";
    public static String filename1 = "second";

    private LruCache<String, Bitmap> mMemoryCache;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Megatokyo_data = getSharedPreferences("Latest", 0);
        super.onCreate(savedInstanceState);
        StartAppSDK.init(this, "107171153", "207061844", true);
        setContentView(R.layout.main);
        loadFirstComic();
        buttonlistener();
        beginService();


        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        // Use 1/8th of the available memory for this memory cache.
        final int cacheSize = maxMemory / 8;
        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                // The cache size will be measured in kilobytes rather than
                // number of items.
                return  (bitmap.getRowBytes() * bitmap.getHeight()) / 1024;
            }
        };
    }
    public void beginService() {
       if (Servicecheck()) {
           Calendar cal = Calendar.getInstance();
           Intent intent = new Intent(this, TheService.class);
           PendingIntent pintent = PendingIntent.getService(this, 0, intent, 0);
           AlarmManager alarm = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
// Start every 30 seconds
           alarm.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), 1200 * 1000, pintent);
       } else {
           //do Nothing!!!!!!!!!~!
       }
    }
    public boolean Servicecheck(){
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE))
        {
            if ("com.isotope.Megatokyo_Comic_Viewer.TheService"
                    .equals(service.service.getClassName()))
            {
                return true;
            }
        }
        return false;
    }
    public void loadFirstComic() {
        if (checkNet()){
            try{
                Document doc = Jsoup.connect("http://megatokyo.com/").get();
                Elements spans = doc.select("span#strip-bl");
                Element mySpan = spans.first();
                Element image = mySpan.child(0);
                final String cakeString= image.attr("src");
                if (cakeString.length() > 0){
                    // Toast.makeText(getApplicationContext(), "off", Toast.LENGTH_SHORT).show();
                }
                String shortenedString = cakeString.substring(7,11);
                currentcomic = Integer.parseInt(shortenedString);
                latestComic = currentcomic;
                comicString = Integer.toString(currentcomic);
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
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        TextView view = (TextView) findViewById(R.id.textView);
        String restoredTitle = Megatokyo_data.getString(Integer.toString(currentcomic), "An Error Occured");

        view.setText(restoredTitle);
    }
    public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null) {
            mMemoryCache.put(key, bitmap);
        }
    }
    public Bitmap getBitmapFromMemCache(String key) {
        return mMemoryCache.get(key);
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
                    String comicString = Integer.toString(currentcomic);
                    prev.setEnabled(false);
                    rand.setEnabled(false);
                    next.setEnabled(false);
                    last.setEnabled(false);
                    ImageDownloader loadAncomic = new ImageDownloader();
                    loadAncomic.execute(currentcomic);
                }else{
                    nopeNet();
                }
            }
        });
        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkNet()) {
                    currentcomic--;
                    String comicString = Integer.toString(currentcomic);
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
                    String comicString = Integer.toString(currentcomic);
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
                    String comicString = Integer.toString(currentcomic);
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
                    String comicString = Integer.toString(currentcomic);
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
                Megatokyo_data = getSharedPreferences(filename1, 0);
                int number = params[0];
                String key = Integer.toString(number);
                String result = Megatokyo_data.getString(key, "default");
                if (result.equals("default")) {
                    Document doc = Jsoup.connect("http://megatokyo.com/strip/"+ number).get();
                    Elements titleDivs = doc.select("div#comic");  //start of title getter
                    Element myDivs = titleDivs.first();
                    Element myDiv = myDivs.child(0);
                    Element theDiv = myDiv.child(1);
                    String title = theDiv.ownText();    //end title getter
                    SharedPreferences.Editor editor = Megatokyo_data.edit();
                    editor.putString(key, title);
                    editor.commit();
                    return title;
                }else{
               /* Document doc = Jsoup.connect("http://megatokyo.com/strip/"+ number).get();

                Elements titleDivs = doc.select("div#comic");  //start of title getter
                Element myDivs = titleDivs.first();
                Element myDiv = myDivs.child(0);
                Element theDiv = myDiv.child(1);
                String title = theDiv.ownText();    //end title getter */
                final String title = Megatokyo_data.getString(key, "default");
                return title;
                }
            }catch(Exception e){
                Log.e("Image", "Failed to load image", e);
                Toast.makeText(getApplicationContext(), "An Error Occured", Toast.LENGTH_LONG).show();
            }
            return null;
        }
        protected void onProgressUpdate(Integer... params){

        }
        protected void onPostExecute( final String title){
           TextView durrrr = (TextView) findViewById(R.id.textView);
            durrrr.setText(title);
            totalTitle = title;
            SharedPreferences.Editor editor = Megatokyo_data.edit();
            editor.putString(Integer.toString(currentcomic), totalTitle);
            editor.commit();
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
                Megatokyo_data = getSharedPreferences(filename, 0);
                int number = params[0];
                String key = Integer.toString(number);
                String result = Megatokyo_data.getString(key, "default");
                if (result.equals("default")) {
                    Document doc = Jsoup.connect("http://megatokyo.com/strip/"+ number).get();
                    Elements spans = doc.select("span#strip-bl");
                    Element mySpan = spans.first();
                    Element image = mySpan.child(0);
                    final String srcString = image.attr("src");
                    SharedPreferences.Editor editor = Megatokyo_data.edit();
                    editor.putString(key, srcString);
                    editor.commit();
                    URL url = new URL("http://megatokyo.com/"+ srcString);
                    HttpURLConnection httpCon =
                            (HttpURLConnection)url.openConnection();
                    if(httpCon.getResponseCode() != 200)
                        throw new Exception("Failed to connect");
                    InputStream is = httpCon.getInputStream();
                    return BitmapFactory.decodeStream(is);
                }  else{
                    final String srcString = Megatokyo_data.getString(key, "default");
                    URL url = new URL("http://megatokyo.com/"+ srcString);
                    HttpURLConnection httpCon =
                            (HttpURLConnection)url.openConnection();
                    if(httpCon.getResponseCode() != 200)
                        throw new Exception("Failed to connect");
                    InputStream is = httpCon.getInputStream();
                    return BitmapFactory.decodeStream(is);
                }
            }catch(Exception e){
                Log.e("Image", "Failed to load image", e);
                Toast.makeText(getApplicationContext(), "off", Toast.LENGTH_SHORT).show();
            }
            return null;
        }
        protected void onProgressUpdate(Integer... params){

        }
        protected void onPostExecute(final Bitmap img){
            ImageView iv = (ImageView) findViewById(R.id.imageView);
            if(iv!=null && img!=null){
                iv.setImageBitmap(img);
                new PhotoViewAttacher(iv);
                addBitmapToMemoryCache(Integer.toString(currentcomic), img);

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
  /*  @Override
    public void onResume() {
        super.onResume();
        ImageView view = (ImageView) findViewById(R.id.imageView);
        //view.setImageBitmap(img);
    }                                  */
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
                            String comicString = Integer.toString(currentcomic);
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
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setContentView(R.layout.main);

        } else {
            setContentView(R.layout.main);
        }
    }
}

