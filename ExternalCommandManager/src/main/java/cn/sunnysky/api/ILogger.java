package cn.sunnysky.api;

import java.text.SimpleDateFormat;
import java.util.Date;

public interface ILogger {

    /**
     * @author Sunnysky
     * @implNote The method should print a message line in a console or in a log file.
     * @param s The message logged by the method.
     * @param type The message type (INFORMATION, WARNING, ERROR)
     */
    void log(String s,LogType type);

    default void log(String s){
        log(s,LogType.INFORMATION);
    }

    /**
     * @author Sunnysky
     * @param s The original message you want to log.
     * @param type The message type
     * @return The message with a specific format, usually contains the time stamp, the log type and the message itself.
     */
    String getFormattedLog(String s,LogType type);

    default String getFormattedTime(){
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateString = formatter.format(date);
        return dateString;
    }
}

