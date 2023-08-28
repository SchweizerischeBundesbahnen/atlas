package ch.sbb.line.directory.service.export;

import ch.sbb.atlas.amazon.service.AmazonService;
import ch.sbb.atlas.amazon.service.FileService;
import ch.sbb.atlas.export.AtlasCsvMapper;
import ch.sbb.atlas.export.BaseExportService;
import ch.sbb.atlas.export.enumeration.ExportType;
import ch.sbb.atlas.export.model.VersionCsvModel;
import ch.sbb.atlas.model.FutureTimetableHelper;
import ch.sbb.line.directory.entity.SublineVersion;
import ch.sbb.line.directory.model.csv.SublineVersionCsvModel;
import ch.sbb.line.directory.repository.SublineVersionRepository;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.springframework.stereotype.Service;

import java.io.File;
import java.time.LocalDate;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
public class SublineVersionExportService extends BaseExportService<SublineVersion> {

    private final SublineVersionRepository sublineVersionRepository;

    public SublineVersionExportService(FileService fileService, AmazonService amazonService,
        SublineVersionRepository sublineVersionRepository) {
        super(fileService, amazonService);
        this.sublineVersionRepository = sublineVersionRepository;
    }

    @Override
    public String getDirectory() {
        return "subline";
    }

    @Override
    public String getFileName() {
        return "subline_versions_";
    }

    @Override
    protected File getFullVersionsCsv() {
        List<SublineVersion> fullLineVersions = sublineVersionRepository.getFullSublineVersions();
        return createCsvFile(fullLineVersions, ExportType.FULL);
    }

    @Override
    protected File getActualVersionsCsv() {
        List<SublineVersion> actualLineVersions = sublineVersionRepository.getActualSublineVersions(
            LocalDate.now());
        return createCsvFile(actualLineVersions, ExportType.ACTUAL_DATE);
    }

    @Override
    protected File getFutureTimetableVersionsCsv() {
        List<SublineVersion> actualLineVersions = sublineVersionRepository.getActualSublineVersions(
            FutureTimetableHelper.getTimetableYearChangeDateToExportData(LocalDate.now()));
        return createCsvFile(actualLineVersions, ExportType.FUTURE_TIMETABLE);
    }

    @Override
    protected ObjectWriter getObjectWriter() {
        return new AtlasCsvMapper(SublineVersionCsvModel.class).getObjectWriter();
    }

    @Override
    protected List<VersionCsvModel> convertToCsvModel(List<SublineVersion> versions) {
        return versions.stream()
            .map(SublineVersionCsvModel::toCsvModel)
            .collect(toList());
    }

}