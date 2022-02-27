package com.example.daily_cashbook.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.daily_cashbook.R;
import com.example.daily_cashbook.dbutils.CashbookDatabase;
import com.example.daily_cashbook.entity.Cashbook;
import com.example.daily_cashbook.fragment.HomeFragment;
import com.example.daily_cashbook.msg.Hint;
import com.squareup.picasso.Picasso;

import java.io.File;

public class ItemsDetail extends AppCompatActivity {

    Cashbook booking;

    TextView type;
    TextView money;
    TextView date;
    TextView comment;
    ImageView photo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.items_detail);
        booking = (Cashbook) getIntent().getSerializableExtra("booking");

        type = findViewById(R.id.items_detail_type);
        money = findViewById(R.id.items_detail_money);
        date = findViewById(R.id.items_detail_date);
        comment = findViewById(R.id.items_detail_comment);
        photo = findViewById(R.id.items_detail_photo);

        //读取数据
        type.setText(booking.getInorout().equals("output") ? "支出" : "收入");
        money.setText(booking.getMoney());
        comment.setText(booking.getComment());
        date.setText(booking.getTime());

        if (booking.getImage().equals("")) {
            photo.setVisibility(View.GONE);
        } else {
            File file = new File(booking.getImage());
            Picasso.with(ItemsDetail.this).load(file).into(photo);
        }


        ImageView back = findViewById(R.id.items_detail_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        TextView delete = findViewById(R.id.items_detail_delete);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CashbookDatabase.getInstence().delete(booking.getId());
                Hint.show("记录已删除");
                finish();
            }
        });
    }
}
