package com.archer.tools.test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.archer.tools.java.ArcherList;
import com.archer.tools.java.ArcherMap;

public class MapTest {

	static void testArcherMap() {
		Map<String, String> map = new ArcherMap<>();
        String s = null;
		long t1 = System.currentTimeMillis();
		for(int i = 1; i < 100001; i++) {
            s = map.get("xuyi99998");
			if(i % 13 == 0) {
				map.clear();
			}
			map.put("xuyi" + i, "haoshuai" + i);
		}

		System.out.println("archerMap cost = " + (System.currentTimeMillis() - t1));
        System.out.println("s: " + s);
        List<String> l = new ArcherList<>(map.values());
        l.add("xuyihaoshuai");
        l.set(2, "xuyihao2");
        l.add(2, "xuyihaoadd2");
        System.out.println("contains: " + l.contains("haoshuai99998"));
        System.out.println("contains: " + l.contains("haoshuai99999"));
        l.remove("haoshuai100000");
        for(String st: l) {
            System.out.println("--->: " + st);
        }
        System.out.println("get: " + l.get(3));

        System.out.println("archerMap get = " + map.getOrDefault("xuyi100000", "none"));
		
	}
	
	static void testHashMap() {
		Map<String, String> map = new HashMap<>();
        String s = null;
        long t1 = System.currentTimeMillis();
        for(int i = 1; i < 100001; i++) {
            s = map.get("xuyi99998");
			if(i % 13 == 0) {
				map.clear();
			}
			map.put("xuyi" + i, "haoshuai" + i);
		}
		System.out.println("hashMap cost = " + (System.currentTimeMillis() - t1));
        System.out.println("s: " + s);
		System.out.println("hashMap get = " + map.getOrDefault("xuyi100000", "none"));
		
	}
	
	public static void main(String args[]) {
		testHashMap();
		testArcherMap();
	}
}
