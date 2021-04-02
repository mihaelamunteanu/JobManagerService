package com.interview.jobsmanager;

import java.util.concurrent.TimeUnit;
/**
 * A context for when a Job is needs to be run by a thread more info about when to run it
 * 	 the order that was provided to the JobManager
 *   the initial delay before running the job, if it is the case //zero means immediate
 *   the fixedRate if it is the case
 *   the fixedDelay if the case
 *   the time unit for these 
 *   
 * @author Mihaela Munteanu
 * @since 02.04.2021
 */
public class JobContext {
	private int jobNumber; //we use this to know the order where the job was provided
	private int delay; 
	private int fixedRate;
	private int fixedDelay;
	private TimeUnit timeUnit;
	
	private Job job; 
	
	public JobContext(Job job, int jobNumber, int delay, int fixedRate, int fixedDelay, TimeUnit timeUnit) {
		super();
		this.jobNumber = jobNumber;
		this.delay = delay;
		this.fixedRate = fixedRate;
		this.fixedDelay = fixedDelay;
		this.job = job;
		this.timeUnit = timeUnit;
	}

	public int getDelay() {
		return delay;
	}

	public int getFixedRate() {
		return fixedRate;
	}

	public int getFixedDelay() {
		return fixedDelay;
	}

	public int getJobNumber() {
		return jobNumber;
	}

	public Job getJob() {
		return job;
	}

	public TimeUnit getTimeUnit() {
		return timeUnit;
	}

}
