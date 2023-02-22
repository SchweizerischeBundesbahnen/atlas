package ch.sbb.business.organisation.directory.service.export;

import static java.util.stream.Collectors.toList;

import ch.sbb.atlas.amazon.helper.FutureTimetableHelper;
import ch.sbb.atlas.amazon.service.AmazonService;
import ch.sbb.atlas.amazon.service.FileService;
import ch.sbb.atlas.export.BaseExportService;
import ch.sbb.atlas.export.ExportType;
import ch.sbb.atlas.export.model.VersionCsvModel;
import ch.sbb.business.organisation.directory.entity.BusinessOrganisationVersion;
import ch.sbb.business.organisation.directory.model.csv.BusinessOrganisationVersionCsvModel;
import ch.sbb.business.organisation.directory.repository.BusinessOrganisationVersionRepository;
import com.fasterxml.jackson.databind.ObjectWriter;
import java.io.File;
import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class BusinessOrganisationVersionExportService extends
    BaseExportService<BusinessOrganisationVersion> {

    private final BusinessOrganisationVersionRepository businessOrganisationVersionRepository;

    public BusinessOrganisationVersionExportService(FileService fileService,
        AmazonService amazonService,
        BusinessOrganisationVersionRepository businessOrganisationVersionRepository) {
        super(fileService, amazonService);
        this.businessOrganisationVersionRepository = businessOrganisationVersionRepository;
    }

    @Override
    public String getFileName() {
        return "business_organisation_versions_";
    }

    @Override
    public String getDirectory() {
        return "business_organisation";
    }

    @Override
    protected File getFullVersionsCsv() {
        List<BusinessOrganisationVersion> fullLineVersions = businessOrganisationVersionRepository.getFullLineVersions();
        return createCsvFile(fullLineVersions, ExportType.FULL);
    }

    @Override
    protected File getActualVersionsCsv() {
        List<BusinessOrganisationVersion> actualLineVersions = businessOrganisationVersionRepository.getActualLineVersions(
            LocalDate.now());
        return createCsvFile(actualLineVersions, ExportType.ACTUAL_DATE);
    }

    @Override
    protected File getFutureTimetableVersionsCsv() {
        List<BusinessOrganisationVersion> actualLineVersions = businessOrganisationVersionRepository.getActualLineVersions(
            FutureTimetableHelper.getTimetableYearChangeDateToExportData(LocalDate.now()));
        return createCsvFile(actualLineVersions, ExportType.FUTURE_TIMETABLE);
    }

    @Override
    protected ObjectWriter getObjectWriter() {
        return new AtlasCsvMapper(BusinessOrganisationVersionCsvModel.class).getObjectWriter();
    }

    @Override
    protected List<? extends VersionCsvModel> convertToCsvModel(
        List<BusinessOrganisationVersion> versions) {
        return versions.stream()
            .map(BusinessOrganisationVersionCsvModel::toCsvModel)
            .collect(toList());
    }

}
