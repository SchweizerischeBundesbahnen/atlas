package ch.sbb.exportservice.service;

import static ch.sbb.exportservice.utils.JobDescriptionConstants.EXPORT_TOILET_CSV_JOB_NAME;
import static ch.sbb.exportservice.utils.JobDescriptionConstants.EXPORT_TOILET_JSON_JOB_NAME;

import ch.sbb.atlas.export.enumeration.ExportTypeBase;
import ch.sbb.exportservice.model.PrmExportType;
import java.util.List;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class ExportToiletJobService extends BaseExportJobService {

    public ExportToiletJobService(
            JobLauncher jobLauncher,
            @Qualifier(EXPORT_TOILET_CSV_JOB_NAME) Job exportToiletCsvJob,
            @Qualifier(EXPORT_TOILET_JSON_JOB_NAME) Job exportToiletJsonJob) {
        super(jobLauncher, exportToiletCsvJob, exportToiletJsonJob);
    }

    @Override
    protected List<ExportTypeBase> getExportTypes() {
        return List.of(PrmExportType.values());
    }

}
