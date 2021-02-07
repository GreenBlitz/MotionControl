package org.greenblitz.utils;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.*;
import java.util.function.Consumer;

public class LinkedList<E> extends AbstractSequentialList<E> implements List<E>, Deque<E>, Cloneable, Serializable {
    transient int size;
    transient LinkedList.Node<E> first;
    transient LinkedList.Node<E> last;
    private static final long serialVersionUID = 876323262645176354L;

    // our code for optimal time complexity

    public void addAll(LinkedList<E> list){
        this.size += list.size();
        list.first.prev =this.last;
        this.last.next = list.first;
        this.last = list.last;
    }

    public Node<E> getNodeFirst(){
        LinkedList.Node<E> f = this.first;
        if (f == null) {
            throw new NoSuchElementException();
        } else {
            return f;
        }
    }

    public Node<E> getNodeLast(){
        LinkedList.Node<E> f = this.last;
        if (f == null) {
            throw new NoSuchElementException();
        } else {
            return f;
        }
    }

    public void merge(LinkedList<E> mergedList, int mergeIndexOfMergedList, Node<E> mergeNodeOfMergedList){
        this.last.next = mergeNodeOfMergedList;
        mergeNodeOfMergedList.prev = this.last;
        this.size = this.size + mergedList.size - mergeIndexOfMergedList;
        this.last = mergedList.last;
    }

    //original code

    public LinkedList() {
        this.size = 0;
    }

    public LinkedList(Collection<? extends E> c) {
        this();
        this.addAll(c);
    }

    private void linkFirst(E e) {
        LinkedList.Node<E> f = this.first;
        LinkedList.Node<E> newNode = new LinkedList.Node((LinkedList.Node)null, e, f);
        this.first = newNode;
        if (f == null) {
            this.last = newNode;
        } else {
            f.prev = newNode;
        }

        ++this.size;
        ++this.modCount;
    }

    void linkLast(E e) {
        LinkedList.Node<E> l = this.last;
        LinkedList.Node<E> newNode = new LinkedList.Node(l, e, (LinkedList.Node)null);
        this.last = newNode;
        if (l == null) {
            this.first = newNode;
        } else {
            l.next = newNode;
        }

        ++this.size;
        ++this.modCount;
    }

    void linkBefore(E e, LinkedList.Node<E> succ) {
        LinkedList.Node<E> pred = succ.prev;
        LinkedList.Node<E> newNode = new LinkedList.Node(pred, e, succ);
        succ.prev = newNode;
        if (pred == null) {
            this.first = newNode;
        } else {
            pred.next = newNode;
        }

        ++this.size;
        ++this.modCount;
    }

    private E unlinkFirst(LinkedList.Node<E> f) {
        E element = f.item;
        LinkedList.Node<E> next = f.next;
        f.item = null;
        f.next = null;
        this.first = next;
        if (next == null) {
            this.last = null;
        } else {
            next.prev = null;
        }

        --this.size;
        ++this.modCount;
        return element;
    }

    private E unlinkLast(LinkedList.Node<E> l) {
        E element = l.item;
        LinkedList.Node<E> prev = l.prev;
        l.item = null;
        l.prev = null;
        this.last = prev;
        if (prev == null) {
            this.first = null;
        } else {
            prev.next = null;
        }

        --this.size;
        ++this.modCount;
        return element;
    }

    E unlink(LinkedList.Node<E> x) {
        E element = x.item;
        LinkedList.Node<E> next = x.next;
        LinkedList.Node<E> prev = x.prev;
        if (prev == null) {
            this.first = next;
        } else {
            prev.next = next;
            x.prev = null;
        }

        if (next == null) {
            this.last = prev;
        } else {
            next.prev = prev;
            x.next = null;
        }

        x.item = null;
        --this.size;
        ++this.modCount;
        return element;
    }

    public E getFirst() {
        LinkedList.Node<E> f = this.first;
        if (f == null) {
            throw new NoSuchElementException();
        } else {
            return f.item;
        }
    }

    public E getLast() {
        LinkedList.Node<E> l = this.last;
        if (l == null) {
            throw new NoSuchElementException();
        } else {
            return l.item;
        }
    }

    public E removeFirst() {
        LinkedList.Node<E> f = this.first;
        if (f == null) {
            throw new NoSuchElementException();
        } else {
            return this.unlinkFirst(f);
        }
    }

    public E removeLast() {
        LinkedList.Node<E> l = this.last;
        if (l == null) {
            throw new NoSuchElementException();
        } else {
            return this.unlinkLast(l);
        }
    }

    public void addFirst(E e) {
        this.linkFirst(e);
    }

    public void addLast(E e) {
        this.linkLast(e);
    }

    public boolean contains(Object o) {
        return this.indexOf(o) >= 0;
    }

    public int size() {
        return this.size;
    }

