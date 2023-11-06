# Batch Util

<!-- toc -->

- [Delete specific batch job](#delete-specific-batch-job)
- [Reset batch db](#reset-batch-db)

<!-- tocstop -->

## Reset specific batch job

In order to rerun a specific job, to import the initial data, use the following script: 

```sql
DELETE from batch_job_execution_context where job_execution_id in (
    SELECT job_instance_id FROM batch_job_instance where job_name = '{job_name}'
    );
DELETE from batch_job_execution_params where job_execution_id in (
    SELECT job_instance_id FROM batch_job_instance where job_name = '{job_name}'
    );
DELETE from batch_step_execution_context where step_execution_id in (
    SELECT batch_step_execution.step_execution_id FROM batch_step_execution where job_execution_id in (
        SELECT job_instance_id FROM batch_job_instance where job_name = '{job_name}'
        )
    );
DELETE from batch_step_execution where job_execution_id in (
    SELECT job_instance_id FROM batch_job_instance where job_name = '{job_name}'
    );
DELETE from batch_job_execution where job_instance_id in (
    SELECT job_instance_id FROM batch_job_instance where job_name = '{job_name}'
    );
DELETE FROM batch_job_instance where job_name = '{job_name}';
```

## Reset all batch db

:warning: **If you use this script the all jobs are run from scratch!** Please consider to use the above script if you want to 
delete just a specific job. 

```sql
DELETE
FROM BATCH_STEP_EXECUTION_CONTEXT;
DELETE
FROM BATCH_STEP_EXECUTION;
DELETE
FROM BATCH_JOB_EXECUTION_PARAMS;
DELETE
FROM batch_job_execution_context;
DELETE
FROM BATCH_JOB_EXECUTION;
DELETE
FROM BATCH_JOB_INSTANCE;
DELETE
FROM import_process_item;
```