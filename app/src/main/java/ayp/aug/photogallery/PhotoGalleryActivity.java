package ayp.aug.photogallery;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class PhotoGalleryActivity extends SingleFragmentActivity {

    @Override
    protected Fragment onCreateFragment() {
        return PhotoGalleryFragment.newInstance();
    }

    public static Intent newIntent(Context ctx) {
        return new Intent(ctx, PhotoGalleryActivity.class);
    }
}
