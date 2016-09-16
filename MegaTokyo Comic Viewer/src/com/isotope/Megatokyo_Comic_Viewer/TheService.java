package com.isotope.Megatokyo_Comic_Viewer;

import android.app.*;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.Calendar;

/**
 * Created by Sam on 3/12/14.
 */
public class TheService extends Service {
    SharedPreferences Megatokyo_data;
    @Override
    public void onCreate() {
// TODO Auto-generated method stub
       // Toast.makeText(this, "MyAlarmService.onCreate()", Toast.LENGTH_LONG).show();
    }
    @Override
    public IBinder onBind(Intent intent) {
// TODO Auto-generated method stub
      //  Toast.makeText(this, "MyAlarmService.onBind()", Toast.LENGTH_LONG).show();
        return null;
    }
    @Override
    public void onDestroy() {
// TODO Auto-generated method stub
        super.onDestroy();
       // Toast.makeText(this, "MyAlarmService.onDestroy()", Toast.LENGTH_LONG).show();
    }
    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
     //   Toast.makeText(this, "MyAlarmService.onStart()", Toast.LENGTH_LONG).show();
        Megatokyo_data = getSharedPreferences("Latest", 0);
        if (checkNet()){
            try{
                int latestKnownComic = Megatokyo_data.getInt("Latest", 1000);
                Document doc = Jsoup.connect("http://megatokyo.com/").get();
                Elements spans = doc.select("span#strip-bl");
                Element mySpan = spans.first();
                Element image = mySpan.child(0);
                final String cakeString= image.attr("src");
                String shortenedString = cakeString.substring(7,11);
                int latestComic = Integer.parseInt(shortenedString);
                Elements titleDivs = doc.select("div#comic");  //start of title getter
                Element myDivs = titleDivs.first();
                Element myDiv = myDivs.child(0);
                Element theDiv = myDiv.child(1);
                String title = theDiv.ownText();    //end title getter
                if (latestComic != latestKnownComic){
                    notification(title);
                }
            }catch(Exception e){
                Log.e("Image", "Failed to load image", e);
            }
        }else{
            //nopeNet();
        }
        Intent myIntent = new Intent(TheService.this, TheService.class);
        PendingIntent pendingIntent = PendingIntent.getService(TheService.this, 0, myIntent, 0);
        AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.add(Calendar.HOUR, 1);
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
    }
    public boolean checkNet() {
        ConnectivityManager cn=(ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo nf=cn.getActiveNetworkInfo();
        if(nf == null)
        {

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
    public void notification(String title) {
        final NotificationManager mgr=
                (NotificationManager)this.getSystemService(Context.NOTIFICATION_SERVICE);
        final Notification note=new Notification(R.drawable.ic_launcher,
                "New Megatokyo Comic Avaliable!",
                System.currentTimeMillis());
        Intent intent = new Intent(this, MyActivity.class);
        PendingIntent i = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_ONE_SHOT);
        note.setLatestEventInfo(this, "New Megatokyo Comic Avaliable!",
                title, i);
        note.flags |= note.FLAG_AUTO_CANCEL;
        mgr.notify(1337, note);
    }
    @Override
    public boolean onUnbind(Intent intent) {
// TODO Auto-generated method stub
        Toast.makeText(this, "MyAlarmService.onUnbind()", Toast.LENGTH_LONG).show();
        return super.onUnbind(intent);

    }
}
