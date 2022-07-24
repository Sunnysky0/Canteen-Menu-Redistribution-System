package cn.sunnysky.model;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class DTree<T> {
    private T data;

    private List<DTree<T>> children;

    private DTree<T> parent;

    public List<DTree<T>> getChildren() {
        return children;
    }

    public DTree(T data){
        this.data = data;
        this.children = new ArrayList<>();
    }

    public boolean isLeaf(DTree<T> tree){
        return tree.children != null && tree.children.size() > 0 &&tree.data != null;
    }

    public void addNode(DTree<T> tree){
        this.children.add(tree);
        tree.parent = this;
    }

    public DTree<T> createNode(T data){
        DTree<T> temp = new DTree<>(data);
        addNode(temp);
        return temp;
    }

    /**
     * Print the entire tree recursively.
     * @param layer The white space you want to assign.
     */
    @SuppressWarnings("NewApi")
    public void visualize(int layer){
        System.out.print("|-");
        for(int i = 0; i < layer * 2;i++)
            System.out.print("-");
        System.out.print(this.data.toString() + "\n");
        this.children.forEach(c -> c.visualize(layer + 1));
    }

    public boolean contains(T data){ return this.data.equals(data) ;}

    public DTree<T> getParent() {
        return parent;
    }

    /**
     * Check if the data is contained i the whole tree.
     * @param data The data you want to check.
     * @return A DTree node which contains the data, if not, returns null.
     */
    @Nullable
    public DTree<T> allContains(T data){
        if( this.data.equals(data)) return this;
        for(DTree<T> t : children){
            if(t.contains(data))
                return t;
        }
        return null;
    }

    /**
     * Modify the data as you want.
     * @param action A function which accepts the data of the tree and do anything you want to the data.
     * @param <R> The return type of the function, it cannot be null.
     * @return The return value of the function you give to the method.
     */
    @SuppressWarnings("NewApi")
    public <R> R modifyData(Function<T,R> action){
        return action.apply(this.data);
    }

    /**
     *
     * @param data The data you want to look for in the tree.
     * @return A DTree node which contains the data.
     */
    @Nullable
    public DTree<T> getSubTree(T data){

        if(this.data.equals(data)){
            return this;
        }
        if(this.children != null){
           for(DTree<T> st : this.children) {
               DTree<T> result = st.getSubTree(data);
                if(result != null){
                    return result;
                }
           }
        }
        return null;
    }

    public T getData() {
        return data;
    }

    @SuppressWarnings("NewApi")
    @Nullable
    public <S> DTree<T> customSearch(@NotNull Function<S,DTree<T>> comparator, S object){
        return comparator.apply(object);
    }


    /**
     * Search for a particular DTree node (in the whole tree) according to the comparator function you send.
     * @param comparator A function which accepts a particular type of data (S) and returns the result as a DTree node.
     * @param object The data which is given to the comparator.
     * @param recursionDataConverter A function that converts the recursion data (the children of the current result) to S.
     * @param <S> The particular data type, mostly is a DTree array.
     * @return The final result after the recursive search.
     */
    @SuppressWarnings("NewApi")
    @Nullable
    public final <S> DTree<T> customSearchRecursively(@NotNull Function<S,DTree<T>> comparator,@NotNull S object,@NotNull Function<DTree<T>,S> recursionDataConverter){
        DTree<T> result = comparator.apply(object);
        if(isLeaf(result)) return result.customSearchRecursively(comparator, recursionDataConverter.apply(result),recursionDataConverter);
        return result;
    }
}
