package cn.sunnysky.model;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Objects;

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

    public FoodType[] parsePath(String... path){
        ArrayList<FoodType> list = new ArrayList<>();
        for( String str : path)
            list.add(new FoodType(str));
        return  list.toArray(new FoodType[0]);
    }

}
