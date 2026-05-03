package com.archer.tools.test;

import java.util.HashMap;
import java.util.Map;

import com.archer.tools.java.ArcherMap;

public class MapTest {

	static void testArcherMap() {
		Map<String, String> map = new ArcherMap<>();
		long t1 = System.currentTimeMillis();
		for(int i = 1; i < 100001; i++) {
			if(i % 5 == 0) {
				map.clear();
			}
			map.put("xuyi", "haoshuai");
		}
		System.out.println("archerMap cost = " + (System.currentTimeMillis() - t1));
		System.out.println("archerMap get = " + map.getOrDefault("xuyi", "none"));
		
	}
	
	static void testHashMap() {
		Map<String, String> map = new HashMap<>();
		long t1 = System.currentTimeMillis();
		for(int i = 1; i < 100001; i++) {
			if(i % 5 == 0) {
				map.clear();
			}
			map.put("xuyi", "haoshuai");
		}
		System.out.println("hashMap cost = " + (System.currentTimeMillis() - t1));
		System.out.println("hashMap get = " + map.getOrDefault("xuyi", "none"));
		
	}
	
	public static void main(String args[]) {
		testHashMap();
		testArcherMap();
	}
}
