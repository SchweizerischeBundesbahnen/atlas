package ch.sbb.exportservice.job.prm.parkinglot;

import static ch.sbb.exportservice.utile.JobDescriptionConstant.EXPORT_PARKING_LOT_CSV_JOB_NAME;
import static ch.sbb.exportservice.utile.JobDescriptionConstant.EXPORT_PARKING_LOT_JSON_JOB_NAME;

import ch.sbb.exportservice.job.BaseExportJobService;
import ch.sbb.exportservice.model.ExportTypeV2;
import ch.sbb.exportservice.model.PrmExportType;
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
        new JobParams(ExportTypeV2.FULL, PrmExportType.FULL),
        new JobParams(ExportTypeV2.ACTUAL, PrmExportType.ACTUAL),
        new JobParams(ExportTypeV2.FUTURE_TIMETABLE, PrmExportType.TIMETABLE_FUTURE)
    );
  }

}
