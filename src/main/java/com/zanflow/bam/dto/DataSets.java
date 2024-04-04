package com.zanflow.bam.dto;

public class DataSets {
	
	private int[] data;
	private String label;
	private String backgroundColor;
	
	public DataSets() {
		
	}
	public DataSets(String lable, String backgroundColor,int[] data) {
		this.label=lable;this.data=data;this.backgroundColor=backgroundColor;
	}
	public int[] getData() {
		return data;
	}
	public void setData(int[] data) {
		this.data = data;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public String getBackgroundColor() {
		return backgroundColor;
	}
	public void setBackgroundColor(String backgroundColor) {
		this.backgroundColor = backgroundColor;
	}
	
}
