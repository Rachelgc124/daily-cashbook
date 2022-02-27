package com.example.daily_cashbook.activity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.daily_cashbook.R;
import com.example.daily_cashbook.dbutils.DatabaseHelper;
import com.example.daily_cashbook.dbutils.SharedPref;
import com.example.daily_cashbook.dbutils.UserDatabase;
import com.example.daily_cashbook.entity.User;
import com.example.daily_cashbook.msg.Hint;

public class SignIn extends AppCompatActivity {

    private EditText edtUserName;
    private EditText edtPassword;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        //缓存登陆用户状态
        if (SharedPref.isLogin()) {
            queryUser(SharedPref.getUser().getUserName(), SharedPref.getUser().getPassword());
        }

        //隐藏系统自带的标题栏
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        //sign_up_button点击事件
        Button signUpButton = (Button) findViewById(R.id.sign_up_button);
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignIn.this, SignUp.class);
                startActivity(intent);
            }
        });

        //sign_in_button点击事件
        edtUserName = (EditText) findViewById(R.id.user_name);
        edtPassword = (EditText) findViewById(R.id.password);
        Button signInButton = (Button) findViewById(R.id.sign_in_button);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String currentUserName = edtUserName.getText().toString();
                String currentPassword = edtPassword.getText().toString();

                if (currentUserName.equals("") || currentPassword.equals("")) {
                    if (currentUserName.equals("")) {
                        Toast.makeText(SignIn.this,
                                "请输入有效用户名",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(SignIn.this,
                                "请输入有效密码",
                                Toast.LENGTH_SHORT).show();
                    }
                } else {
                    queryUser(currentUserName, currentPassword);
                }
            }
        });
    }

    /** 查询用户并核对密码 */
    public void queryUser(String currentUserName, String currentPassword) {
        User user = UserDatabase.getInstence().search(currentUserName, currentPassword);
        if(user==null){
            Toast.makeText(SignIn.this,
                    "账号或密码输入有误！",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        MetaApplication metaApplication = (MetaApplication) getApplicationContext();
        metaApplication.setUser(user);
        SharedPref.setUser(user);
        SharedPref.setLogin(true);
        Intent intent = new Intent(SignIn.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

}
