package com.zncm.rwallpaper.service;

/**
 * Created by jiaomx on 2017/4/19.
 */


import com.zncm.rwallpaper.service.backdrops.BackdropsData;
import com.zncm.rwallpaper.service.backdrops.BackdropsImg;

import retrofit2.http.GET;
import rx.Observable;

/**
 * http://www.backdrops.io/walls/api_wip.php?task=social_wallpaper&sort=1
 */
public interface BackdropsService {
    String BASE_URL = "http://www.backdrops.io/";
    //http://www.backdrops.io/walls/upload/1476137379_pixel.jpg --thumb_
    String WALLS_URL = "http://www.backdrops.io/walls/upload/";

    @GET("walls/api_wip.php?task=social_wallpaper&sort=1")
    Observable<BackdropsData<BackdropsImg>> getRandomImg();
}
