package com.interview.jobsmanager;

import java.util.Comparator;

/**
 * Comparator for JOBs. 
 * At this point the priority is calculated depending on the defined priority and on the time scheduled.
 * If the comparison of the Jobs shoud be done differently this is where if should e modified.
 * 
 *  
 * @author Mihaela Munteanu
 * @since 29th March 2021
 *
 */
public class JobContextComparator implements Comparator<JobContext>{

	public int compare(JobContext jobC1, JobContext jobC2) {
		if (jobC1.getJob().getJobPriority().equals(jobC2.getJob().getJobPriority())) {
			//if the same priority, get the order it was introduced
			return jobC1.getJobNumber() - jobC2.getJobNumber(); 
		} else {
			return jobC1.getJob().getJobPriority().getPrioNum() - jobC2.getJob().getJobPriority().getPrioNum();
		}
	}

}
