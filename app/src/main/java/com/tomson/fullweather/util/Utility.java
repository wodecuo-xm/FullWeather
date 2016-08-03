package com.tomson.fullweather.util;

import android.text.TextUtils;

import com.tomson.fullweather.db.FullWeatherDB;
import com.tomson.fullweather.db.FullWeatherOpenHelper;
import com.tomson.fullweather.model.City;
import com.tomson.fullweather.model.County;
import com.tomson.fullweather.model.Province;

/**
 * Created by Tomson on 2016/8/2.
 */
public class Utility {

    /**
     * 解析和处理服务器返回的省级数据
     */
    public synchronized static boolean handleProvincesResponse(FullWeatherDB fullWeatherDB, String response) {

        if (!TextUtils.isEmpty(response)) {
            String[] allProvinces = response.split(",");
            if (allProvinces != null && allProvinces.length > 0) {
                for (String p : allProvinces) {
                    String[] array = p.split("\\|");
                    Province province = new Province();
                    province.setProvinceCode(array[0]);
                    province.setProvinceName(array[1]);
                    //将解析出来的数据存储到Province表
                    fullWeatherDB.saveProvince(province);
                }
            }
            return true;
        }
        return false;
    }

    /**
     * 解析和处理服务器返回的城市数据
     */
    public synchronized static boolean handleCitysResponse(FullWeatherDB fullWeatherDB, String response, int
            provinceId) {

        if (!TextUtils.isEmpty(response)) {
            String[] allCities = response.split(",");
            if (allCities != null && allCities.length > 0) {
                for (String c : allCities) {
                    String[] array = c.split("\\|");
                    City city = new City();
                    city.setCityCode(array[0]);
                    city.setCityName(array[1]);
                    city.setProvinceId(provinceId);
                    //将解析出来的数据存储到City表
                    fullWeatherDB.saveCity(city);
                }
            }
            return true;
        }
        return false;
    }

    /**
     * 解析和处理服务器返回的县城数据
     */
    public synchronized static boolean handleCountyResponse(FullWeatherDB fullWeatherDB, String response, int cityId) {

        if (!TextUtils.isEmpty(response)) {
            String[] allCounties = response.split(",");
            if (allCounties != null && allCounties.length > 0) {
                for (String c : allCounties) {
                    String[] array = c.split("\\|");
                    County county = new County();
                    county.setCountyCode(array[0]);
                    county.setCountyName(array[1]);
                    county.setCityId(cityId);
                    //将解析出来的数据存储到County表
                    fullWeatherDB.saveCounty(county);
                }
            }
            return true;
        }
        return false;
    }
}
