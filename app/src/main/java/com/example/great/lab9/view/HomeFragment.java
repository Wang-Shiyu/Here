package com.example.great.lab9.view;

import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.great.lab9.NestedListView;
import com.example.great.lab9.R;
import com.example.great.lab9.util.SharedPreferencesUtils;

import org.w3c.dom.Text;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import me.drakeet.uiview.UIImageView;
import oushikotoba.mylibrary.Eyes;

public class HomeFragment extends BaseFragment {

    private List<Map<String, Object>> listItem = new ArrayList();
    private List<Map<String, Object>> listItem2 = new ArrayList();
    //private ListView chat_list;
    private NestedListView chat_list;
    private SimpleAdapter listAdp;

    private EditText input;
    private String userName;
    private TimerHandler handler;
    private Bitmap bitmap;
    Connection cn;
    private int number;
    TextView total;

    public class TimerHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            listAdp = new SimpleAdapter(getActivity(), listItem, R.layout.chat_item, new String[]{"name", "content"}, new int[]{R.id.nickname, R.id.chat_content});
            chat_list.setAdapter(listAdp);
            chat_list.setDivider(null);
            total.setText("附近人数："+number);
            listAdp.notifyDataSetChanged();
        }
    }



    public Toolbar initToolbar(View view) {
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("StatusBarColorToolbar");

        AppBarLayout mAppBarLayout = (AppBarLayout) view.findViewById(R.id.appbar);
        CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) view.findViewById(R.id.collapsing_layout);
        collapsingToolbarLayout.setTitle(getString(R.string.app_name));
        Eyes.setStatusBarColorForCollapsingToolbar((Activity) this.getContext(), mAppBarLayout, collapsingToolbarLayout, toolbar, ContextCompat.getColor(this.getContext(), R.color.colorPrimary));
        return toolbar;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.new_activity_home, container, false);
        isPrepared = true;
        //初始化view的各控件
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        initToolbar(view);

        initTimer();
        getServerData();

        //Toast.makeText(this.getContext(),LocationFragment.LOCATION,Toast.LENGTH_SHORT).show();

        input = view.findViewById(R.id.input);
        chat_list = view.findViewById(R.id.chat_list);
        total = (TextView) view.findViewById(R.id.number);
        listAdp = new SimpleAdapter(this.getContext(), listItem, R.layout.chat_item, new String[]{"name", "content","image"}, new int[]{R.id.nickname, R.id.chat_content,R.id.UIImageView});
        chat_list.setAdapter(listAdp);
        setListViewHeightBasedOnChildren(chat_list);
        view.findViewById(R.id.send).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                send();
                input.setText("");
            }
        });

        userName = (String) SharedPreferencesUtils.getParam(getActivity(), "name", "");

        loadData();

        chat_list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view1, int position, long id) {
                if(position>-1){
                    final String cur_content = listItem.get(position).get("content").toString();
                    Log.i("mytag",":"+listItem2.size());
                    for(int i=0;i<listItem2.size();i++){
                        String p1 = listItem.get(position).get("name").toString();
                        String p2 = listItem2.get(i).get("word").toString();
                        if( p1.equals(p2)) {
                            String _wechat = listItem2.get(i).get("wechat").toString();
                            String _QQ = listItem2.get(i).get("QQ").toString();
                            String _signature = listItem2.get(i).get("signature").toString();
                            String _email = listItem2.get(i).get("email").toString();
                            final String _name = listItem2.get(i).get("name").toString();
                            String _word = listItem2.get(i).get("word").toString();
                            AlertDialog.Builder mydialog = new AlertDialog.Builder(getActivity());
                            View dialogview = LayoutInflater.from(getActivity()).inflate(R.layout.dialog,null);
                            mydialog.setView(dialogview);
                            TextView dialogname = dialogview.findViewById(R.id.new_nick);
                            TextView dialogword = dialogview.findViewById(R.id.diaword);
                            TextView dialogsignature = dialogview.findViewById(R.id.diasignature);
                            TextView dialogemial = dialogview.findViewById(R.id.diaemail);
                            TextView dialogwechat = dialogview.findViewById(R.id.diawechat);
                            TextView dialogqq = dialogview.findViewById(R.id.diaqq);
                            dialogname.setText(_name);
                            dialogword.setText(_word);
                            dialogsignature.setText(_signature);
                            dialogemial.setText(_email);
                            dialogwechat.setText(_wechat);
                            dialogqq.setText(_QQ);
                            Log.i("mytag","11111111111");
                            mydialog.setTitle("Information")
                                    .setPositiveButton("收藏", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            collect_handler(cur_content,_name);
                                        }
                                    })
                                    .create().show();break;


                        }
                    }
                }
                return true;
            }
        });
        return view;
    }

    /**
     * 设置一个定时器定时启动服务，获取聊天数据
     */
    private void initTimer() {
        handler = new TimerHandler();

        Timer timer = new Timer();     //设置定时器Timer
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                getServerData();
                handler.sendEmptyMessage(0);
            }
        }, 0, 1000);  //0表示无延迟，1000表示隔1000ms
    }
    private void collect(String content,String who){
        try {
            Class.forName("com.mysql.jdbc.Driver");
            cn = DriverManager.getConnection("jdbc:mysql://172.18.187.230:53306/kingjames", "user", "123");

            String fmt = "insert into collection(name,content,location,who) values('%s','%s','%s','%s')";


            String sql = String.format(fmt, userName, content, LocationFragment.LOCATION,who);

            Statement st = (Statement) cn.createStatement();
            int cnt = st.executeUpdate(sql);
            if (cnt > 0)
                cn.close();
            st.close();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private void getServerData() {

        try {
            Class.forName("com.mysql.jdbc.Driver");
            cn = DriverManager.getConnection("jdbc:mysql://172.18.187.230:53306/kingjames", "user", "123");

            String tempSql = "select chat.*,`user`.image,`user`.word from chat,user where location='%s'and chat.name = `user`.name order by _id desc";
            String sql = String.format(tempSql, LocationFragment.LOCATION);
            Statement st = this.cn.createStatement();
            ResultSet rs = st.executeQuery(sql);
            listItem = new ArrayList<>();
            listItem.clear();
            while (rs.next()) {
                Map<String, Object> tmp = new LinkedHashMap<>();
                tmp.put("name", rs.getString("word"));
                tmp.put("content", rs.getString("content"));
                //Log.i("mytag","rs____"+rs.getString("image"));
//                byte[] bytes= Base64.decode(rs.getString("image"), Base64.DEFAULT);
               // Log.i("mytag","byte____"+bytes);
                //bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
               // tmp.put("image",bitmap);
                listItem.add(tmp);
            }

            cn.close();
            st.close();
            rs.close();

        }  catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try{
            Class.forName("com.mysql.jdbc.Driver");
            cn = DriverManager.getConnection("jdbc:mysql://172.18.187.230:53306/kingjames", "user", "123");
            String tempSql2 = "select * from user ";
            Statement st2 = this.cn.createStatement();
            ResultSet rs2 = st2.executeQuery(tempSql2);
            listItem2 = new ArrayList<>();
            listItem2.clear();
            while (rs2.next()) {
                Log.d("w","w");
                Map<String, Object> tmp = new LinkedHashMap<>();
                tmp.put("name", rs2.getString("name"));
                tmp.put("wechat", rs2.getString("wechat"));
                tmp.put("QQ", rs2.getString("QQ"));
                tmp.put("email", rs2.getString("email"));
                tmp.put("signature", rs2.getString("signature"));
                tmp.put("word", rs2.getString("word"));
                listItem2.add(tmp);
            }
            cn.close();
            st2.close();
            rs2.close();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try{
            Class.forName("com.mysql.jdbc.Driver");
            cn = DriverManager.getConnection("jdbc:mysql://172.18.187.230:53306/kingjames", "user", "123");
            String tempSql3 = "select COUNT(DISTINCT chat. NAME) TOTAL from user,chat where chat.NAME IN(SELECT chat.NAME FROM chat WHERE location = '%s') ";
            String sql = String .format(tempSql3,LocationFragment.LOCATION);
            Statement st3 = this.cn.createStatement();
            ResultSet rs3 = st3.executeQuery(sql);
            while(rs3.next()) number = rs3.getInt("TOTAL");
            Log.i("mytag3","near"+number);
            cn.close();
            st3.close();
            rs3.close();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void setListViewHeightBasedOnChildren(ListView listView) {
        // 获取ListView对应的Adapter
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }


        int totalHeight = 0;
        for (int i = 0, len = listAdapter.getCount(); i < len; i++) {
            // listAdapter.getCount()返回数据项的数目
            View listItem = listAdapter.getView(i, null, listView);
            // 计算子项View 的宽高
            listItem.measure(0, 0);
            // 统计所有子项的总高度
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight+ (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        // listView.getDividerHeight()获取子项间分隔符占用的高度
        // params.height最后得到整个ListView完整显示需要的高度
        listView.setLayoutParams(params);


    }
    public void send() {
        final String content = input.getText().toString();
        new Thread(new Runnable() {
            @Override
            public void run() {
                new Handler(Looper.getMainLooper());
                savaServerData(content);
            }
        }).start();
    }
    private void collect_handler(final String con,final String who) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                new Handler(Looper.getMainLooper());
                collect(con,who);
            }
        }).start();
    }
    public void savaServerData(String content) {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            cn = DriverManager.getConnection("jdbc:mysql://172.18.187.230:53306/kingjames", "user", "123");

            String fmt = "insert into chat(name,content,location) values('%s','%s','%s')";


            String sql = String.format(fmt, userName, content, LocationFragment.LOCATION);

            Statement st = (Statement) cn.createStatement();
            int cnt = st.executeUpdate(sql);
            if (cnt > 0)
                cn.close();
            st.close();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void loadData() {
        if (!isPrepared || !isVisible) {
            return;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
