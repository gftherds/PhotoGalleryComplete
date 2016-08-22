package ayp.aug.photogallery;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.net.ConnectivityManagerCompat;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rawin on 22-Aug-16.
 */
public class PollService extends IntentService {

    private static final String TAG = "PollService";

    private static final int POLL_INTERVAL = 1000 * 60; // 60 secs

    public static Intent newIntent(Context context) {
        return new Intent(context, PollService.class);
    }

    public static void setServiceAlarm(Context c, boolean isOn) {
        Intent i = PollService.newIntent(c);
        PendingIntent pi = PendingIntent.getService(c, 0, i, 0);

        AlarmManager am = (AlarmManager) c.getSystemService(Context.ALARM_SERVICE);

        if(isOn) {
            //AlarmManager.RTC ->>> System.currentTimeMillis();
            am.setInexactRepeating(AlarmManager.ELAPSED_REALTIME,               // param 1: Mode
                    SystemClock.elapsedRealtime(),                              // param 2: Start
                    POLL_INTERVAL,                                              // param 3: Interval
                    pi);                                                        // param 4: Pending action(intent)
        } else {
            am.cancel(pi);      // Cancel interval call
            pi.cancel();        // Cancel Pending intent call
        }
    }

    public static boolean isServiceAlarmOn(Context ctx) {
        Intent i = PollService.newIntent(ctx);
        PendingIntent pi = PendingIntent.getService(ctx, 0, i, PendingIntent.FLAG_NO_CREATE);
        return pi != null;
    }

    public PollService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.i(TAG, "Receive a call from intent: " + intent);

        if(!isNetworkAvailableAndConnected()) {
            return;
        }

        Log.i(TAG, "Active network!!");

        String query = PhotoGalleryPreference.getStoredSearchKey(this);
        String storedLastId = PhotoGalleryPreference.getStoredLastId(this);

        List<GalleryItem> galleryItemList = new ArrayList<>();

        FlickrFetcher flickrFetcher = new FlickrFetcher();
        if(query == null) {
            flickrFetcher.getRecentPhotos(galleryItemList);
        } else {
            flickrFetcher.searchPhotos(galleryItemList, query);
        }

        if( galleryItemList.size() == 0 ) {
            return;
        }

        Log.i(TAG, "Found search or recent items");

        String newestId = galleryItemList.get(0).getId(); // fetching first Item

        if(newestId.equals(storedLastId)) {
            Log.i(TAG, "No new item");
        } else {
            Log.i(TAG, "New item found");

            Resources res = getResources();
            Intent i = PhotoGalleryActivity.newIntent(this);
            PendingIntent pi = PendingIntent.getActivity(this, 0, i, 0);

            // Build to build notification object
            NotificationCompat.Builder notiBuilder = new NotificationCompat.Builder(this);
            notiBuilder.setTicker(res.getString(R.string.new_picture_arriving));
            notiBuilder.setSmallIcon(android.R.drawable.ic_menu_report_image);
            notiBuilder.setContentTitle(res.getString(R.string.new_picture_title));
            notiBuilder.setContentText(res.getString(R.string.new_picture_content));
            notiBuilder.setContentIntent(pi);
            notiBuilder.setAutoCancel(true);

            Notification notification = notiBuilder.build();  // << Build notification from builder

            //  Get notification manager from context
            NotificationManagerCompat nm = NotificationManagerCompat.from(this);
            nm.notify(0, notification);  // call notification
        }

        PhotoGalleryPreference.setStoredLastId(this, newestId);
    }

    private boolean isNetworkAvailableAndConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);

        boolean isActiveNetwork = cm.getActiveNetworkInfo() != null ;
        boolean isActiveNetworkConnected = isActiveNetwork && cm.getActiveNetworkInfo().isConnected();

        return isActiveNetworkConnected;
    }
}
