package com.archer.tools.java;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;


public class ArcherMap<K, V> implements Map<K, V> {

	private static final int HASH_THRESHOLD = 32;

	private int cap, size;
	private Object[] keys, vals;
	private int[] hashes;
	private final int[] slots = new int[16];

	private int[] buckets;
	private int[] next;
	private boolean indexDirty = false;

	public ArcherMap() {
		this(16);
	}

	public ArcherMap(int cap) {
		if(cap <= 0) {
			throw new IllegalArgumentException("cap must be bigger than zero");
		}
		this.cap = cap;
		keys = new Object[cap];
		vals = new Object[cap];
		hashes = new int[cap];
	}

	@Override
	public int size() {
		return size;
	}

	@Override
	public boolean isEmpty() {
		return size == 0;
	}

	private static int spread(int h) {
		return h ^ (h >>> 16);
	}

	private int findIndex(Object key) {
		if(key == null) {
			return -1;
		}
		int h = key.hashCode();
		if(size < HASH_THRESHOLD) {
			// 快路径：16 槽直接映射，一次探测命中
			int j = slots[h & 15] - 1;
			if(j >= 0) {
				Object k = keys[j];
				if(k == key || (k != null && k.equals(key))) {
					return j;
				}
			}
			return scanIndex(key, h);
		}
		return hashIndex(key, h);
	}

	/** 小 map 线性回退路径（探测未命中时） */
	private int scanIndex(Object key, int h) {
		Object[] ks = keys;
		int[] hs = hashes;
		int n = size;
		for(int i = 0; i < n; i++) {
			if(hs[i] == h && (ks[i] == key || ks[i].equals(key))) {
				return i;
			}
		}
		return -1;
	}

	/** >= HASH_THRESHOLD 的桶链路径 */
	private int hashIndex(Object key, int h) {
		if(buckets == null || indexDirty) {
			rebuildIndex();
		}
		for(int i = buckets[spread(h) & (buckets.length - 1)]; i >= 0; i = next[i]) {
			if(hashes[i] == h && (keys[i] == key || keys[i].equals(key))) {
				return i;
			}
		}
		return -1;
	}

	private void rebuildIndex() {
		int n = 64;
		while(n < size * 2) {
			n <<= 1;
		}
		if(buckets == null || buckets.length != n) {
			buckets = new int[n];
		}
		Arrays.fill(buckets, -1);
		if(next == null || next.length < keys.length) {
			next = new int[keys.length];
		}
		for(int i = 0; i < size; i++) {
			int h = spread(hashes[i]) & (n - 1);
			next[i] = buckets[h];
			buckets[h] = i;
		}
		indexDirty = false;
	}

	private void ensureCapacity(int minCap) {
		if(minCap > cap) {
			int nc = cap + (cap >> 1);
			if(nc < minCap) {
				nc = minCap;
			}
			cap = nc;
			keys = Arrays.copyOf(keys, nc);
			vals = Arrays.copyOf(vals, nc);
			hashes = Arrays.copyOf(hashes, nc);
			if(next != null) {
				next = Arrays.copyOf(next, nc);
			}
		}
	}

	@Override
	public boolean containsKey(Object key) {
		return findIndex(key) >= 0;
	}

