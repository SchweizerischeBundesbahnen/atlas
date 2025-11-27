package ch.sbb.exportservice.job.sepodi.sector.service;

import static ch.sbb.exportservice.util.JobDescriptionConstant.EXPORT_SECTORS_AND_SECTOR_GROUPS_CSV_JOB_NAME;

import ch.sbb.exportservice.job.BaseExportJobService;
import ch.sbb.exportservice.job.BaseExportType;
import ch.sbb.exportservice.model.ExportObjectV2;
import java.util.List;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class ExportSectorsAndSectorGroupsJobService extends BaseExportJobService {

  public ExportSectorsAndSectorGroupsJobService(
      JobLauncher jobLauncher,
      @Qualifier(EXPORT_SECTORS_AND_SECTOR_GROUPS_CSV_JOB_NAME) Job exportCsvJob
  ) {
    super(jobLauncher, exportCsvJob);
  }

  @Override
  protected List<JobParams> getExportTypes() {
    return BaseExportType.BASE;
  }

  @Override
  public ExportObjectV2 getExportObject() {
    return ExportObjectV2.SECTORS_AND_SECTORGROUPS;
  }
}
