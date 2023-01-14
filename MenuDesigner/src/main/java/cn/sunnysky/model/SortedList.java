package cn.sunnysky.model;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;

/**
 * A special kind of linked list which
 * automatically sorts itself by the comparator given in the constructor when adding elements into it.
 * @param <T> The particular data type the list storages.
 * @author Sunnysky0
 */
public class SortedList<T> implements Collection<T> {


    private LinkableNode<T> dummyHead = new LinkableNode<>(null);

    private int SIZE = 0;

    private Comparator<T> comparator;

    public SortedList(Comparator<T> comparator) {
        this.comparator = comparator;
    }

    public boolean add(T data){
        add( new LinkableNode<>(data));

        return true;
    }

    @Override
    public boolean remove(Object o) {
        LinkableNode<T> currentNode = dummyHead.next;

        boolean flag = false;
        while (true){
            if (currentNode == o || comparator.compare(currentNode.getData(), (T) o) == 0){
                currentNode.previous.setNext(currentNode.next);
                if (currentNode.hasNext())
                    currentNode.next.setPrevious(currentNode.previous);

                flag = true;
            }

            if (currentNode.hasNext())
                currentNode = currentNode.next;
            else
                break;
        }

        SIZE--;
        return flag;
    }

    @Override
    public boolean containsAll(@NotNull Collection<?> c) {
        return false;
    }

    @Override
    public boolean addAll(@NotNull Collection<? extends T> c) {
        return false;
    }

    @SuppressWarnings("Operation not supported")
    @Deprecated
    @Override
    public boolean removeAll(@NotNull Collection<?> c) { throw new UnsupportedOperationException(); }

    @SuppressWarnings("Operation not supported")
    @Deprecated
    @Override
    public boolean retainAll(@NotNull Collection<?> c) { throw new UnsupportedOperationException(); }



    @Override
    public void clear() {
        LinkableNode<T> currentNode = dummyHead.next;

        while (true){
            currentNode.setPrevious(null);

            if (currentNode.hasNext())
                currentNode = currentNode.next;
            else
                break;
        }

        this.dummyHead.next = null;
        SIZE = 0;
    }

    public void add(LinkableNode<T> node){ add(node,dummyHead);}

    private void add(@NotNull LinkableNode<T> node,
                     @NotNull LinkableNode<T> startNode){

        if(comparator.compare(startNode.getData(), node.getData()) <= 0)
            if(startNode.hasNext())
                add(node,startNode.next);
            else {
                startNode.setNext(node);
                node.setPrevious(startNode);

                this.SIZE++;
            }
        else {
            if(startNode.hasPrevious()){
                startNode.previous.setNext(node);
                node.setPrevious(startNode.previous);
            }

            startNode.setPrevious(node);
            node.setNext(startNode);

            this.SIZE++;
        }
    }

    @Override
    public String toString() {
        LinkableNode<T> currentNode = dummyHead.next;

        StringBuilder str = new StringBuilder();

        while (true){
            str.append(currentNode.getData().toString());

            if(currentNode.hasNext()){
                str.append(",");
                currentNode = currentNode.next;
            } else {
                break;
            }
        }
        return "[" + str + "]";
    }

    @NotNull
    @Override
    public synchronized Iterator<T> iterator() {

        return new Iterator<>() {
            private  LinkableNode<T> pointedNode = dummyHead;

            @Override
            public boolean hasNext() {
                return pointedNode.hasNext();
            }

            @Override
            public T next() {
                pointedNode = pointedNode.next;
                return pointedNode.getData();
            }
        };
    }

    @NotNull
    @Override
    public Object[] toArray() {
        Object[] array = new Object[size()];
        int i = 0;
        for (T t : this){
            array[i] = t;
            i++;
        }
        return new Object[0];
    }

    @SuppressWarnings("Input array must fit the size")
    @NotNull
    @Override
    public <ExT> ExT[] toArray(@NotNull ExT[] a) {
        if(a.length < SIZE) throw new ArrayStoreException();

        ExT[] array = a;
        int i = 0;
        for (T t : this) {
            array[i] = (ExT) t;
            i++;
        }
        return array;
    }

    public int size() { return SIZE; }

    @Override
    public boolean isEmpty() {
        return SIZE == 0;
    }

    @Override
    public boolean contains(Object o ) {
        for (T t : this)
            if ( t == o || comparator.compare(t, (T) o) == 0)
                return true;
        return false;
    }
}
