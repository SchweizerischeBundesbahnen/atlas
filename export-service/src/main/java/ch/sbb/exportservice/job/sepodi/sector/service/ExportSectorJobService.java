package ch.sbb.exportservice.job.sepodi.sector.service;

import static ch.sbb.exportservice.util.JobDescriptionConstant.EXPORT_SECTOR_JSON_JOB_NAME;

import ch.sbb.exportservice.job.BaseExportJobService;
import ch.sbb.exportservice.job.BaseExportType;
import ch.sbb.exportservice.model.ExportObjectV2;
import java.util.List;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class ExportSectorJobService extends BaseExportJobService {

  public ExportSectorJobService(
      JobLauncher jobLauncher,
      @Qualifier(EXPORT_SECTOR_JSON_JOB_NAME) Job exportServiceJsonJob
  ) {
    super(jobLauncher, exportServiceJsonJob);
  }

  @Override
  protected List<JobParams> getExportTypes() {
    return BaseExportType.getBaseExportTypes();
  }

  @Override
  public ExportObjectV2 getExportObject() {
    return ExportObjectV2.SECTOR;
  }
}
