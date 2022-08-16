package cn.sunnysky.model;

import cn.sunnysky.IntegratedManager;
import cn.sunnysky.api.default_impl.DefaultFileManager;
import cn.sunnysky.security.SecurityManager;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

public class DataModelManager<T> {
    private DTree<T> root;
    private Comparator<DTree<T>> comparator;

    public DataModelManager(DTree<T> root, Comparator<DTree<T>> comparator) {
        this.root = root;
        this.comparator = comparator;
    }

    //public DataModelManager(DTree<T> root) { this.root = root;}

    @SuppressWarnings("Only for FoodType values")
    public final void addTreeNode(String target,String... path){
        addTreeNode(root,(T) new FoodType(target), (T[]) parsePath(path));
    }

    @SafeVarargs
    public final void addTreeNode(T target, T... path){ addTreeNode(root,target,path); }


    public final void addTreeNode(DTree<T> object, T target, T[] path){
        assert object != null;
        DTree<T> finalObjectTree = object;
        DTree<T> temp = null;
        if(path != null && path.length > 0){
            for(T t : path){
                if((temp = finalObjectTree.getIfContains(t,comparator)) != null)
                    finalObjectTree = temp;
                else{
                    finalObjectTree = finalObjectTree.createNode(t);
                }
            }
        }
        finalObjectTree.createNode(target);
    }

    public static URI getResourceURI(String name) throws URISyntaxException {
        return Objects.requireNonNull(DataModelManager.class.getResource(name)).toURI();
    }

    public static File copyResourceDir(String targetDir) throws URISyntaxException, IOException {
        final URI path = DataModelManager.getResourceURI("/assets/idc.pom");

        final File file = new File(path);
        if (file.exists()){
            file.delete();
            file.createNewFile();
        }

        File fileDir = file.getParentFile();
        final HashMap<String,String> map = new HashMap<>();
        for (File f : Objects.requireNonNull(fileDir.listFiles()))
            if (! f.getName().contentEquals("idc.pom"))
                map.put(f.getName(),
                                SecurityManager.md5HashCode(
                                        new FileInputStream(
                                                f)));

        new DefaultFileManager().writeSerializedData(map,path);

        for (File f : Objects.requireNonNull(fileDir.listFiles()))
            copyResource(f.getName(),targetDir);

        return fileDir;

    }

    public static File copyResource(String name, String targetDir) throws URISyntaxException, IOException {
        String finalURI;

        if (!name.startsWith("/assets/"))
            finalURI = "/assets/" + name;
        else
            finalURI = name;

        final URI path = DataModelManager.getResourceURI(finalURI);

        File file = new File(path);

        File output = new File(targetDir,file.getName());

        output.createNewFile();

        FileInputStream inputStream = new FileInputStream(file);
        FileOutputStream outputStream = new FileOutputStream(output,false);

        byte[] bytes = new byte[inputStream.available()];

        inputStream.read(bytes);
        outputStream.write(bytes);

        outputStream.flush();

        outputStream.close();
        inputStream.close();

        return output;
    }

    public FoodType[] parsePath(String... path){
        ArrayList<FoodType> list = new ArrayList<>();
        for( String str : path)
            list.add(new FoodType(str));
        return  list.toArray(new FoodType[0]);
    }

}
