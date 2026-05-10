package com.archer.tools.test.bytecode;

public class SimpleClass {

	private String a;

	public String getA() {
		return a;
	}

	public void setA(String a) {
		this.a = a;
	}
	
	public static class SimpleInner {

		private String b;

		public String getB() {
			return b;
		}

		public void setB(String b) {
			this.b = b;
		}
		
	}
	
}
