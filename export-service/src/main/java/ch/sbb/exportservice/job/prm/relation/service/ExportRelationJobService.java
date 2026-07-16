package ch.sbb.exportservice.job.prm.relation.service;

import static ch.sbb.exportservice.util.JobDescriptionConstant.EXPORT_RELATION_CSV_JOB_NAME;
import static ch.sbb.exportservice.util.JobDescriptionConstant.EXPORT_RELATION_JSON_JOB_NAME;

import ch.sbb.exportservice.job.BaseExportJobService;
import ch.sbb.exportservice.job.BaseExportType;
import ch.sbb.exportservice.model.ExportObjectV2;
import java.util.List;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class ExportRelationJobService extends BaseExportJobService {

  public ExportRelationJobService(
      JobOperator jobOperator,
      @Qualifier(EXPORT_RELATION_CSV_JOB_NAME) Job exportRelationsCsvJob,
      @Qualifier(EXPORT_RELATION_JSON_JOB_NAME) Job exportRelationsJsonJob) {
    super(jobOperator, exportRelationsCsvJob, exportRelationsJsonJob);
  }

  @Override
  protected List<JobParams> getExportTypes() {
    return BaseExportType.BASE;
  }

  @Override
  public ExportObjectV2 getExportObject() {
    return ExportObjectV2.RELATION;
  }

}
