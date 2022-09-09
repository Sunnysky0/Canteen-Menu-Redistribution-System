package cn.sunnysky.api;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Map;

public interface IFileManager {
    ArrayList<String> fileIndex = new ArrayList<>();
    Runnable fileSaveThread = null;

    void initialize();
    void createNewFileInstance(String fileName);
    <K,V> void writeSerializedData(Map<K,V> data,String targetFile);
    @Nullable
    Map<String, String> readSerializedDataFromFile(String fileName);
}
