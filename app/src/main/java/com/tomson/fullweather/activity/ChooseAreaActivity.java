package com.tomson.fullweather.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.tomson.fullweather.R;
import com.tomson.fullweather.db.FullWeatherDB;
import com.tomson.fullweather.model.City;
import com.tomson.fullweather.model.County;
import com.tomson.fullweather.model.Province;
import com.tomson.fullweather.util.HttpCallBackListener;
import com.tomson.fullweather.util.HttpUtil;
import com.tomson.fullweather.util.Utility;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tomson on 2016/8/2.
 */
public class ChooseAreaActivity extends AppCompatActivity {

    public static final int LEVEL_PROVINCE = 0;
    public static final int LEVEL_CITY = 1;
    public static final int LEVEL_COUNTY = 2;

    private ProgressDialog mProgressDialog;
    private TextView mTxtTilte;
    private ListView mLvList;
    private ArrayAdapter<String> mAdapter;
    private FullWeatherDB fullWeatherDB;
    private List<String> dataList = new ArrayList<String>();

    /**
     * 省列表
     */
    private List<Province> provinceList;
    /**
     * 市列表
     */
    private List<City> cityList;
    /**
     * 县列表
     */
    private List<County> countyList;
    /**
     * 选中的省份
     */
    private Province selectedProvince;
    /**
     * 选中的城市
     */
    private City selectedCity;
    /**
     * 当前选中的级别
     */
    private int currentLevel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choose_area);
        findViewById();

        initView();
    }

    private void findViewById() {
        mLvList = (ListView) findViewById(R.id.lv_list);
        mTxtTilte = (TextView) findViewById(R.id.txt_titile);
    }

    private void initView() {
        mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, dataList);
        mLvList.setAdapter(mAdapter);
        fullWeatherDB = FullWeatherDB.getInstance(this);
        mLvList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (currentLevel == LEVEL_PROVINCE) {
                    selectedProvince = provinceList.get(i);
                    queryCities();
                } else if (currentLevel == LEVEL_CITY) {
                    selectedCity = cityList.get(i);
                    queryCounties();
                }
            }
        });
        queryProvinces();
    }

    /**
     * 查询全国所有省份，优先从数据库查询，如果没有查询到再去服务器上查询
     */
    private void queryProvinces() {
        provinceList = fullWeatherDB.loadProvinces();
        if (provinceList.size() > 0) {
            dataList.clear();
            for (Province province : provinceList) {
                dataList.add(province.getProvinceName());
            }
            mAdapter.notifyDataSetChanged();
            mLvList.setSelection(0);
            mTxtTilte.setText("中国");
            currentLevel = LEVEL_PROVINCE;
        } else {
            queryFromServer(null, "province");
        }
    }

    /**
     * 查询选中省内所有城市，优先从数据库查询，如果没有查询到再去服务器上查询
     */
    private void queryCities() {
        cityList = fullWeatherDB.loadCity(selectedProvince.getId());
        if (cityList.size() > 0) {
            dataList.clear();
            for (City city : cityList) {
                dataList.add(city.getCityName());
            }
            mAdapter.notifyDataSetChanged();
            mLvList.setSelection(0);
            mTxtTilte.setText(selectedProvince.getProvinceName());
            currentLevel = LEVEL_CITY;
        } else {
            queryFromServer(selectedProvince.getProvinceCode(), "city");
        }
    }

    /**
     * 查询选中城市内所有县城，优先从数据库查询，如果没有查询到再去服务器上查询
     */
    private void queryCounties() {
        countyList = fullWeatherDB.loadCounty(selectedCity.getId());
        if (countyList.size() > 0) {
            dataList.clear();
            for (County county : countyList) {
                dataList.add(county.getCountyName());
            }
            mAdapter.notifyDataSetChanged();
            mLvList.setSelection(0);
            mTxtTilte.setText(selectedCity.getCityName());
            currentLevel = LEVEL_COUNTY;
        } else {
            queryFromServer(selectedCity.getCityCode(), "county");
        }
    }

    /**
     * 根据传入的代号和类型从服务器上查询省市县的数据
     *
     * @param codeId
     * @param type
     */
    private void queryFromServer(final String codeId, final String type) {
        String address;
        if (!TextUtils.isEmpty(codeId)) {
            address = "htpp://www.weather.com.cn/data/list3/city" + codeId + ".xml";
        } else {
            address = "http://www.weather.com.cn/data/list3/city.xml";
        }
        showProgressDialog();
        HttpUtil.sendHttpRequest(address, new HttpCallBackListener() {
            @Override
            public void onFinish(String response) {
                boolean result = false;
                if ("province".equals(type)) {
                    result = Utility.handleProvincesResponse(fullWeatherDB, response);
                } else if ("city".equals(type)) {
                    result = Utility.handleCitysResponse(fullWeatherDB, response, selectedProvince.getId());
                } else if ("county".equals(type)) {
                    result = Utility.handleCountyResponse(fullWeatherDB, response, selectedCity.getId());
                }
                if (result) {
                    //通过runOnUiThread()方法回到主线程处理UI
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            if ("province".equals(type)) {
                                queryProvinces();
                            } else if ("city".equals(type)) {
                                queryCities();
                            } else if ("county".equals(type)) {
                                queryCounties();
                            }
                        }
                    });
                }
            }

            @Override
            public void onError(Exception e) {
                //通过runOnUiThread()方法回到主线程处理UI
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(ChooseAreaActivity.this, "加载失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    /**
     * 显示进度对话框
     */
    private void closeProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage("正在加载...");
            mProgressDialog.setCancelable(false);
        }
        mProgressDialog.show();
    }

    /**
     * 关闭进度对话框
     */
    private void showProgressDialog() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
    }

    /**
     * 捕获back键，根据当前的级别来判断，此时应该返回到那个列表，还是直接退出
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (currentLevel == LEVEL_COUNTY) {
            queryCities();
        } else if (currentLevel == LEVEL_CITY) {
            queryCities();
        } else {
            finish();
        }
    }

}

