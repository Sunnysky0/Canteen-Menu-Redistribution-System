package cn.sunnysky.api.default_impl;

import cn.sunnysky.IntegratedManager;
import cn.sunnysky.api.IFileManager;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class DefaultFileManager implements IFileManager {
    private static final String PREFIX = "data/";
    private static final String INDEX = "INDEX";
    private static final String SUFFIX = ".cfg";

    public DefaultFileManager(){
        initialize();
    }

    @Override
    public void initialize() {
        File prefix = new File("data");
        if(!prefix.exists())
            prefix.mkdir();

        File index = new File(PREFIX + INDEX + SUFFIX);

        try {
            if(!index.exists())
                index.createNewFile();

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream(index)));
            String strTmp = null;
            while ((strTmp = reader.readLine()) != null){
                if(strTmp.contentEquals("")) continue;
                fileIndex.add(strTmp);
            }
        } catch ( IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void createNewFileInstance(String fileName) {
        if(fileIndex.contains(fileName)){
            IntegratedManager.logger.log("File already exists!");
            return;
        }
        String fullName = PREFIX + fileName + SUFFIX;

        File newFile = new File(fullName);
        try {
            newFile.createNewFile();
            IntegratedManager.logger.log(newFile.getAbsolutePath());

            String path = PREFIX + INDEX + SUFFIX;
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(
                            new FileOutputStream(path,true)));
            writer.newLine();
            writer.write(fileName);
            writer.close();

            fileIndex.add(fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    @Override
    public <K,V> void  writeSerializedData(Map<K,V> data, String targetFile) {
        if(!fileIndex.contains(targetFile)) createNewFileInstance(targetFile);
        String path = PREFIX + targetFile + SUFFIX;
        try {
            BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(
                        new FileOutputStream(path,true)));
            try{

                for(K key : data.keySet()){
                    writer.newLine();
                    String dataLine =
                            key.toString() + ":" + data.get(key).toString();
                    writer.write(dataLine);
                }
            } catch ( IOException e) {
                e.printStackTrace();
            } finally{
                writer.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    @Nullable
    public Map readSerializedDataFromFile(String fileName) {
        if(!fileIndex.contains(fileName)){
            IntegratedManager.logger.log("File does not exist!");
            return null;
        }
        File targetFile = new File(PREFIX + fileName + SUFFIX);
        Map<String,String> result = new HashMap<>();
        try {
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream(targetFile)));
            String strTmp = null;
            while ((strTmp = reader.readLine()) != null){
                if(strTmp.contentEquals("")) continue;
                String[] temporary = strTmp.split(":");
                result.put(temporary[0],temporary[1]);
            }
        } catch ( IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static void main(String[] args){
        Map<String,String> testMap = new HashMap<>();
        testMap.put("Vivi", "Student");
        testMap.put("Alice", "Friend");
        IFileManager fileManager = new DefaultFileManager();
        fileManager.createNewFileInstance("ALS");
        fileManager.writeSerializedData(testMap,"ALS");
        IntegratedManager.logger.log(
                Objects.requireNonNull(
                        fileManager.readSerializedDataFromFile("ALS"))
                        .toString());
    }
}
