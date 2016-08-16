package ayp.aug.photogallery;

import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadFactory;

/**
 * Created by wind on 8/16/2016 AD.
 */
public class FlickrFetcher {
    private static final String TAG = "FlickrFetcher";

    public byte[] getUrlBytes(String urlSpec) throws IOException {
        URL url = new URL(urlSpec);

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            InputStream in = connection.getInputStream();

            // if connection is not OK throw new IOException
            if(connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new IOException(connection.getResponseMessage() + ": with " + urlSpec);
            }

            int bytesRead = 0;

            byte[] buffer = new byte[2048];

            while((bytesRead = in.read(buffer)) > 0) {
                out.write(buffer, 0, bytesRead);
            }

            out.close();

            return out.toByteArray();

        } finally {
            connection.disconnect();
        }
    }

    public String getUrlString(String urlSpec) throws IOException {
        return new String(getUrlBytes(urlSpec));
    }

    //
    private static final String FLICKR_URL = "https://api.flickr.com/services/rest/";

    private static final String API_KEY = "9d495812808b84fda6313aa89484f395";

    public String fetchItems() throws IOException {
        String jsonString = null;
        String url = Uri.parse(FLICKR_URL).buildUpon()
                .appendQueryParameter("method", "flickr.photos.getRecent")
                .appendQueryParameter("api_key", API_KEY)
                .appendQueryParameter("format", "json")
                .appendQueryParameter("nojsoncallback", "1")
                .appendQueryParameter("extras", "url_s")
                .build().toString();

        jsonString = getUrlString(url);

        Log.i(TAG, "Received JSON: " + jsonString);
        return jsonString;
    }

    public void fetchItems(List<GalleryItem> items) {
        fetchItems(items, null);
    }

    public void fetchItems(List<GalleryItem> items, CallBack callBack) {
        try {
            String jsonStr = fetchItems();
            if(jsonStr != null) {
                parseJSON(items, jsonStr, callBack);
            }

        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Failed to fetch items", e);
        }
    }

    private void parseJSON(List<GalleryItem> newGalleryItemList, String jsonBodyStr, CallBack callBack)
            throws IOException, JSONException {

        JSONObject jsonBody = new JSONObject(jsonBodyStr);
        JSONObject photosJson = jsonBody.getJSONObject("photos");
        JSONArray photoListJson = photosJson.getJSONArray("photo");

        int len = photoListJson.length();

        if(callBack != null)
            callBack.totalLen(len);

        for(int i = 0; i < photoListJson.length(); i++) {

            JSONObject jsonPhotoItem = photoListJson.getJSONObject(i);

            GalleryItem item = new GalleryItem();

            item.setId(jsonPhotoItem.getString("id"));
            item.setTitle(jsonPhotoItem.getString("title"));

            if(!jsonPhotoItem.has("url_s")) {
                continue;
            }

            item.setUrl(jsonPhotoItem.getString("url_s"));
            newGalleryItemList.add(item);

            if(callBack != null)
                callBack.fetchItem(i + 1);
        }
    }

    public interface CallBack {
        void fetchItem(int itemCount);
        void totalLen(int len);
    }
}
