package ch.sbb.exportservice.service;

import static ch.sbb.atlas.export.enumeration.ExportType.ACTUAL_DATE;
import static ch.sbb.atlas.export.enumeration.ExportType.FULL;
import static ch.sbb.atlas.export.enumeration.ExportType.FUTURE_TIMETABLE;
import static ch.sbb.exportservice.utils.JobDescriptionConstants.EXPORT_PARKING_LOT_CSV_JOB_NAME;
import static ch.sbb.exportservice.utils.JobDescriptionConstants.EXPORT_PARKING_LOT_JSON_JOB_NAME;

import ch.sbb.exportservice.model.ExportTypeV2;
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
        new JobParams(ExportTypeV2.FULL, FULL),
        new JobParams(ExportTypeV2.ACTUAL, ACTUAL_DATE),
        new JobParams(ExportTypeV2.FUTURE_TIMETABLE, FUTURE_TIMETABLE)
    );
  }

}