    public boolean add(E e) {
        this.linkLast(e);
        return true;
    }

    public boolean remove(Object o) {
        LinkedList.Node x;
        if (o == null) {
            for(x = this.first; x != null; x = x.next) {
                if (x.item == null) {
                    this.unlink(x);
                    return true;
                }
            }
        } else {
            for(x = this.first; x != null; x = x.next) {
                if (o.equals(x.item)) {
                    this.unlink(x);
                    return true;
                }
            }
        }

        return false;
    }

    public boolean addAll(Collection<? extends E> c) {
        return this.addAll(this.size, c);
    }

    public boolean addAll(int index, Collection<? extends E> c) {
        this.checkPositionIndex(index);
        Object[] a = c.toArray();
        int numNew = a.length;
        if (numNew == 0) {
            return false;
        } else {
            LinkedList.Node pred;
            LinkedList.Node succ;
            if (index == this.size) {
                succ = null;
                pred = this.last;
            } else {
                succ = this.node(index);
                pred = succ.prev;
            }

            Object[] var7 = a;
            int var8 = a.length;

            for(int var9 = 0; var9 < var8; ++var9) {
                Object o = var7[var9];
                LinkedList.Node<E> newNode = new LinkedList.Node(pred, o, (LinkedList.Node)null);
                if (pred == null) {
                    this.first = newNode;
                } else {
                    pred.next = newNode;
                }

                pred = newNode;
            }

            if (succ == null) {
                this.last = pred;
            } else {
                pred.next = succ;
                succ.prev = pred;
            }

            this.size += numNew;
            ++this.modCount;
            return true;
        }
    }

    public void clear() {
        LinkedList.Node next;
        for(LinkedList.Node x = this.first; x != null; x = next) {
            next = x.next;
            x.item = null;
            x.next = null;
            x.prev = null;
        }

        this.first = this.last = null;
        this.size = 0;
        ++this.modCount;
    }

    public E get(int index) {
        this.checkElementIndex(index);
        return this.node(index).item;
    }

    public E set(int index, E element) {
        this.checkElementIndex(index);
        LinkedList.Node<E> x = this.node(index);
        E oldVal = x.item;
        x.item = element;
        return oldVal;
    }

    public void add(int index, E element) {
        this.checkPositionIndex(index);
        if (index == this.size) {
            this.linkLast(element);
        } else {
            this.linkBefore(element, this.node(index));
        }

    }

    public E remove(int index) {
        this.checkElementIndex(index);
        return this.unlink(this.node(index));
    }

    private boolean isElementIndex(int index) {
        return index >= 0 && index < this.size;
    }

    private boolean isPositionIndex(int index) {
        return index >= 0 && index <= this.size;
    }

    private String outOfBoundsMsg(int index) {
        return "Index: " + index + ", Size: " + this.size;
    }

    private void checkElementIndex(int index) {
        if (!this.isElementIndex(index)) {
            throw new IndexOutOfBoundsException(this.outOfBoundsMsg(index));
        }
    }

    private void checkPositionIndex(int index) {
        if (!this.isPositionIndex(index)) {
            throw new IndexOutOfBoundsException(this.outOfBoundsMsg(index));
        }
    }

    LinkedList.Node<E> node(int index) {
        LinkedList.Node x;
        int i;
        if (index < this.size >> 1) {
            x = this.first;

            for(i = 0; i < index; ++i) {
                x = x.next;
            }

            return x;
        } else {
            x = this.last;

            for(i = this.size - 1; i > index; --i) {
                x = x.prev;
            }

            return x;
        }
    }

    public int indexOf(Object o) {
        int index = 0;
        LinkedList.Node x;
        if (o == null) {
            for(x = this.first; x != null; x = x.next) {
                if (x.item == null) {
                    return index;
                }

                ++index;
            }
        } else {
            for(x = this.first; x != null; x = x.next) {
                if (o.equals(x.item)) {
                    return index;
                }

                ++index;
            }
        }

        return -1;
    }

    public int lastIndexOf(Object o) {
        int index = this.size;
        LinkedList.Node x;
        if (o == null) {
            for(x = this.last; x != null; x = x.prev) {
                --index;
                if (x.item == null) {
                    return index;
                }
            }
        } else {
            for(x = this.last; x != null; x = x.prev) {
                --index;
                if (o.equals(x.item)) {
                    return index;
                }
            }
        }

        return -1;
    }

    public E peek() {
        LinkedList.Node<E> f = this.first;
        return f == null ? null : f.item;
    }

    public E element() {
        return this.getFirst();
    }

    public E poll() {
        LinkedList.Node<E> f = this.first;
        return f == null ? null : this.unlinkFirst(f);
    }

    public E remove() {
        return this.removeFirst();
    }

    public boolean offer(E e) {
        return this.add(e);
    }

