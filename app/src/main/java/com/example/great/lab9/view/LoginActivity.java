package com.example.great.lab9.view;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.great.lab9.JellyInterpolator;
import com.example.great.lab9.R;
import com.example.great.lab9.db.UserDB;
import com.example.great.lab9.model.User;
import com.example.great.lab9.util.SharedPreferencesUtils;
import com.example.great.lab9.util.ToastUtil;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class LoginActivity extends Activity {

    private TextView mBtnLogin;
    private View progress;
    private View mInputLayout;
    private float mWidth, mHeight;
    private LinearLayout mName, mPsw;

    EditText mUserName, mUserPwd;
    String userName = "";//记录输入的用户名
    String userPwd = "";//记录用户输入的密码
    User user;
    UserDB db;
    Connection cn;
    private int flag=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main2);


        initView();
    }

    private void initView() {
        mBtnLogin = (TextView) findViewById(R.id.main_btn_login);
        progress = findViewById(R.id.layout_progress);
        mInputLayout = findViewById(R.id.input_layout);
        mName = (LinearLayout) findViewById(R.id.input_layout_name);
        mPsw = (LinearLayout) findViewById(R.id.input_layout_psw);
        //mBtnLogin.setOnClickListener(this);
        mUserName = (EditText)findViewById(R.id.input_name);
        mUserPwd = (EditText) findViewById(R.id.input_pwd);
    }


    public void signin(View v) throws InterruptedException {

        userName = mUserName.getText().toString();
        userPwd = mUserPwd.getText().toString();


        SharedPreferencesUtils.setParam(LoginActivity.this, "name", userName);
        SharedPreferencesUtils.setParam(LoginActivity.this, "password", userPwd);

        if (userName.isEmpty()) {
            Toast.makeText(this,"请输入用户名",Toast.LENGTH_SHORT).show();
            return;
        }

        if (userPwd.isEmpty()) {
            Toast.makeText(this,"请输入密码",Toast.LENGTH_SHORT).show();
            return;
        }


        mWidth = mBtnLogin.getMeasuredWidth();
        mHeight = mBtnLogin.getMeasuredHeight();

        mName.setVisibility(View.INVISIBLE);
        mPsw.setVisibility(View.INVISIBLE);

        inputAnimator(mInputLayout, mWidth, mHeight);

        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    Thread.currentThread().sleep(1000);//阻断2秒
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                new Handler(Looper.getMainLooper());

                getServerData(userName);


            }
        }).start();

    }



    private void inputAnimator(final View view, float w, float h) {

        AnimatorSet set = new AnimatorSet();

        ValueAnimator animator = ValueAnimator.ofFloat(0, w);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (Float) animation.getAnimatedValue();
                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) view
                        .getLayoutParams();
                params.leftMargin = (int) value;
                params.rightMargin = (int) value;
                view.setLayoutParams(params);
            }
        });

        ObjectAnimator animator2 = ObjectAnimator.ofFloat(mInputLayout,
                "scaleX", 1f, 0.5f);
        set.setDuration(1000);
        set.setInterpolator(new AccelerateDecelerateInterpolator());
        set.playTogether(animator, animator2);
        set.start();
        set.addListener(new Animator.AnimatorListener() {

            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onAnimationEnd(Animator animation) {

                progress.setVisibility(View.VISIBLE);
                progressAnimator(progress);
                mInputLayout.setVisibility(View.INVISIBLE);

            }

            @Override
            public void onAnimationCancel(Animator animation) {
                // TODO Auto-generated method stub

            }
        });

    }

    private void progressAnimator(final View view) {
        PropertyValuesHolder animator = PropertyValuesHolder.ofFloat("scaleX",
                0.5f, 1f);
        PropertyValuesHolder animator2 = PropertyValuesHolder.ofFloat("scaleY",
                0.5f, 1f);
        ObjectAnimator animator3 = ObjectAnimator.ofPropertyValuesHolder(view,
                animator, animator2);
        animator3.setDuration(2000);
        animator3.setInterpolator(new JellyInterpolator());
        animator3.start();

    }



    private void getServerData(String name) {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            cn = DriverManager.getConnection("jdbc:mysql://172.18.187.230:53306/kingjames", "user", "123");

            String tempSql = "select password from user where name='%s' ";
            String sql = String.format(tempSql, name);
            //Toast.makeText(this,"fuck you",Toast.LENGTH_SHORT).show();
            Statement st = (Statement) this.cn.createStatement();
            ResultSet rs = st.executeQuery(sql);

            Handler mHandler = new Handler(Looper.getMainLooper()){
                @Override
                public void handleMessage(Message msg) {
                    switch(msg.what){
                        case 1:
                            Bundle data = msg.getData();
                            int error = data.getInt("error");
                            Toast.makeText(getApplicationContext(),"账号不存在",Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(),LoginActivity.class));
                            break;
                        case 2:
                            //Bundle data2 = msg.getData();
                            //int error2 = data2.getInt("error");
                            Toast.makeText(getApplicationContext(),"密码错误",Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(),LoginActivity.class));
                            break;
                    }
                }
            };


            if(rs.next()==false){
                flag=1;
                Message msg = new Message();
                msg.what = flag;
                Bundle data = new Bundle();
                data.putInt("error",1);
                msg.setData(data);
                mHandler.sendMessage(msg);
                //Toast.makeText(this,"账号不存在",Toast.LENGTH_SHORT).show();
                return;
            }
            else{
                rs.first();
                //while(rs.next())
                //{
                    String pwd = rs.getString("password");
                    if(pwd.equals(userPwd))
                    {
                        Intent intent_login_to_user = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent_login_to_user);
                    }
                    else
                    {
                        flag=2;
                        Message msg = new Message();
                        msg.what = flag;
                        Bundle data = new Bundle();
                        data.putInt("error",1);
                        msg.setData(data);
                        mHandler.sendMessage(msg);
                        //Toast.makeText(getApplicationContext(),"密码错误",Toast.LENGTH_SHORT).show();
                        return;
                    }
               // }
            }




            cn.close();
            st.close();
            rs.close();
        }  catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



    public void signup(View v) {
        Intent intent_login_to_user = new Intent(LoginActivity.this, Sign_upActivity.class);
        startActivity(intent_login_to_user);
    }
}

