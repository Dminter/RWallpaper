package com.zncm.rwallpaper.service;

/**
 * Created by jiaomx on 2017/4/19.
 */


import com.zncm.rwallpaper.service.lovebizhi.LovebizhiData;
import com.zncm.rwallpaper.service.lovebizhi.LovebizhiImg;
import com.zncm.rwallpaper.service.lovebizhi.LovebizhiImgUrl;

import retrofit2.http.GET;
import rx.Observable;

/**
 * http://api.lovebizhi.com/android_v3.php?a=tryluck&spdy=1&device=Xiaomi%28MI+5%29&uuid=7ea474262cf0db960f630ab71e71c582&mode=1&client_id=1001&device_id=72354190&model_id=102&size_id=0&channel_id=1&screen_width=1080&screen_height=1920&bizhi_width=2160&bizhi_height=1920&version_code=85&language=zh-CN&mac=&original=0
 */
public interface LovebizhiService {
    String BASE_URL = "http://api.lovebizhi.com/";

    @GET("android_v3.php?a=tryluck&spdy=1&device=Xiaomi%28MI+5%29&uuid=7ea474262cf0db960f630ab71e71c582&mode=1&client_id=1001&device_id=72354190&model_id=102&size_id=0&channel_id=1&screen_width=1080&screen_height=1920&bizhi_width=2160&bizhi_height=1920&version_code=85&language=zh-CN&mac=&original=0")
    Observable<LovebizhiData<LovebizhiImg<LovebizhiImgUrl>>> getRandomImg();
}