    public boolean offerFirst(E e) {
        this.addFirst(e);
        return true;
    }

    public boolean offerLast(E e) {
        this.addLast(e);
        return true;
    }

    public E peekFirst() {
        LinkedList.Node<E> f = this.first;
        return f == null ? null : f.item;
    }

    public E peekLast() {
        LinkedList.Node<E> l = this.last;
        return l == null ? null : l.item;
    }

    public E pollFirst() {
        LinkedList.Node<E> f = this.first;
        return f == null ? null : this.unlinkFirst(f);
    }

    public E pollLast() {
        LinkedList.Node<E> l = this.last;
        return l == null ? null : this.unlinkLast(l);
    }

    public void push(E e) {
        this.addFirst(e);
    }

    public E pop() {
        return this.removeFirst();
    }

    public boolean removeFirstOccurrence(Object o) {
        return this.remove(o);
    }

    public boolean removeLastOccurrence(Object o) {
        LinkedList.Node x;
        if (o == null) {
            for(x = this.last; x != null; x = x.prev) {
                if (x.item == null) {
                    this.unlink(x);
                    return true;
                }
            }
        } else {
            for(x = this.last; x != null; x = x.prev) {
                if (o.equals(x.item)) {
                    this.unlink(x);
                    return true;
                }
            }
        }

        return false;
    }

    public ListIterator<E> listIterator(int index) {
        this.checkPositionIndex(index);
        return new LinkedList.ListItr(index);
    }

    public Iterator<E> descendingIterator() {
        return new LinkedList.DescendingIterator();
    }

    private LinkedList<E> superClone() {
        try {
            return (LinkedList)super.clone();
        } catch (CloneNotSupportedException var2) {
            throw new InternalError(var2);
        }
    }

    public Object clone() {
        LinkedList<E> clone = this.superClone();
        clone.first = clone.last = null;
        clone.size = 0;
        clone.modCount = 0;

        for(LinkedList.Node x = this.first; x != null; x = x.next) {
            clone.add((E) x.item);
        }

        return clone;
    }


    public Object[] toArray() {
        Object[] result = new Object[this.size];
        int i = 0;

        for(LinkedList.Node x = this.first; x != null; x = x.next) {
            result[i++] = x.item;
        }

        return result;
    }

    public <T> T[] toArray(T[] a) {
        if (a.length < this.size) {
            a = (T[]) Array.newInstance(a.getClass().getComponentType(), this.size);
        }

        int i = 0;
        Object[] result = a;

        for(LinkedList.Node x = this.first; x != null; x = x.next) {
            result[i++] = x.item;
        }

        if (a.length > this.size) {
            a[this.size] = null;
        }

        return a;
    }

    private void writeObject(ObjectOutputStream s) throws IOException {
        s.defaultWriteObject();
        s.writeInt(this.size);

        for(LinkedList.Node x = this.first; x != null; x = x.next) {
            s.writeObject(x.item);
        }

    }

    private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
        s.defaultReadObject();
        int size = s.readInt();

