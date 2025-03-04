# Batch Util

<!-- toc -->

- [Reset specific batch job](#reset-specific-batch-job)
- [Reset all batch db](#reset-all-batch-db)

<!-- tocstop -->

## Reset a specific batch job

To rerun a specific job, to import the initial data, use the following script: 

```sql
DELETE from batch_job_execution_context where job_execution_id in (
    select job_execution_id from batch_job_execution where job_instance_id in (
        SELECT job_instance_id FROM batch_job_instance where job_name = '{{jobname}}'
    )
);
DELETE from batch_job_execution_params where job_execution_id in (
    select job_execution_id from batch_job_execution where job_instance_id in (
        SELECT job_instance_id FROM batch_job_instance where job_name = '{{jobname}}'
    )
);
DELETE from batch_step_execution_context where step_execution_id in (
    SELECT batch_step_execution.step_execution_id FROM batch_step_execution where job_execution_id in (
        select job_execution_id from batch_job_execution where job_instance_id in (
            SELECT job_instance_id FROM batch_job_instance where job_name = '{{jobname}}'
        )
    )
);
DELETE from batch_step_execution where job_execution_id in (
    select job_execution_id from batch_job_execution where job_instance_id in (
        SELECT job_instance_id FROM batch_job_instance where job_name = '{{jobname}}'
    )
);
DELETE from batch_job_execution where job_instance_id in (
    SELECT job_instance_id FROM batch_job_instance where job_name = '{{jobname}}'
);
DELETE FROM batch_job_instance where job_name = '{{jobname}}';
```

## Reset all batch db

:warning: **If you use this script, all jobs are run from scratch!**
Please consider using the above script if you want to 
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
FROM bulk_import;
DELETE
FROM bulk_import_log;
DELETE
FROM bulk_import_log_seq;
DELETE
FROM geo_update_import_process_item;
DELETE
FROM permission_restriction;
```