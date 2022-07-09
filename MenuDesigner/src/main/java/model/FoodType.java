package model;

public class FoodType {
    public volatile int popularity = 0;

    public final String name;

    public FoodType(String name){
        this.name = name;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof FoodType && ((FoodType) obj).name.contentEquals(this.name);
    }

    @Override
    public String toString() {
        return name + " : " + popularity;
    }
}
