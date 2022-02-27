package com.example.daily_cashbook.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.example.daily_cashbook.R;
import com.example.daily_cashbook.dbutils.CashbookDatabase;
import com.example.daily_cashbook.dbutils.SharedPref;
import com.example.daily_cashbook.fragment.MyFragment;
import com.example.daily_cashbook.fragment.ReportFragment;
import com.example.daily_cashbook.msg.Hint;

import java.io.File;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    //TA 提供
    private TextView txt_topbar;
    private TextView txt_home;
    private TextView txt_dash;
    private TextView txt_cash;
    private TextView txt_mine;
    private FrameLayout ly_content;

    private MyFragment fragment1;
    private ReportFragment fragment2;
    private MyFragment fragment3;
    private MyFragment fragment4;
    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        fragmentManager = getSupportFragmentManager();
        bindViews();
        txt_home.performClick();    //默认进去选择第一项

        if (!CashbookDatabase.getInstence().isBooking(SharedPref.getUser().getId(), Hint.getDate())) {
            notification();
        }

    }

    private void bindViews() {
        txt_topbar = (TextView) findViewById(R.id.txt_topbar);
        txt_home = (TextView) findViewById(R.id.rb_home);
        txt_dash = (TextView) findViewById(R.id.rb_dash);
        txt_cash = (TextView) findViewById(R.id.rb_cash);
        txt_mine = (TextView) findViewById(R.id.rb_mine);
        ly_content = (FrameLayout) findViewById(R.id.ly_content);

        txt_home.setOnClickListener(this);
        txt_dash.setOnClickListener(this);
        txt_cash.setOnClickListener(this);
        txt_mine.setOnClickListener(this);
    }

    private void setSelected(){
        txt_home.setSelected(false);
        txt_dash.setSelected(false);
        txt_cash.setSelected(false);
        txt_mine.setSelected(false);
    }

    private void hideAllFragment(FragmentTransaction fragmentTransaction){
        if (fragment1 != null){
            fragmentTransaction.hide(fragment1);
        }
        if (fragment2 != null){
            fragmentTransaction.hide(fragment2);
        }
        if (fragment3 != null){
            fragmentTransaction.hide(fragment3);
        }
        if (fragment4 != null){
            fragmentTransaction.hide(fragment4);
        }
    }

    @Override
    public void onClick(View v) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        //首先隐藏所有的fragment
        hideAllFragment(fragmentTransaction);
        //判断点击的是哪个按钮
        switch (v.getId()){
            case R.id.rb_home:
                setSelected();
                txt_home.setSelected(true);
                if (fragment1 == null){
                    fragment1 = new MyFragment(0);
                    fragmentTransaction.add(R.id.ly_content, fragment1);
                }else {
                    setSelected();
                    //设置一下初试时间
//                    fragment1.getBookingItems(date, outcome, income);
                    fragmentTransaction.show(fragment1);
                }
                break;
            case R.id.rb_dash:
                setSelected();
                txt_dash.setSelected(true);
                if (fragment2 == null){
                    fragment2 = new ReportFragment();
                    fragmentTransaction.add(R.id.ly_content, fragment2);
                }else {
                    fragmentTransaction.show(fragment2);
                }
                break;
            case R.id.rb_cash:  // 支出和收入的情况
                setSelected();
                txt_cash.setSelected(true);
                if (fragment3 == null){
                    fragment3 = new MyFragment(2);
                    fragmentTransaction.add(R.id.ly_content, fragment3);
                }else {
                    fragmentTransaction.show(fragment3);
                }
                break;
            case R.id.rb_mine:
                setSelected();
                txt_mine.setSelected(true);
                if (fragment4 == null){
                    fragment4 = new MyFragment(3);
                    fragmentTransaction.add(R.id.ly_content, fragment4);
                }else {
                    fragmentTransaction.show(fragment4);
                }
                break;
        }
        fragmentTransaction.commit();
    }

    void notification() {
        Notification notification;
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String id = getPackageName() + "my_channel" + System.currentTimeMillis();
            String name = "app";
            NotificationChannel mChannel = new NotificationChannel(id, name, NotificationManager.IMPORTANCE_LOW);
            manager.createNotificationChannel(mChannel);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(MainActivity.this, id);
            builder.setContentTitle("记账提醒")
                    .setContentText("今日尚未记账")
                    .setSubText("今日尚未记账")
                    .setTicker("今日尚未记账")
                    .setWhen(System.currentTimeMillis())
                    .setSmallIcon(R.drawable.logo)
                    .setPriority(NotificationCompat.PRIORITY_MAX)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setAutoCancel(true);
            builder.setContentIntent(pendingIntent);
            notification = builder.build();
            manager.notify(1, notification);
        } else {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                    .setContentTitle("记账提醒")
                    .setContentText("今日尚未记账")
                    .setWhen(System.currentTimeMillis())
                    .setSmallIcon(R.drawable.logo)
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.logo))
                    .setContentIntent(pendingIntent)
                    .setPriority(NotificationCompat.PRIORITY_MAX)
                    .setAutoCancel(true)
                    .setDefaults(NotificationCompat.DEFAULT_ALL);
            notification = builder.setStyle(new NotificationCompat.BigTextStyle().bigText("今日尚未记账"))
                    .build();
            manager.notify(1, notification);
        }
    }
}