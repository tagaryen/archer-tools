package com.archer.tools.java;

import java.util.*;

public class ArcherList<E> implements List<E> {

    private Object[] data;
    private int size = 0;
    private boolean hasNull = false;

    public ArcherList() {
        this(16);
    }

    public ArcherList(int cap) {
        if(cap < 0) {
            throw new IllegalArgumentException("cap must not be negative");
        }
        data = new Object[cap];
    }


    @SafeVarargs
    public ArcherList(E... es) {
        data = es;
        size = es.length;
        checkNull();
    }

    public ArcherList(Collection<E> c) {
        this(c.size());
        addAll(c);
    }

    private ArcherList(Object[] data, int size) {
        this.data = data;
        this.size = size;
        checkNull();
    }

    private void ensureCapacity(int minCap) {
        if(minCap > data.length) {
            int newCap = data.length + (data.length >> 1);
            if(newCap < minCap) {
                newCap = minCap;
            }
            if(newCap < 8) {
                newCap = 8;
            }
            data = Arrays.copyOf(data, newCap);
        }
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public boolean contains(Object o) {
        if(o == null) {
            return hasNull;
        }
        return indexOf(o) >= 0;
    }

    @Override
    @SuppressWarnings(value = "unchecked")
    public Iterator<E> iterator() {
        return new ArcherIt<E>((E[]) data, size);
    }

    @Override
    public Object[] toArray() {
        return Arrays.copyOf(data, size);
    }

    @Override
    @SuppressWarnings(value = "unchecked")
    public <T> T[] toArray(T[] a) {
        if(a.length < size) {
            return (T[]) Arrays.copyOf(data, size, a.getClass());
        }
        System.arraycopy(data, 0, a, 0, size);
        if(a.length > size) {
            a[size] = null;
        }
        return a;
    }

    @Override
    public boolean add(E e) {
        ensureCapacity(size + 1);
        data[size++] = e;
        if(e == null) {
            hasNull = true;
        }
        return true;
    }

    @Override
    public boolean remove(Object o) {
        int i = indexOf(o);
        if(i < 0) {
            return false;
        }
        removeAt(i);
        if(o == null) {
            checkNull();
        }
        return true;
    }

    private void removeAt(int index) {
        int moved = size - index - 1;
        if(moved > 0) {
            System.arraycopy(data, index + 1, data, index, moved);
        }
        data[--size] = null;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        if(c == null || c.isEmpty()) {
            return true;
        }
        for(Object o: c) {
            if(!contains(o)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        if(c == null || c.isEmpty()) {
            return false;
        }
        Object[] arr = c.toArray();
        ensureCapacity(size + arr.length);
        System.arraycopy(arr, 0, data, size, arr.length);
        size += arr.length;
        if(!hasNull) {
            for(Object o: arr) {
                if(o == null) {
                    hasNull = true;
                    break;
                }
            }
        }
        return true;
    }

    @Override
    public boolean addAll(int index, Collection<? extends E> c) {
        if(c == null || c.isEmpty()) {
            return false;
        }
        if(index < 0) {
            index = 0;
        } else if(index > size) {
            index = size;
        }
        Object[] arr = c.toArray();
        ensureCapacity(size + arr.length);
        int moved = size - index;
        if(moved > 0) {
            System.arraycopy(data, index, data, index + arr.length, moved);
        }
        System.arraycopy(arr, 0, data, index, arr.length);
        size += arr.length;
        if(!hasNull) {
            for(Object o: arr) {
                if(o == null) {
                    hasNull = true;
                    break;
                }
            }
        }
        return true;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        if(c == null || c.isEmpty()) {
            return false;
        }
        boolean modified = false;
        for(Object o: c) {
            while(remove(o)) {
                modified = true;
            }
        }
        return modified;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        if(c == null || c.isEmpty()) {
            if(size == 0) {
                return false;
            }
            Arrays.fill(data, 0, size, null);
            size = 0;
            hasNull = false;
            return true;
        }
        int w = 0;
        for(int i = 0; i < size; i++) {
            if(c.contains(data[i])) {
                data[w++] = data[i];
            }
        }
        if(w == size) {
            return false;
        }
        Arrays.fill(data, w, size, null);
        size = w;
        checkNull();
        return true;
    }

    @Override
    public void clear() {
        Arrays.fill(data, 0, size, null);
        size = 0;
        hasNull = false;
    }

    @Override
    @SuppressWarnings(value = "unchecked")
    public E get(int index) {
        if(index < 0 || index >= size) {
            return null;
        }
        return (E) data[index];
    }

    @Override
    @SuppressWarnings(value = "unchecked")
    public E set(int index, E element) {
        if(index < 0) {
            index = 0;
        } else if(index >= size) {
            index = size - 1;
        }
        Object old = data[index];
        data[index] = element;
        if(element == null) {
            hasNull = true;
        } else if(old == null && hasNull) {
            checkNull();
        }
        return (E) old;
    }

    @Override
    public void add(int index, E element) {
        if(index < 0) {
            index = 0;
        } else if(index > size) {
            index = size;
        }
        ensureCapacity(size + 1);
        int moved = size - index;
        if(moved > 0) {
            System.arraycopy(data, index, data, index + 1, moved);
        }
        data[index] = element;
        size++;
        if(element == null) {
            hasNull = true;
        }
    }

    @Override
    @SuppressWarnings(value = "unchecked")
    public E remove(int index) {
        if(index < 0) {
            index = 0;
        } else if(index >= size) {
            index = size - 1;
        }
        Object old = data[index];
        removeAt(index);
        if(old == null) {
            checkNull();
        }
        return (E) old;
    }

    @Override
    public int indexOf(Object o) {
        for(int i = 0; i < size; i++) {
            if(o == null) {
                if(data[i] == null) {
                    return i;
                }
            } else if(o.equals(data[i])) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public int lastIndexOf(Object o) {
        for(int i = size - 1; i >= 0; i--) {
            if(o == null) {
                if(data[i] == null) {
                    return i;
                }
            } else if(o.equals(data[i])) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public ListIterator<E> listIterator() {
        return listIterator(0);
    }

    @Override
    public ListIterator<E> listIterator(int index) {
        if(index < 0) {
            index = 0;
        } else if(index >= size) {
            index = size - 1;
        }
        return new ArcherListIt<E>(data, size, index);
    }

    @Override
    public List<E> subList(int fromIndex, int toIndex) {
        if(toIndex < 0) {
            toIndex = 0;
        } else if(toIndex > size) {
            toIndex = size;
        }
        if(fromIndex < 0) {
            fromIndex = 0;
        } else if(fromIndex > size) {
            fromIndex = size;
        }
        if(toIndex < fromIndex) {
            toIndex = fromIndex;
        }
        Object[] nd = Arrays.copyOfRange(data, fromIndex, toIndex);
        return new ArcherList<E>(nd, nd.length);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder(size * 8 + 2);
        sb.append('[');
        for(int i = 0; i < size; i++) {
            if(i > 0) {
                sb.append(", ");
            }
            sb.append(data[i]);
        }
        return sb.append(']').toString();
    }

    private void checkNull() {
        this.hasNull = false;
        for(int i = 0; i < size; i++) {
            if(data[i] == null) {
                this.hasNull = true;
                return ;
            }
        }
    }

    class ArcherIt<E> implements Iterator<E> {

        E[] es;
        int size;
        int off = 0;

        public ArcherIt(E[] es, int size) {
            this.es = es;
            this.size = size;
        }

        @Override
        public boolean hasNext() {
            return off < size;
        }

        @Override
        public E next() {
            if(off >= size) {
                return null;
            }
            return es[off++];
        }
    }

    class ArcherListIt<E> implements ListIterator<E> {

        private Object[] es;
        private int size;
        private int off;

        public ArcherListIt(Object[] es, int size, int off) {
            this.es = es;
            this.size = size;
            this.off = off;
        }

        @Override
        public boolean hasNext() {
            return off < size;
        }

        @Override
        @SuppressWarnings(value = "unchecked")
        public E next() {
            if(off >= size) {
                return null;
            }
            return (E) es[off++];
        }

        @Override
        public boolean hasPrevious() {
            return off >= 0;
        }

        @Override
        @SuppressWarnings(value = "unchecked")
        public E previous() {
            if(off < 0) {
                return null;
            }
            return (E) es[off--];
        }

        @Override
        public int nextIndex() {
            if(off >= size) {
                return size;
            }
            return off + 1;
        }

        @Override
        public int previousIndex() {
            if(off < 0) {
                return -1;
            }
            return off - 1;
        }

        @Override
        public void remove() {
            if(off < 0 || off >= size) {
                return;
            }
            int moved = size - off - 1;
            if(moved > 0) {
                System.arraycopy(es, off + 1, es, off, moved);
            }
            es[--size] = null;
            off--;
        }

        @Override
        public void set(E e) {
            if(off < 0 || off >= size) {
                return;
            }
            es[off] = e;
        }

        @Override
        public void add(E e) {
            if(off < 0 || off >= size) {
                return;
            }
            if(size >= es.length) {
                es = Arrays.copyOf(es, es.length + (es.length >> 1) + 1);
            }
            System.arraycopy(es, off, es, off + 1, size - off);
            es[off] = e;
            size++;
        }
    }
}
