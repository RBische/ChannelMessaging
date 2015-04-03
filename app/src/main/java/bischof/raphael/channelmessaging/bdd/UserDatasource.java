package bischof.raphael.channelmessaging.bdd;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import bischof.raphael.channelmessaging.model.User;

/**
 * Created by biche on 01/02/2015.
 */
public class UserDatasource {
    // Database fields
    private SQLiteDatabase database;
    private FriendDB dbHelper;
    private String[] allColumns = { FriendDB.KEY_ID,FriendDB.KEY_IMAGEURL,FriendDB.KEY_USERNAME};

    public UserDatasource(Context context) {
        dbHelper = new FriendDB(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public User createFriend(String imageUrl, String username,int userid) {
        ContentValues values = new ContentValues();
        values.put(FriendDB.KEY_IMAGEURL, imageUrl);
        values.put(FriendDB.KEY_ID, userid);
        values.put(FriendDB.KEY_USERNAME, username);
        database.insert(FriendDB.FRIEND_TABLE_NAME, null,
                values);
        Cursor cursor = database.query(FriendDB.FRIEND_TABLE_NAME,
                allColumns, FriendDB.KEY_ID + " = "+userid+"", null,
                null, null, null);
        cursor.moveToFirst();
        User newHomme = cursorToUser(cursor);
        cursor.close();
        return newHomme;
    }

    private User cursorToUser(Cursor cursor) {
        User comment = new User();
        comment.userID = cursor.getInt(0);
        comment.imageUrl = cursor.getString(1);
        comment.username= cursor.getString(2);
        return comment;

    }

    public List<User> getAllHommes() {
        List<User> lesHommes = new ArrayList<User>();

        Cursor cursor = database.query(FriendDB.FRIEND_TABLE_NAME,
                allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            User unHomme = cursorToUser(cursor);
            lesHommes.add(unHomme);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return lesHommes;
    }

}
