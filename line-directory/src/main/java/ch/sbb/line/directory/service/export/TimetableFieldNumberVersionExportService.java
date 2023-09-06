package ch.sbb.line.directory.service.export;

import ch.sbb.atlas.amazon.service.AmazonService;
import ch.sbb.atlas.amazon.service.FileService;
import ch.sbb.atlas.export.AtlasCsvMapper;
import ch.sbb.atlas.export.BaseExportService;
import ch.sbb.atlas.export.enumeration.ExportType;
import ch.sbb.atlas.export.model.VersionCsvModel;
import ch.sbb.atlas.model.FutureTimetableHelper;
import ch.sbb.line.directory.entity.TimetableFieldNumberVersion;
import ch.sbb.line.directory.model.csv.TimetableFieldNumberVersionCsvModel;
import ch.sbb.line.directory.repository.TimetableFieldNumberVersionRepository;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.springframework.stereotype.Service;

import java.io.File;
import java.time.LocalDate;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
public class TimetableFieldNumberVersionExportService extends
    BaseExportService<TimetableFieldNumberVersion> {

    private final TimetableFieldNumberVersionRepository timetableFieldNumberVersionRepository;

    public TimetableFieldNumberVersionExportService(FileService fileService,
        AmazonService amazonService,
        TimetableFieldNumberVersionRepository timetableFieldNumberVersionRepository) {
        super(fileService, amazonService);
        this.timetableFieldNumberVersionRepository = timetableFieldNumberVersionRepository;
    }

    @Override
    public String getDirectory() {
        return "timetable_field_number";
    }

    @Override
    public String getFileName() {
        return "timetable_field_number_versions_";
    }

    @Override
    protected File getFullVersionsCsv() {
        List<TimetableFieldNumberVersion> fullTimeTableNumberVersions = timetableFieldNumberVersionRepository.getFullTimeTableNumberVersions();
        return createCsvFile(fullTimeTableNumberVersions, ExportType.FULL);
    }

    @Override
    protected File getActualVersionsCsv() {
        List<TimetableFieldNumberVersion> actualTimeTableNumberVersions = timetableFieldNumberVersionRepository.getActualTimeTableNumberVersions(
            LocalDate.now());
        return createCsvFile(actualTimeTableNumberVersions, ExportType.ACTUAL_DATE);
    }

    @Override
    protected File getFutureTimetableVersionsCsv() {
        List<TimetableFieldNumberVersion> actualTimeTableNumberVersions = timetableFieldNumberVersionRepository.getActualTimeTableNumberVersions(
            FutureTimetableHelper.getTimetableYearChangeDateToExportData(LocalDate.now()));
        return createCsvFile(actualTimeTableNumberVersions, ExportType.FUTURE_TIMETABLE);
    }

    @Override
    protected ObjectWriter getObjectWriter() {
        return new AtlasCsvMapper(TimetableFieldNumberVersionCsvModel.class).getObjectWriter();
    }

    @Override
    protected List<VersionCsvModel> convertToCsvModel(
        List<TimetableFieldNumberVersion> versions) {
        return versions.stream()
            .map(TimetableFieldNumberVersionCsvModel::toCsvModel)
            .collect(toList());
    }

}