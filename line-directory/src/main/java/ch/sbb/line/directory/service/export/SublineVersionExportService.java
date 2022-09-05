package ch.sbb.line.directory.service.export;

import static java.util.stream.Collectors.toList;

import ch.sbb.atlas.amazon.helper.FutureTimetableHelper;
import ch.sbb.atlas.amazon.service.AmazonService;
import ch.sbb.atlas.amazon.service.FileService;
import ch.sbb.atlas.model.exception.ExportException;
import ch.sbb.line.directory.entity.SublineVersion;
import ch.sbb.line.directory.entity.SublineVersionCsvModel;
import ch.sbb.line.directory.repository.SublineVersionRepository;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SequenceWriter;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class SublineVersionExportService extends BaseExportService<SublineVersion> {

  private final SublineVersionRepository sublineVersionRepository;

  public SublineVersionExportService(FileService fileService, AmazonService amazonService,
      SublineVersionRepository sublineVersionRepository) {
    super(fileService, amazonService);
    this.sublineVersionRepository = sublineVersionRepository;
  }


  @Override
  protected String getDirectory() {
    return "subline";
  }

  @Override
  protected File getFullVersionsCsv() {
    List<SublineVersion> fullLineVersions = sublineVersionRepository.getFullSublineVersions();
    return createCsvFile(fullLineVersions, ExportType.FULL);
  }

  @Override
  public String getFileName() {
    return "subline_versions_";
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

  private File createCsvFile(List<SublineVersion> sublineVersions, ExportType exportType) {

    File csvFile = createFile(exportType);
    AtlasCsvMapper atlasCsvMapper = new AtlasCsvMapper(SublineVersionCsvModel.class);

    List<SublineVersionCsvModel> lineVersionCsvModels =
        sublineVersions.stream()
                       .map(SublineVersionCsvModel::toCsvModel)
                       .collect(toList());

    ObjectWriter objectWriter = atlasCsvMapper.getCsvMapper()
                                              .writerFor(SublineVersionCsvModel.class)
                                              .with(atlasCsvMapper.getCsvSchema());
    try (SequenceWriter sequenceWriter = objectWriter.writeValues(csvFile)) {
      sequenceWriter.writeAll(lineVersionCsvModels);
      return csvFile;
    } catch (IOException e) {
      throw new ExportException(csvFile, e);
    }
  }


}
