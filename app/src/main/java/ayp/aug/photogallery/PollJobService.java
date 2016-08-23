package ayp.aug.photogallery;

import android.annotation.TargetApi;
import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.util.List;

/**
 * Created by Rawin on 23-Aug-16.
 */
@TargetApi(21)
public class PollJobService extends JobService implements PollTask.PollTaskListener {
    private static final String TAG = "PollJobService";
    private static final int POLL_INTERVAL = 1000 * 1;
    private PollAsyncTask mPollTask;

    @Override
    public boolean onStartJob(JobParameters params) {
        Log.d(TAG, "Start job : PollJobService");
        mPollTask = new PollAsyncTask();
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
        builder.setMinimumLatency(POLL_INTERVAL);
        builder.setOverrideDeadline(POLL_INTERVAL);
        //builder.setPersisted(true);
        JobInfo jobInfo = builder.build();

        JobScheduler sch = (JobScheduler) ctx.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        sch.schedule(jobInfo);
    }

    @Override
    public void updateComplete() {
        // run completed
    }

    private class PollAsyncTask extends AsyncTask<JobParameters, Void, Void> {
        @Override
        protected Void doInBackground(JobParameters... params) {
            run();

            jobFinished(params[0], false);
            return null;
        }
    }

    private void run() {
        new PollTask(this, this);
    }
}
