package cn.sunnysky.api.default_impl;

import cn.sunnysky.IntegratedManager;
import cn.sunnysky.api.IFileManager;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class DefaultFileManager implements IFileManager {
    private final String PREFIX;
    private final String DATA_FOLDER;
    private static final String INDEX = "INDEX";
    private static final String SUFFIX = ".cfg";

    public DefaultFileManager(String dataFolder){
        DATA_FOLDER = dataFolder;
        this.PREFIX = DATA_FOLDER + "/";
        initialize();
    }

    @SuppressWarnings("Read-Only")
    public DefaultFileManager() {
        this.PREFIX = null;
        this.DATA_FOLDER = null;
    }

    @Override
    public void initialize() {
        File prefix = new File(DATA_FOLDER);
        if(!prefix.exists())
            prefix.mkdir();

        File index = new File(PREFIX + INDEX + SUFFIX);

        try {
            if(!index.exists()) {
                try{
                    index.createNewFile();
                }
                catch (Exception e){
                    e.printStackTrace();

                }
            }

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

        String fullName = PREFIX + fileName + SUFFIX;

        File newFile = new File(fullName);
        try {

            if (newFile.exists())
                newFile.delete();

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
        writeSerializedData(data, new File(path).toURI());
    }



    public <K,V> void  writeSerializedData(Map<K,V> data, URI fileLocation) {
        File targetFile = new File(fileLocation);

        if(!targetFile.exists()){
            IntegratedManager.logger.log("File does not exist!");
            return;
        }
        try {
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(
                            new FileOutputStream(targetFile.getPath(),true)));
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
    public @Nullable Map<String, String> readSerializedDataFromFile(String fileName) {
        try {
            return
                    this.readSerializedDataFromFile(
                            (new File(PREFIX + fileName + SUFFIX)).toURI(), null);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Read serialized data and construct a map according to the data from a specific file.
     * @param fileLocation The URI for the target file.
     * @param processor A BiConsumer which reads a data line and extracts specific data and put them in the map.
     * @param <K> The value type for the key of the map.
     * @param <V> The value type for the value of the map.
     * @return A fully-constructed map containing the data.
     * @throws URISyntaxException
     */
    @SuppressWarnings("NewApi")
    @Nullable
    public <K, V> Map<K, V> readSerializedDataFromFile(URI fileLocation, @Nullable BiConsumer<String,Map<K,V>> processor) throws URISyntaxException {

        File targetFile = new File(fileLocation);

        if(!targetFile.exists()){
            IntegratedManager.logger.log("File does not exist!");
            return null;
        }
        Map<K, V> result = new HashMap<>();
        try {
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream(targetFile)));
            String strTmp = new String();
            while ((strTmp = reader.readLine()) != null){
                strTmp = new String(strTmp.getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8);
                if(strTmp.contentEquals("")) continue;
                if(processor != null)
                    processor.accept(strTmp,result);
                else{
                    String[] temporary = strTmp.split(":");
                    result.put((K) temporary[0], (V) temporary[1]);
                }
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
        IFileManager fileManager = new DefaultFileManager("data");
        fileManager.createNewFileInstance("ALS");
        fileManager.writeSerializedData(testMap,"ALS");
        IntegratedManager.logger.log(
                Objects.requireNonNull(
                        fileManager.readSerializedDataFromFile("ALS"))
                        .toString());
    }
}
