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
public class TheService extends Service
{

    private NotificationManager mManager;
    SharedPreferences Megatokyo_data;
    @Override
    public IBinder onBind(Intent arg0)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void onCreate()
    {
        // TODO Auto-generated method stub
        super.onCreate();
    }

    @SuppressWarnings("static-access")
    @Override
    public void onStart(Intent intent, int startId)
    {
        super.onStart(intent, startId);

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
            int latestComic = Integer.parseInt(shortenedString);
            compareThings(latestComic);
            //sendNotification("the thing do");
        }catch(Exception e){
            Log.e("Image", "Failed to load image", e);
        }

    }
    public void compareThings(int latestComic){
        Megatokyo_data = getSharedPreferences("Latest", 1);
        int latestSavedComic = Megatokyo_data.getInt("Latest",1);
        if (latestSavedComic > latestComic) {
            latestSavedComic = latestComic;
            SharedPreferences.Editor editor = Megatokyo_data.edit();
            editor.putInt("Latest", latestSavedComic);
            editor.commit();
            sendNotification("it was broke"); //REMOVE THIS
        } else if (latestComic > latestSavedComic){
            latestSavedComic = latestComic;
            SharedPreferences.Editor editor = Megatokyo_data.edit();
            editor.putInt("Latest", latestSavedComic);
            editor.commit();
            sendNotification("New Comic Avaliable: #" + latestComic);
        }else {
            //do nothing
           // sendNotification("they were the same"); //remove this!!!
        }
    }
    public void sendNotification(String text) {
        mManager = (NotificationManager) this.getApplicationContext().getSystemService(this.getApplicationContext().NOTIFICATION_SERVICE);
        Intent intent1 = new Intent(this.getApplicationContext(),MyActivity.class);

        Notification notification = new Notification(R.drawable.ic_launcher, text , System.currentTimeMillis());
        intent1.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP| Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingNotificationIntent = PendingIntent.getActivity( this.getApplicationContext(),0, intent1, PendingIntent.FLAG_UPDATE_CURRENT);
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        notification.setLatestEventInfo(this.getApplicationContext(), "Megatokyo Comic Viewer", text , pendingNotificationIntent);

        mManager.notify(0, notification);
    }
    @Override
    public void onDestroy()
    {
        // TODO Auto-generated method stub
        super.onDestroy();
    }

}
