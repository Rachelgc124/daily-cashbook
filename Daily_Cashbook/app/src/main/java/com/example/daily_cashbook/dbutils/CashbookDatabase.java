package com.example.daily_cashbook.dbutils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.example.daily_cashbook.entity.Cashbook;
import com.example.daily_cashbook.msg.Hint;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class CashbookDatabase {

    private static CashbookDatabase mInstence;
    private DatabaseHelper helper;
    private String TABLE_NAME = "cashbook";

    public CashbookDatabase(Context context) {
        close();
        helper = new DatabaseHelper(context);
    }

    public boolean save(int user_id, String type, String title, String time,
                        String comment, String money, String image, String inorout) {

        ContentValues inputValues = new ContentValues();
        inputValues.put("user_id", user_id);
        inputValues.put("type", type);
        inputValues.put("title", title);
        inputValues.put("time", time);
        inputValues.put("comment", comment);
        inputValues.put("money", money);
        inputValues.put("image", image);
        inputValues.put("inorout", inorout);
        long result = helper.getWritableDatabase().insert(TABLE_NAME, null, inputValues);
        if (result > 0) {
            return true;
        } else {
            return false;
        }
    }

    public ArrayList<Cashbook> search(int uid, String time) {
        Cursor cursor = helper.getReadableDatabase().query(TABLE_NAME, null,
                "user_id = ? and time like ?", new String[]{uid + "", "%" + time + "%"},
                null, null, "time desc", null);
        ArrayList<Cashbook> array = new ArrayList<Cashbook>();
        while (cursor.moveToNext()) {
            Cashbook object = new Cashbook();
            object.setId(cursor.getInt(cursor.getColumnIndex("id")));
            object.setUser_id(cursor.getInt(cursor.getColumnIndex("user_id")));
            object.setType(cursor.getString(cursor.getColumnIndex("type")));
            object.setTime(cursor.getString(cursor.getColumnIndex("time")));
            object.setComment(cursor.getString(cursor.getColumnIndex("comment")));
            object.setMoney(cursor.getString(cursor.getColumnIndex("money")));
            object.setImage(cursor.getString(cursor.getColumnIndex("image")));
            object.setInorout(cursor.getString(cursor.getColumnIndex("inorout")));
            array.add(object);
        }
        return array;
    }

    public ArrayList<Cashbook> searchPeriod(int uid, String beginDate, String endDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");

        Cursor cursor = helper.getReadableDatabase().query(TABLE_NAME, null,
                "user_id = ?", new String[]{uid + ""},
                null, null, "time desc", null);
        ArrayList<Cashbook> array = new ArrayList<Cashbook>();
        while (cursor.moveToNext()) {
            Cashbook object = new Cashbook();
            object.setId(cursor.getInt(cursor.getColumnIndex("id")));
            object.setUser_id(cursor.getInt(cursor.getColumnIndex("user_id")));
            object.setType(cursor.getString(cursor.getColumnIndex("type")));
            object.setTime(cursor.getString(cursor.getColumnIndex("time")));
            object.setComment(cursor.getString(cursor.getColumnIndex("comment")));
            object.setMoney(cursor.getString(cursor.getColumnIndex("money")));
            object.setImage(cursor.getString(cursor.getColumnIndex("image")));
            object.setInorout(cursor.getString(cursor.getColumnIndex("inorout")));

            try {
                long begin = sdf.parse(beginDate).getTime();
                long end = sdf.parse(endDate).getTime();
                long now = sdf.parse(object.getTime()).getTime();
                if (now >= begin && now <= end) {
                    array.add(object);
                }

            } catch (ParseException e) {
                e.printStackTrace();
            }

        }
        return array;
    }

    public boolean isBooking(int uid, String time) {
        Cursor cursor = helper.getReadableDatabase()
                .query(TABLE_NAME, null, "user_id = ? and time like ?",
                        new String[]{uid + "", "%" + time + "%"}, null,
                null, null, null);
        while (cursor.moveToNext()) {
            return true;
        }
        return false;
    }

    public JSONArray searchMoney(int uid, String time, String inorout) {
        String sql = "select SUM(money) total,type from " + TABLE_NAME
                + " where user_id = ? and time like ? and inorout=? group by type ORDER BY (money+0) DESC";
        Cursor cursor = helper.getReadableDatabase().rawQuery(sql, new String[]{uid + "", "%" + time + "%", inorout});

        JSONArray array = new JSONArray();
        while (cursor.moveToNext()) {
            try {
                JSONObject object = new JSONObject();
                object.put("total", cursor.getString(cursor.getColumnIndex("total")));
                object.put("type", cursor.getString(cursor.getColumnIndex("type")));
                array.put(object);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return array;
    }

    public JSONArray ranking(int uid, String time, String inorout) {
        String sql = "select SUM(money) total,type from " + TABLE_NAME
                + " where user_id = ? and time like ? and inorout=? group by type order by total desc";
        Cursor cursor = helper.getReadableDatabase().rawQuery(sql, new String[]{uid + "", "%" + time + "%", inorout});

        JSONArray array = new JSONArray();
        while (cursor.moveToNext()) {
            try {
                JSONObject object = new JSONObject();
                object.put("total", cursor.getString(cursor.getColumnIndex("total")));
                object.put("type", cursor.getString(cursor.getColumnIndex("type")));
                array.put(object);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return array;
    }

    public boolean delete(int id) {
        long result = helper.getWritableDatabase()
                .delete(TABLE_NAME, "id = ?", new String[]{id + ""});
        if (result > 0) {
            return true;
        } else {
            return false;
        }
    }

    public static CashbookDatabase getInstence() {
        if (mInstence == null) {
            synchronized (CashbookDatabase.class) {
                if (mInstence == null) {
                    mInstence = new CashbookDatabase(Hint.context);
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
