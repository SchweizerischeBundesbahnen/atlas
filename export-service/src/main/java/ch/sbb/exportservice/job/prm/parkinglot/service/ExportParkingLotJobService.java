package ch.sbb.exportservice.job.prm.parkinglot.service;

import static ch.sbb.exportservice.util.JobDescriptionConstant.EXPORT_PARKING_LOT_CSV_JOB_NAME;
import static ch.sbb.exportservice.util.JobDescriptionConstant.EXPORT_PARKING_LOT_JSON_JOB_NAME;

import ch.sbb.exportservice.job.BaseExportJobService;
import ch.sbb.exportservice.job.BaseExportType;
import ch.sbb.exportservice.model.ExportObjectV2;
import java.util.List;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class ExportParkingLotJobService extends BaseExportJobService {

  public ExportParkingLotJobService(
      JobLauncher jobLauncher,
      @Qualifier(EXPORT_PARKING_LOT_CSV_JOB_NAME) Job exportParkingLotCsvJob,
      @Qualifier(EXPORT_PARKING_LOT_JSON_JOB_NAME) Job exportParkingLotJsonJob) {
    super(jobLauncher, exportParkingLotCsvJob, exportParkingLotJsonJob);
  }

  @Override
  protected List<JobParams> getExportTypes() {
    return BaseExportType.BASE_WITH_FUTURE;
  }

  @Override
  public ExportObjectV2 getExportObject() {
    return ExportObjectV2.PARKING_LOT;
  }

}
