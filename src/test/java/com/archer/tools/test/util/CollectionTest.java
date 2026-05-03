package com.archer.tools.test.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.archer.tools.java.ContainerUtil;
import com.archer.xjson.XJSONStatic;

public class CollectionTest {
	

	public static void main(String args[]) {
		List<ClassA> test = new ArrayList<>();
		for(int i = 0; i < 10; i++) {
			test.add(new ClassA());
		}
		
		Map<String, Integer> map = ContainerUtil.toMap(test, ClassA::getName, (t) -> {
			return t.getAge() + 10;
		});
		
		System.out.println(XJSONStatic.stringify(map));
		
		Map<String, ClassA> map2 = ContainerUtil.toMap(test, ClassA::getName);

		System.out.println(XJSONStatic.stringify(map2));
	}

}
