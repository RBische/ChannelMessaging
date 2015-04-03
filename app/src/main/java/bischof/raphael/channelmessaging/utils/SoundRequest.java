package bischof.raphael.channelmessaging.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by biche on 02/02/14.
 */
public class SoundRequest {
    private final String mFilePath;
    private String url;
    private int requestCode;
    private Context cont;
    private List<OnSoundContentLoadedListener> listeners = new ArrayList<OnSoundContentLoadedListener>();
    public SoundRequest(Context currentContext, String url, String filePath, int requestCode)
    {
        this.url = url;
        this.requestCode = requestCode;
        this.cont = currentContext;
        this.mFilePath = filePath;
    }

    public void execute() {
        new AsyncPostRequest(cont).execute();
    }

    public class AsyncPostRequest extends AsyncTask<Long,Integer, String> {
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
        protected String doInBackground(Long... arg0)  {
            downloadFromUrl(url,mFilePath);
            return mFilePath;
        }
        @Override
        protected void onPostExecute(String result) {
            onContentLoaded(result);
        }

    }

    public void downloadFromUrl(String fileURL, String fileName) {
        try {
            URL url = new URL( fileURL);
            File file = new File(fileName);
            file.createNewFile();
               /* Open a connection to that URL. */
            URLConnection ucon = url.openConnection();
               /* Define InputStreams to read from the URLConnection.*/
            InputStream is = ucon.getInputStream();
               /* Read bytes to the Buffer until there is nothing more to read(-1) and write on the fly in the file.*/
            FileOutputStream fos = new FileOutputStream(file);
            final int BUFFER_SIZE = 23 * 1024;
            BufferedInputStream bis = new BufferedInputStream(is, BUFFER_SIZE);
            byte[] baf = new byte[BUFFER_SIZE];
            int actual = 0;
            while (actual != -1) {
                fos.write(baf, 0, actual);
                actual = bis.read(baf, 0, BUFFER_SIZE);
            }
            fos.close();
        } catch (IOException e) {
            //TODO HANDLER
        }
    }

    private void onContentLoaded(String result)
    {
        for(OnSoundContentLoadedListener oneListener:listeners)
        {
            oneListener.onSoundContentLoaded(result, requestCode);
        }
    }
    public void setOnContentLoaded (OnSoundContentLoadedListener listener)
    {
        // Store the listener object
        this.listeners.add(listener);
    }

    public interface OnSoundContentLoadedListener {
        public void onSoundContentLoaded(String news, int requestCode);
    }
}