	@Override
	public boolean containsValue(Object value) {
		for(int i = 0; i < size; i++) {
			if(vals[i] == null) {
				if(value == null) {
					return true;
				}
			} else if(vals[i].equals(value)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public V get(Object key) {
		if(key == null) {
			return null;
		}
		int h = key.hashCode();
		if(size < HASH_THRESHOLD) {
			// 快路径：16 槽直接映射，一次探测命中
			int j = slots[h & 15] - 1;
			if(j >= 0) {
				Object k = keys[j];
				if(k == key || (k != null && k.equals(key))) {
					return valueAt(j);
				}
			}
			int i = scanIndex(key, h);
			return i < 0 ? null : valueAt(i);
		}
		int i = hashIndex(key, h);
		return i < 0 ? null : valueAt(i);
	}

	@Override
	@SuppressWarnings("unchecked")
	public V put(K key, V value) {
		if(key == null) {
			throw new NullPointerException();
		}
		int i = findIndex(key);
		if(i >= 0) {
			V old = (V) vals[i];
			vals[i] = value;
			return old;
		}
		ensureCapacity(size + 1);
		int h = key.hashCode();
		keys[size] = key;
		vals[size] = value;
		hashes[size] = h;
		if(size < HASH_THRESHOLD) {
			slots[h & 15] = size + 1;
		}
		if(buckets != null && !indexDirty) {
			if((size + 1) * 2 > buckets.length) {
				indexDirty = true;
			} else {
				int b = spread(h) & (buckets.length - 1);
				next[size] = buckets[b];
				buckets[b] = size;
			}
		}
		size++;
		return null;
	}

	@SuppressWarnings("unchecked")
	private V valueAt(int i) {
		return (V) vals[i];
	}

	@Override
	@SuppressWarnings("unchecked")
	public V remove(Object key) {
		int i = findIndex(key);
		if(i < 0) {
			return null;
		}
		V old = (V) vals[i];
		int moved = size - i - 1;
		if(moved > 0) {
			System.arraycopy(keys, i + 1, keys, i, moved);
			System.arraycopy(vals, i + 1, vals, i, moved);
			System.arraycopy(hashes, i + 1, hashes, i, moved);
		}
		keys[--size] = null;
		vals[size] = null;
		if(buckets != null) {
			indexDirty = true;
		}
		return old;
	}

	@Override
	public void putAll(Map<? extends K, ? extends V> m) {
		if(m == null || m.isEmpty()) {
			return ;
		}
		ensureCapacity(size + m.size());
		for(Entry<? extends K, ? extends V> en : m.entrySet()) {
			put(en.getKey(), en.getValue());
		}
	}

	@Override
	public void clear() {
		Arrays.fill(keys, 0, size, null);
		Arrays.fill(vals, 0, size, null);
		Arrays.fill(slots, 0);
		size = 0;
		if(buckets != null) {
			Arrays.fill(buckets, -1);
			indexDirty = false;
		}
	}

	@Override
	public Set<K> keySet() {
		return new KeySet();
	}

	@Override
	public Collection<V> values() {
		return new Values();
	}

	@Override
	public Set<Map.Entry<K, V>> entrySet() {
		return new PairSet();
	}

    public String toString() {
        StringBuilder sb = new StringBuilder(size * 32 + 2);
        sb.append('{');
        for(int i = 0; i < size; i++) {
            if(i > 0) {
                sb.append(", ");
            }
            sb.append('"').append(keys[i]).append("\":\"").append(vals[i]).append('"');
        }
        return sb.append('}').toString();
    }

	final class KeySet implements Set<K> {

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
			return containsKey(o);
		}

		@Override
		public Iterator<K> iterator() {
			return new KetIterator();
		}

		@Override
		public Object[] toArray() {
			return Arrays.copyOf(keys, size);
		}

		@Override
		public <T> T[] toArray(T[] a) {
	        throw new UnsupportedOperationException();
		}

		@Override
		public boolean add(K e) {
	        throw new UnsupportedOperationException();
		}

		@Override
		public boolean remove(Object o) {
	        throw new UnsupportedOperationException();
		}

		@Override
		public boolean containsAll(Collection<?> c) {
	        throw new UnsupportedOperationException();
		}

		@Override
		public boolean addAll(Collection<? extends K> c) {
	        throw new UnsupportedOperationException();
		}

		@Override
		public boolean retainAll(Collection<?> c) {
	        throw new UnsupportedOperationException();
		}

		@Override
		public boolean removeAll(Collection<?> c) {
	        throw new UnsupportedOperationException();
		}

		@Override
		public void clear() {
	        throw new UnsupportedOperationException();
		}

	}

	final class Values implements Collection<V> {

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
			return containsValue(o);
		}

		@Override
		public Iterator<V> iterator() {
			return new ValIterator();
		}

		@Override
		public Object[] toArray() {
			return Arrays.copyOf(vals, size);
		}

		@Override
		public <T> T[] toArray(T[] a) {
	        throw new UnsupportedOperationException();
		}

		@Override
		public boolean add(V e) {
	        throw new UnsupportedOperationException();
		}

		@Override
		public boolean remove(Object o) {
	        throw new UnsupportedOperationException();
		}

		@Override
		public boolean containsAll(Collection<?> c) {
	        throw new UnsupportedOperationException();
		}

		@Override
		public boolean addAll(Collection<? extends V> c) {
	        throw new UnsupportedOperationException();
		}

		@Override
		public boolean retainAll(Collection<?> c) {
	        throw new UnsupportedOperationException();
		}

		@Override
		public boolean removeAll(Collection<?> c) {
	        throw new UnsupportedOperationException();
		}

		@Override
		public void clear() {
	        throw new UnsupportedOperationException();
		}

	}

	final class KetIterator implements Iterator<K> {

		int i = 0;

		@Override
		public boolean hasNext() {
			return i < size;
		}

		@Override
		@SuppressWarnings("unchecked")
		public K next() {
			return (K) keys[i++];
		}

	}

	final class ValIterator implements Iterator<V> {

		int i = 0;

		@Override
		public boolean hasNext() {
			return i < size;
		}

		@Override
		@SuppressWarnings("unchecked")
		public V next() {
			return (V) vals[i++];
		}

	}

	final class PairSet implements Set<Map.Entry<K, V>> {

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
	        throw new UnsupportedOperationException();
		}

		@Override
		public Iterator<Map.Entry<K, V>> iterator() {
			return new PairIt();
		}

		@Override
		public Object[] toArray() {
	        throw new UnsupportedOperationException();
		}

		@Override
		public <T> T[] toArray(T[] a) {
	        throw new UnsupportedOperationException();
		}

		@Override
		public boolean add(Entry<K, V> e) {
	        throw new UnsupportedOperationException();
		}

		@Override
		public boolean remove(Object o) {
	        throw new UnsupportedOperationException();
		}

		@Override
		public boolean containsAll(Collection<?> c) {
	        throw new UnsupportedOperationException();
		}

		@Override
		public boolean addAll(Collection<? extends Entry<K, V>> c) {
	        throw new UnsupportedOperationException();
		}

		@Override
		public boolean retainAll(Collection<?> c) {
	        throw new UnsupportedOperationException();
		}

		@Override
		public boolean removeAll(Collection<?> c) {
	        throw new UnsupportedOperationException();
		}

		@Override
		public void clear() {
	        throw new UnsupportedOperationException();
		}

	}

