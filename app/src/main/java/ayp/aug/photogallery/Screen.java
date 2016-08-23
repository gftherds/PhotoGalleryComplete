package ayp.aug.photogallery;

import android.content.Context;
import android.os.PowerManager;

/**
 * Created by Rawin on 23-Aug-16.
 */
public class Screen {
    private static final String SCREEN_CLASS_TAG = "SCREEN";

    public Screen() {}

    public void on(Context context) {
        PowerManager powerManager =
                (PowerManager)
                        context.getSystemService(Context.POWER_SERVICE);

        if(!powerManager.isScreenOn()) {
            PowerManager.WakeLock wl = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK |
                            PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.ON_AFTER_RELEASE,
                    SCREEN_CLASS_TAG);

            wl.acquire();
            wl.release();
        }
    }
}
