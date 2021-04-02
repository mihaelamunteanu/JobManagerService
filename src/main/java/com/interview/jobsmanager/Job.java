package com.interview.jobsmanager;

import java.util.Comparator;

import com.interview.jobsmanager.jobs.JobPriority;
import com.interview.jobsmanager.jobs.JobStatus;

/**
 * Abstract JOB class to be extended by each Job implementation.
 * 
 * @author Mihaela Munteanu
 * @since 29th March 2021
 *
 */
public abstract class Job implements Runnable, Comparator<Job> {
	private String jobName;
	private JobStatus jobStatus = JobStatus.QUEUED; //the only reasonable default state from the four(QUEUED, SUCCES, RUNNING, FAILED)
	private JobPriority jobPriority = JobPriority.MEDIUM; //assumed by default the priority is MEDIUM
	
	public void run() {
//no one should come and modify the status - but there's no possibility to modify the status as it is not accesible from outside.
//		synchronized (this.jobStatus) {
			try {
				this.jobStatus = JobStatus.RUNNING;
				jobAction();
				this.jobStatus = JobStatus.SUCCESS;
			} catch (Exception exception) {//InterruptedException
				this.jobStatus = JobStatus.FAILED;
				System.out.println(this.jobName + " failed.");
				revertModifications();
			}
//		}
	};
	
	public abstract void jobAction() throws Exception;
	
	//in case of failing it should be checked what was done and reverted - e.g. a file delete, if data was loaded into a file 
	public abstract void revertModifications(); 
	
	public int compare(Job o1, Job o2) {
		return 0;
	}
	
	public Job() {}

	public Job(JobPriority jobPriority) {
		this.jobPriority = jobPriority;
	}
	
	public Job(String jobName, JobPriority jobPriority) {
		this.jobName = jobName;
		this.jobPriority = jobPriority;
	}
	
	public Job(int jobNumber, String jobName, JobPriority jobPriority) {
		super();
		this.jobName = jobName;
		this.jobPriority = jobPriority;
	}
	
	public String getJobName() {
		return jobName;
	}
	
	public JobStatus getJobStatus() {
		return jobStatus;
	}

	public JobPriority getJobPriority() {
		return jobPriority;
	}
}
