package ch.sbb.exportservice.service;

import ch.sbb.atlas.export.enumeration.ExportTypeBase;
import ch.sbb.exportservice.model.PrmExportType;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;

import static ch.sbb.exportservice.utils.JobDescriptionConstants.EXPORT_CONTACT_POINT_CSV_JOB_NAME;
import static ch.sbb.exportservice.utils.JobDescriptionConstants.EXPORT_CONTACT_POINT_JSON_JOB_NAME;

@Component
public class ExportContactPointJobService extends BaseExportJobService {

    public ExportContactPointJobService(
            JobLauncher jobLauncher,
            @Qualifier(EXPORT_CONTACT_POINT_CSV_JOB_NAME) Job exportContactPointCsvJob,
            @Qualifier(EXPORT_CONTACT_POINT_JSON_JOB_NAME) Job exportContactPointJsonJob) {
        super(jobLauncher, exportContactPointCsvJob, exportContactPointJsonJob);
    }

    @Override
    protected List<ExportTypeBase> getExportTypes() {
        return List.of(PrmExportType.values());
    }

}
