package controller;

import model.DTree;
import model.FoodType;

import java.util.*;
import java.util.function.Function;

public class DTreeController {
    private DTree<FoodType> dataTree;

    public DTreeController(DTree<FoodType> dataTree) {
        this.dataTree = dataTree;
    }

    Function<FoodType,Boolean> addChoiceOption
            = foodType -> {
                foodType.popularity += 1;
                return true;
            };

    private DTree<FoodType> maxPopularity(DTree<FoodType>... data){
        ArrayList<DTree<FoodType>> array = new ArrayList<>();
        Collections.addAll(array,data);
        return Collections.max(array, Comparator.comparingInt(o -> o.getData().popularity));
    }

    public void addChoice(DTree<FoodType> choice){
        DTree<FoodType> parent = choice.getParent();
        choice.modifyData(addChoiceOption);
        if(parent != null) addChoice(parent);
    }

    public void addChoice(FoodType choice){
        addChoice(Objects.requireNonNull(dataTree.getSubTree(choice)));
    }

    public void addChoice(String choice){ addChoice(new FoodType(choice));}

    public DTree<FoodType> calculateResult(){
        DTree<FoodType>[] children = dataTree.getChildren().toArray(new DTree[0]);
        return dataTree.customSearchRecursively(this::maxPopularity,children);
    }
}
