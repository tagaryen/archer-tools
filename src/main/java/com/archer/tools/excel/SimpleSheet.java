package com.archer.tools.excel;

import java.util.List;

public class SimpleSheet {

	
	private String name;
	
	private List<List<String>> rows;

	public SimpleSheet(String name) {
		this.name = name;
	}

    public SimpleSheet(String name, List<List<String>> rows) {
        this.name = name;
        this.rows = rows;
    }
	
	public String getName() {
		return name;
	}

	public List<List<String>> rows() {
		return rows;
	}

	public SimpleSheet rows(List<List<String>> rows) {
		this.rows = rows;
		return this;
	}
}
