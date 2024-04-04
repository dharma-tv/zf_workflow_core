package com.zanflow.bam.dto;

public class BarChartDataSubset {
	
	String label;
	int completed=0;
	int inprogress=0;
	int terminated=0;
	
	
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public int getCompleted() {
		return completed;
	}
	public void setCompleted(int completed) {
		this.completed = completed;
	}
	public int getInprogress() {
		return inprogress;
	}
	public void setInprogress(int inprogress) {
		this.inprogress = inprogress;
	}
	public int getTerminated() {
		return terminated;
	}
	public void setTerminated(int terminated) {
		this.terminated = terminated;
	}
	
	
	

}
