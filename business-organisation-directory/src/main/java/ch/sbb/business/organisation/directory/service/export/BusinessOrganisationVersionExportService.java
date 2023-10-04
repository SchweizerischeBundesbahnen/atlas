package ch.sbb.business.organisation.directory.service.export;

import ch.sbb.atlas.amazon.service.AmazonService;
import ch.sbb.atlas.amazon.service.FileService;
import ch.sbb.atlas.api.model.BaseVersionModel;
import ch.sbb.atlas.export.AtlasCsvMapper;
import ch.sbb.atlas.export.BaseExportService;
import ch.sbb.atlas.export.enumeration.ExportType;
import ch.sbb.atlas.export.model.VersionCsvModel;
import ch.sbb.atlas.model.FutureTimetableHelper;
import ch.sbb.business.organisation.directory.entity.BusinessOrganisationExportVersionWithTuInfo;
import ch.sbb.business.organisation.directory.mapper.BusinessOrganisationVersionMapper;
import ch.sbb.business.organisation.directory.model.csv.BusinessOrganisationVersionCsvModel;
import ch.sbb.business.organisation.directory.repository.BusinessOrganisationVersionExportRepository;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.springframework.stereotype.Service;

import java.io.File;
import java.time.LocalDate;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
public class BusinessOrganisationVersionExportService extends
    BaseExportService<BusinessOrganisationExportVersionWithTuInfo> {

    private final BusinessOrganisationVersionExportRepository businessOrganisationVersionExportRepository;

    public BusinessOrganisationVersionExportService(FileService fileService,
        AmazonService amazonService,
        BusinessOrganisationVersionExportRepository businessOrganisationVersionExportRepository) {
        super(fileService, amazonService);
        this.businessOrganisationVersionExportRepository = businessOrganisationVersionExportRepository;
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
        List<BusinessOrganisationExportVersionWithTuInfo> fullBOVersions = businessOrganisationVersionExportRepository.findAll();
        return createCsvFile(fullBOVersions, ExportType.FULL);
    }

    @Override
    protected File getFullVersionsJson() {
        List<BusinessOrganisationExportVersionWithTuInfo> fullBOVersions = businessOrganisationVersionExportRepository.findAll();
        return createJsonFile(fullBOVersions, ExportType.FULL);
    }

    @Override
    protected File getActualVersionsCsv() {
        List<BusinessOrganisationExportVersionWithTuInfo> actualBOVersions =
            businessOrganisationVersionExportRepository.findVersionsValidOn(LocalDate.now());
        return createCsvFile(actualBOVersions, ExportType.ACTUAL_DATE);
    }

    @Override
    protected File getActualVersionsJson() {
        List<BusinessOrganisationExportVersionWithTuInfo> actualBOVersions =
                businessOrganisationVersionExportRepository.findVersionsValidOn(LocalDate.now());
        return createJsonFile(actualBOVersions, ExportType.ACTUAL_DATE);
    }

    @Override
    protected File getFutureTimetableVersionsCsv() {
        List<BusinessOrganisationExportVersionWithTuInfo> actualBOVersions =
            businessOrganisationVersionExportRepository.findVersionsValidOn(
                FutureTimetableHelper.getTimetableYearChangeDateToExportData(LocalDate.now()));
        return createCsvFile(actualBOVersions, ExportType.FUTURE_TIMETABLE);
    }

    @Override
    protected File getFutureTimetableVersionsJson() {
        List<BusinessOrganisationExportVersionWithTuInfo> actualBOVersions = businessOrganisationVersionExportRepository.findVersionsValidOn(
                FutureTimetableHelper.getTimetableYearChangeDateToExportData(LocalDate.now()));
        return createJsonFile(actualBOVersions, ExportType.FUTURE_TIMETABLE);
    }

    @Override
    protected ObjectWriter getObjectWriter() {
        return new AtlasCsvMapper(BusinessOrganisationVersionCsvModel.class).getObjectWriter();
    }

    @Override
    protected List<VersionCsvModel> convertToCsvModel(
        List<BusinessOrganisationExportVersionWithTuInfo> versions) {
        return versions.stream()
            .map(BusinessOrganisationVersionCsvModel::toCsvModel)
            .collect(toList());
    }

    @Override
    protected List<BaseVersionModel> convertToJsonModel(List<BusinessOrganisationExportVersionWithTuInfo> versions) {
        return versions.stream()
                .map(BusinessOrganisationVersionMapper::toModelFromBOExportVersionWithTuInfo)
                .collect(toList());
    }

}
