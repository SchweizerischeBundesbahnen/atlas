package ch.sbb.exportservice.service;

import ch.sbb.atlas.batch.exception.JobExecutionException;
import ch.sbb.exportservice.model.ExportFileName;
import ch.sbb.exportservice.model.ExportType;
import ch.sbb.exportservice.utils.JobDescriptionConstants;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.stereotype.Component;

import static ch.sbb.exportservice.utils.JobDescriptionConstants.EXPORT_FILE_NAME_JOB_PARAMETER;
import static ch.sbb.exportservice.utils.JobDescriptionConstants.EXPORT_TYPE_JOB_PARAMETER;

@Slf4j
@Component
@AllArgsConstructor
public abstract class BaseExportJobService {


  protected final JobLauncher jobLauncher;

  protected void startExportJob(ExportType exportType, ExportFileName exportFileName, Job job) {
    JobParameters jobParameters = new JobParametersBuilder()
        .addString(JobDescriptionConstants.EXECUTION_TYPE_PARAMETER, JobDescriptionConstants.EXECUTION_BATCH_PARAMETER)
        .addString(EXPORT_TYPE_JOB_PARAMETER, exportType.toString())
        .addString(EXPORT_FILE_NAME_JOB_PARAMETER, exportFileName.toString())
        .addLong(JobDescriptionConstants.START_AT_JOB_PARAMETER, System.currentTimeMillis()).toJobParameters();
    try {
      JobExecution execution = jobLauncher.run(job, jobParameters);
      log.info("Job executed with status: {}", execution.getExitStatus().getExitCode());
    } catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException |
             JobParametersInvalidException e) {
      throw new JobExecutionException(job.getName(), e);
    }
  }

}
