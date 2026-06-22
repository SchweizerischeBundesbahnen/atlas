package ch.sbb.workflow.module.lidi.tth.service;

import ch.sbb.atlas.amazon.service.FileService;
import ch.sbb.atlas.api.client.line.workflow.TimetableHearingStatementClient;
import ch.sbb.atlas.api.timetable.hearing.model.TimetableHearingAnonymStatementCsvModel;
import ch.sbb.atlas.export.CsvExportWriter;
import ch.sbb.atlas.export.CsvWriteConfig;
import ch.sbb.atlas.model.AtlasListUtil;
import ch.sbb.workflow.module.lidi.tth.entity.TthDossier;
import ch.sbb.workflow.module.lidi.tth.model.TthDossierTuCsvModel;
import java.io.File;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TthDossierCsvExportService {

  private static final String OUTPUT_DIR = "dossiers";
  private static final int MAX_STATEMENT_IDS_REQ_PARAM = 100;

  private final FileService fileService;
  private final MessageSource tthDossierCsvTranslations;
  private final TimetableHearingStatementClient timetableHearingStatementClient;

  public List<TthDossierTuCsvModel> getTthDossierTuCsvModels(Page<TthDossier> dossiers) {
    Set<Long> statementIds = dossiers.stream()
        .flatMap(dossier -> dossier.getStatementIds().stream())
        .collect(Collectors.toSet());

    Collection<List<Long>> chunks = AtlasListUtil.getPartitionedSublists(statementIds, MAX_STATEMENT_IDS_REQ_PARAM);

    Map<Long, TimetableHearingAnonymStatementCsvModel> statementModels = getStatementModels(chunks);

    return dossiers.stream()
        .sorted(Comparator.comparing(TthDossier::getId))
        .flatMap(dossier -> dossier.getStatementIds().stream()
            .sorted()
            .map(statementId -> TthDossierTuCsvModel.fromDossierAndStatement(dossier, statementModels.get(statementId)))
        )
        .toList();
  }

  private Map<Long, TimetableHearingAnonymStatementCsvModel> getStatementModels(Collection<List<Long>> chunks) {
    return chunks.stream()
        .flatMap(chunk -> timetableHearingStatementClient.getStatementsByIdAnonymized(chunk).stream())
        .collect(Collectors.toMap(TimetableHearingAnonymStatementCsvModel::getTimetableHearingStatementId, Function.identity()));
  }

  public <T> File writeCsv(List<T> csvData, Class<T> elementClass, Locale locale) {
    return CsvExportWriter.writeCsv(
        CsvWriteConfig.<T>builder()
            .messageSource(tthDossierCsvTranslations)
            .locale(locale)
            .elementClass(elementClass)
            .csvData(csvData)
            .filePath(Path.of(fileService.getDir(), OUTPUT_DIR))
            .build()
    );
  }
}
