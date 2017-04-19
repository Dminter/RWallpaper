package com.zncm.rwallpaper;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
        /**
         *直接跳转到设置界面
         */
        startActivity(new Intent(this, SettingAc.class));
        finish();
    }
}
