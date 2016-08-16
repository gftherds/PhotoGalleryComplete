package ayp.aug.photogallery;

import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.net.Inet4Address;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wind on 8/16/2016 AD.
 */
public class PhotoGalleryFragment extends Fragment {

    private static final String TAG = "PhotoGalleryFragment";

    public static PhotoGalleryFragment newInstance() {

        Bundle args = new Bundle();

        PhotoGalleryFragment fragment = new PhotoGalleryFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private RecyclerView mRecyclerView;
    private FlickrFetcher mFlickrFetcher;
    private List<GalleryItem> mItems;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
        setRetainInstance(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.menu_main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mnu_reload:
                new FetcherTask().execute();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_photo_gallery, container, false);

        mRecyclerView = (RecyclerView) v.findViewById(R.id.photo_gallery_recycler_view);
        Resources r = getResources();
        int gridSize = r.getInteger(R.integer.grid_size);

        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), gridSize));
        mItems = new ArrayList<>();
        mRecyclerView.setAdapter(new PhotoGalleryAdapter(mItems));

        mFlickrFetcher = new FlickrFetcher();
        new FetcherTask().execute(); // run another thread
        return v;
    }

    class PhotoGalleryVH extends RecyclerView.ViewHolder {

        TextView mText;

        public PhotoGalleryVH(View itemView) {
            super(itemView);

            mText = (TextView) itemView;
        }

        public void bindGalleryItem(GalleryItem galleryItem) {
            mText.setText(galleryItem.getTitle());
        }
    }

    class PhotoGalleryAdapter extends RecyclerView.Adapter<PhotoGalleryVH> {

        List<GalleryItem> mGalleryItemList;

        PhotoGalleryAdapter(List<GalleryItem> galleryItems) {
            mGalleryItemList = galleryItems;
        }

        @Override
        public PhotoGalleryVH onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(getActivity()).inflate(
                    android.R.layout.simple_list_item_1, parent, false);

            return new PhotoGalleryVH(v);
        }

        @Override
        public void onBindViewHolder(PhotoGalleryVH holder, int position) {
            holder.bindGalleryItem(mGalleryItemList.get(position));
        }

        @Override
        public int getItemCount() {
            return mGalleryItemList.size();
        }
    }

    class FetcherTask extends AsyncTask<Void, Integer, List<GalleryItem>> implements FlickrFetcher.CallBack {

        int total;

        @Override
        protected List<GalleryItem> doInBackground(Void... voids) {
            Log.d(TAG, "Start fetcher task");
            List<GalleryItem> itemList = new ArrayList<>();

            mFlickrFetcher.fetchItems(itemList, this);
            Log.d(TAG, "Fetcher task finished");
            return itemList;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);

            String formatString = getResources().getString(R.string.photo_progress_loaded, values[0]);
            Snackbar.make(mRecyclerView, formatString, Snackbar.LENGTH_SHORT).show();
        }

        @Override
        protected void onPostExecute(List<GalleryItem> galleryItems) {
            mItems = galleryItems;
            mRecyclerView.setAdapter(new PhotoGalleryAdapter(mItems));
        }

        ////////////////////////

        @Override
        public void fetchItem(int itemCount) {
            float result = (float) itemCount / total * 100;
            publishProgress( Math.round(result) );
        }

        @Override
        public void totalLen(int len) {
            total = len;
        }
    }
}