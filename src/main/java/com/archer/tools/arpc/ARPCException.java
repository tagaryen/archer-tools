package com.archer.tools.arpc;

public class ARPCException extends RuntimeException {

	private static final long serialVersionUID = 724263452643283L;

	public ARPCException(String s) {
		super(s);
	}
	

	public ARPCException(String s, Exception e) {
		super(s, e);
	}
}
