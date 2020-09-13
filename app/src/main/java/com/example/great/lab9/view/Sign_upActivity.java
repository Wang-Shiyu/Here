package com.example.great.lab9.view;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.great.lab9.R;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by peter on 2017/12/28.
 */

public class Sign_upActivity extends AppCompatActivity {

    private EditText name;
    private EditText pwd;
    private EditText pwd_conf;
    private EditText nick;

    private View progress;

    private LinearLayout sign1;
    private LinearLayout sign2;
    private LinearLayout sign3;
    private LinearLayout sign4;
    Connection cn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up);

        name = (EditText)findViewById(R.id.sign_name);
        pwd = (EditText) findViewById(R.id.sign_pwd);
        pwd_conf = (EditText) findViewById(R.id.sign_pwd_confirm);
        nick = (EditText) findViewById(R.id.nickname);
        progress = findViewById(R.id.layout_progress);
        sign1 = (LinearLayout) findViewById(R.id.sign1);
        sign2 = (LinearLayout) findViewById(R.id.sign2);
        sign3 = (LinearLayout) findViewById(R.id.sign3);
        sign4 = (LinearLayout) findViewById(R.id.sign4);

    }

    public void sign(View v){
        final String get_name = name.getText().toString();
        final String get_pwd = pwd.getText().toString();
        final String get_pwd_conf = pwd_conf.getText().toString();
        final String get_nick = nick.getText().toString();

        setvisible();

        if(get_pwd.equals(get_pwd_conf)&&get_pwd!=null&&get_name!=null)
        {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    new Handler(Looper.getMainLooper());
                    savaServerData(get_name, get_pwd, get_nick);
                }
            }).start();

            Toast.makeText(this,"注册成功,3秒后跳转",Toast.LENGTH_SHORT).show();

            new Handler(new Handler.Callback() {
                //处理接收到的消息的方法
                @Override
                public boolean handleMessage(Message arg0) {
                    //实现页面跳转
                    startActivity(new Intent(getApplicationContext(),LoginActivity.class));
                    return false;
                }
            }).sendEmptyMessageDelayed(0, 3000); //表示延时三秒进行任务的执行

        }
        else{
            Toast.makeText(this,"密码不一致",Toast.LENGTH_SHORT).show();
        }


    }

    public void savaServerData(String name, String pwd, String nick) {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            cn = DriverManager.getConnection("jdbc:mysql://172.18.187.230:53306/kingjames", "user", "123");

            String fmt = "insert into user(name,password,word) values('%s','%s','%s')";


            String sql = String.format(fmt, name, pwd, nick);
            Log.d("fuck","fuck");
            Statement st = (Statement) cn.createStatement();
            int cnt = st.executeUpdate(sql);

            cn.close();
            st.close();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void setvisible(){
        sign1.setVisibility(View.INVISIBLE);
        sign2.setVisibility(View.INVISIBLE);
        sign3.setVisibility(View.INVISIBLE);
        sign4.setVisibility(View.INVISIBLE);
        progress.setVisibility(View.VISIBLE);
    }

    public void sign_back(View v){
        startActivity(new Intent(getApplicationContext(),LoginActivity.class));
    }

}
