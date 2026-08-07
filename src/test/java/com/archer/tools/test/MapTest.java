package com.archer.tools.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.archer.tools.java.ArcherList;
import com.archer.tools.java.ArcherMap;

public class MapTest {

	static void testArcherMap() {
        ArcherMap<String, String> map = new ArcherMap<>();
		long t1 = System.currentTimeMillis();
		for(int i = 1; i < 100001; i++) {
			if(i % 7 == 0) {
				map.clear();
			}
			map.put("xuyi" + i, "haoshuai" + i);
		}

		System.out.println("archerMap cost = " + (System.currentTimeMillis() - t1));
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
        HashMap<String, String> map = new HashMap<>();
        long t1 = System.currentTimeMillis();
        for(int i = 1; i < 100001; i++) {
			if(i % 7 == 0) {
				map.clear();
			}
			map.put("xuyi" + i, "haoshuai" + i);
		}
		System.out.println("hashMap cost = " + (System.currentTimeMillis() - t1));
		System.out.println("hashMap get = " + map.getOrDefault("xuyi100000", "none"));
		
	}


    static void testArcherList() {
        List<String> l = new ArcherList<>();
        long t1 = System.currentTimeMillis();
        for(int i = 1; i < 100001; i++) {
            if(i % 13 == 0) {
                l.clear();
            }
            l.add("haoshuai" + i);
        }

        System.out.println("archerList cost = " + (System.currentTimeMillis() - t1));
        System.out.println(l);
    }

    static void testArrayList() {
        List<String> l = new ArrayList<>();
        String s = null;
        long t1 = System.currentTimeMillis();
        for(int i = 1; i < 100001; i++) {
            if(i % 13 == 0) {
                l.clear();
            }
            l.add("haoshuai" + i);
        }
        System.out.println("arrayList cost = " + (System.currentTimeMillis() - t1));
        System.out.println("arrayList get = " + l.get(1) + "; " + l.get(2));

    }
	
	public static void main(String args[]) {
        testArcherList();
		testHashMap();
		testArcherMap();

//        testArrayList();
//        testArcherList();
	}
}
