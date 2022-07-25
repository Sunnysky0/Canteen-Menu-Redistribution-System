package cn.sunnysky;

import cn.sunnysky.controller.Comparators;
import cn.sunnysky.controller.DTreeBuilder;
import cn.sunnysky.controller.DTreeController;
import cn.sunnysky.model.DTree;
import cn.sunnysky.model.DataModelManager;
import cn.sunnysky.model.FoodType;
import cn.sunnysky.model.SortedList;

public class StartTest {
    public static void main(String[] args) {
        new StartTest().test3();
    }

    public final void test1(){
        DTree<FoodType> root = new DTree<>( new FoodType("Base"),Comparators.DTreeComparator);
        DataModelManager<FoodType> mgr =  new DataModelManager<FoodType>(root);
        DTreeController dTreeController = new DTreeController(root);

        mgr.addTreeNode("Layer R","Layer F","Layer S");
        mgr.addTreeNode("Layer C","Layer A","Layer B");
        mgr.addTreeNode("Class Vivi","Layer Alpha","Layer Beta");

        for (int i = 0; i < 5; i++) {
            mgr.addTreeNode(new FoodType("Layer R" + i),mgr.parsePath("Layer F","Layer S"));
            dTreeController.addChoice("Layer R" + i);
        }

        dTreeController.addChoice("Class Vivi");
        dTreeController.addChoice("Class Vivi");
        dTreeController.addChoice("Class Vivi");

        dTreeController.addChoice("Layer R");
        dTreeController.addChoice("Layer R");

        root.visualize(0);

        final DTree<FoodType> result = dTreeController.calculateResult();
        System.out.print("Final result: ");
        System.out.println(result.getData());
    }

    public final void test2(){
        new DTreeBuilder().buildFromFile("/assets/food_data_s1.cfg").visualize(0);
    }

    public final void test3(){
        Comparators.FoodTypeComparator comparator = new Comparators.FoodTypeComparator();

        SortedList<FoodType> list = new SortedList<>(comparator);

        list.add( new FoodType("Vivi"));
        list.add( new FoodType("Hermione"));
        list.add( new FoodType("Leo"));

        System.out.println("Size: " + list.size());

        for (FoodType f : list)
            System.out.println(f);

        System.out.println("Original: " + list);
        list.remove(new FoodType("Hermione"));
        System.out.println("Modified: " + list);
    }
}
