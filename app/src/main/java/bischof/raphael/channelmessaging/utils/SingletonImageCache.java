package bischof.raphael.channelmessaging.utils;

import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by biche on 24/01/2015.
 */
public class SingletonImageCache {
    private HashMap<String,Bitmap> btms;
    private static volatile SingletonImageCache instance = null;

    private SingletonImageCache() {
        super();
        btms = new HashMap<>();
    }

    public final static SingletonImageCache getInstance() {
        if (SingletonImageCache.instance == null) {
            synchronized(SingletonImageCache.class) {
                if (SingletonImageCache.instance == null) {
                    SingletonImageCache.instance = new SingletonImageCache();
                }
            }
        }
        return SingletonImageCache.instance;
    }

    public Bitmap getImage(String url){
        if (btms.containsKey(url)){
            return btms.get(url);
        }else{
            return null;
        }
    }

    public void putImage(String url,Bitmap btm){
        if (!btms.containsKey(url))
            btms.put(url,btm);
    }
}
