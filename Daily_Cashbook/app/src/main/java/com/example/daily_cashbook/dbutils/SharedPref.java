package com.example.daily_cashbook.dbutils;

import android.content.SharedPreferences;

import com.example.daily_cashbook.entity.User;
import com.example.daily_cashbook.msg.Hint;
import com.google.gson.Gson;

public class SharedPref {

    public static boolean isLogin() {
        SharedPreferences preferences = Hint.context.getSharedPreferences("isLogin", Hint.context.MODE_PRIVATE);
        return preferences.getBoolean("isLogin", false);

    }

    public static void setLogin(boolean isLogin) {
        SharedPreferences preferences = Hint.context.getSharedPreferences("isLogin", Hint.context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("isLogin", isLogin).commit();

    }


    public static void setUser(User u) {
        SharedPreferences preferences = Hint.context.getSharedPreferences("user", Hint.context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("user", new Gson().toJson(u)).commit();

    }

    public static User getUser() {
        SharedPreferences preferences = Hint.context.getSharedPreferences("user", Hint.context.MODE_PRIVATE);
        return new Gson().fromJson(preferences.getString("user", ""), User.class);


    }

}
