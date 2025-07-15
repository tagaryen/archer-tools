package com.archer.tools.http.client;

import java.util.ArrayList;
import java.util.List;

import com.archer.net.http.multipart.Multipart;

public class FormData {
	
	private List<Multipart> parts;
	
	public FormData() {
		this.parts = new ArrayList<>();
	}
	
	public void put(String key, String value) {
		parts.add(new Multipart(key, value));
	}

	public void putFile(String key, String fileName, byte[] content) {
		String[] fns = fileName.split(".");
		String contentType;
		if(fns.length > 1) {
			contentType = "application/"+ fns[fns.length-1];
		} else {
			contentType = "application/none";
		}
		parts.add(new Multipart(key, fileName, content, contentType));
	}
	
	public List<Multipart> getMultiparts() {
		return parts;
	}
}
