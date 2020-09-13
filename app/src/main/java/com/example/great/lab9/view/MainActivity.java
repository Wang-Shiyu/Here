package com.example.great.lab9.view;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;

import com.example.great.lab9.R;
import com.example.great.lab9.adapter.FragmentAdapter;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    //fragment
    ViewPager fragmentViewPager;
    FragmentAdapter fragmentAdapter;
    List<Fragment> fragmentList = new ArrayList<>();

    //选项卡
    Button homeBtn, locationBtn, userBtn;
    List<Button> tabButtonList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        setContentView(R.layout.activity_main);

        initFragment();
        initButton();
        initAdapter();

        changAlpha(0);
        fragmentViewPager.setCurrentItem(0);
    }

    private void initButton() {
        homeBtn = (Button) findViewById(R.id.home_btn_true);
        homeBtn.setOnClickListener(this);

        locationBtn = (Button) findViewById(R.id.location_btn_true);
        locationBtn.setOnClickListener(this);

        userBtn = (Button) findViewById(R.id.user_btn_true);
        userBtn.setOnClickListener(this);

        tabButtonList.add(homeBtn);
        tabButtonList.add(locationBtn);
        tabButtonList.add(userBtn);

        Drawable drawable1 = getResources().getDrawable(R.mipmap.home1);
        drawable1.setBounds(0,0,10,10);
        Drawable drawable2 = getResources().getDrawable(R.mipmap.home2);
        drawable2.setBounds(0,0,10,10);

    }

    /**
     * 初始化fagment
     */
    private void initFragment() {
        fragmentList.add(new LocationFragment());
        fragmentList.add(new HomeFragment());
        fragmentList.add(new UserFragment());

        fragmentViewPager = (ViewPager) findViewById(R.id.m_main_viewpager);
    }

    /**
     * 初始化adapter
     */
    private void initAdapter() {
        fragmentAdapter = new FragmentAdapter(getSupportFragmentManager(), fragmentList);
        fragmentViewPager.setAdapter(fragmentAdapter);

        //viewpager滑动监听
        fragmentViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                changAlpha(position, positionOffset);
            }

            @Override
            public void onPageSelected(int position) {
                changAlpha(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.home_btn_true:
                fragmentViewPager.setCurrentItem(0, false);
                break;
            case R.id.location_btn_true:
                fragmentViewPager.setCurrentItem(1, false);
                break;
            case R.id.user_btn_true:
                fragmentViewPager.setCurrentItem(2, false);
                break;
        }
    }

    /**
     * 一开始运行、滑动和点击tab结束后设置tab的透明度，fragment的透明度和大小
     */
    private void changAlpha(int postion) {
        for (int i = 0; i < tabButtonList.size(); i++) {
            if (i == postion) {
                tabButtonList.get(i).setAlpha(1.0f);
                if (null != fragmentList.get(i).getView()) {
                    fragmentList.get(i).getView().setAlpha(1.0f);
                    fragmentList.get(i).getView().setScaleX(1.0f);
                    fragmentList.get(i).getView().setScaleY(1.0f);
                }
            } else {
                tabButtonList.get(i).setAlpha(0.0f);
                if (null != fragmentList.get(i).getView()) {
                    fragmentList.get(i).getView().setAlpha(0.0f);
                    fragmentList.get(i).getView().setScaleX(0.0f);
                    fragmentList.get(i).getView().setScaleY(0.0f);
                }
            }
        }
    }

    /**
     * 根据滑动设置透明度
     */
    private void changAlpha(int pos, float posOffset) {
        int nextIndex = pos + 1;
        if (posOffset > 0 && nextIndex < 3) {
            Button nextButton = tabButtonList.get(nextIndex);
            Button curentButton = tabButtonList.get(pos);
            if (nextButton == null || curentButton == null) return;

            View nextView = fragmentList.get(nextIndex).getView();
            View curentView = fragmentList.get(pos).getView();
            if (nextView == null || curentView == null) return;

            //设置tab的颜色渐变效果
            nextButton.setAlpha(posOffset);
            curentButton.setAlpha(1 - posOffset);

            //设置fragment的颜色渐变效果
            nextView.setAlpha(posOffset);
            curentView.setAlpha(1 - posOffset);

            //设置fragment滑动视图由大到小，由小到大的效果
            nextView.setScaleX(0.5F + posOffset / 2);
            nextView.setScaleY(0.5F + posOffset / 2);
            curentView.setScaleX(1 - (posOffset / 2));
            curentView.setScaleY(1 - (posOffset / 2));
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            onBackPressed();
            return true;
        }
        return super.onKeyDown(keyCode, event);

    }

    @Override
    public void onBackPressed() {
        //返回手机的主屏幕
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addCategory(Intent.CATEGORY_HOME);
        startActivity(intent);
    }
}

