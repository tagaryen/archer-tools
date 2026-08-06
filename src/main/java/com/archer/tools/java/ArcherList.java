package com.archer.tools.java;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class ArcherList<E> implements List<E> {

    private Object[] data;
    private int size = 0;
    private boolean hasNull = false;

    public ArcherList() {
        this(32);
    }

    public ArcherList(int cap) {
        data = new Object[cap];
    }


    @SafeVarargs
    public ArcherList(E... es) {
        data = es;
        size = es.length;
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
        for(int i = 0; i < size; i++) {
            if(o.equals(data[i])) {
                return true;
            }
        }
        return false;
    }

    @Override
    @SuppressWarnings(value = "unchecked")
    public Iterator<E> iterator() {
        return new ArcherIt<E>((E[]) data, size);
    }

    @Override
    public Object[] toArray() {
        Object[] ret = new Object[size];
        System.arraycopy(data, 0, ret, 0, data.length);
        return ret;
    }

    @Override
    @SuppressWarnings(value = "unchecked")
    public <T> T[] toArray(T[] a) {
        return (T[]) toArray();
    }

    @Override
    public boolean add(E e) {
        if(size >= data.length) {
            Object[] nd = new Object[data.length * 2];
            System.arraycopy(data, 0, nd, 0, data.length);
            data = nd;
        }
        data[size++] = e;
        if(e == null) {
            hasNull = true;
        }
        return true;
    }

    @Override
    public boolean remove(Object o) {
        int i = 0;
        boolean isNull = false;
        for(; i < size; i++) {
            if(o == null) {
                if(data[i] == null) {
                    isNull = true;
                    break;
                }
            } else if(o.equals(data[i])) {
                break;
            }
        }
        i++;
        if(isNull) {
            this.hasNull = false;
        }
        if(i < size) {
            for(; i < size; i++) {
                data[i-1] = data[i];
                if(data[i] == null) {
                    this.hasNull = true;
                }
            }
            size--;
        }
        return true;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        if(c == null || c.isEmpty()) {
            return true;
        }
        boolean ok = true;
        for(int i = 0; i < size; i++) {
            ok = false;
            for(Object o: c) {
                if(o == null) {
                    if(data[i] == null) {
                        ok = true;
                        break;
                    }
                } else if(o.equals(data[i])) {
                    ok = true;
                    break;
                }
            }
            if(!ok) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        if(c == null || c.isEmpty()) {
            return true;
        }
        if(size + c.size() > data.length) {
            Object[] nd = new Object[(data.length +c.size()) * 2];
            System.arraycopy(data, 0, nd, 0, data.length);
            data = nd;
        }
        for(Object o: c) {
            data[size++] = o;
            if(o == null) {
                hasNull = true;
            }
        }
        return true;
    }

    @Override
    public boolean addAll(int index, Collection<? extends E> c) {
        if(c == null || c.isEmpty()) {
            return true;
        }
        if(index < 0) {
            index = 0;
        } else if(index >= size) {
            index = size;
        }
        Object[] nd = new Object[(data.length +c.size())];
        System.arraycopy(data, 0, nd, 0, index);
        int i = index;
        for(Object o: c) {
            nd[i++] = o;
            if(o == null) {
                hasNull = true;
            }
        }
        System.arraycopy(data, index, nd, i, size  - index);
        data = nd;
        size += c.size();
        return true;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        for(Object o: c) {
            remove(o);
        }
        return true;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        if(c == null || c.isEmpty()) {
            size = 0;
            return true;
        }
        int idx = 0;
        Object[] nd = new Object[data.length];
        hasNull = false;
        for(int i = 0; i < size; i++) {
            for(Object o: c) {
                if(o == null) {
                    if(data[i] == null) {
                        nd[idx++] = null;
                        hasNull = true;
                        break;
                    }
                } else if(o.equals(data[i])) {
                    nd[idx++] = o;
                    break;
                }
            }
        }
        data = nd;
        size = idx;
        return true;
    }

    @Override
    public void clear() {
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
        }
        return (E) old;
    }

    @Override
    public void add(int index, E element) {
        if(index < 0) {
            index = 0;
        } else if(index >= size) {
            index = size;
        }
        if(size >= data.length) {
            Object[] nd = new Object[data.length * 2];
            System.arraycopy(data, 0, nd, 0, data.length);
            data = nd;
        }
        for(int i = size; i > index; i--) {
            data[i] = data[i-1];
        }
        data[index] = element;
        size++;
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
        for(int i = index ; i < size - 1; i++) {
            data[i] = data[i+1];
        }
        size--;
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
        int nSize = toIndex - fromIndex;
        Object[] nd = new Object[nSize];
        System.arraycopy(data, fromIndex, nd, 0, toIndex - fromIndex);
        return new ArcherList<E>(nd, nSize);
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
            for(int i = off ; i < size - 1; i++) {
                es[i] = es[i+1];
            }
            size--;
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
                Object[] nd = new Object[es.length * 2];
                System.arraycopy(es, 0, nd, 0, es.length);
                es = nd;
            }
            for(int i = size; i > off; i--) {
                es[i] = es[i-1];
            }
            es[off] = e;
            size++;
        }
    }
}
