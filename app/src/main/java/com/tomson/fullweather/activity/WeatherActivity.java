package com.tomson.fullweather.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tomson.fullweather.R;
import com.tomson.fullweather.util.HttpCallBackListener;
import com.tomson.fullweather.util.HttpUtil;
import com.tomson.fullweather.util.Utility;

/**
 * Created by Tomson on 2016/8/6.
 */
public class WeatherActivity extends AppCompatActivity {

    private TextView mTextCity;
    private TextView mTextPublishTime;
    private TextView mTextDate;
    private TextView mTextWeather;
    private TextView mTextTemp;
    private LinearLayout mLyWeatherInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.weather_show);

        findViewById();
        initView();
    }

    private void initView() {
        String countyCode = getIntent().getStringExtra("county_code");
        if (!TextUtils.isEmpty(countyCode)) {
            // 跳转的时候，有县的id传过来 就去查询对应的天气
            mTextPublishTime.setText("同步中...");
            mLyWeatherInfo.setVisibility(View.INVISIBLE);
            mTextCity.setVisibility(View.INVISIBLE);
            queryWeatherCode(countyCode);
        } else {
            // 没有县的id 就直接显示本地天气
            showWeather();
        }
    }

    private void findViewById() {
        mTextCity = (TextView) findViewById(R.id.txt_city);
        mTextPublishTime = (TextView) findViewById(R.id.txt_publish_time);
        mTextDate = (TextView) findViewById(R.id.txt_date);
        mTextTemp = (TextView) findViewById(R.id.txt_temp);
        mTextWeather = (TextView) findViewById(R.id.txt_weather);
        mLyWeatherInfo = (LinearLayout) findViewById(R.id.ly_weather_info);
    }

    /**
     * 查询县code对应的天气code
     *
     * @param countyCode
     */
    private void queryWeatherCode(String countyCode) {

        String address = "http://www.weather.com.cn/data/list3/city" + countyCode + ".xml";
        queryFromServer(address, "countyCode");
    }

    /**
     * 查询天气code对应的天气信息
     *
     * @param weatherCode
     */
    private void queryWeatherInfo(String weatherCode) {

        String address = "http://http://www.weather.com.cn/data/cityinfo/" + weatherCode + ".html";
        queryFromServer(address, "weatherCode");
    }


    /**
     * 根据传入的地址和类型去向服务器查询天气code或者天气信息
     *
     * @param address
     * @param type
     */
    private void queryFromServer(final String address, final String type) {

        HttpUtil.sendHttpRequest(address, new HttpCallBackListener() {
            @Override
            public void onFinish(String response) {
                if ("countyCode".equals(type)) {
                    if (!TextUtils.isEmpty(response)) {
                        // 从服务器返回的数据中解析出天气code
                        String[] array = response.split("\\|");
                        if (array != null && array.length == 2) {
                            String weatherCode = array[1];
                            queryWeatherInfo(weatherCode);
                        }
                    }
                } else if ("weatherCode".equals(type)) {
                    // 处理服务器返回的天气信息
                    Utility.handleWeatherResponse(WeatherActivity.this, response);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showWeather();
                        }
                    });
                }
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mTextPublishTime.setText("同步失败！");
                    }
                });
            }
        });
    }

    /**
     * 从SharePreferences文件中读取存储的天气信息，并显示到界面上
     */
    private void showWeather() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        mTextCity.setText(prefs.getString("city_name", ""));
        mTextPublishTime.setText(prefs.getString("今天 " + "publish_time", "") + " 发布");
        mTextTemp.setText(prefs.getString("temp1", "") + " ~ " + prefs.getString("temp2", ""));
        mTextDate.setText(prefs.getString("current_date", ""));
        mTextWeather.setText(prefs.getString("weather_desp", ""));
        mLyWeatherInfo.setVisibility(View.VISIBLE);
        mTextCity.setVisibility(View.VISIBLE);
    }
}
