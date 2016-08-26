package ayp.aug.photogallery;

import android.app.Activity;
import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

public class NotificationReceiver extends BroadcastReceiver {
    private static final String TAG = "NotificationReceiver";

    public NotificationReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "Notification calling");

        //
        if(getResultCode() != Activity.RESULT_OK) {
            return;
        }

        Notification notification = (Notification)
                intent.getParcelableExtra(PollService.NOTIFICATION);

        int requestCode = intent.getIntExtra(PollService.REQUEST_CODE, 0);

        NotificationManagerCompat.from(context).notify(requestCode, notification);

        Log.i(TAG, "Notify new item displayed! ");
    }
}