	final class PairIt implements Iterator<Map.Entry<K, V>> {

		int i = 0;
		final Pair cursor = new Pair(null, null);

		@Override
		public boolean hasNext() {
			return i < size;
		}

		@Override
		@SuppressWarnings("unchecked")
		public Entry<K, V> next() {
			cursor.k = (K) keys[i];
			cursor.v = (V) vals[i];
			i++;
			return cursor;
		}

	}

	final class Pair implements Map.Entry<K, V> {

		K k;
		V v;

		Pair(K k, V v) {
			this.k = k;
			this.v = v;
		}

		@Override
		public K getKey() {
			return k;
		}

		@Override
		public V getValue() {
			return v;
		}

		@Override
		public V setValue(V value) {
	        throw new UnsupportedOperationException();
		}
	}

	@SuppressWarnings("unchecked")
	public Map.Entry<K, V> getEntryByIndex(int index) {
		if(index >= size || index < 0) {
			return null;
		}
		return new Pair((K) keys[index], (V) vals[index]);
	}
	@SuppressWarnings("unchecked")
	public K getKeyByIndex(int index) {
		if(index >= size || index < 0) {
			return null;
		}
		return (K) keys[index];
	}
	@SuppressWarnings("unchecked")
	public V getValueByIndex(int index) {
		if(index >= size || index < 0) {
			return null;
		}
		return (V) vals[index];
	}

