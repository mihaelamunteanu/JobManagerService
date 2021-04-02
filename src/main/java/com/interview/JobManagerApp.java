package com.interview;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.interview.jobsmanager.Job;
import com.interview.jobsmanager.JobManagerService;
import com.interview.jobsmanager.jobs.DataLoadJob;
import com.interview.jobsmanager.jobs.IndexDocumentsJob;
import com.interview.jobsmanager.jobs.JobPriority;
import com.interview.jobsmanager.jobs.SendMailJob;

/**
 * Entry point to call entryJobs.jobManagementService.
 * 
 * @author Mihaela Munteanu
 * @since 02.04.2021
 *
 */
public class JobManagerApp {
	List<Job> jobsToBeInitiated = new ArrayList<Job>();
	JobManagerService jobManagementService;

	public static void main(String[] args) throws InterruptedException {
		JobManagerApp entryJobs = new JobManagerApp();
		entryJobs.jobManagementService = new JobManagerService(20, 5, (jobC1, jobC2) -> {
			if (jobC1.getJob().getJobPriority().equals(jobC2.getJob().getJobPriority())) {
				//if the same priority, get the order it was introduced
				return jobC1.getJobNumber() - jobC2.getJobNumber(); 
			} else {
				return jobC1.getJob().getJobPriority().getPrioNum() - jobC2.getJob().getJobPriority().getPrioNum();
			}
		});
		// or JobManagementService jobManagementService = new JobManagementService(20, 5, new JobComparator());
		
		Job loadFromDBJob1 =  new DataLoadJob("DataLoad1", JobPriority.LOW);
		Job sendMailJob2 = new SendMailJob("SendMail2", JobPriority.HIGH, "munteanumihaela@gmail.com", "#thepass", 
				"TEST_MAIL", "This is a message to check the JobManager Service. \n Thank you for your patience.", 
				"pyoneer@pyoneer.com");
		Job indexFilesJob3 = new IndexDocumentsJob("IndexFile3", JobPriority.HIGH, "temp");
		Job loadFromDBJob4 =  new DataLoadJob("DataLoad4", JobPriority.MEDIUM);
		
		
		entryJobs.jobsToBeInitiated.add(loadFromDBJob1);
		entryJobs.jobsToBeInitiated.add(sendMailJob2);
		entryJobs.jobsToBeInitiated.add(indexFilesJob3);
		entryJobs.jobsToBeInitiated.add(loadFromDBJob4);
		
		// if a task should be executed immediately the initialDelay should be 0 and other int params have to be zero
		//addJobToService(Job job, int initialDelay, int fixedRate, int fixedDelay, TimeUnit timeUnit)
		
		for (Job job : entryJobs.jobsToBeInitiated) {
			entryJobs.jobManagementService.addJobToService(job);
		}
		
//		entryJobs.jobManagementService.addJobToService(indexFilesJob3, 0, 0, 0,  TimeUnit.SECONDS);
//		entryJobs.jobManagementService.addJobToService(loadFromDBJob4, 0, 0, 0, TimeUnit.SECONDS);
//		entryJobs.jobManagementService.addJobToService(loadFromDBJob1, 0, 0, 0, TimeUnit.SECONDS);
//		entryJobs.jobManagementService.addJobToService(loadFromDBJob1, 0, 0, 0, TimeUnit.SECONDS);
		
		entryJobs.sleepMainThreadAndCheckStatus(2, TimeUnit.SECONDS);
		
		entryJobs.jobManagementService.shutdownAndAwaitTermination();
	}
	
	private void sleepMainThreadAndCheckStatus(int sleepTime, TimeUnit timeUnit) throws InterruptedException {
		
		for (int i=0; i<jobsToBeInitiated.size(); i++) {
			timeUnit.sleep(sleepTime);//if too early then there is no time to 
			System.out.println(jobsToBeInitiated.get(i).getJobName() + " " + jobsToBeInitiated.get(i).getJobStatus());
		}
	}

}
