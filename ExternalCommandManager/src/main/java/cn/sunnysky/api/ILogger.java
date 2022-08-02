package cn.sunnysky.api;

import java.text.SimpleDateFormat;
import java.util.Date;

import static cn.sunnysky.api.LogType.INFORMATION;

public interface ILogger {

    /**
     * @author Sunnysky
     * @implNote The method should print a message line in a console or in a log file.
     * @param s The message logged by the method.
     * @param type The message type (INFORMATION, WARNING, ERROR)
     */
    void log(String s,LogType type);

    default void log(String s){
        log(s,INFORMATION);
    }

    /**
     * @author Sunnysky
     * @param s The original message you want to log.
     * @param type The message type
     * @throws UnsupportedOperationException When this method is not supported by the instance.
     * @return The message with a specific format, usually contains the time stamp, the log type and the message itself.
     */
    String getFormattedLog(String s,LogType type) throws UnsupportedOperationException;

    default String getFormattedTime(){
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateString = formatter.format(date);
        return dateString;
    }
}

