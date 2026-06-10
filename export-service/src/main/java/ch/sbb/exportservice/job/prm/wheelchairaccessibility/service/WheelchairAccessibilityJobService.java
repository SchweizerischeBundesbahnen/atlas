package ch.sbb.exportservice.job.prm.wheelchairaccessibility.service;

import static ch.sbb.exportservice.util.JobDescriptionConstant.EXPORT_WHEELCHAIR_ACCESSIBILITY_CSV_JOB_NAME;

import ch.sbb.exportservice.job.BaseExportJobService;
import ch.sbb.exportservice.model.ExportObjectV2;
import ch.sbb.exportservice.model.ExportTypeV2;
import java.util.List;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class WheelchairAccessibilityJobService extends BaseExportJobService {

  public WheelchairAccessibilityJobService(JobOperator jobOperator,
      @Qualifier(EXPORT_WHEELCHAIR_ACCESSIBILITY_CSV_JOB_NAME) Job exportWheelchairAccessibilityCsvJob) {
    super(jobOperator, exportWheelchairAccessibilityCsvJob, null);
  }

  @Override
  protected List<JobParams> getExportTypes() {
    return List.of(
        new JobParams(ExportTypeV2.ACTUAL)
    );
  }

  @Override
  public ExportObjectV2 getExportObject() {
    return ExportObjectV2.WHEELCHAIR_ACCESSIBILITY;
  }

}
