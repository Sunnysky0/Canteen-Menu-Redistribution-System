package model;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;

public class DataModelManager<T> {
    private DTree<T> root;

    public DataModelManager(DTree<T> root) { this.root = root;}

    @SafeVarargs
    public final void addTreeNode(T target, T... path){ addTreeNode(root,target,path); }

    @SafeVarargs
    public final void addTreeNode(DTree<T> object, T target, T... path){
        assert object != null;
        DTree<T> finalObjectTree = object;
        DTree<T> temp = null;
        if(path != null && path.length > 0){
            for(T t : path){
                if((temp = finalObjectTree.allContains(t)) != null)
                    finalObjectTree = temp;
                else{
                    finalObjectTree = finalObjectTree.createNode(t);
                }
            }
        }
        finalObjectTree.createNode(target);
    }

    public static URI getResourceURI(String name) throws URISyntaxException {
        return DataModelManager.class.getResource(name).toURI();
    }

    public FoodType[] parsePath(String... path){
        ArrayList<FoodType> list = new ArrayList<>();
        for( String str : path)
            list.add(new FoodType(str));
        return  list.toArray(new FoodType[0]);
    }

}
