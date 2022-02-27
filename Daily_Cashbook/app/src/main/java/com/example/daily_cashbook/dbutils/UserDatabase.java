package com.example.daily_cashbook.dbutils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.example.daily_cashbook.entity.User;
import com.example.daily_cashbook.msg.Hint;

public class UserDatabase {

    private static UserDatabase mInstence;
    private DatabaseHelper helper;
    private String TABLE_NAME = "user";

    public UserDatabase(Context context) {
        close();
        helper = new DatabaseHelper(context);
    }

    /** 登陆查询用户 */
    public User search(String user, String password) {
        Cursor cursor = helper.getReadableDatabase()
                .query(TABLE_NAME, null, "username = ? and password = ?",
                        new String[]{user, password}, null,
                null, null, null);
        while (cursor.moveToNext()) {
            User object = new User();
            object.setId(cursor.getInt(cursor.getColumnIndex("id")));
            object.setUserName(cursor.getString(cursor.getColumnIndex("username")));
            object.setPassword(cursor.getString(cursor.getColumnIndex("password")));
            object.setEmail(cursor.getString(cursor.getColumnIndex("email")));
            object.setTel(cursor.getString(cursor.getColumnIndex("tel")));
            object.setProfile(cursor.getString(cursor.getColumnIndex("profile")));
            return object;
        }
        return null;
    }

    /** 判断用户是否存在 */
    public boolean isExit(String username) {
        Cursor cursor = helper.getReadableDatabase()
                .query(TABLE_NAME, null, "username = ?", new String[]{username}, null,
                null, null, null);
        while (cursor.moveToNext()) {
            return true;
        }
        return false;
    }

    /** 保存数据 */
    public boolean save(String username, String password, String email, String tel) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("username", username);
        contentValues.put("password", password);
        contentValues.put("email", email);
        contentValues.put("tel", tel);
        contentValues.put("profile", "");
        long result = helper.getWritableDatabase()
                .insert(TABLE_NAME, null, contentValues);
        if (result > 0) {
            return true;
        } else {
            return false;
        }
    }

    /** 更新 */
    public boolean update(User object) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("password", object.getPassword());
        contentValues.put("email", object.getEmail());
        contentValues.put("tel", object.getTel());
        contentValues.put("profile", object.getProfile());
        long result = helper.getWritableDatabase()
                .update(TABLE_NAME, contentValues, "id = ?", new String[]{object.getId() + ""});
        Hint.show("修改成功");
        if (result > 0) {
            SharedPref.setUser(object);
            return true;
        } else {
            return false;
        }
    }

    public static UserDatabase getInstence() {
        if (mInstence == null) {
            synchronized (UserDatabase.class) {
                if (mInstence == null) {
                    mInstence = new UserDatabase(Hint.context);
                }
            }
        }
        return mInstence;
    }

    private synchronized void close() {
        if (helper != null) {
            helper.close();
            helper = null;
        }
    }

}
