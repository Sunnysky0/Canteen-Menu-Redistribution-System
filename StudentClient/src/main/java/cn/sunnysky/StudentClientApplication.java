package cn.sunnysky;

import android.accounts.NetworkErrorException;
import android.app.Application;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import cn.sunnysky.api.IFileManager;
import cn.sunnysky.api.ILogger;
import cn.sunnysky.api.LogType;
import cn.sunnysky.api.default_impl.DefaultFileManager;
import cn.sunnysky.database.SqliteMgr;
import cn.sunnysky.network.NetworkHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class StudentClientApplication extends Application implements IFileManager {

    public static NetworkHandler internalNetworkHandler;

    public static StudentClientApplication DATABASE_INSTANCE;

    private static ExecutorService executorService;

    private static SQLiteDatabase Database;
    private static SqliteMgr sqliteMgr;

    private final String dbName = "clientData.db";

    private ILogger logger = new ILogger() {

        @Override
        public void log(String s, LogType type) {
            switch (type){
                case ERROR:
                    Log.e("[StudentClient]",s);
                    break;
                case INFORMATION:
                    Log.i("[StudentClient]",s);
                    break;
                case WARNING:
                    Log.w("[StudentClient]",s);
                    break;
            }
        }

        @Override
        public String getFormattedLog(String s, LogType type) {
            throw new UnsupportedOperationException();
        }
    };

    public static boolean isNetworkPrepared(){
        return internalNetworkHandler != null && internalNetworkHandler.getClient() != null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        IntegratedManager.setLogger(logger);

        initialize();
        IntegratedManager.setFileManager(new DefaultFileManager(getFilesDir().getPath() + "/DATA_OF_CLIENT"));

        executorService = Executors.newCachedThreadPool();

        try {
            initializeNetwork();
        } catch (NetworkErrorException e) {
            e.printStackTrace();
        }

        DATABASE_INSTANCE = this;
    }

    public static void initializeNetwork() throws NetworkErrorException {
        internalNetworkHandler = new NetworkHandler();
    }

    public static void join(@NotNull Runnable r){ executorService.execute(r);}


    @Override
    public void onTerminate() {
        super.onTerminate();
        IntegratedManager.logger.log("App terminating",LogType.WARNING);
        if (internalNetworkHandler != null)
            internalNetworkHandler.disconnect();
    }



    @Override
    public void initialize() {
        final String finalName = dbName;

        sqliteMgr = new SqliteMgr(this, finalName, null, 1);
        Database = this.openOrCreateDatabase(finalName, Context.MODE_PRIVATE, null);

        if (!sqliteMgr.tabIsExist("INDEX_T")) {
            Database.execSQL("create table INDEX_T(id integer primary key autoincrement,name TEXT)");
        } else IntegratedManager.logger.log("Table INDEX already exists");
    }

    @Override
    public void createNewFileInstance(String fileName) {
        if (sqliteMgr.tabIsExist(fileName)){
            IntegratedManager.logger.log("Table already exists, removing the old one");
            Database.execSQL("DROP TABLE " + fileName);
        }

        Database.execSQL("create table " + fileName + "(_key TEXT primary key, value TEXT)");
        IntegratedManager.logger.log("Table created");
    }

    @Override
    public <K, V> void writeSerializedData(Map<K, V> data, String targetFile) {
        if (!sqliteMgr.tabIsExist(targetFile)) createNewFileInstance(targetFile);

        if (Database.isOpen())
            for (K key : data.keySet())
                Database.execSQL("INSERT OR REPLACE into " + targetFile + " (_key,value) "
                        + "values(" + "\"" +key.toString() + "\"" + "," +
                         "\"" + data.get(key).toString() + "\"" + ")");
    }

    @Override
    public @Nullable Map<String, String> readSerializedDataFromFile(String fileName) {
        Map<String, String> map = new HashMap<>();

        if (sqliteMgr.tabIsExist(fileName)){
            Cursor c = Database.rawQuery("select * from " + fileName, null);
            if (c != null) {
                while (c.moveToNext())
                    map.put(c.getString(0),c.getString(1));
                c.close();
                return map;
            }
        } else IntegratedManager.logger.log("Table does not exist");

        return null;
    }
}
