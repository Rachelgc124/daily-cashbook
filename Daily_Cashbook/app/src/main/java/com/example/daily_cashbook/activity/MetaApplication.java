package com.example.daily_cashbook.activity;

import android.app.Application;

import com.example.daily_cashbook.entity.User;
import com.example.daily_cashbook.msg.Hint;

public class MetaApplication extends Application {

    private User user;  //全局user

    public void setUser(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Hint.context = this;
    }
}
