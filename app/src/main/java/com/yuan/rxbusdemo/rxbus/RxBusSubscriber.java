package com.yuan.rxbusdemo.rxbus;

/**
 * Note:
 * Created by Yuan on 2016/9/19,15:46.
 */

import rx.Subscriber;

/**
 * 实现发生异常后订阅不中断的类
 * @param <T>
 */
public  abstract class RxBusSubscriber<T> extends Subscriber<T>
{
    @Override
    public void onNext(T event)
    {
        //在这里进行异常处理,所以发生了异常会在catch语句中进行处理,不会到onError方法中进行处理,也就不会断开订阅关系了
        try
        {
            onEvent(event);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void onError(Throwable e)
    {
        e.printStackTrace();
    }

    @Override
    public void onCompleted()
    {

    }

    protected abstract void onEvent(T event);
}
