package bischof.raphael.channelmessaging.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by biche on 02/02/14.
 */
public class ImageRequest {
    private String url;
    private int requestCode;
    private Context cont;
    private List<OnImageContentLoadedListener> listeners = new ArrayList<OnImageContentLoadedListener>();
    public ImageRequest(Context currentContext,String url, int requestCode)
    {
        this.url = url;
        this.requestCode = requestCode;
        this.cont = currentContext;
    }

    public void execute() {
        if (SingletonImageCache.getInstance().getImage(url)!=null){
            onContentLoaded(SingletonImageCache.getInstance().getImage(url));
        }else {
            new AsyncPostRequest(cont).execute();
        }
    }

    public class AsyncPostRequest extends AsyncTask<Long,Integer, Bitmap> {
        private Context myContext;
        public AsyncPostRequest(Context myContext)
        {
            this.myContext = myContext;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected void onProgressUpdate(Integer... values){
            super.onProgressUpdate(values);
        }
        @Override
        protected Bitmap doInBackground(Long... arg0)  {

            return getBitmapFromURL(url);
        }
        @Override
        protected void onPostExecute(Bitmap result) {
            onContentLoaded(result);
        }
        public Bitmap getBitmapFromURL(String src) {
            try {
                URL urle = new URL(src);
                HttpURLConnection connection = (HttpURLConnection) urle.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(input);
                SingletonImageCache.getInstance().putImage(url,myBitmap);
                return myBitmap;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
    }
    private void onContentLoaded(Bitmap result)
    {
        for(OnImageContentLoadedListener oneListener:listeners)
        {
            oneListener.onImageContentLoaded(result,requestCode);
        }
    }
    public void setOnContentLoaded (OnImageContentLoadedListener listener)
    {
        // Store the listener object
        this.listeners.add(listener);
    }
}
