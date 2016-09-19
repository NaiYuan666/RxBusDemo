package com.yuan.rxbusdemo.event;

/**
 * Note: 粘性事件类
 * Created by Yuan on 2016/9/19,14:59.
 */
public class StickyEvent
{
    private String event;

    public StickyEvent(String event)
    {
        this.event = event;
    }

    public String getEvent()
    {
        return event;
    }

    public void setEvent(String event)
    {
        this.event = event;
    }

    @Override
    public String toString()
    {
        return "StickyEvent{" +
                "event='" + event + '\'' +
                '}';
    }
}
