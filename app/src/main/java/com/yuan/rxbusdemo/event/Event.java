package com.yuan.rxbusdemo.event;

/**
 * Note: 普通事件类
 * Created by Yuan on 2016/9/19,14:57.
 */
public class Event
{
    private int event;

    public Event(int event)
    {
        this.event = event;
    }

    public int getEvent()
    {
        return event;
    }

    public void setEvent(int event)
    {
        this.event = event;
    }

    @Override
    public String toString()
    {
        return "Event{" +
                "event=" + event +
                '}';
    }
}
