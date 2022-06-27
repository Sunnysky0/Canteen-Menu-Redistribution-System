package cn.sunnysky;

import android.app.Application;
import client.ClientBase;

public class StudentClientApplication extends Application {
    public static ClientBase client;

    @Override
    public void onCreate() {
        super.onCreate();
        int ct = 0;
        while(client == null && ct < 200){
            try{
                client = new ClientBase();
            }catch (Exception e){
                ct++;
            }
        }
    }
}
