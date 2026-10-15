package ch.sbb.exportservice.job.prm.ersad.service;

import static ch.sbb.exportservice.util.JobDescriptionConstant.EXPORT_ERSAD_NETEX_XML_JOB_NAME;

import ch.sbb.exportservice.job.BaseExportJobService;
import ch.sbb.exportservice.model.ExportObjectV2;
import ch.sbb.exportservice.model.ExportTypeV2;
import java.util.List;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class ErsadNetexJobService extends BaseExportJobService {

  public ErsadNetexJobService(JobOperator jobOperator,
      @Qualifier(EXPORT_ERSAD_NETEX_XML_JOB_NAME) Job exportErsadNetexXmlJob) {
    super(jobOperator, exportErsadNetexXmlJob, null);
  }

  @Override
  protected List<JobParams> getExportTypes() {
    return List.of(
        new JobParams(ExportTypeV2.ACTUAL)
    );
  }

  @Override
  public ExportObjectV2 getExportObject() {
    return ExportObjectV2.ERSAD_NETEX;
  }

}
