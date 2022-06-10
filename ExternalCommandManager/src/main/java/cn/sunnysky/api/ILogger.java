package cn.sunnysky.api;

import java.text.SimpleDateFormat;
import java.util.Date;

public interface ILogger {

    /**
     * @author Sunnysky
     * @implNote The method should print a message line in a console or in a log file.
     * @param s The message logged by the method.
     */
    void log(String s);

    default String getFormattedTime(){
        Date date = new Date();

        long times = date.getTime();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        String dateString = formatter.format(date);

        return dateString;
    }
}
