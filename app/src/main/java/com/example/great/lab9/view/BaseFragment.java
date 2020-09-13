package com.example.great.lab9.view;

import android.support.v4.app.Fragment;

public abstract class BaseFragment extends Fragment {
    //是否可见
    protected boolean isVisible;
    // 标志位，标志Fragment已经初始化完成。
    public boolean isPrepared = false;

    /**
     * 实现Fragment数据的缓加载  
     * @param isVisibleToUser
     */
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser); 
       if (getUserVisibleHint()) {
            isVisible = true; 
           onVisible();
        } else {
            isVisible = false;
            onInVisible();
        } 
   }
    protected void onInVisible() {
    }
    protected void onVisible() {
        //加载数据
        loadData();
    } 
   protected abstract void loadData();
}