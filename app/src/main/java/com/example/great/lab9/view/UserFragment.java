package com.example.great.lab9.view;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;

import android.graphics.BitmapFactory;
import android.util.Base64;

import com.example.great.lab9.R;
import com.example.great.lab9.util.SharedPreferencesUtils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

import android.widget.EditText;
import android.widget.Toast;

import java.sql.Statement;

public class UserFragment extends BaseFragment{

    TextView txt_name,txt_wechat,txt_email,txt_qq,txt_signature,txt_nick;

    Connection cn;
    String getname;
    String wechat;
    String qq;
    String email;
    String signature;
    String nick;
    String image;

    private View upload;
    private View collection;
    private String userName;
    String result_photo;


    private static final int PHOTO_REQUEST_GALLERY = 2;// 从相册中选择
    private static final int PHOTO_REQUEST_CUT = 3;// 结果

    private ImageView iv_image;
    /* 头像名称 */
    private static final String PHOTO_FILE_NAME = "temp_photo.jpg";
    private File tempFile;

    private ByteArrayOutputStream baos = new ByteArrayOutputStream();
    private byte [] bitmapByte;

    @Override
    protected void loadData() {
        if(!isPrepared || !isVisible) {
            return;
        }

        final String name = (String) SharedPreferencesUtils.getParam(getActivity(),"name","");
        userName = name;
        //String password  = (String) SharedPreferencesUtils.getParam(getActivity(),"password","");

        //User user =  new UserDB(getActivity()).getUser(name,password);

        //if (user==null)return;

        new Thread(new Runnable() {
            @Override
            public void run() {
                new Handler(Looper.getMainLooper());
                getServerData(name);
            }
        }).start();

        txt_name.setText(getname);
        txt_wechat.setText(wechat);
        txt_qq.setText(qq);
        txt_email.setText(email);
        txt_signature.setText(signature);
        txt_nick.setText(nick);

    }

