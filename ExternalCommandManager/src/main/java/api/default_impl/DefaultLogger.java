package api.default_impl;

import api.ILogger;

public class DefaultLogger implements ILogger {
    @Override
    public void log(String s) {
        String time = getFormattedTime();
        System.out.println("[" + time + "]" + "[DefaultLogger]" + s);
    }
}
