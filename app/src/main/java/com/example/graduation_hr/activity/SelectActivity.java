package com.example.graduation_hr.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.graduation_hr.R;

import zxing.activity.CaptureActivity;

/**
 * Created by 高浩然 on 2018/4/30.
 */
public class SelectActivity extends Activity {

    private Button zh_btn,sm_btn;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select);
        initView();
        zh_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SelectActivity.this,LoginActivity.class);
                startActivity(intent);
            }
        });

        sm_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SelectActivity.this,CaptureActivity.class);
                startActivity(intent);
            }
        });
    }

    private void initView() {
        zh_btn = (Button) findViewById(R.id.zh_btn);
        sm_btn = (Button) findViewById(R.id.sm_btn);
    }
}
