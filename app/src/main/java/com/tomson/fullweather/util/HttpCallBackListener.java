package com.tomson.fullweather.util;

/**
 * Created by Tomson on 2016/8/2.
 */
public interface HttpCallBackListener {
    void onFinish(String response);

    void onError(Exception e);
}
