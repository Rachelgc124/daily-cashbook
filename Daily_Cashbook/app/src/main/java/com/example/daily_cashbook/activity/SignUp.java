package com.example.daily_cashbook.activity;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.daily_cashbook.R;
import com.example.daily_cashbook.dbutils.DatabaseHelper;
import com.example.daily_cashbook.dbutils.UserDatabase;
import com.example.daily_cashbook.fragment.SignUpDialog;
import com.example.daily_cashbook.msg.Hint;

public class SignUp extends AppCompatActivity {

    private DatabaseHelper dbHelper;

    private EditText signUpUserName;
    private EditText signUpPassword;
    private EditText signUpPasswordAgain;
    private EditText signUpEmail;
    private EditText signUpTel;

    private AlertDialog alert = null;
    private AlertDialog.Builder builder = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up);
        //创建数据库
        dbHelper = new DatabaseHelper(this);

        //sign_up_back_button点击事件
        Button signUpBackButton = (Button) findViewById(R.id.sign_up_back_button);
        signUpBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUp.this, SignIn.class);
                startActivity(intent);
            }
        });

        //EditText
        signUpUserName = (EditText) findViewById(R.id.sign_in_user_name);
        signUpPassword = (EditText) findViewById(R.id.sign_in_password);
        signUpPasswordAgain = (EditText) findViewById(R.id.sign_in_password_again);
        signUpEmail = (EditText) findViewById(R.id.sign_in_email);
        signUpTel = (EditText) findViewById(R.id.sign_in_tel);

        //密码要求提示
        signUpPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialog();
            }
        });

        // User Database(sign_up_confirm_button点击事件——创建数据库)
        Button signUpConfirmButton = (Button) findViewById(R.id.sign_up_confirm_button);
        signUpConfirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userName = signUpUserName.getText().toString();
                String password = signUpPassword.getText().toString();
                String passwordAgain = signUpPasswordAgain.getText().toString();
                String email = signUpEmail.getText().toString();
                String tel = signUpTel.getText().toString();

                //Task:0用户名存在/1密码要求验证//2重新输入密码匹配/3邮箱要求验证/4电话要求验证
                if (checkInfo(userName, password, passwordAgain, email, tel) == "") {
                    UserDatabase.getInstence().save(userName, password, email, tel);

                    Toast.makeText(SignUp.this,
                            "Successfully registered!",
                            Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(SignUp.this, SignIn.class);
                    startActivity(intent);
                    finish();
                } else {
                    alert = null;
                    builder = new AlertDialog.Builder(SignUp.this);
                    //设置icon setIcon(R.mipmap.ic_icon_fish)
                    alert = builder.setTitle("提示：")
                            .setMessage(checkInfo(userName, password, passwordAgain, email, tel))
                            .setNeutralButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            }).create();             //创建AlertDialog对象
                    alert.show();
                }
            }
        });

    }

    public void openDialog() {
        SignUpDialog dialog = new SignUpDialog();
        dialog.show(getSupportFragmentManager(), "Sign up Dialog");
    }

    public String checkInfo(String userName, String password, String passwordAgain, String email, String tel) {
        String hint = "";

        if (verifyUserName(userName) == 5) {
            hint += "用户名已存在\n";
        }
        if (verifyPassword(password) == 1) {
            hint += "密码须至少6位数\n";
        } else if (verifyPassword(password) == 11) {
            hint += "密码须至少包含一个字母\n";
        } else if (verifyPassword(password) == 111) {
            hint += "密码须至少包含一个数字\n";
        }
        if (checkPassword(password, passwordAgain) == 2) {
            hint += "两次输入密码需相同\n";
        }
        if (verifyEmail(email) == 3) {
            hint += "请输入正确的邮箱地址\n";
        }
        if (verifyTel(tel) == 4) {
            hint += "请输入正确的手机号\n";
        }

        return hint;
    }

    /** 0用户名要求 */
    public int verifyUserName(String userName) { //通过=0，不通过=5
        int verifyUserName = 5;
        if (UserDatabase.getInstence().isExit(userName)) {

            return verifyUserName;
        }
        return verifyUserName = 0;
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

    /** 2重新输入密码匹配 */
    public int checkPassword(String password, String passwordAgain) { //通过=0，不通过=2
        int checkPassword = 2;
        if (password.equals(passwordAgain)) {
            return checkPassword = 0;
        }
        return checkPassword;
    }

    /** 3邮箱要求验证 */
    public int verifyEmail(String email) { //通过=0，不通过=3
        int verifyEmail = 3;
        if (email.matches(".*@.*")) {
            return verifyEmail = 0;
        }
        return verifyEmail;
    }

    /** 4电话要求验证 */
    public int verifyTel(String tel) { //通过=0，不通过=4
        int verifyTel = 4;
        if (tel.length() == 11) {
            return verifyTel = 0;
        }
        return verifyTel;
    }

}
