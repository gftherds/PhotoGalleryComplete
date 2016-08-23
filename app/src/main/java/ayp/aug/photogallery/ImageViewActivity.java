package ayp.aug.photogallery;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;

public class ImageViewActivity extends SingleFragmentActivity {

    private static final String TAG = "ImageViewActivity";
    private static final String KEY_URL = "K_URL";

    public static Intent newIntent(Context ctx, String url) {
        Intent intent = new Intent(ctx, ImageViewActivity.class);
        intent.putExtra(KEY_URL, url);
        return intent;
    }

    @Override
    protected Fragment onCreateFragment() {
        if(getIntent() != null) {
            String url = getIntent().getStringExtra(KEY_URL);
            Log.d(TAG, "Get url: " + url);
            return ImageViewFragment.newInstance(url);
        }

        throw new UnsupportedOperationException("No URL loaded");
    }
}
