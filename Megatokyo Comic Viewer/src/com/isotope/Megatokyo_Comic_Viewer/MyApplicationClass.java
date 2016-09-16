package com.isotope.Megatokyo_Comic_Viewer;

import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.ads.AdRequest;
import com.google.ads.AdView;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import uk.co.senab.photoview.PhotoViewAttacher;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;

/**
 * Created by Sam on 3/12/14.
 */
public class MyApplicationClass extends Application {
    SharedPreferences Megatokyo_data;
    @Override
    public void onCreate() {
        super.onCreate();
       // doStuff();
    }
    @Override
    public void onTerminate() {
        super.onTerminate();
        SharedPreferences.Editor editor = Megatokyo_data.edit();
        editor.putInt("dunno", 0);
        editor.commit();
    }
    public void doStuff() {
        SharedPreferences.Editor editor = Megatokyo_data.edit();
        editor.putInt("dunno", 0);
        editor.commit();
    }
}