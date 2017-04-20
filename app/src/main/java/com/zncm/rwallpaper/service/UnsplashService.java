package com.zncm.rwallpaper.service;

/**
 * Created by jiaomx on 2017/4/19.
 */


import com.zncm.rwallpaper.service.unsplash.UnsplashData;
import com.zncm.rwallpaper.service.unsplash.UnsplashImg;

import retrofit2.http.GET;
import rx.Observable;

/**
 * https://api.unsplash.com/photos/random?client_id=20c1aa97b359765b805e5049e87295d51ff5f3505a6270d810f6bfaf52eedd9f&w=1920&h=1080&orientation=portrait
 */
public interface UnsplashService {
    String BASE_URL = "https://api.unsplash.com/";

    @GET("photos/random?client_id=20c1aa97b359765b805e5049e87295d51ff5f3505a6270d810f6bfaf52eedd9f&w=1920&h=1080&orientation=portrait")
    Observable<UnsplashData<UnsplashImg>> getRandomImg();
}
