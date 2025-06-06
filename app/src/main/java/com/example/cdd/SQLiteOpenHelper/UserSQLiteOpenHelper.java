package com.example.cdd.SQLiteOpenHelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import androidx.annotation.Nullable;

import com.example.cdd.Pojo.LoginResult;
import com.example.cdd.Pojo.PlayerInformation;

import java.util.ArrayList;
import java.util.List;

public class UserSQLiteOpenHelper extends SQLiteOpenHelper {
    // 数据库名称
    private static final String DB_NAME = "MySqlite.db";
    // 创建用户表SQL语句
    private static final String CREATE_USERS = "create table users(userID varchar(32), password varchar(32), score integer)";

    public UserSQLiteOpenHelper(@Nullable Context context) {
        super(context, DB_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_USERS);
    }

    @Override
    //只适用于新增字段或修改约束啊啊啊
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("CREATE TEMPORARY TABLE users_backup(userID TEXT, password TEXT, score INTEGER);");
        db.execSQL("INSERT INTO users_backup SELECT userID, password, score FROM users;");

        // 删除旧表并创建新表
        db.execSQL("DROP TABLE IF EXISTS users;");
        db.execSQL(CREATE_USERS); // 使用onCreate中的建表语句

        // 恢复数据
        db.execSQL("INSERT INTO users(userID, password, score) SELECT userID, password, score FROM users_backup;");
        db.execSQL("DROP TABLE IF EXISTS users_backup;");
    }

    // 注册方法，将玩家信息插入数据库
    public long register(PlayerInformation user) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("userID", user.getUserID());
        cv.put("password", user.getPassword());
        cv.put("score", user.getScore());
        long result = db.insert("users", null, cv);
        db.close();
        return result;
    }

    // 登录方法，验证用户名和密码是否匹配
    public LoginResult login(String name, String password) {
        LoginResult loginResult=new LoginResult(false,0);
        SQLiteDatabase db1 = getReadableDatabase();
        boolean result = false;
        Cursor users =db1.query("users", new String[]{"userID","password","score"}, "userID = ?", new String[]{name}, null, null, null);
        try {
                while (users.moveToNext()) {

                    int passwordColumnIndex = users.getColumnIndex("password"); // 通过列名获取索引

                    if (passwordColumnIndex != -1)
                    {
                        String password1 = users.getString(passwordColumnIndex);

                        if (password1 != null && password1.equals(password))
                        {
                            result = true;
                            loginResult.setSuccess(result);
                            loginResult.setScore(users.getInt(2));
                            break;
                        }
                    }
                }
        }
        finally
        {
            users.close(); // 确保Cursor被关闭
            db1.close();
        }
        return loginResult;
    }

    // 检查用户是否存在
    public boolean checkUserExists(String username) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = null;
        boolean exists = false;

        try {
            // 查询用户表中是否存在指定用户名的记录
            cursor = db.query("users",new String[]{"userID"},"userID = ?",new String[]{username},null, null, null);
            exists = cursor.moveToFirst();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 确保关闭游标和数据库

                cursor.close();
                db.close();

        }
        return exists;
    }

    public boolean updateScore(String userID, int newScore) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("score", newScore);
        // 条件：userID 匹配
        int rowsAffected = db.update(
                "users",
                values,
                "userID = ?",
                new String[]{ userID }
        );
        db.close();
        return rowsAffected > 0; // 返回是否成功（影响行数 > 0）
    }
    // 返回所有用户的用户名和分数（二维ArrayList格式）
    public List<List<String>> getAllUserScores() {
        List<List<String>> resultList = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = null;

        try {
            // 查询所有用户的userID和score字段
            cursor = db.query("users", new String[]{"userID", "score"}, null, null, null, null, null);

            // 遍历结果集
            while (cursor.moveToNext()) {
                List<String> row = new ArrayList<>();
                row.add(cursor.getString(0)); // 用户名
                row.add(cursor.getString(1));     // 分数
                resultList.add(row);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 确保关闭资源
            if (cursor != null) cursor.close();
            db.close();
        }

        return resultList;
    }
}