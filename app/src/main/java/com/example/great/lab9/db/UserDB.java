package com.example.great.lab9.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.great.lab9.model.User;

public class UserDB extends SQLiteOpenHelper {
    public final static String DATABASE_NAME = "user.db";
    public final static int DATABASE_VERSION = 1;
    public final static String TABLE_NAME = "user_table";
    public final static String user_id = "user_id";
    public final static String name = "name";
    public final static String password = "password";
    public final static String age = "age";
    public final static String email = "email";
    public final static String hobby = "hobby";

    public UserDB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // 创建数据库
    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE " + TABLE_NAME +
                " (" + user_id + " INTEGER primary key autoincrement, "
                + age
                + " INTEGER, " + name + " text, " + password
                + " text, " + email + " text, " + hobby
                + " text);";

        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String sql = "DROP TABLE IF EXISTS " + TABLE_NAME;
        db.execSQL(sql);
        onCreate(db);
    }

    public User getUser(String userName, String userPassword) {

        User user = null;

        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "select * from tableName where nameKey='nameValue' and passwordKey='passwordValue'";
        sql = sql.replace("tableName", TABLE_NAME).replace("nameKey", name).replace("passwordKey", password).replace("nameValue", userName).replace("passwordValue", userPassword);

        Cursor cursor = db.rawQuery(sql, null);

        // 是否存在数据
        if (cursor.moveToFirst()) {
            for (int i = 0; i < cursor.getCount(); i++) {
                user = new User();
                user.setName(cursor.getString(cursor
                        .getColumnIndex(name)));
                user.setPassword(cursor.getString(cursor
                        .getColumnIndex(password)));
                user.setAge(cursor.getInt(cursor
                        .getColumnIndex(age)));
                user.setEmail(cursor.getString(cursor
                        .getColumnIndex(email)));
                user.setHobby(cursor.getString(cursor
                        .getColumnIndex(hobby)));
            }
        }

        return user;
    }

    //新增
    public long insert(User user) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(name, user.getName());
        cv.put(age, user.getAge());
        cv.put(email, user.getEmail());
        cv.put(password, user.getPassword());
        cv.put(hobby, user.getHobby());

        long row = db.insert(TABLE_NAME, null, cv);
        return row;
    }
}