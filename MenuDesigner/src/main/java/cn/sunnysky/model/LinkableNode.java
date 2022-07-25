package cn.sunnysky.model;

public class LinkableNode<T> {
    public LinkableNode<T> previous;
    public LinkableNode<T> next;

    private T data;

    public LinkableNode(LinkableNode<T> previous, LinkableNode<T> next, T data) {
        this.previous = previous;
        this.next = next;
        this.data = data;
    }

    public LinkableNode(T data) {
        this.data = data;
    }

    public boolean hasNext(){ return this.next != null;}

    public boolean hasPrevious(){ return this.previous != null;}

    public void setPrevious(LinkableNode<T> previous) {
        this.previous = previous;
    }

    public void setNext(LinkableNode<T> next) {
        this.next = next;
    }

    public T getData() {
        return data;
    }
}
