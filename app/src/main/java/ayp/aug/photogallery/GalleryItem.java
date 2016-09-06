package ayp.aug.photogallery;

import android.net.Uri;

import java.lang.reflect.GenericArrayType;

/**
 * Created by wind on 8/16/2016 AD.
 */
public class GalleryItem {
    private String mId;
    private String mTitle;
    private String mUrl;
    private String mBigSizeUrl;
    private String mOwner;
    private String mLat;
    private String mLong;

    public String getmLat() {
        return mLat;
    }

    public void setmLat(String mLat) {
        this.mLat = mLat;
    }

    public String getmLong() {
        return mLong;
    }

    public void setmLong(String mLong) {
        this.mLong = mLong;
    }

    public void setId(String id) {
        mId = id;
    }

    public String getId() {
        return mId;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public void setUrl(String url) {
        mUrl = url;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getUrl() {
        return mUrl;
    }

    public String getName() {
        return getTitle();
    }

    public void setName(String name) {
        setTitle(name);
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof GalleryItem) {
            // is GalleryItem too !!
            GalleryItem that = (GalleryItem) obj;

            return that.mId != null && mId != null && that.mId.equals(mId);
        }

        return false;
    }

    public void setBigSizeUrl(String bigSizeUrl) {
        mBigSizeUrl
                = bigSizeUrl;
    }

    public String getBigSizeUrl() {
        return mBigSizeUrl;
    }

    public void setOwner(String owner) {
        mOwner = owner;
    }

    public String getOwner() {
        return mOwner;
    }

    private static final String PHOTO_URL_PREFIX = "https://www.flickr.com/photos/";

    public Uri getPhotoUri() {
        return Uri.parse(PHOTO_URL_PREFIX).buildUpon() // Return builder
                .appendPath(mOwner)
                .appendPath(mId)
                .build(); // Return Uri
    }

    public boolean isGeoCorrect(){
        return !("0".equals(mLat) || "0".equals(mLong));
     }
}
