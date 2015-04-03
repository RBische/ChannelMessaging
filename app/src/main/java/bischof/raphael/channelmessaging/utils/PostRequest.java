package bischof.raphael.channelmessaging.utils;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by biche on 02/02/14.
 */
public class PostRequest {
    private String url;
    private List<NameValuePair> values;
    private int requestCode;
    private Context cont;
    private List<OnContentLoadedListener> listeners = new ArrayList<OnContentLoadedListener>();
    public PostRequest(Context currentContext,String url,List<NameValuePair> values, int requestCode)
    {
        this.url = url;
        this.values = values;
        this.requestCode = requestCode;
        this.cont = currentContext;
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

            return postSample(url);
        }
        @Override
        protected void onPostExecute(String result) {
            onContentLoaded(result);
        }
        public String postSample(String url)
        {
            String content = null;
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(url);
            try {
                httppost.setEntity(new UrlEncodedFormEntity(values));
            } catch (UnsupportedEncodingException e) {
                //TODO Handler
            }
            HttpResponse response = null;
            try {
                response = httpclient.execute(httppost);
                content = EntityUtils.toString(response.getEntity());
            } catch (IOException e) {
                int i = 0;
                //TODO Handler
            }
            return content;
        }
    }
    private void onContentLoaded(String result)
    {
        for(OnContentLoadedListener oneListener:listeners)
        {
            if (oneListener!=null)
            oneListener.onContentLoaded(result,requestCode);
        }
    }
    public void setOnContentLoaded (OnContentLoadedListener listener)
    {
        // Store the listener object
        this.listeners.add(listener);
    }
}
