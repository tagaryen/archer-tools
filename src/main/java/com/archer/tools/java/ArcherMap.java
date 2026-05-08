package com.archer.tools.java;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class ArcherMap<K, V> implements Map<K, V> {
	
	private int cap, size;
	private Object[] keys, vals;
	
	public ArcherMap() {
		cap = 16;
		keys = new Object[cap];
		vals = new Object[cap];
	}
	
	public ArcherMap(int cap) {
		if(cap <= 0) {
			throw new IllegalArgumentException("cap must be bigger than zero");
		}
		this.cap = cap;
		keys = new Object[cap];
		vals = new Object[cap];
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
	public boolean containsKey(Object key) {
		for(int i = 0; i < size; i++) {
			if(keys[i].equals(key)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean containsValue(Object value) {
		for(int i = 0; i < size; i++) {
			if(vals[i].equals(value)) {
				return true;
			}
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	@Override
	public V get(Object key) {
		for(int i = 0; i < size; i++) {
			if(keys[i].equals(key)) {
				return (V)vals[i];
			}
		}
		return null;
	}

	@Override
	@SuppressWarnings("unchecked")
	public V put(K key, V value) {
		if(key == null) {
			throw new NullPointerException();
		}
		for(int i = 0; i < size; i++) {
			if(keys[i].equals(key)) {
				V old = (V)vals[i];
				vals[i] = value;
				return old;
			}
		}
		if(size >= cap) {
			cap *= 2;
			Object[] nk = new Object[cap];
			Object[] nv = new Object[cap];
			System.arraycopy(keys, 0, nk, 0, size);
			System.arraycopy(vals, 0, nv, 0, size);
			keys = nk;
			vals = nv;
		}
		keys[size] = key;
		vals[size++] = value;
		return null;
	}

	@Override
	@SuppressWarnings("unchecked")
	public V remove(Object key) {
		int i = 0;
		for(; i < size; i++) {
			if(keys[i].equals(key)) {
				break;
			}
		}
		if(i == size) {
			return null;
		}
		V old = (V) vals[i];
		keys[i] = null;
		vals[i] = null;

		for(; i < size - 1; i++) {
			keys[i] = keys[i+1];
			vals[i] = vals[i+1];
		}
		return old;
	}

	@Override
	public void putAll(Map<? extends K, ? extends V> m) {
		for(Entry<? extends K, ? extends V> en : m.entrySet()) {
			put(en.getKey(), en.getValue());
		}
	}

	@Override
	public void clear() {
		size = 0;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Set<K> keySet() {
		return new KeySet<K>((K[])Arrays.copyOfRange(keys, 0, size));
	}

	@SuppressWarnings("unchecked")
	@Override
	public Collection<V> values() {
		return Arrays.asList((V[])Arrays.copyOfRange(vals, 0, size));
	}

	@SuppressWarnings("unchecked")
	@Override
	public Set<Map.Entry<K, V>> entrySet() {
		return new PairSet<K, V>((K[])Arrays.copyOfRange(keys, 0, size), (V[])Arrays.copyOfRange(vals, 0, size));
	}

	@SuppressWarnings("hiding")
	final class KeySet<K> implements Set<K> {

		K[] keys;
		
		public KeySet(K[] keys) {
			this.keys = keys;
		}
		
		@Override
		public int size() {
			return keys.length;
		}

		@Override
		public boolean isEmpty() {
			return keys.length == 0;
		}

		@Override
		public boolean contains(Object o) {
			for(K k: keys) {
				if(k.equals(o)) {
					return true;
				}
			}
			return false;
		}

		@Override
		public Iterator<K> iterator() {
			return new KetIterator<K>(keys);
		}

		@Override
		public K[] toArray() {
			return keys;
		}

		@SuppressWarnings("unchecked")
		@Override
		public K[] toArray(Object[] a) {
	        throw new UnsupportedOperationException();
		}

		@Override
		public boolean add(Object e) {
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
		public boolean addAll(@SuppressWarnings("rawtypes") Collection c) {
	        throw new UnsupportedOperationException();
		}

		@Override
		public boolean retainAll(@SuppressWarnings("rawtypes") Collection c) {
	        throw new UnsupportedOperationException();
		}

		@Override
		public boolean removeAll(@SuppressWarnings("rawtypes") Collection c) {
	        throw new UnsupportedOperationException();
		}

		@Override
		public void clear() {
	        throw new UnsupportedOperationException();
		}
		
	}
	
	@SuppressWarnings("hiding")
	final class KetIterator<K> implements Iterator<K> {

		K[] keys;
		int i;
		
		public KetIterator(K[] keys) {
			this.keys = keys;
			this.i = 0;
		}
		
		@Override
		public boolean hasNext() {
			return i < keys.length;
		}

		@Override
		public K next() {
			return keys[i++];
		}
		
	}
	
	@SuppressWarnings("hiding")
	final class PairSet<K, V> implements Set<Map.Entry<K, V>> {

		K[] keys;
		V[] vals;
		
		public PairSet(K[] keys, V[] vals) {
			this.keys = keys;
			this.vals = vals;
		}
		
		@Override
		public int size() {
			return keys.length;
		}

		@Override
		public boolean isEmpty() {
			return keys.length == 0;
		}

		@Override
		public boolean contains(Object o) {
	        throw new UnsupportedOperationException();
		}

		@Override
		public Iterator<Map.Entry<K, V>> iterator() {
			return new PairIt<K, V>(keys, vals);
		}

		@SuppressWarnings("rawtypes")
		@Override
		public Pair[] toArray() {
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
	
	@SuppressWarnings("hiding")
	final class PairIt<K, V> implements Iterator<Map.Entry<K, V>> {

		int i = 0;
		K[] keys;
		V[] vals;
		
		public PairIt(K[] keys, V[] vals) {
			this.keys = keys;
			this.vals = vals;
		}
		
		@Override
		public boolean hasNext() {
			return i < keys.length;
		}

		@Override
		public Entry<K, V> next() {
			return new Pair<K, V>(keys[i], vals[i++]);
		}
		
	}
	
	@SuppressWarnings("hiding")
	final class Pair<K, V> implements Map.Entry<K, V> {

		K k;
		V v;
		
		public Pair(K k, V v) {
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
		return new Pair<K, V>((K)keys[index], (V)vals[index]);
	}
	@SuppressWarnings("unchecked")
	public K getKeyByIndex(int index) {
		if(index >= size || index < 0) {
			return null;
		}
		return (K)keys[index];
	}
	@SuppressWarnings("unchecked")
	public V getValueByIndex(int index) {
		if(index >= size || index < 0) {
			return null;
		}
		return (V)vals[index];
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
