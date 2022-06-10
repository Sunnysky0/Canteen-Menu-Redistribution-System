package cn.sunnysky.api.default_impl;

import cn.sunnysky.api.ILogger;
import cn.sunnysky.api.LogType;

public class DefaultLogger implements ILogger {

    @Override
    public void log(String s, LogType type) {
        System.out.println(getFormattedLog(s,type));
    }

    @Override
    public String getFormattedLog(String s, LogType type) {
        String time = getFormattedTime();
        return "[" + time + "]" + "[DefaultLogger][" + type.toString() + "]" + s;
    }
}
