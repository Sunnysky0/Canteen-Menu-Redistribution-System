package cn.sunnysky.controller;

import cn.sunnysky.model.DTree;
import cn.sunnysky.model.FoodType;

import java.util.Comparator;

public class Comparators {

    public static final FoodTypeComparator foodTypeComparator
            = new FoodTypeComparator();

    public static final DTreeComparator<FoodType> foodTypeDTreeComparator
            = new DTreeComparator<FoodType>(foodTypeComparator);

    /**
     * Compare by name.
     */
    public static final class FoodTypeComparator
            implements Comparator<FoodType> {

        @Override
        public int compare(FoodType o1, FoodType o2) {
            if (o1 == null) return -1;
            else if (o2 == null) return 1;
            return o1.name.compareTo(o2.name);
        }
    }

    /**
     * Compare two tree nodes by a given comparator for its data.
     * @param <T>
     */
    public static final class DTreeComparator<T>
            implements Comparator<DTree<T>>{

        private Comparator<T> dataComparator;

        public DTreeComparator(Comparator<T> dataComparator) {
            this.dataComparator = dataComparator;
        }

        @Override
        public int compare(DTree<T> o1, DTree<T> o2) {
            if (o1 == null) return -1;
            else if (o2 == null) return 1;
            return dataComparator.compare(o1.getData(),o2.getData());
        }
    }
}
