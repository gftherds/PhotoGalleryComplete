package ayp.aug.photogallery;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.app.job.JobParameters;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Build;
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

    private static final int POLL_INTERVAL = 1000 * 60; // 1 min

    // public broadcast name for this action
    public static final String ACTION_SHOW_NOTIFICATION = "ayp.aug.photogallery.ACTION_SHOW_NOTIFICATION";

    public static final String PERMISSION_SHOW_NOTIF = "ayp.aug.photogallery.RECEIVE_SHOW_NOTIFICATION";
    public static final String REQUEST_CODE = "REQUEST_CODE_INTENT";
    public static final String NOTIFICATION = "NOTIFICATION_OBJ";

    public static Intent newIntent(Context context) {
        return new Intent(context, PollService.class);
    }

    public static void setServiceAlarm(Context c, boolean isOn) {
        Intent i = PollService.newIntent(c);
        PendingIntent pi = PendingIntent.getService(c, 0, i, 0);

        AlarmManager am = (AlarmManager) c.getSystemService(Context.ALARM_SERVICE);

        if (isOn) {
            //AlarmManager.RTC ->>> System.currentTimeMillis();
            am.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,               // param 1: Mode
                    SystemClock.elapsedRealtime(),                                 // param 2: Start
                    POLL_INTERVAL,                                              // param 3: Interval
                    pi);                                                        // param 4: Pending action(intent)

            Log.d(TAG, "Run by Alarm Manager");

        } else {
            am.cancel(pi);      // Cancel interval call
            pi.cancel();        // Cancel Pending intent call
        }

        PhotoGalleryPreference.setStoredIsAlarmOn(c, isOn);
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

            sendBackgroundNotification(0, notification);
        }

        PhotoGalleryPreference.setStoredLastId(this, newestId);
    }

    private void sendBackgroundNotification(int requestCode, Notification notification) {
        Intent intent = new Intent(ACTION_SHOW_NOTIFICATION);
        intent.putExtra(REQUEST_CODE, requestCode);
        intent.putExtra(NOTIFICATION, notification);

        sendOrderedBroadcast(intent, PERMISSION_SHOW_NOTIF,
                null, null,
                Activity.RESULT_OK,
                null, null);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "Service create.");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "Service start command. flags=" + flags + ", startId=" + startId);
        int result = super.onStartCommand(intent, flags, startId);
        Log.d(TAG, "Service start result = " + result);

        return result;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "Service destroy.");
    }

    private boolean isNetworkAvailableAndConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);

        boolean isActiveNetwork = cm.getActiveNetworkInfo() != null ;
        boolean isActiveNetworkConnected = isActiveNetwork && cm.getActiveNetworkInfo().isConnected();

        return isActiveNetworkConnected;
    }

}
