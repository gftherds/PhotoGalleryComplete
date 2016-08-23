package ayp.aug.photogallery;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rawin on 23-Aug-16.
 */
@TargetApi(21)
public class PollJobService extends JobService {
    private static final String TAG = "PollJobService";
    private static final int POLL_INTERVAL = 1000 * 1;
    private PollTask mPollTask;

    @Override
    public boolean onStartJob(JobParameters params) {
        Log.d(TAG, "Start job : PollJobService");
        mPollTask = new PollTask();
        mPollTask.execute(params);
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        if(mPollTask != null) {
            mPollTask.cancel(true);
        }

        return true;
    }

    private static final int JOB_ID = 2186;

    public static boolean isRun(Context ctx) {
        JobScheduler sch = (JobScheduler) ctx.getSystemService(Context.JOB_SCHEDULER_SERVICE);

        List<JobInfo> jobInfoList = sch.getAllPendingJobs();
        for(JobInfo jobInfo : jobInfoList) {
            if(jobInfo.getId() == JOB_ID) {
                return true;
            }
        }

        return false;
    }

    public static void stop(Context ctx) {
        JobScheduler sch = (JobScheduler) ctx.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        sch.cancel(JOB_ID);
    }

    public static void start(Context ctx) {
        // create job
        JobInfo.Builder builder = new JobInfo.Builder(JOB_ID,
                new ComponentName(ctx, PollJobService.class));
        builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY);
        builder.setPeriodic(POLL_INTERVAL);
        builder.setRequiresDeviceIdle(false);
        //builder.setPersisted(true);
        JobInfo jobInfo = builder.build();

        JobScheduler sch = (JobScheduler) ctx.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        sch.schedule(jobInfo);
    }

    public class PollTask extends AsyncTask<JobParameters, Void, Void> {

        @Override
        protected Void doInBackground(JobParameters... params) {
            // Do whatever job
            //
            Log.d(TAG, "Job poll running" );

            String query = PhotoGalleryPreference.getStoredSearchKey(PollJobService.this);
            String storedLastId = PhotoGalleryPreference.getStoredLastId(PollJobService.this);

            List<GalleryItem> galleryItemList = new ArrayList<>();

            FlickrFetcher flickrFetcher = new FlickrFetcher();
            if(query == null) {
                flickrFetcher.getRecentPhotos(galleryItemList);
            } else {
                flickrFetcher.searchPhotos(galleryItemList, query);
            }

            if( galleryItemList.size() == 0 ) {
                return null;
            }

            Log.i(TAG, "Found search or recent items");

            String newestId = galleryItemList.get(0).getId(); // fetching first Item

            if(newestId.equals(storedLastId)) {
                Log.i(TAG, "No new item");
            } else {
                Log.i(TAG, "New item found");

                Resources res = getResources();
                Intent i = PhotoGalleryActivity.newIntent(PollJobService.this);
                PendingIntent pi = PendingIntent.getActivity(PollJobService.this, 0, i, 0);

                // Build to build notification object
                NotificationCompat.Builder notiBuilder = new NotificationCompat.Builder(PollJobService.this);
                notiBuilder.setTicker(res.getString(R.string.new_picture_arriving));
                notiBuilder.setSmallIcon(android.R.drawable.ic_menu_report_image);
                notiBuilder.setContentTitle(res.getString(R.string.new_picture_title));
                notiBuilder.setContentText(res.getString(R.string.new_picture_content));
                notiBuilder.setContentIntent(pi);
                notiBuilder.setAutoCancel(true);

                Notification notification = notiBuilder.build();  // << Build notification from builder

                //  Get notification manager from context
                NotificationManagerCompat nm = NotificationManagerCompat.from(PollJobService.this);
                nm.notify(0, notification);  // call notification

                new Screen().on(PollJobService.this);
            }

            PhotoGalleryPreference.setStoredLastId(PollJobService.this, newestId);

            // Finish
            jobFinished(params[0], false);

            return null;
        }
    }
}
