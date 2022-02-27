package com.example.daily_cashbook.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.daily_cashbook.R;
import com.example.daily_cashbook.dbutils.UserDatabase;
import com.example.daily_cashbook.entity.User;
import com.example.daily_cashbook.msg.Hint;

public class ChangePswd extends AppCompatActivity {

    User user;

    ImageView back;
    EditText etorgPassword;
    EditText etnewPassword;
    EditText etcfPssword;
    TextView cf;
    String orgPswd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_pswd);

        user = ((MetaApplication) getApplication()).getUser();
        orgPswd = ((MetaApplication) getApplication()).getUser().getPassword();
        etorgPassword = (EditText) findViewById(R.id.changePswd_org_password);
        etnewPassword = (EditText) findViewById(R.id.changePswd_new_password);
        etcfPssword = (EditText) findViewById(R.id.changePswd_cf_password);

        back = (ImageView) findViewById(R.id.changePswd_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        cf = (TextView) findViewById(R.id.changePswd_cf);
        cf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newPassword = etnewPassword.getText().toString();
                String newPasswordAgain = etcfPssword.getText().toString();
                String oldPassword = etorgPassword.getText().toString();

                if (!orgPswd.equals(oldPassword)) {
                    Hint.show("原密码错误");
                    return;
                }
                if (verifyPassword(newPassword) == 1) {
                    Hint.show("请输入至少六位数的新密码");
                    return;
                } else if (verifyPassword(newPassword) == 11) {
                    Hint.show("请输入包含至少一个字母的新密码");
                    return;
                } else if (verifyPassword(newPassword) == 111) {
                    Hint.show("请输入至少包含一个数字的新密码");
                    return;
                }

                if (!newPassword.equals(newPasswordAgain)) {
                    Hint.show("两次密码输入不一致");
                    return;
                }

                User user = ((MetaApplication) getApplication()).getUser();
                user.setPassword(newPassword);
                UserDatabase.getInstence().update(user);
                ((MetaApplication) getApplication()).setUser(user);
                finish();
            }
        });

    }

    /** 1密码要求验证 */
    public int verifyPassword(String password) { //通过=0，长度不通过=1，无字母=11，无数字=111
        int verifyPassword = 0;
        if (password.length() < 6) {
            return verifyPassword = 1;
        }
        if (!password.matches(".*[\\w].*")) {
            return verifyPassword = 11;
        }
        if (!password.matches(".*[\\d].*")) {
            return verifyPassword = 111;
        }
        return verifyPassword;
    }
}
