package com.example.graduation_hr.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;

import com.example.graduation_hr.R;

/**
 * Created by 高浩然 on 2018/3/29.
 */
public class SplashActivity extends Activity {

    private final int SPLASH_DISPLAY_LENGHT=3000;   //  延时3秒,,可以不写这段，直接在下面SPLASH_DISPLAY_LENGHT改为延时的时间就行

    //加载欢迎界面
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);      //设置无标题
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        // 为了减少代码使用匿名Handler创建一个延时的调用
        new Handler().postDelayed(new Runnable(){

            @Override
            public void run() {
                //通过Intent打开最终真正的登录界面这个Activity
                Intent mainIntent = new Intent(SplashActivity.this,SelectActivity.class);
                //启动Main界面
                SplashActivity.this.startActivity(mainIntent);
                //关闭自己这个开场屏
                SplashActivity.this.finish();
            }

        }, SPLASH_DISPLAY_LENGHT);
    }
}
