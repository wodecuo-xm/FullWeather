package com.tomson.fullweather.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Tomson on 2016/8/2.
 */
public class FullWeatherOpenHelper extends SQLiteOpenHelper {

    /**
     * Province表 建表语句
     */
    public static final String CREATE_PROVINCE = "create table Province (" + "id integer primary key autoincrement, "
            + "province_name text, " + "province_code text )";

    /**
     * City表 建表语句
     */
    public static final String CREATE_CITY = "create table City (" + "id integer primary key autoincrement, " +
            "city_name text," + "city_code text," + "province_id integer)";

    /**
     * County表 建表语句
     */
    public static final String CREATE_COUNTY = "create table County (" + "id integer primary key autoincrement," +
            "county_name text," + "county_code text," + "city_id integer)";

    public FullWeatherOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_PROVINCE);
        sqLiteDatabase.execSQL(CREATE_CITY);
        sqLiteDatabase.execSQL(CREATE_COUNTY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {

    }
}
