package ch.sbb.exportservice.service;

import ch.sbb.exportservice.model.ExportType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;

import static ch.sbb.exportservice.utils.JobDescriptionConstants.EXPORT_TRAFFIC_POINT_ELEMENT_CSV_JOB_NAME;
import static ch.sbb.exportservice.utils.JobDescriptionConstants.EXPORT_TRAFFIC_POINT_ELEMENT_JSON_JOB_NAME;

@Slf4j
@Component
public class ExportTrafficPointElementJobService extends BaseExportJobService{


  @Qualifier(EXPORT_TRAFFIC_POINT_ELEMENT_CSV_JOB_NAME)
  private final Job exportTrafficPointElementCsvJob;

  @Qualifier(EXPORT_TRAFFIC_POINT_ELEMENT_JSON_JOB_NAME)
  private final Job exportTrafficPointElementJsonJob;

  public ExportTrafficPointElementJobService(JobLauncher jobLauncher, Job exportTrafficPointElementCsvJob, Job exportTrafficPointElementJsonJob) {
    super(jobLauncher);
    this.exportTrafficPointElementCsvJob = exportTrafficPointElementCsvJob;
    this.exportTrafficPointElementJsonJob = exportTrafficPointElementJsonJob;
  }

  public void startExportJobs() {
    log.info("Starting export CSV and JSON execution...");
    List<ExportType> exportTypes = List.of(ExportType.WORLD_FULL, ExportType.WORLD_ONLY_ACTUAL, ExportType.WORLD_ONLY_TIMETABLE_FUTURE);

    for (ExportType exportType : exportTypes) {
      startExportJob(exportType, exportTrafficPointElementCsvJob);
      startExportJob(exportType, exportTrafficPointElementJsonJob);
    }
    log.info("CSV and JSON export execution finished!");
  }



}
