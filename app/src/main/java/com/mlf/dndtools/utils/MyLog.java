package com.mlf.dndtools.utils;

public class MyLog
{
    private static final String TAG_MY_LOG = "LogApp";

    public static void i(String msg)
    {
        if(Constant.DEBUG)
        {
            android.util.Log.i(TAG_MY_LOG, msg);
        }
    }
    public static void e(String msg)
    {
        if(Constant.DEBUG)
        {
            android.util.Log.e(TAG_MY_LOG, msg);

        }
    }
    public static void e(String msg, Exception e)
    {
        if(Constant.DEBUG)
        {
            android.util.Log.e(TAG_MY_LOG, msg, e);
        }
    }
    public static void e(Exception e)
    {
        if(Constant.DEBUG)
        {
            android.util.Log.e(TAG_MY_LOG, "", e);
        }
    }
    public static void v(String msg)
    {
        if(Constant.DEBUG)
        {
            android.util.Log.v(TAG_MY_LOG, msg);
        }
    }
    public static void d(String msg)
    {
        if(Constant.DEBUG)
        {
            android.util.Log.d(TAG_MY_LOG, msg);
        }
    }
}
