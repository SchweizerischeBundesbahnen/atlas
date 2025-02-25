package ch.sbb.exportservice.service;

import static ch.sbb.exportservice.utils.JobDescriptionConstants.EXPORT_PARKING_LOT_CSV_JOB_NAME;
import static ch.sbb.exportservice.utils.JobDescriptionConstants.EXPORT_PARKING_LOT_JSON_JOB_NAME;

import ch.sbb.exportservice.model.ExportType;
import ch.sbb.exportservice.model.ExportTypeV1;
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
    return List.of(
        new JobParams(ExportType.FULL, ExportTypeV1.FULL),
        new JobParams(ExportType.ACTUAL, ExportTypeV1.ACTUAL),
        new JobParams(ExportType.FUTURE_TIMETABLE, ExportTypeV1.TIMETABLE_FUTURE)
    );
  }

}
