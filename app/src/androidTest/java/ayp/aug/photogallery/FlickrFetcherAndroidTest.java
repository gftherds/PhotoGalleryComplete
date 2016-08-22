package ayp.aug.photogallery;

import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.isA;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.*;

/**
 * Created by Rawin on 16-Aug-16.
 */
@RunWith(AndroidJUnit4.class)
@SmallTest
public class FlickrFetcherAndroidTest {

    private static final String TAG = "FTest";
    private FlickrFetcher mFlickrFetcher;

    @Before
    public void setUp() throws Exception {
        mFlickrFetcher = new FlickrFetcher();
    }

    @Test
    public void testGetUrlString() throws Exception {
        String htmlResult = mFlickrFetcher.getUrlString("https://www.augmentis.biz/");

        System.out.println(htmlResult);

        assertThat(htmlResult, containsString("IT Professional Services"));
    }

    @Test
    public void testSearch() throws Exception {
        List<GalleryItem> galleryItemList = new ArrayList<>();
        mFlickrFetcher.searchPhotos(galleryItemList, "bird");

        Log.d(TAG, "testSearch : size = " + galleryItemList.size());
        assertThat(galleryItemList.size(), not(0));
    }

    @Test
    public void testGetRecent() throws Exception {
        List<GalleryItem> galleryItemList = new ArrayList<>();
        mFlickrFetcher.getRecentPhotos(galleryItemList);

        Log.d(TAG, "testGetRecent : size = " + galleryItemList.size());
        assertThat(galleryItemList.size(), not(0));
        assertThat(galleryItemList.get(0).getBigSizeUrl(), notNullValue());
    }
}