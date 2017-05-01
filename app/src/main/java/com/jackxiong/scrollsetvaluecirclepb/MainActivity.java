package com.jackxiong.scrollsetvaluecirclepb;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.jackxiong.scrollsetvaluecircleprogressbar.ScrollSetValueProgressCircle;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ScrollSetValueProgressCircle testPb = (ScrollSetValueProgressCircle) findViewById(R.id.pb_test);
        //设置默认值
        testPb.setValue(6.9f);
        testPb.setValueChangeListener(new ScrollSetValueProgressCircle.ValueChangeListener() {
            @Override
            public void currentValue(float value) {

                Log.d(TAG,"current value= "+ value);
            }
        });
    }
}
