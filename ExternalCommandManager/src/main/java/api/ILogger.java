package api;

import java.text.SimpleDateFormat;
import java.util.Date;

public interface ILogger {
    void log(String s);
    default String getFormattedTime(){
        Date date = new Date();

        long times = date.getTime();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        String dateString = formatter.format(date);

        return dateString;
    }
}
