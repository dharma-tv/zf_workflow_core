package com.zanflow.bam.dto;

import java.io.Serializable;

public class BAMDetailDTO extends ResponseDTO implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	protected int totalCount;

	protected int inProgressCount;
	
	protected int completedCount;
	
	protected int terminatedCount;
	
	protected BarChartData chartData;
	
	public BarChartData getChartData() {
		return chartData;
	}

	public void setChartData(BarChartData chartData) {
		this.chartData = chartData;
	}

	public int getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}

	public int getInProgressCount() {
		return inProgressCount;
	}

	public void setInProgressCount(int inProgressCount) {
		this.inProgressCount = inProgressCount;
	}

	public int getCompletedCount() {
		return completedCount;
	}

	public void setCompletedCount(int completedCount) {
		this.completedCount = completedCount;
	}

	public int getTerminatedCount() {
		return terminatedCount;
	}

	public void setTerminatedCount(int terminatedCount) {
		this.terminatedCount = terminatedCount;
	}
	
	
	
}