    private void getServerData(String name) {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            cn = DriverManager.getConnection("jdbc:mysql://172.18.187.230:53306/kingjames", "user", "123");

            String tempSql = "select * from user where name='%s' ";
            String sql = String.format(tempSql, name);
            //Toast.makeText(this,"fuck you",Toast.LENGTH_SHORT).show();
            Statement st = (Statement) this.cn.createStatement();
            ResultSet rs = st.executeQuery(sql);

            while(rs.next())
            {
                getname = rs.getString("name");
                wechat = rs.getString("wechat");
                qq = rs.getString("QQ");
                email = rs.getString("email");
                signature = rs.getString("signature");
                nick = rs.getString("word");
                image = rs.getString("image");

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


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user, container, false);
        //初始化view的各控件
        isPrepared = true;
        iv_image = (ImageView) view.findViewById(R.id.iv_image);
        txt_name = (TextView) view.findViewById(R.id.txt_name);
        txt_wechat = (TextView)view.findViewById(R.id.txt_email);
        txt_qq = (TextView)view.findViewById(R.id.txt_qq);
        txt_email = (TextView)view.findViewById(R.id.txt_email);
        txt_signature = (TextView)view.findViewById(R.id.txt_signature);
        txt_nick = (TextView)view.findViewById(R.id.txt_nickname);
        upload = (View) view.findViewById(R.id.upload);
        collection = view.findViewById(R.id.collection);
        loadData();
        getServerData(userName);
        iv_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                // 开启一个带有返回值的Activity，请求码为PHOTO_REQUEST_GALLERY
                startActivityForResult(intent, PHOTO_REQUEST_GALLERY);
            }
        });
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                AlertDialog.Builder mydialog = new AlertDialog.Builder(getActivity());
                final View dialogview = LayoutInflater.from(getActivity()).inflate(R.layout.usereditor,null);
                mydialog.setView(dialogview);

                mydialog.setTitle("个人信息修改").setPositiveButton("保存修改", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                       send1(dialogview);

                    }
                }).create().show();
            }
        });
        collection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),MyCollection.class);
                intent.putExtra("user",userName);
                startActivityForResult(intent,0);
            }
        });
        return view;
    }

    public void send1(final View v) {
        final EditText e1  = v.findViewById(R.id.new_pw);
        final String get1 = e1.getText().toString();
        final EditText e2  = v.findViewById(R.id.new_nick);
        final String get2 = e2.getText().toString();
        final EditText e3  = v.findViewById(R.id.new_em);
        final String get3 = e3.getText().toString();
        final EditText e4  = v.findViewById(R.id.new_wechat);
        final String get4 = e4.getText().toString();
        final EditText e5  = v.findViewById(R.id.new_qq);
        final String get5 = e5.getText().toString();
        final EditText e6  = v.findViewById(R.id.new_sig);
        final String get6 = e6.getText().toString();
        Log.d("get1",get1);
        new Thread(new Runnable() {
            @Override
            public void run() {
                new Handler(Looper.getMainLooper());
                edit(get1,get2,get3,get4,get5,get6);
            }
        }).start();
    }
    public void edit(String str1,String str2,String str3,String str4,String str5,String str6){

        try {
            Class.forName("com.mysql.jdbc.Driver");
            Log.i("mytag:","123");
            cn = DriverManager.getConnection("jdbc:mysql://172.18.187.230:53306/kingjames", "user", "123");
            Log.i("mytag:","321b");
            String tempSql = "UPDATE user SET password='%s',word='%s',email='%s',wechat='%s',QQ='%s',signature='%s'where name ='%s' LIMIT 1";
            Log.i("mytag:","666");
            String sql = String.format(tempSql,str1,str2,str3,str4,str5,str6,userName);

            Statement st = cn.createStatement();
            Log.i("mytag:",sql);
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

    private void crop(Uri uri) {
        // 裁剪图片意图
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        // 裁剪框的比例，1：1
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // 裁剪后输出图片的尺寸大小
        intent.putExtra("outputX", 200);
        intent.putExtra("outputY", 200);

        intent.putExtra("outputFormat", "PNG");// 图片格式
        intent.putExtra("noFaceDetection", true);// 取消人脸识别
        intent.putExtra("return-data", true);
        // 开启一个带有返回值的Activity，请求码为PHOTO_REQUEST_CUT
        startActivityForResult(intent, PHOTO_REQUEST_CUT);
    }

    private boolean hasSdcard() {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PHOTO_REQUEST_GALLERY) {
            // 从相册返回的数据
            if (data != null) {
                // 得到图片的全路径
                Uri uri = data.getData();
                crop(uri);
            }

        }  else if (requestCode == PHOTO_REQUEST_CUT) {
            // 从剪切图片返回的数据
            if (data != null) {
                Bitmap bitmap1 = data.getParcelableExtra("data");
                //ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap1.compress(Bitmap.CompressFormat.PNG,10,baos);

                int options = 90;

                while (baos.toByteArray().length / 1024 > 500) { // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
                    baos.reset(); // 重置baos即清空baos
                    bitmap1.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
                    options -= 10;// 每次都减少10

                }
                ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());// 把压缩后的数据baos存放到ByteArrayInputStream中
                Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);// 把ByteArrayInputStream数据生成图片
                //return bitmap;
                bitmapByte = baos.toByteArray();

                result_photo = Base64.encodeToString(bitmapByte,Base64.DEFAULT);

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        new Handler(Looper.getMainLooper());
                        setimage(result_photo);
                    }
                }).start();
                //Toast.makeText(UserFragment.this,result_photo,Toast.LENGTH_LONG).sho();
                //System.out.println(result_photo);
                this.iv_image.setImageBitmap(bitmap);
            }
            try {
                // 将临时文件删除
                tempFile.delete();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    private void setimage(String result){
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Log.i("mytag:","aaa");
            cn = DriverManager.getConnection("jdbc:mysql://172.18.187.230:53306/kingjames", "user", "123");
            Log.i("mytag:","bbb");
            String tempSql = "UPDATE user SET image='%s' WHERE (name='123') LIMIT 1";
            Log.i("mytag:","ccc");
            String sql = String.format(tempSql, result_photo);

            Statement st = (Statement) this.cn.createStatement();
            Log.i("mytag:",sql);
            int cnt = st.executeUpdate(sql);

            if (cnt > 0)
                //Toast.makeText(getActivity(),result_photo,Toast.LENGTH_LONG).show();
                cn.close();
            st.close();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


}
