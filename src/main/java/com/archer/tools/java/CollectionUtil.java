package com.archer.tools.java;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class CollectionUtil {
	
	public static boolean isEmpty(Collection<?> list) {
		return list == null || list.isEmpty();
	}
	
	public static boolean isMapEmpty(Map<?, ?> map) {
		return map == null || map.isEmpty();
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
}
