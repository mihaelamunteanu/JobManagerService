# Job Manager System

A Job Management System that accepts jobs with a given schedule or immediate. The jobs have to extend the _Job_ abstract class. 
Assumption was made that Scheduling and Immediate tasks have to be handled by the same JobManager and Jobs can come in all kinds of flavor. 

## Flexibility
The new types of Jobs are unknown to the Manager System. 
Every time a new job is needed it has to extend the Job and implement it's abstract methods:

_jobAction()_ where the actual job resides (sending mail etc) and _revertModifications()_ in case of failure if Connections need to be closed, files deleted because they were used in the jobAction and it failed


## Reliability
Each Job should either complete successfully or perform no action at all. 
The actions for jobs should be in transaction for example for DB (all or nothing), setters are not provided so that the object can not be modified from the outside and in case anything is modified during action on catch _revertModifications()_ is called and it should reset the status of the Job etc. 
 
## Internal Consistency
At any one time a Job has one of four states: **QUEUED, RUNNING, SUCCESS, FAILED**. 
Enum is used. 

## Priority (Optional)
Each Job can be executed based on its priority relative to other Jobs. Enum used for Job Priority and provided to the Comparator which uses the Priority and for the same priority the order that was used to add jobs to the manager.
To make sure the priority is respected a PriorityBlockedQueue is used. A single Thread consumes from it and delegates further to a SchedulerExecutorService or single thread ExecutorService.

## Scheduling
Depending on the provided request for the job: delay, fixedRate, fixedDelay the manager will consider if that is a Scheduler or a a simple ThreadPool. To make sure that jobs are started in the order. For embedding the info about schedule a JobConext is used.

The sizePool of the SchedulerExecutorService and ExecutorService for immediate jobs can be configured.
The email addresses (from, to), content, title to be parsed. 
The same JOB can be run multiple times with different schedule params.

As an improvement putting the elements in the PriorityQueue from another thread, other than Main. Proposed with ExecutorService Single Thread to be Able to keep the priority in the Queue and not get random results. 

# See the diagram below for an overview of the system: 

![JobManagerSystemDiagram](/resources/diagram.png)
