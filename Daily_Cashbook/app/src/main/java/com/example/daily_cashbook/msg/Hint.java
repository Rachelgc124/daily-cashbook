package com.example.daily_cashbook.msg;

import android.content.Context;
import android.widget.Toast;

import java.text.SimpleDateFormat;

public class Hint {
    public static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    public static Context context;

    public static void show(String content) {
        Toast.makeText(context, content, Toast.LENGTH_SHORT).show();
    }


    public static String getDate() {
        return simpleDateFormat.format(System.currentTimeMillis());
    }
    public static String getTime(long date) {
        return simpleDateFormat.format(date);
    }
}
