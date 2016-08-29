package ayp.aug.photogallery;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.io.IOException;

/**
 * Created by Rawin on 22-Aug-16.
 */
public class ImageViewFragment extends Fragment {

    private static final String KEY_URL = "K_URL";

    private static final String TAG = "ImageViewFragment";

    public static ImageViewFragment newInstance(String url) {
        Bundle args = new Bundle();
        args.putString(KEY_URL, url);
        
        ImageViewFragment fragment = new ImageViewFragment();
        fragment.setArguments(args);
        return fragment;
    }

    ImageView mPhotoViewer;
    String mUrl;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getArguments() != null) {
            mUrl = getArguments().getString(KEY_URL);
            Log.d(TAG, "Get URL : " + mUrl);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = LayoutInflater.from(getActivity()).inflate(
                R.layout.fragment_image_view, container, false);

        mPhotoViewer = (ImageView) v.findViewById(R.id.view_image_photo_viewer);

        if(mUrl != null) {
            new LoadPhotoViewer().execute(mUrl);
        }

        Log.d(TAG, "Finish view :D");

        return v;
    }

    class LoadPhotoViewer extends AsyncTask<String, Void, Bitmap> {
        @Override
        protected Bitmap doInBackground(String... urls) {
            FlickrFetcher flickrFetcher = new FlickrFetcher();
            Bitmap bm = null;
            try {
                byte[] bytes = flickrFetcher.getUrlBytes(urls[0]);
                bm = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            } catch (IOException ioe) {
                Log.e(TAG, "error in reading Bitmap", ioe);
                return null;
            }
            return bm;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            Log.d(TAG, "Finish image :D");
            mPhotoViewer.setImageDrawable(new BitmapDrawable(getResources(), bitmap));
        }
    }

}
