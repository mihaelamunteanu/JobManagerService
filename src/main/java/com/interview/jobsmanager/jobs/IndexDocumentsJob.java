package com.interview.jobsmanager.jobs;

import com.interview.jobsmanager.Job;

public class IndexDocumentsJob extends Job{
	private String folderToIndex = "\\";
	
	public IndexDocumentsJob() {
		super();
	}
	
	public IndexDocumentsJob(JobPriority jobPriority, String folderToIndex) {
		super(jobPriority);
		this.folderToIndex = folderToIndex;
	}
	
	public IndexDocumentsJob(String jobName, JobPriority jobPriority, String folderToIndex) {
		super(jobName, jobPriority);
		this.folderToIndex = folderToIndex;
	}

	@Override
	public void jobAction() throws Exception {
		System.out.println("The job executed is for INDEXING DOCUMENTS: " + folderToIndex);
		
		for(int i = 1; i<=1000;i++) { 
			System.out.println(this.getJobName() + " " + i);
			Thread.sleep((int)(Math.random()*10));
		}
	}
	
	@Override
	public void revertModifications() {
		
	}

}
