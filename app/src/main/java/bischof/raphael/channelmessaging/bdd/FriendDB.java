package bischof.raphael.channelmessaging.bdd;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;

/**
 * Created by biche on 01/02/2015.
 */
public class FriendDB extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 2;
    private static final String DATABASE_NAME = "MyDB.db";
    public static final String FRIEND_TABLE_NAME = "Friends";
    public static final String KEY_ID = "userid";
    public static final String KEY_USERNAME = "username";
    public static final String KEY_IMAGEURL = "name";
    private static final String FRIEND_TABLE_CREATE = "CREATE TABLE " + FRIEND_TABLE_NAME + " (" + KEY_ID + " INTEGER, " +
            KEY_IMAGEURL + " TEXT, \"" +
                        KEY_USERNAME + "\" TEXT);";

    public FriendDB(Context context) {
        super(context, Environment.getExternalStorageDirectory()+"/"+DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(FRIEND_TABLE_CREATE);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {db.execSQL("DROP TABLE IF EXISTS " + FRIEND_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + FRIEND_TABLE_NAME);
        onCreate(db);
    }
}

