package com.interview.jobsmanager.jobs;

import com.interview.jobsmanager.Job;

public class DataLoadJob extends Job {
	public DataLoadJob() {
		super();
	}
	
	public DataLoadJob(JobPriority jobPriority) {
		super(jobPriority);
	}
	
	public DataLoadJob(String jobName, JobPriority jobPriority) {
		super(jobName, jobPriority);
	}

	@Override
	public void jobAction() throws Exception {
		System.out.println("The job executed is for LOADING FROM WebService or DB: ");
		Thread.sleep((int)(Math.random()*1000));
		for(int i = 1; i<=1000;i++) {
			System.out.println(this.getJobName() + " " + i);
			Thread.sleep((int)(Math.random()*10));
		}
	}

	@Override
	public void revertModifications() {
		
	}
}
