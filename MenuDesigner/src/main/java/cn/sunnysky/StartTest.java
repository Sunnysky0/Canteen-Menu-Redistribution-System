package cn.sunnysky;

import cn.sunnysky.api.IFileManager;
import cn.sunnysky.api.default_impl.DefaultFileManager;
import cn.sunnysky.controller.DTreeBuilder;
import cn.sunnysky.controller.DTreeController;
import cn.sunnysky.model.DTree;
import cn.sunnysky.model.DataModelManager;
import cn.sunnysky.model.FoodType;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

public class StartTest {
    public static void main(String[] args) {
        new StartTest().test2();
    }

    public final void test1(){
        DTree<FoodType> root = new DTree<>( new FoodType("Base"));
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
}
