package com.interview.jobsmanager;

import java.util.Comparator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * JobManager - the jobs will be submitted to this class. 
 * This Manager can handle any type of Job without caring about the jobType. 
 * If a new Job has to be handled it will have to extend abstract Job class.
 * 
 * It will be each job's job to ensure a transact in case of DB and 
 * that no more than one action that creates side effect is done in the end of the function.
 * For example sending and email and then sending another email in the same job is assumed is not allowed.
 * 
 * @author Mihaela Munteanu
 * @since 31st 03.2021
 */
public class JobManagerService {
	
	//will be used only when executor is ThreadPool and not Schedule
	private PriorityBlockingQueue<JobContext> queue;
	
	private ExecutorService mainExecutorService = Executors.newSingleThreadExecutor();
	private ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(5);
	private ExecutorService immediateJobsExecutorService = Executors.newFixedThreadPool(5);
	
	private boolean shutdownAsked = false;
	private int jobIndex = 0;

	public JobManagerService(int intialQueueCapacity, int noCorePoolThreads, Comparator<JobContext> jobComparator) {
		this.scheduledExecutorService = Executors.newScheduledThreadPool(noCorePoolThreads);
		this.immediateJobsExecutorService = Executors.newFixedThreadPool(noCorePoolThreads);
		
		mainExecutorService = Executors.newSingleThreadExecutor();
		queue = new PriorityBlockingQueue<JobContext>(intialQueueCapacity, jobComparator);
	}

	
	//the servce starts consumming from the queue when it told start, start() can be called at any time after construction even if queue is empty
	public void start() {
		mainExecutorService.execute(() -> {
			while (!shutdownAsked) {
//	            try { //not needed if take is not used
	            	//to check what happens if queue is empty
	            	JobContext jobContext = queue.poll();//take(); we do not make it wait as it comes again in the while to check
	            	if (jobContext != null) {
	            		if (jobContext.getFixedRate() != 0) {
	            			scheduledExecutorService.scheduleAtFixedRate(
	            					jobContext.getJob(), jobContext.getDelay(), jobContext.getFixedRate(), jobContext.getTimeUnit());
	            		} else if (jobContext.getFixedDelay() != 0) {
	            			scheduledExecutorService.scheduleWithFixedDelay(
	            					jobContext.getJob(), jobContext.getDelay(), jobContext.getFixedDelay(), jobContext.getTimeUnit());
	            		} else if (jobContext.getFixedDelay() != 0) {
	            			scheduledExecutorService.schedule(jobContext.getJob(), jobContext.getDelay(), jobContext.getTimeUnit());
	            		} else {
	            			immediateJobsExecutorService.execute(jobContext.getJob());
	            		}
	            	}
	            	
//	            } catch (InterruptedException e) {
//	                // log - handle accordingly
//	                e.printStackTrace();;
//	            }
			}
		});
	}
	
	public void addJobToService(Job job, int initialDelay, int fixedRate, int fixedDelay, TimeUnit timeUnit) {
		//assuming there are more threads adding to the queue the one that is fastest gets the first number for jobIndex;
		queue.put(new JobContext(job, jobIndex++, initialDelay, fixedRate, fixedDelay, timeUnit));
	}
	
	
	public void addJobToService(Job job) {
		//if no info about time is provided 
		queue.put(new JobContext(job, jobIndex++, 0, 0, 0, TimeUnit.SECONDS));
	}
	
	//from java doc - https://docs.oracle.com/javase/7/docs/api/java/util/concurrent/ExecutorService.html
	public void shutdownAndAwaitTermination() {
		 shutdownAsked = true;
		 mainExecutorService.shutdown(); // Disable new tasks from being submitted
		 scheduledExecutorService.shutdown();
		 immediateJobsExecutorService.shutdown();
		 
		   try {
		     // Wait a while for existing tasks to terminate
		     if (!mainExecutorService.awaitTermination(60, TimeUnit.SECONDS)) {
		    	 
		    	 mainExecutorService.shutdownNow(); // Cancel currently executing tasks
		    	 scheduledExecutorService.shutdown();
		    	 immediateJobsExecutorService.shutdown();
		    	 
		       // Wait a while for tasks to respond to being cancelled
		       if (!mainExecutorService.awaitTermination(60, TimeUnit.SECONDS))
		           System.err.println("Pool did not terminate");
		     }
		   } catch (InterruptedException ie) {
		     // (Re-)Cancel if current thread also interrupted
			   mainExecutorService.shutdownNow();
			   scheduledExecutorService.shutdown();
			   immediateJobsExecutorService.shutdown();
			   
		     // Preserve interrupt status
		     Thread.currentThread().interrupt();
		   }
	}

	public PriorityBlockingQueue<JobContext> getQueue() {
		return queue; //peek does not remove head
	}

}