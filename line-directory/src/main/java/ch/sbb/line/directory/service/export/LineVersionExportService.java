package ch.sbb.line.directory.service.export;

import static java.util.stream.Collectors.toList;

import ch.sbb.atlas.amazon.helper.FutureTimetableHelper;
import ch.sbb.atlas.amazon.service.AmazonService;
import ch.sbb.atlas.amazon.service.FileService;
import ch.sbb.atlas.export.BaseExportService;
import ch.sbb.atlas.export.ExportType;
import ch.sbb.atlas.export.model.VersionCsvModel;
import ch.sbb.line.directory.entity.LineVersion;
import ch.sbb.line.directory.model.csv.LineVersionCsvModel;
import ch.sbb.line.directory.repository.LineVersionRepository;
import com.fasterxml.jackson.databind.ObjectWriter;
import java.io.File;
import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class LineVersionExportService extends BaseExportService<LineVersion> {

  private final LineVersionRepository lineVersionRepository;

  public LineVersionExportService(FileService fileServiceImpl, AmazonService amazonService,
      LineVersionRepository lineVersionRepository) {
    super(fileServiceImpl, amazonService);
    this.lineVersionRepository = lineVersionRepository;
  }

  @Override
  public String getFileName() {
    return "line_versions_";
  }

  @Override
  public String getDirectory() {
    return "line";
  }

  @Override
  protected File getFullVersionsCsv() {
    List<LineVersion> fullLineVersions = lineVersionRepository.getFullLineVersions();
    return createCsvFile(fullLineVersions, ExportType.FULL);
  }

  @Override
  protected File getActualVersionsCsv() {
    List<LineVersion> actualLineVersions = lineVersionRepository.getActualLineVersions(
        LocalDate.now());
    return createCsvFile(actualLineVersions, ExportType.ACTUAL_DATE);
  }

  @Override
  protected File getFutureTimetableVersionsCsv() {
    List<LineVersion> actualLineVersions = lineVersionRepository.getActualLineVersions(
        FutureTimetableHelper.getTimetableYearChangeDateToExportData(LocalDate.now()));
    return createCsvFile(actualLineVersions, ExportType.FUTURE_TIMETABLE);
  }

  @Override
  protected ObjectWriter getObjectWriter() {
    return new AtlasCsvMapper(LineVersionCsvModel.class).getObjectWriter();
  }

  @Override
  protected List<? extends VersionCsvModel> convertToCsvModel(List<LineVersion> versions) {
    return versions.stream()
        .map(LineVersionCsvModel::toCsvModel)
        .collect(toList());
  }

}
