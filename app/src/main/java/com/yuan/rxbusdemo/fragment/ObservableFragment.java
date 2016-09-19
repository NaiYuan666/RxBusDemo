package com.yuan.rxbusdemo.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.yuan.rxbusdemo.R;
import com.yuan.rxbusdemo.event.Event;
import com.yuan.rxbusdemo.event.StickyEvent;
import com.yuan.rxbusdemo.rxbus.RxBus;

/**
 * Note:
 * Created by Yuan on 2016/9/19,15:08.
 */
public class ObservableFragment extends Fragment
{
    private static volatile ObservableFragment mInstance;

    private int mCountNum, mCountStickyNum;
    private Button mBtnPost, mBtnPostSticky;
    private TextView mTvPost, mTvPostSticky;

    public static ObservableFragment getInstance()
    {
        if (mInstance == null)
        {
            synchronized (ObservableFragment.class)
            {
                if(mInstance == null)
                {
                    mInstance = new ObservableFragment();
                }
            }
        }
        return mInstance;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_observable, container,false);

        mBtnPost = (Button) view.findViewById(R.id.btn_post);
        mTvPost = (TextView) view.findViewById(R.id.tv_post);
        mBtnPostSticky = (Button) view.findViewById(R.id.btn_postSticky);
        mTvPostSticky = (TextView) view.findViewById(R.id.tv_postSticky);

        mBtnPost.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                //发送对象
                RxBus.getInstance().post(new Event(++mCountNum));
                //将发送的数据显示在TextView上边
                String str = mTvPost.getText().toString();
                mTvPost.setText(TextUtils.isEmpty(str) ? String.valueOf(mCountNum) : str + ", " + mCountNum);
            }
        });

        mBtnPostSticky.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                RxBus.getInstance().postSticky(new StickyEvent(String.valueOf(--mCountStickyNum)));
                String str = mTvPostSticky.getText().toString();
                mTvPostSticky.setText(TextUtils.isEmpty(str) ? String.valueOf(mCountStickyNum) : str + ", " + mCountStickyNum);
            }
        });

        return view;
    }
}
