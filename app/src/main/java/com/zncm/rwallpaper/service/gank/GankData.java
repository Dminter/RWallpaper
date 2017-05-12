package com.zncm.rwallpaper.service.gank;

import java.util.List;

/**
 * Created by jiaomx on 2017/4/19.
 */

public class GankData<T> {

    private List<T> results;

    public List<T> getResults() {
        return results;
    }

    public void setResults(List<T> results) {
        this.results = results;
    }
}
