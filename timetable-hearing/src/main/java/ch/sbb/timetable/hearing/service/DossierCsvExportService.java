package ch.sbb.timetable.hearing.service;

import ch.sbb.atlas.amazon.service.FileService;
import ch.sbb.atlas.api.timetable.hearing.model.TimetableHearingAnonymStatementCsvModel;
import ch.sbb.atlas.export.CsvExportWriter;
import ch.sbb.atlas.export.CsvWriteConfig;
import ch.sbb.timetable.hearing.entity.Dossier;
import ch.sbb.timetable.hearing.model.DossierTuCsvModel;
import java.io.File;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DossierCsvExportService {

  private static final String OUTPUT_DIR = "dossiers";

  private final FileService fileService;
  private final MessageSource dossierCsvTranslations;
  private final TimetableHearingStatementService timetableHearingStatementService;

  public List<DossierTuCsvModel> getDossierTuCsvModels(Page<Dossier> dossiers) {
    List<Long> statementIds = dossiers.stream()
        .flatMap(dossier -> dossier.getStatementIds().stream())
        .distinct()
        .toList();

    Map<Long, TimetableHearingAnonymStatementCsvModel> statementModels = getStatementModels(statementIds);

    return dossiers.stream()
        .sorted(Comparator.comparing(Dossier::getId))
        .flatMap(dossier -> dossier.getStatementIds().stream()
            .sorted()
            .map(statementId -> DossierTuCsvModel.fromDossierAndStatement(dossier, statementModels.get(statementId)))
        )
        .toList();
  }

  private Map<Long, TimetableHearingAnonymStatementCsvModel> getStatementModels(List<Long> statementIds) {
    return timetableHearingStatementService.getStatementsByIdAnonymized(statementIds).stream()
        .collect(Collectors.toMap(TimetableHearingAnonymStatementCsvModel::getTimetableHearingStatementId, Function.identity()));
  }

  public <T> File writeCsv(List<T> csvData, Class<T> elementClass, Locale locale) {
    return CsvExportWriter.writeCsv(
        CsvWriteConfig.<T>builder()
            .messageSource(dossierCsvTranslations)
            .locale(locale)
            .elementClass(elementClass)
            .csvData(csvData)
            .filePath(Path.of(fileService.getDir(), OUTPUT_DIR))
            .build()
    );
  }
}
