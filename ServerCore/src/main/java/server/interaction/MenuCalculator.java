package server.interaction;

import cn.sunnysky.IntegratedManager;
import cn.sunnysky.api.LogType;
import cn.sunnysky.controller.DTreeBuilder;
import cn.sunnysky.controller.DTreeController;
import cn.sunnysky.model.DTree;
import cn.sunnysky.model.FoodType;
import cn.sunnysky.user.UserManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import static cn.sunnysky.IntegratedManager.*;

public class MenuCalculator {

    public static void loadAndCalculate(String index,String... buildFiles){

        if ( fileManager == null) {
            logger.log("Dummy file manager!", LogType.ERROR);
            return;
        }

        logger.log("Loading data");

        final Set<String> users = fileManager.readSerializedDataFromFile(index).keySet();
        ArrayList<String> loadedData = new ArrayList<>();
        Map<String,String> temp;

        for (String u : users)
            if ((temp = fileManager.readSerializedDataFromFile(u)) != null)
                loadedData.addAll(temp.keySet());

        DTree<FoodType>[] roots = new DTree[buildFiles.length];
        DTreeBuilder builder = new DTreeBuilder();

        logger.log("Building data models");

        AtomicInteger i = new AtomicInteger(0);
        for (String buildFile : buildFiles)
            roots[i.getAndIncrement()] = builder.buildFromFile(buildFile);

        logger.log("Calculating");

        i.set(1);
        for (DTree<FoodType> t : roots){
            DTreeController controller = new DTreeController(t);

            for (String k : loadedData)
                controller.addChoice(k);

            DTree<FoodType>[] result = controller.calculateResults(3,0,1);
            logger.log("All results: ");

            Map<String,String> dataMap = new HashMap<>();

            for (DTree<FoodType> f : result)
                dataMap.put(f.getData().name, "recommended");

            fileManager.writeSerializedData(dataMap,"RecommendedMenu");

            recommendedMenu = dataMap.keySet();
        }

    }

}