	public static <K, V> ArcherMap<K, V> of(K k1, V v1) {
		ArcherMap<K, V> map = new ArcherMap<>();
		if(k1 != null && v1 != null) {
			map.put(k1,  v1);
		}
		return map;
	}
	public static <K, V> ArcherMap<K, V> of(K k1, V v1, K k2, V v2) {
		ArcherMap<K, V> map = new ArcherMap<>();
		if(k1 != null && v1 != null) {
			map.put(k1,  v1);
		}
		if(k2 != null && v2 != null) {
			map.put(k2,  v2);
		}
		return map;
	}
	public static <K, V> ArcherMap<K, V> of(K k1, V v1, K k2, V v2, K k3, V v3) {
		ArcherMap<K, V> map = new ArcherMap<>();
		if(k1 != null && v1 != null) {
			map.put(k1,  v1);
		}
		if(k2 != null && v2 != null) {
			map.put(k2,  v2);
		}
		if(k3 != null && v3 != null) {
			map.put(k3,  v3);
		}
		return map;
	}
	public static <K, V> ArcherMap<K, V> of(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4) {
		ArcherMap<K, V> map = new ArcherMap<>();
		if(k1 != null && v1 != null) {
			map.put(k1,  v1);
		}
		if(k2 != null && v2 != null) {
			map.put(k2,  v2);
		}
		if(k3 != null && v3 != null) {
			map.put(k3,  v3);
		}
		if(k4 != null && v4 != null) {
			map.put(k4,  v4);
		}
		return map;
	}
	public static <K, V> ArcherMap<K, V> of(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5) {
		ArcherMap<K, V> map = new ArcherMap<>();
		if(k1 != null && v1 != null) {
			map.put(k1,  v1);
		}
		if(k2 != null && v2 != null) {
			map.put(k2,  v2);
		}
		if(k3 != null && v3 != null) {
			map.put(k3,  v3);
		}
		if(k4 != null && v4 != null) {
			map.put(k4,  v4);
		}
		if(k5 != null && v5 != null) {
			map.put(k5,  v5);
		}
		return map;
	}
	public static <K, V> ArcherMap<K, V> of(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5,
            K k6, V v6) {
		ArcherMap<K, V> map = new ArcherMap<>();
		if(k1 != null && v1 != null) {
			map.put(k1,  v1);
		}
		if(k2 != null && v2 != null) {
			map.put(k2,  v2);
		}
		if(k3 != null && v3 != null) {
			map.put(k3,  v3);
		}
		if(k4 != null && v4 != null) {
			map.put(k4,  v4);
		}
		if(k5 != null && v5 != null) {
			map.put(k5,  v5);
		}
		if(k6 != null && v6 != null) {
			map.put(k6,  v6);
		}
		return map;
	}
	public static <K, V> ArcherMap<K, V> of(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5,
            K k6, V v6, K k7, V v7) {
		ArcherMap<K, V> map = new ArcherMap<>();
		if(k1 != null && v1 != null) {
			map.put(k1,  v1);
		}
		if(k2 != null && v2 != null) {
			map.put(k2,  v2);
		}
		if(k3 != null && v3 != null) {
			map.put(k3,  v3);
		}
		if(k4 != null && v4 != null) {
			map.put(k4,  v4);
		}
		if(k5 != null && v5 != null) {
			map.put(k5,  v5);
		}
		if(k6 != null && v6 != null) {
			map.put(k6,  v6);
		}
		if(k7 != null && v7 != null) {
			map.put(k7,  v7);
		}
		return map;
	}
	public static <K, V> ArcherMap<K, V> of(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5,
            K k6, V v6, K k7, V v7, K k8, V v8) {
		ArcherMap<K, V> map = new ArcherMap<>();
		if(k1 != null && v1 != null) {
			map.put(k1,  v1);
		}
		if(k2 != null && v2 != null) {
			map.put(k2,  v2);
		}
		if(k3 != null && v3 != null) {
			map.put(k3,  v3);
		}
		if(k4 != null && v4 != null) {
			map.put(k4,  v4);
		}
		if(k5 != null && v5 != null) {
			map.put(k5,  v5);
		}
		if(k6 != null && v6 != null) {
			map.put(k6,  v6);
		}
		if(k7 != null && v7 != null) {
			map.put(k7,  v7);
		}
		if(k8 != null && v8 != null) {
			map.put(k8,  v8);
		}
		return map;
	}

	public static <K, V> ArcherMap<K, V> of(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5,
            K k6, V v6, K k7, V v7, K k8, V v8, K k9, V v9) {
		ArcherMap<K, V> map = new ArcherMap<>();
		if(k1 != null && v1 != null) {
			map.put(k1,  v1);
		}
		if(k2 != null && v2 != null) {
			map.put(k2,  v2);
		}
		if(k3 != null && v3 != null) {
			map.put(k3,  v3);
		}
		if(k4 != null && v4 != null) {
			map.put(k4,  v4);
		}
		if(k5 != null && v5 != null) {
			map.put(k5,  v5);
		}
		if(k6 != null && v6 != null) {
			map.put(k6,  v6);
		}
		if(k7 != null && v7 != null) {
			map.put(k7,  v7);
		}
		if(k8 != null && v8 != null) {
			map.put(k8,  v8);
		}
		if(k9 != null && v9 != null) {
			map.put(k9,  v9);
		}
		return map;
	}
}
