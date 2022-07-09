import controller.DTreeController;
import model.DTree;
import model.DataModelManager;
import model.FoodType;

public class StartTest {
    public static void main(String[] args) {
        DTree<FoodType> root = new DTree<>( new FoodType("Base"));
        DataModelManager<FoodType> mgr =  new DataModelManager<FoodType>(root);
        DTreeController dTreeController = new DTreeController(root);

        mgr.addTreeNode(new FoodType("Layer R"),mgr.parsePath("Layer F","Layer S"));
        mgr.addTreeNode(new FoodType("Layer C"),mgr.parsePath("Layer A","Layer B"));
        mgr.addTreeNode(new FoodType("Class Vivi"),mgr.parsePath("Layer Alpha","Layer Beta"));

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
}
