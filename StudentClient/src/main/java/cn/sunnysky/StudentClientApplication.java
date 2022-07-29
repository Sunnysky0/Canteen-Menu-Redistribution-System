package cn.sunnysky;

import android.app.Application;
import client.ClientBase;

import java.io.IOException;

public class StudentClientApplication extends Application {
    public static ClientBase client;

    @Override
    public void onCreate() {
        super.onCreate();
    }
}
