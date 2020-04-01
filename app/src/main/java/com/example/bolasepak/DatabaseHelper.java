package com.example.bolasepak;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "bolasepak.db";
    private static final int DATABASE_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("create table user(step int, subscription varchar(255));");
        db.execSQL("create table teams(id int primary key, name varchar(255), logo text);");
        db.execSQL("create table schedules(id int primary key, home_team_id int, away_team_id int, location varchar(255), match_time datetime);");
        db.execSQL("create table matches(id int primary key,schedule_id int, home_shoot int, away_shoot int, score varchar(255));");
        db.execSQL("create table goals(match_id int,scorer_name varchar(255), scorer_team_is_home bool ,minute time);");
        db.execSQL("insert into user(step,subscription) values(0,'')");
        Log.d("Database CREATE", "onCreate called");

    }
    @Override
    public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {

    }
}