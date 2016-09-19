package com.yuan.rxbusdemo.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.yuan.rxbusdemo.R;
import com.yuan.rxbusdemo.event.Event;
import com.yuan.rxbusdemo.event.StickyEvent;
import com.yuan.rxbusdemo.rxbus.RxBus;
import com.yuan.rxbusdemo.rxbus.RxBusSubscriber;
import com.yuan.rxbusdemo.rxbus.RxSubscriptions;

import rx.Subscription;
import rx.functions.Func1;

/**
 * Note:
 * Created by Yuan on 2016/9/19,15:09.
 */
public class SubscriberFragment extends Fragment
{

    private static final String TAG = "SubscriberFragment";
    private static volatile SubscriberFragment mInstance;

    private TextView mTvResult, mTvResultSticky;
    private Button mBtnSubscribeSticky;
    private CheckBox mCheckBox;

    private Subscription mRxSub, mRxSubSticky;

    public static SubscriberFragment getInstance()
    {
        if (mInstance == null)
        {
            synchronized (SubscriberFragment.class)
            {
                if (mInstance == null)
                {
                    mInstance = new SubscriberFragment();
                }
            }
        }
        return mInstance;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_subscribe, container, false);

        mTvResult = (TextView) view.findViewById(R.id.tv_result);
        mTvResultSticky = (TextView) view.findViewById(R.id.tv_resultSticky);
        mBtnSubscribeSticky = (Button) view.findViewById(R.id.btn_subscribeSticky);
        mCheckBox = (CheckBox) view.findViewById(R.id.checkbox);

        subscribeEvent();
        Toast.makeText(getActivity(), "普通RxBus正在订阅中", Toast.LENGTH_SHORT).show();
        mBtnSubscribeSticky.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // 订阅Sticky事件
                subscribeEventSticky();
            }
        });


        return view;
    }

    private void subscribeEvent()
    {
        RxSubscriptions.remove(mRxSub);
        mRxSub = RxBus.getInstance().toObservable(Event.class)
                .map(new Func1<Event, Event>()
                {
                    @Override
                    public Event call(Event event)
                    {
                        return event;
                    }
                })
                .subscribe(new RxBusSubscriber<Event>()
                {
                    @Override
                    protected void onEvent(Event event)
                    {
                        String str = mTvResult.getText().toString();
                        mTvResult.setText(TextUtils.isEmpty(str) ? String.valueOf(event.getEvent()) : str + ", " + event.getEvent());

                        // 这里模拟产生 Error,会发生空指针异常,触发onError,但是在onError中又重新订阅了事件
                        if (mCheckBox.isChecked())
                        {
                            event = null;
                            Toast.makeText(getActivity(), "发生了Error, 已重新订阅", Toast.LENGTH_SHORT).show();
                            int error = event.getEvent();
                        }
                    }

                    @Override
                    public void onError(Throwable e)
                    {
                        super.onError(e);
                        /**
                         * 这里注意: 一旦订阅过程中发生异常,走到onError,则代表此次订阅事件完成,后续将收不到onNext()事件,
                         * 即 接受不到后续的任何事件,实际环境中,我们需要在onError里 重新订阅事件!
                         */
                        subscribeEvent();
                        //因为在RxBusSubscriber的onCompleted方法里面进行了异常的处理,而onCompleted中又调用了onEvent方法
                        //因此,在onEvent方法中发生了异常不会到onError方法里面去执行,会直接在onCompleted方法的catch语句中进行,这样订阅关系就不会结束
                        Log.w(TAG, "onError: "+"执行到了这里" );
                    }
                });
        //将这个订阅关系保存起来,到destory的时候取消订阅
        RxSubscriptions.add(mRxSub);
    }

    private void subscribeEventSticky()
    {
        if (mRxSubSticky != null && !mRxSubSticky.isUnsubscribed())
        {
            mTvResultSticky.setText("");
            RxSubscriptions.remove(mRxSubSticky);

            mBtnSubscribeSticky.setText(R.string.subscribeSticky);
            Toast.makeText(getActivity(), "取消订阅Sticky", Toast.LENGTH_SHORT).show();
        }
        else
        {
            StickyEvent stickyEvent = RxBus.getInstance().getStickyEvent(StickyEvent.class);
            mRxSubSticky = RxBus.getInstance().toStickyObservable(StickyEvent.class)
                    .map(new Func1<StickyEvent, StickyEvent>()
                    {
                        @Override
                        public StickyEvent call(StickyEvent stickyEvent)
                        {
                            return stickyEvent;
                        }
                    })
                    .subscribe(new RxBusSubscriber<StickyEvent>()
                    {
                        @Override
                        protected void onEvent(StickyEvent event)
                        {
                            String str = mTvResultSticky.getText().toString();
                            mTvResultSticky.setText(TextUtils.isEmpty(str) ? String.valueOf(event.getEvent()) : str + ", " + event.getEvent());

                            // 这里模拟产生 Error
                            if (mCheckBox.isChecked())
                            {
                                event = null;
                                Toast.makeText(getActivity(), "发生了Error,已经catch", Toast.LENGTH_SHORT).show();
                                String error = event.getEvent();
                            }
                        }

                        @Override
                        public void onError(Throwable e)
                        {
                            super.onError(e);
                            /**
                             * 这里注意: Sticky事件 不能在onError时重绑事件,这可能导致因绑定时得到引起Error的Sticky数据而产生死循环
                             */
                        }
                    });
        }

        RxSubscriptions.add(mRxSubSticky);
        Toast.makeText(getActivity(), "取消订阅Sticky", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        RxSubscriptions.unsubscribe();
        RxSubscriptions.remove(mRxSub);
        RxSubscriptions.remove(mRxSubSticky);


    }
}
