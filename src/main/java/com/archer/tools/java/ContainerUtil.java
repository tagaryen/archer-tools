package com.archer.tools.java;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class ContainerUtil {
	
	public static boolean isEmpty(Collection<?> list) {
		return list == null || list.isEmpty();
	}
	
	public static boolean isNotEmpty(Collection<?> list) {
		return !isEmpty(list);
	}
	
	public static boolean isMapEmpty(Map<?, ?> map) {
		return map == null || map.isEmpty();
	}
	
	public static boolean isMapNotEmpty(Map<?, ?> map) {
		return !isMapEmpty(map);
	}

	public static <V, T> List<V> forEach(Collection<T> list, Function<? super T, ? extends V> trans) {
		if(list == null || trans == null) {
			throw new NullPointerException();
		}
		List<V> newList = new ArrayList<>();
		for(T t: list) {
			newList.add(trans.apply(t));
		}
		return newList;
	}
	
	public static <K, T> Map<K, T> toMap(Collection<T> list, Function<? super T, ? extends K> key) {
		if(list == null || key == null) {
			throw new NullPointerException();
		}
		Map<K, T> map = new HashMap<>();
		for(T t: list) {
			map.put(key.apply(t), t);
		}
		return map;
	}
	
	public static <K, V, T> Map<K, V> toMap(Collection<T> list, Function<? super T, ? extends K> key, Function<? super T, ? extends V> val) {
		if(list == null || key == null || val == null) {
			throw new NullPointerException();
		}
		Map<K, V> map = new HashMap<>();
		for(T t: list) {
			map.put(key.apply(t), val.apply(t));
		}
		return map;
	}

	public static <K, V, T> List<T> mapToList(Map<K, V> map, Function<? super V, ? extends T> trans) {
		if(map == null || trans == null) {
			throw new NullPointerException();
		}
		return forEach(map.values(), trans);
	}
	
	public static <T> List<T> find(Collection<T> list, Function<? super T, Boolean> trans) {
		if(list == null || trans == null) {
			throw new NullPointerException();
		}
		List<T> newList = new ArrayList<>();
		for(T t: list) {
			if(trans.apply(t)) {
				newList.add(t);
			}
		}
		return newList;
	}
	

	public static <T, V> List<V> forEachFind(Collection<T> list, Function<? super T, Boolean> ok, Function<? super T, ? extends V> trans) {
		if(list == null || ok == null || trans == null) {
			throw new NullPointerException();
		}
		List<V> newList = new ArrayList<>();
		for(T t: list) {
			if(ok.apply(t)) {
				newList.add(trans.apply(t));
			}
		}
		return newList;
	}

	public static <K, V> Map<K, V> newMap(K k1, V v1) {
		Map<K, V> map = new ArcherMap<>();
		if(k1 != null && v1 != null) {
			map.put(k1,  v1);
		}
		return map;
	}
	public static <K, V> Map<K, V> newMap(K k1, V v1, K k2, V v2) {
		Map<K, V> map = new ArcherMap<>();
		if(k1 != null && v1 != null) {
			map.put(k1,  v1);
		}
		if(k2 != null && v2 != null) {
			map.put(k2,  v2);
		}
		return map;
	}
	public static <K, V> Map<K, V> newMap(K k1, V v1, K k2, V v2, K k3, V v3) {
		Map<K, V> map = new ArcherMap<>();
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
	public static <K, V> Map<K, V> newMap(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4) {
		Map<K, V> map = new ArcherMap<>();
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
	public static <K, V> Map<K, V> newMap(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5) {
		Map<K, V> map = new ArcherMap<>();
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
	public static <K, V> Map<K, V> newMap(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5,
            K k6, V v6) {
		Map<K, V> map = new ArcherMap<>();
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
	public static <K, V> Map<K, V> newMap(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5,
            K k6, V v6, K k7, V v7) {
		Map<K, V> map = new ArcherMap<>();
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
	public static <K, V> Map<K, V> newMap(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5,
            K k6, V v6, K k7, V v7, K k8, V v8) {
		Map<K, V> map = new ArcherMap<>();
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
	
	public static <K, V> Map<K, V> newMap(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5,
            K k6, V v6, K k7, V v7, K k8, V v8, K k9, V v9) {
		Map<K, V> map = new ArcherMap<>();
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
