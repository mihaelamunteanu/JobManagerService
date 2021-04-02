package com.interview;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.TimeUnit;

import com.interview.jobsmanager.Job;
import com.interview.jobsmanager.JobContext;
import com.interview.jobsmanager.JobManagerService;
import com.interview.jobsmanager.jobs.DataLoadJob;
import com.interview.jobsmanager.jobs.IndexDocumentsJob;
import com.interview.jobsmanager.jobs.JobPriority;
import com.interview.jobsmanager.jobs.JobStatus;
import com.interview.jobsmanager.jobs.SendMailJob;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for  JobManagerApp.
 */
public class JobManagerAppTest extends TestCase {

	List<Job> jobsToBeInitiated = new ArrayList<Job>();
    private JobManagerService jobManagementService; // class under test

    @Override
    public void setUp() throws Exception {
		jobManagementService = new JobManagerService(10, 3, (jobC1, jobC2) -> {
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
		
		jobsToBeInitiated.add(loadFromDBJob1);
		jobsToBeInitiated.add(sendMailJob2);
		jobsToBeInitiated.add(indexFilesJob3);
		jobsToBeInitiated.add(loadFromDBJob4);
		
		int delayed = 0;
		for (Job job : jobsToBeInitiated) {
			jobManagementService.addJobToService(job, delayed++, 0, 0, TimeUnit.SECONDS);
		}
		
    }

    @Override
    public void tearDown() throws Exception {
    	jobManagementService.shutdownAndAwaitTermination();
    }
    
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public JobManagerAppTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( JobManagerAppTest.class );
    }

    /**
     * Rigourous Test :-)
     * @throws InterruptedException 
     */
    public void testJobsOrderByPriority()
    {
    	//test first the Queue is in the right order
    	PriorityBlockingQueue<JobContext> priorityBlockingQueueShallow = new PriorityBlockingQueue<>(jobManagementService.getQueue());
    	//start() wasn't called so first check if the Queue is in the expected order
    	assertTrue("The job with highest priority expected first", priorityBlockingQueueShallow.poll().getJob().getJobPriority().equals(JobPriority.HIGH));
    	assertTrue("The job with second highest priority expected second ", priorityBlockingQueueShallow.poll().getJob().getJobPriority().equals(JobPriority.HIGH));
    	assertTrue("The job with medium priority expected third", priorityBlockingQueueShallow.poll().getJob().getJobPriority().equals(JobPriority.MEDIUM));
    	assertTrue("The job with lowest priority expected last", priorityBlockingQueueShallow.poll().getJob().getJobPriority().equals(JobPriority.LOW));
    }
    
    public void testDelayedJobsStatusBeforeRunning() {
    	System.out.println("The status of the first job is uncertain at this point because it starts with no delay=immediatly, probably is already RUNNING, let's see: " + 
    				jobsToBeInitiated.get(0).getJobStatus());
    	assertTrue("Job Status should still be QUEUED as there is a delay before starting" , 
    			JobStatus.QUEUED.equals(jobsToBeInitiated.get(1).getJobStatus()));
    	assertTrue("Job Status should still be QUEUED as there is a delay before starting" , 
    			JobStatus.QUEUED.equals(jobsToBeInitiated.get(2).getJobStatus()));
    	assertTrue("Job Status should still be QUEUED as there is a delay before starting" , 
    			JobStatus.QUEUED.equals(jobsToBeInitiated.get(3).getJobStatus()));
    }
    	
    public void testJobsStatusAfterExecution() throws InterruptedException {
    	jobManagementService.start();
    	//test that when running the job manager only one of the following statuses are there: QUEUED, RUNNING, FAILED, SUCCES
    	Thread.sleep(6000);//enough time to have all threads in the pool running
    	assertTrue("Job Status should already be RUNNING OR SUCCES" , 
    			JobStatus.RUNNING.equals(jobsToBeInitiated.get(0).getJobStatus()) || (JobStatus.SUCCESS.equals(jobsToBeInitiated.get(0).getJobStatus())) );
    	assertTrue("Job Status should already be RUNNING OR FAILED" , 
    			JobStatus.RUNNING.equals(jobsToBeInitiated.get(1).getJobStatus()) || (JobStatus.FAILED.equals(jobsToBeInitiated.get(1).getJobStatus())) );
    	assertTrue("Job Status should already be RUNNING OR SUCCES" , 
    			JobStatus.RUNNING.equals(jobsToBeInitiated.get(2).getJobStatus()) || (JobStatus.SUCCESS.equals(jobsToBeInitiated.get(2).getJobStatus())) );
    	assertTrue("Job Status should already be RUNNING OR SUCCES" , 
    			JobStatus.RUNNING.equals(jobsToBeInitiated.get(3).getJobStatus()) || (JobStatus.SUCCESS.equals(jobsToBeInitiated.get(3).getJobStatus())) );
    }
}
