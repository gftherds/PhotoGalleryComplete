package ayp.aug.photogallery;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rawin on 23-Aug-16.
 */
public class PollTask implements Runnable {

    private static final String TAG = "PollTask";

    private Context mContext;
    private PollTaskListener mListener;

    public PollTask(Context context, PollTaskListener listener) {
        mContext = context;
        mListener = listener;
    }

    public interface PollTaskListener {
        void updateComplete();
    }

    @Override
    public void run() {

        // Do whatever job
        //
        Log.d(TAG, "Job poll running" );

        String query = PhotoGalleryPreference.getStoredSearchKey(mContext);
        String storedLastId = PhotoGalleryPreference.getStoredLastId(mContext);

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

            Resources res = mContext.getResources();
            Intent i = PhotoGalleryActivity.newIntent(mContext);
            PendingIntent pi = PendingIntent.getActivity(mContext, 0, i, 0);

            // Build to build notification object
            NotificationCompat.Builder notiBuilder = new NotificationCompat.Builder(mContext);
            notiBuilder.setTicker(res.getString(R.string.new_picture_arriving));
            notiBuilder.setSmallIcon(android.R.drawable.ic_menu_report_image);
            notiBuilder.setContentTitle(res.getString(R.string.new_picture_title));
            notiBuilder.setContentText(res.getString(R.string.new_picture_content));
            notiBuilder.setContentIntent(pi);
            notiBuilder.setAutoCancel(true);

            Notification notification = notiBuilder.build();  // << Build notification from builder

            //  Get notification manager from context
            NotificationManagerCompat nm = NotificationManagerCompat.from(mContext);
            nm.notify(0, notification);  // call notification

            new Screen().on(mContext);
        }

        PhotoGalleryPreference.setStoredLastId(mContext, newestId);

        mListener.updateComplete();
    }
}
