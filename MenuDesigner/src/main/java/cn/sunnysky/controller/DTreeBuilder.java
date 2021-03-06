package cn.sunnysky.controller;

import cn.sunnysky.api.default_impl.DefaultFileManager;
import cn.sunnysky.model.DTree;
import cn.sunnysky.model.DataModelManager;
import cn.sunnysky.model.FoodType;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

public class DTreeBuilder {

    private void consume(String str, Map<String,String[]> map){
        String[] temp = str.split(";");

        String target = "";
        ArrayList<String> path = new ArrayList<>();

        for( String s : temp){
            if(s.startsWith("target")) target = s.split(":")[1];
            else if(s.startsWith("path")) Collections.addAll(path,s.split(":")[1].split(","));
        }

        map.put(target,path.toArray(new String[0]));

    }

    public final DTree<FoodType> buildFromFile(String fileURI){
        DefaultFileManager fileManager = new DefaultFileManager();
        DTree<FoodType> root = new DTree<>( new FoodType("Root"));
        DataModelManager<FoodType> mgr =  new DataModelManager<FoodType>(root);

        try {
            Map<String,String[]> data = fileManager.readSerializedDataFromFile(
                    DataModelManager.getResourceURI(fileURI),
                    this::consume);
            for (String target : data.keySet())
                mgr.addTreeNode(target,data.get(target));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        return root;
    }
}
