package com.interview.jobsmanager.jobs;

public enum JobPriority {
	HIGH(0), MEDIUM(1), LOW(2);
	
	private int prioNum;
	
	private JobPriority(int prioNum) {
		this.prioNum = prioNum;
	}

	public int getPrioNum() {
		return prioNum;
	}

}
