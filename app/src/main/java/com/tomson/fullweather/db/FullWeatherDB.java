package com.tomson.fullweather.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.tomson.fullweather.model.City;
import com.tomson.fullweather.model.County;
import com.tomson.fullweather.model.Province;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.CRC32;

/**
 * Created by Tomson on 2016/8/2.
 */
public class FullWeatherDB {
    /**
     * 数据库名
     */
    public static final String DB_NAME = "full_weather";

    /**
     * 数据库版本
     */
    public static final int VERSION = 1;
    private volatile static FullWeatherDB fullWeatherDB;
    private static SQLiteDatabase db;

    /**
     * 将构造方法私有化
     *
     * @param context
     */
    private FullWeatherDB(Context context) {
        FullWeatherOpenHelper dbHelper = new FullWeatherOpenHelper(context, DB_NAME, null, VERSION);
        db = dbHelper.getWritableDatabase();
    }

    /**
     * 获取FullWeatherDB的实例
     *
     * @param context
     * @return FullWeatherDB
     */
    public static FullWeatherDB getInstance(Context context) {
        if (null == fullWeatherDB) {
            synchronized (FullWeatherDB.class) {
                if (null == fullWeatherDB) {
                    fullWeatherDB = new FullWeatherDB(context);
                }
            }
        }
        return fullWeatherDB;
    }

    /**
     * 将Province实例存储到数据库
     *
     * @param province
     */
    public void saveProvince(Province province) {
        if (province != null) {
            ContentValues values = new ContentValues();
            values.put("province_name", province.getProvinceName());
            values.put("province_code", province.getProvinceCode());
            db.insert("Province", null, values);
        }
    }

    /**
     * 从数据库读取全国所有省份信息
     *
     * @return List<Province>
     */
    public List<Province> loadProvinces() {
        List<Province> provinceList = new ArrayList<Province>();
        Cursor cursor = db.query("Province", null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                Province province = new Province();
                province.setId(cursor.getInt(cursor.getColumnIndex("id")));
                province.setProvinceName(cursor.getString(cursor.getColumnIndex("province_name")));
                province.setProvinceCode(cursor.getString(cursor.getColumnIndex("province_code")));
                provinceList.add(province);
            } while (cursor.moveToNext());
        }
        return provinceList;
    }

    /**
     * 将City实例存储到数据库
     *
     * @param city
     */
    public void saveCity(City city) {
        if (city != null) {
            ContentValues values = new ContentValues();
            values.put("city_name", city.getCityName());
            values.put("city_code", city.getCityCode());
            values.put("province_id", city.getProvinceId());
            db.insert("City", null, values);
        }
    }

    /**
     * 从数据库读取某省份下所有城市信息
     *
     * @return List<City>
     */
    public List<City> loadCity(int provinceId) {
        List<City> cityList = new ArrayList<City>();
        Cursor cursor = db.query("City", null, "province_id = ?", new String[]{String.valueOf(provinceId)}, null,
                null, null);
        if (cursor.moveToFirst()) {
            do {
                City city = new City();
                city.setId(cursor.getInt(cursor.getColumnIndex("id")));
                city.setCityName(cursor.getString(cursor.getColumnIndex("city_name")));
                city.setCityCode(cursor.getString(cursor.getColumnIndex("city_code")));
                city.setProvinceId(provinceId);
                cityList.add(city);
            } while (cursor.moveToNext());
        }
        return cityList;
    }

    /**
     * 将County实例存储到数据库
     *
     * @param county
     */
    public void saveCounty(County county) {
        if (county != null) {
            ContentValues values = new ContentValues();
            values.put("county_name", county.getCountyName());
            values.put("county_code", county.getCountyCode());
            values.put("city_id", county.getCityId());
            db.insert("County", null, values);
        }
    }

    /**
     * 从数据库读取某城市下所有县信息
     *
     * @return List<City>
     */
    public List<County> loadCounty(int cityId) {
        List<County> countyList = new ArrayList<County>();
        Cursor cursor = db.query("County", null, "city_id = ?", new String[]{String.valueOf(cityId)}, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                County county = new County();
                county.setId(cursor.getInt(cursor.getColumnIndex("id")));
                county.setCountyName(cursor.getString(cursor.getColumnIndex("county_name")));
                county.setCountyCode(cursor.getString(cursor.getColumnIndex("county_code")));
                county.setCityId(cityId);
                countyList.add(county);
            } while (cursor.moveToNext());
        }
        return countyList;
    }
}
