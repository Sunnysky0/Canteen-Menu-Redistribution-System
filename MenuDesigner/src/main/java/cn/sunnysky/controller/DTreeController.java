package cn.sunnysky.controller;

import cn.sunnysky.model.DTree;
import cn.sunnysky.model.FoodType;

import java.util.*;
import java.util.function.Function;

public class DTreeController {
    private DTree<FoodType> dataTree;

    public DTreeController(DTree<FoodType> dataTree) {
        this.dataTree = dataTree;
    }

    @SuppressWarnings("NewApi")
    private DTree<FoodType> maxPopularity(DTree<FoodType>... data){
        ArrayList<DTree<FoodType>> array = new ArrayList<>();
        Collections.addAll(array,data);
        return Collections.max(array, Comparator.comparingInt(o -> o.getData().popularity));
    }

    private DTree<FoodType>[] convert(DTree<FoodType> recursionData){
        return recursionData.getChildren().toArray(
            new DTree[recursionData.getChildren().size()]
    );}

    @SuppressWarnings("NewApi")
    public void addChoice(DTree<FoodType> choice){
        onNodePopularityChanged(choice,1,true);
    }

    public void addChoice(FoodType choice){
        addChoice(Objects.requireNonNull(dataTree.getSubTree(choice)));
    }

    public void addChoice(String choice){ addChoice(new FoodType(choice));}

    public DTree<FoodType> calculateResult(){
        DTree<FoodType>[] children = dataTree.getChildren().toArray(
                new DTree[dataTree.getChildren().size()]);
        return dataTree.customSearchRecursively(this::maxPopularity,children,this::convert);
    }

    public void onDispatchNode(DTree<FoodType> node){ this.onNodePopularityChanged(node,-1 * node.getData().popularity,false);}

    /**
     * To dispatch an entire tree layer, call this first
     * @param layer
     */
    public void onDispatchLayer(DTree<FoodType> layer){
        for (DTree<FoodType> ft : layer.getChildren())
            onDispatchNode(ft);
    }

    /**
     * Change the popularity of a node.
     * @param node The target node
     * @param change The value you want to increase, negative if you want to decrease
     */
    public void onNodePopularityChanged(DTree<FoodType> node, int change, boolean changeCurrent){
        final DTree<FoodType> parent = node.getParent();
        if (changeCurrent)
            node.getData().popularity += change;
        if ( parent != null)
            onNodePopularityChanged(parent,change,true);
    }

    /**
     * Calculate the results of a DTree
     * @param limit The size of the result array
     * @param indicator The indicator array, consists of 0 and 1, 0 for node dispatch while 1 for layer dispatch
     * @return The result array
     */
    public DTree<FoodType>[] calculateResults(int limit,int... indicator){
        DTree<FoodType>[] results =
                (DTree<FoodType>[]) new DTree[limit];
        for (int i = 0; i < limit; i++) {
            results[i] = calculateResult();

            if (indicator.length == limit - 1 && i < limit - 1)
                if ( indicator[i] == 0) {
                    results[i].dispatch();
                    onDispatchNode(results[i]);
                } else {
                    onDispatchLayer(results[i].getParent());
                    results[i].dispatchLayer();
                }
             else {
                results[i].dispatch();
                onDispatchNode(results[i]);
            }

        }

        return results;
    }
}
