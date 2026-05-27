package ch.sbb.line.directory.module.tth.service;

import static java.util.Comparator.comparing;

import ch.sbb.atlas.amazon.service.FileService;
import ch.sbb.atlas.api.timetable.hearing.TimetableHearingStatementModelV2;
import ch.sbb.atlas.api.timetable.hearing.model.TimetableHearingAnonymStatementCsvModel;
import ch.sbb.atlas.api.timetable.hearing.model.TimetableHearingStatementCsvModel;
import ch.sbb.atlas.export.CsvExportWriter;
import ch.sbb.atlas.export.CsvWriteConfig;
import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class TimetableHearingStatementExportService {

  private static final String OUTPUT_DIR = "statements";

  private final FileService fileService;
  private final MessageSource timetableHearingStatementCsvTranslations;

  public File getStatementsAsCsv(List<TimetableHearingStatementModelV2> statements, Locale locale, boolean anonymized) {
    if (anonymized) {
      List<TimetableHearingAnonymStatementCsvModel> csvData = statements.stream()
          .map(TimetableHearingAnonymStatementCsvModel::fromModelAnonymized)
          .sorted(comparing(TimetableHearingAnonymStatementCsvModel::getTimetableHearingStatementId)).toList();
      return CsvExportWriter.writeCsv(
          CsvWriteConfig.<TimetableHearingAnonymStatementCsvModel>builder()
              .messageSource(timetableHearingStatementCsvTranslations)
              .locale(locale)
              .elementClass(TimetableHearingAnonymStatementCsvModel.class)
              .csvData(csvData)
              .filePath(Path.of(fileService.getDir(), OUTPUT_DIR))
              .build()
      );
    } else {
      List<TimetableHearingStatementCsvModel> csvData = statements.stream()
          .map(TimetableHearingStatementCsvModel::fromModel)
          .sorted(comparing(TimetableHearingStatementCsvModel::getTimetableHearingStatementId)).toList();
      return CsvExportWriter.writeCsv(
          CsvWriteConfig.<TimetableHearingStatementCsvModel>builder()
              .messageSource(timetableHearingStatementCsvTranslations)
              .locale(locale)
              .elementClass(TimetableHearingStatementCsvModel.class)
              .csvData(csvData)
              .filePath(Path.of(fileService.getDir(), OUTPUT_DIR))
              .build()
      );
    }
  }
}