        for(int i = 0; i < size; ++i) {
            this.linkLast((E) s.readObject());
        }

    }

    public Spliterator<E> spliterator() {
        return new LinkedList.LLSpliterator(this, -1, 0);
    }

    static final class LLSpliterator<E> implements Spliterator<E> {
        static final int BATCH_UNIT = 1024;
        static final int MAX_BATCH = 33554432;
        final LinkedList<E> list;
        LinkedList.Node<E> current;
        int est;
        int expectedModCount;
        int batch;

        LLSpliterator(LinkedList<E> list, int est, int expectedModCount) {
            this.list = list;
            this.est = est;
            this.expectedModCount = expectedModCount;
        }

        final int getEst() {
            int s;
            if ((s = this.est) < 0) {
                LinkedList lst;
                if ((lst = this.list) == null) {
                    s = this.est = 0;
                } else {
                    this.expectedModCount = lst.modCount;
                    this.current = lst.first;
                    s = this.est = lst.size;
                }
            }

            return s;
        }

        public long estimateSize() {
            return (long)this.getEst();
        }

        public Spliterator<E> trySplit() {
            int s = this.getEst();
            LinkedList.Node p;
            if (s > 1 && (p = this.current) != null) {
                int n = this.batch + 1024;
                if (n > s) {
                    n = s;
                }

                if (n > 33554432) {
                    n = 33554432;
                }

                Object[] a = new Object[n];
                int j = 0;

                do {
                    a[j++] = p.item;
                } while((p = p.next) != null && j < n);

                this.current = p;
                this.batch = j;
                this.est = s - j;
                return Spliterators.spliterator(a, 0, j, 16);
            } else {
                return null;
            }
        }

        public void forEachRemaining(Consumer<? super E> action) {
            if (action == null) {
                throw new NullPointerException();
            } else {
                LinkedList.Node p;
                int n;
                if ((n = this.getEst()) > 0 && (p = this.current) != null) {
                    this.current = null;
                    this.est = 0;

                    do {
                        E e = (E) p.item;
                        p = p.next;
                        action.accept(e);
                        if (p == null) {
                            break;
                        }

                        --n;
                    } while(n > 0);
                }

                if (this.list.modCount != this.expectedModCount) {
                    throw new ConcurrentModificationException();
                }
            }
        }

        public boolean tryAdvance(Consumer<? super E> action) {
            if (action == null) {
                throw new NullPointerException();
            } else {
                LinkedList.Node p;
                if (this.getEst() > 0 && (p = this.current) != null) {
                    --this.est;
                    E e = (E) p.item;
                    this.current = p.next;
                    action.accept(e);
                    if (this.list.modCount != this.expectedModCount) {
                        throw new ConcurrentModificationException();
                    } else {
                        return true;
                    }
                } else {
                    return false;
                }
            }
        }

        public int characteristics() {
            return 16464;
        }
    }

    private class DescendingIterator implements Iterator<E> {
        private final LinkedList<E>.ListItr itr = LinkedList.this.new ListItr(LinkedList.this.size());

        private DescendingIterator() {
        }

        public boolean hasNext() {
            return this.itr.hasPrevious();
        }

        public E next() {
            return this.itr.previous();
        }

        public void remove() {
            this.itr.remove();
        }
    }


    /*
     *turned public and added getters and setters
     */
    public static class Node<E> {
        private E item;
        private LinkedList.Node<E> next;
        private LinkedList.Node<E> prev;



        Node(LinkedList.Node<E> prev, E element, LinkedList.Node<E> next) {
            this.item = element;
            this.next = next;
            this.prev = prev;
        }

        public E getItem() {
            return item;
        }

        public void setItem(E item) {
            this.item = item;
        }

        public Node<E> getNext() {
            return next;
        }


        public Node<E> getPrev() {
            return prev;
        }

    }

    private class ListItr implements ListIterator<E> {
        private LinkedList.Node<E> lastReturned;
        private LinkedList.Node<E> next;
        private int nextIndex;
        private int expectedModCount;

        ListItr(int index) {
            this.expectedModCount = LinkedList.this.modCount;
            this.next = index == LinkedList.this.size ? null : LinkedList.this.node(index);
            this.nextIndex = index;
        }

        public boolean hasNext() {
            return this.nextIndex < LinkedList.this.size;
        }

        public E next() {
            this.checkForComodification();
            if (!this.hasNext()) {
                throw new NoSuchElementException();
            } else {
                this.lastReturned = this.next;
                this.next = this.next.next;
                ++this.nextIndex;
                return this.lastReturned.item;
            }
        }

        public boolean hasPrevious() {
            return this.nextIndex > 0;
        }

        public E previous() {
            this.checkForComodification();
            if (!this.hasPrevious()) {
                throw new NoSuchElementException();
            } else {
                this.lastReturned = this.next = this.next == null ? LinkedList.this.last : this.next.prev;
                --this.nextIndex;
                return this.lastReturned.item;
            }
        }

        public int nextIndex() {
            return this.nextIndex;
        }

        public int previousIndex() {
            return this.nextIndex - 1;
        }

        public void remove() {
            this.checkForComodification();
            if (this.lastReturned == null) {
                throw new IllegalStateException();
            } else {
                LinkedList.Node<E> lastNext = this.lastReturned.next;
                LinkedList.this.unlink(this.lastReturned);
                if (this.next == this.lastReturned) {
                    this.next = lastNext;
                } else {
                    --this.nextIndex;
                }

                this.lastReturned = null;
                ++this.expectedModCount;
            }
        }

        public void set(E e) {
            if (this.lastReturned == null) {
                throw new IllegalStateException();
            } else {
                this.checkForComodification();
                this.lastReturned.item = e;
            }
        }

        public void add(E e) {
            this.checkForComodification();
            this.lastReturned = null;
            if (this.next == null) {
                LinkedList.this.linkLast(e);
            } else {
                LinkedList.this.linkBefore(e, this.next);
            }

            ++this.nextIndex;
            ++this.expectedModCount;
        }

        public void forEachRemaining(Consumer<? super E> action) {
            Objects.requireNonNull(action);

            while(LinkedList.this.modCount == this.expectedModCount && this.nextIndex < LinkedList.this.size) {
                action.accept(this.next.item);
                this.lastReturned = this.next;
                this.next = this.next.next;
                ++this.nextIndex;
            }

            this.checkForComodification();
        }

        final void checkForComodification() {
            if (LinkedList.this.modCount != this.expectedModCount) {
                throw new ConcurrentModificationException();
            }
        }
    }
}

