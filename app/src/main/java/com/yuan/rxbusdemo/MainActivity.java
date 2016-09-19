package com.yuan.rxbusdemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.yuan.rxbusdemo.fragment.ObservableFragment;
import com.yuan.rxbusdemo.fragment.SubscriberFragment;
import com.yuan.rxbusdemo.rxbus.RxBus;

public class MainActivity extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null)
        {
            SubscriberFragment subscriberFragment = SubscriberFragment.getInstance();
            ObservableFragment observableFragment = ObservableFragment.getInstance();
            //将两个碎片添加进去
            getFragmentManager().beginTransaction()
                .add(R.id.fl_subscriber, subscriberFragment)
                .add(R.id.fl_observable, observableFragment)
                .commit();
        }
    }


    @Override
    protected void onDestroy()
    {
        super.onDestroy();

        // 移除所有Sticky事件
        RxBus.getInstance().removeAllStickyEvent();
    }
}
