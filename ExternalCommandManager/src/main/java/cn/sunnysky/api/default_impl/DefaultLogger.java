package cn.sunnysky.api.default_impl;

import cn.sunnysky.api.ILogger;

public class DefaultLogger implements ILogger {
    @Override
    public void log(String s) {
        String time = getFormattedTime();
        System.out.println("[" + time + "]" + "[DefaultLogger]: " + s);
    }
}
