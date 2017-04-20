package com.zncm.rwallpaper.service;

/**
 * Created by jiaomx on 2017/4/19.
 */


import com.zncm.rwallpaper.service.bing.BingData;
import com.zncm.rwallpaper.service.bing.BingImg;

import retrofit2.http.GET;
import rx.Observable;

public interface BingService {
    String BASE_URL = "https://bing.ioliu.cn/v1/";

    @GET("rand?type=json")
    Observable<BingData<BingImg>> getRandomBing();
}
