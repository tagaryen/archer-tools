package com.archer.tools.excel;

import java.util.List;

public class Sheet {
	
	private String name;
	
	private List<Row> rows;

	public Sheet(String name) {
		this.name = name;
	}

    public Sheet(String name, List<Row> rows) {
        this.name = name;
        this.rows = rows;
    }
	
	public String getName() {
		return name;
	}

	public List<Row> rows() {
		return rows;
	}

	public Sheet rows(List<Row> rows) {
		this.rows = rows;
		return this;
	}
}
