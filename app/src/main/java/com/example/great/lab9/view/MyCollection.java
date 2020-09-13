package com.example.great.lab9.view;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import com.example.great.lab9.R;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by oushikotoba on 2018/1/10.
 */

public class MyCollection extends AppCompatActivity {
    private List<Map<String, Object>> listItem = Collections.synchronizedList(new ArrayList<Map<String,Object>>());
    //private List<Hashtable> listItem = new ArrayList<>();
    private ListView cl;
    private SimpleAdapter listAdp;
    Connection cn;
    private String user;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mycollection);
        cl = (ListView) findViewById(R.id.cl_list);


        Intent intent = getIntent();
         user = (String) intent.getExtras().get("user");

        collect_handler();
        listAdp = new SimpleAdapter(MyCollection.this, listItem, R.layout.collection_layout, new String[]{"name", "content","location"}, new int[]{R.id.cl_who, R.id.cl_content,R.id.cl_where});
        cl.setAdapter(listAdp);
        cl.setDivider(null);

    }

    protected void collect_handler() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized (listItem){
                    getcollection();
                }

            }
        }).start();

    }
    private void getcollection(){

        try {
            Class.forName("com.mysql.jdbc.Driver");
            cn = DriverManager.getConnection("jdbc:mysql://172.18.187.230:53306/kingjames", "user", "123");
            String tempSql = "select * from collection where name = '%s' order by id desc";

            String sql = String.format(tempSql,this.user);
            Statement st = cn.createStatement();
            ResultSet rs = st.executeQuery(sql);
            //listItem = new ArrayList<>();
            while (rs.next()) {
                Map<String, Object> tmp = new LinkedHashMap<>();

                tmp.put("name", rs.getString("who") );
                tmp.put("content", rs.getString("content"));
                tmp.put("location", rs.getString("location") );
                listItem.add(tmp);
            }

            Log.i("tag",listItem.get(0).get("name").toString());
            cn.close();
            st.close();
            rs.close();

        }  catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}
