package ch.sbb.exportservice.job;

import static ch.sbb.exportservice.utile.JobDescriptionConstant.EXPORT_TYPE_JOB_PARAMETER;
import static ch.sbb.exportservice.utile.JobDescriptionConstant.EXPORT_TYPE_V1_JOB_PARAMETER;

import ch.sbb.atlas.batch.exception.JobExecutionException;
import ch.sbb.atlas.export.enumeration.ExportTypeBase;
import ch.sbb.exportservice.model.ExportTypeV2;
import ch.sbb.exportservice.utile.JobDescriptionConstant;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;

@Slf4j
public abstract class BaseExportJobService {

  private final JobLauncher jobLauncher;
  private final Job exportCsvJob;
  private final Job exportJsonJob;

  protected BaseExportJobService(JobLauncher jobLauncher, Job exportCsvJob, Job exportJsonJob) {
    this.jobLauncher = jobLauncher;
    this.exportCsvJob = exportCsvJob;
    this.exportJsonJob = exportJsonJob;
  }

  protected abstract List<JobParams> getExportTypes();

  public static JobParameters buildJobParameters(JobParams jobParams) {
    final JobParametersBuilder jobParametersBuilder = new JobParametersBuilder()
        .addString(JobDescriptionConstant.EXECUTION_TYPE_PARAMETER, JobDescriptionConstant.EXECUTION_BATCH_PARAMETER)
        .addString(EXPORT_TYPE_JOB_PARAMETER, jobParams.exportTypeV2.toString())
        .addLong(JobDescriptionConstant.START_AT_JOB_PARAMETER, System.currentTimeMillis());

    if (jobParams.exportTypeV1 != null) {
      jobParametersBuilder.addString(EXPORT_TYPE_V1_JOB_PARAMETER, jobParams.exportTypeV1.toString());
    }
    return jobParametersBuilder.toJobParameters();
  }

  protected void startExportJob(JobParams jobParams, Job job) {
    try {
      JobExecution execution = jobLauncher.run(job, buildJobParameters(jobParams));
      log.info("Job executed with status: {}", execution.getExitStatus().getExitCode());
    } catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException |
             JobParametersInvalidException e) {
      throw new JobExecutionException(job.getName(), e);
    }
  }

  public void startExportJobs() {
    log.info("Starting export CSV and JSON execution...");
    for (JobParams jobParams : getExportTypes()) {
      startExportJob(jobParams, exportCsvJob);
      startExportJob(jobParams, exportJsonJob);
    }
    log.info("CSV and JSON export execution finished!");
  }

  @RequiredArgsConstructor
  public static class JobParams {

    private final ExportTypeV2 exportTypeV2;
    private final ExportTypeBase exportTypeV1;

    public JobParams(ExportTypeV2 exportTypeV2) {
      this.exportTypeV2 = exportTypeV2;
      this.exportTypeV1 = null;
    }

  }

}
