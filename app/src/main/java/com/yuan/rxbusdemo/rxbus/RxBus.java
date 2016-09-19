package com.yuan.rxbusdemo.rxbus;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import rx.Observable;
import rx.Subscriber;
import rx.subjects.PublishSubject;
import rx.subjects.SerializedSubject;
import rx.subjects.Subject;

/**
 * Note: 使用RxJava实现的Rxbus对象
 * Created by Yuan on 2016/9/19,13:47,14:51.
 */
public class RxBus
{
    private static volatile RxBus mInstance;
    // 收发事件的对象
    private final Subject<Object, Object> mRxBus = new SerializedSubject<>(PublishSubject.create());
    //粘性事件保存数组,所谓的粘性事件就是发送之后会保存一下刚刚发送的事件.一般的情况下,当订阅者订阅了之后,它只能接受到后续的事件
    //而通过粘性事件,同样可以接受到刚刚在其订阅之前发送的事件
    private final Map<Class<?>, Object> mStickyEventMap = new ConcurrentHashMap<>();

    /**
     * Double CheckLocked 生成单例对象
     *
     * @return 单例对象
     */
    public static RxBus getInstance()
    {
        if (mInstance == null)
        {
            synchronized (RxBus.class)
            {
                if (mInstance == null)
                {
                    mInstance = new RxBus();
                }
            }
        }
        return mInstance;
    }

    /**
     * Note : 发送事件
     *
     * @param event 被发送的事件对象
     */
    public void post(Object event)
    {
        mRxBus.onNext(event);
    }

    /**
     * Note: 将mRxbus转换成需要的类型,这个方法用来获取RxBus去接收事件
     *
     * @param eventType 事件类型
     * @param <T>       事件的类
     * @return 只发射特定类型的mRxBus
     */
    public <T> rx.Observable<T> toObservable(Class<T> eventType)
    {
        return mRxBus.ofType(eventType);
    }

    /**
     * Note:是否有监听者
     *
     * @return 当前的mRxBus是否有监听者
     */
    public boolean hasObservers()
    {
        return mRxBus.hasObservers();
    }

    /**
     * 重置
     */
    public void reset()
    {
        mInstance = null;
    }

    /**
     * Note: 发送粘性事件
     *
     * @param event 事件对象
     */
    public void postSticky(Object event)
    {
        synchronized (mStickyEventMap)
        {
            mStickyEventMap.put(event.getClass(), event);
        }
        post(event);
    }

    /**
     * Note: 将mRxbus转换成粘性的Observable
     *
     * @param eventType 粘性广播类型
     * @param <T>       粘性广播的类
     * @return 粘性广播对象
     */
    public <T> Observable<T> toStickyObservable(final Class<T> eventType)
    {
        //如果有粘性事件存在的话,就会将携带这个事件的Observable和获取到对象整合成一个对象,这样的话,这个Observable在创建之始就会携带一个对象
        synchronized (mStickyEventMap)
        {
            Observable<T> observable = mRxBus.ofType(eventType);
            final Object event = mStickyEventMap.get(eventType);
            if (event != null)
            {
                return observable.mergeWith(Observable.create(new Observable.OnSubscribe<T>()
                {
                    @Override
                    public void call(Subscriber<? super T> subscriber)
                    {
                        subscriber.onNext(eventType.cast(event));
                    }
                }));
            }
            else
            {
                return observable;
            }
        }
    }

    /**
     * Note : 获取粘性事件对象
     *
     * @param eventType 粘性事件的类型
     * @param <T>       类
     * @return 指定类型的粘性事件
     */
    public <T> T getStickyEvent(Class<T> eventType)
    {
        synchronized (mStickyEventMap)
        {
            return eventType.cast(mStickyEventMap.get(eventType));
        }
    }

    /**
     * 移除指定的粘性事件对象
     *
     * @param eventType 粘性事件的类型
     * @param <T>       粘性十年的类
     * @return 被移除的粘性事件对象
     */
    public <T> T removeStickyEvent(Class<T> eventType)
    {
        synchronized (mStickyEventMap)
        {
            return eventType.cast(mStickyEventMap.remove(eventType));
        }
    }

    /**
     * 移除所有的粘性事件
     */
    public void removeAllStickyEvent()
    {
        synchronized (mStickyEventMap)
        {
            mStickyEventMap.clear();
        }
    }

}
