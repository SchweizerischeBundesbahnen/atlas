package ch.sbb.workflow.module.lidi.tth.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ch.sbb.atlas.amazon.service.FileService;
import ch.sbb.atlas.api.client.line.workflow.TimetableHearingStatementClient;
import ch.sbb.atlas.api.timetable.hearing.model.TimetableHearingAnonymStatementCsvModel;
import ch.sbb.atlas.api.workflow.tth.dossier.DossierStatus;
import ch.sbb.atlas.export.CsvExportWriter;
import ch.sbb.atlas.export.CsvWriteConfig;
import ch.sbb.atlas.kafka.model.SwissCanton;
import ch.sbb.workflow.module.lidi.tth.entity.TthDossier;
import ch.sbb.workflow.module.lidi.tth.entity.TthDossierQuestion;
import ch.sbb.workflow.module.lidi.tth.model.TthDossierTuCsvModel;
import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.stream.LongStream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.PageImpl;

@ExtendWith(MockitoExtension.class)
class TthDossierCsvExportServiceTest {

  private static final int CHUNK_LIMIT = 100;

  @Mock
  private FileService fileService;

  @Mock
  private MessageSource tthDossierCsvTranslations;

  @Mock
  private TimetableHearingStatementClient timetableHearingStatementClient;

  @Test
  void shouldReturnCsvModelsByCombiningDossierAndStatementData() {
    // given
    TthDossier dossierWithQuestion = dossier(11L, "Dossier 11", List.of(101L, 102L));
    dossierWithQuestion.setDossierQuestions(List.of(TthDossierQuestion.builder()
        .question("Question for TU")
        .answerToCanton("Answer from TU")
        .build()));
    TthDossier dossierWithoutQuestion = dossier(22L, "Dossier 22", List.of(201L));

    when(timetableHearingStatementClient.getStatementsByIdAnonymized(any())).thenAnswer(invocation -> {
      @SuppressWarnings("unchecked")
      List<Long> ids = invocation.getArgument(0, List.class);
      return ids.stream().map(this::statementModel).toList();
    });

    // when
    List<TthDossierTuCsvModel> csvModels = new TthDossierCsvExportService(fileService, tthDossierCsvTranslations,
        timetableHearingStatementClient)
        .getTthDossierTuCsvModels(new PageImpl<>(List.of(dossierWithQuestion, dossierWithoutQuestion)));

    // then
    assertThat(csvModels).hasSize(3);
    assertThat(csvModels)
        .extracting(TthDossierTuCsvModel::getDossierId, TthDossierTuCsvModel::getTimetableHearingStatementId,
            TthDossierTuCsvModel::getQuestionForTU, TthDossierTuCsvModel::getAnswerFromTU)
        .containsExactly(
            tuple(11L, 101L, "Question for TU", "Answer from TU"),
            tuple(11L, 102L, "Question for TU", "Answer from TU"),
            tuple(22L, 201L, null, null)
        );
  }

  @Test
  void shouldCallStatementClientInChunksOfMaximum100Ids() {
    // given
    List<Long> statementIds = LongStream.rangeClosed(1, 205).boxed().toList();
    TthDossier dossier = dossier(33L, "Chunk test", statementIds);

    when(timetableHearingStatementClient.getStatementsByIdAnonymized(any())).thenAnswer(invocation -> {
      @SuppressWarnings("unchecked")
      List<Long> ids = invocation.getArgument(0, List.class);
      return ids.stream().map(this::statementModel).toList();
    });

    // when
    List<TthDossierTuCsvModel> csvModels = new TthDossierCsvExportService(fileService, tthDossierCsvTranslations,
        timetableHearingStatementClient)
        .getTthDossierTuCsvModels(new PageImpl<>(List.of(dossier)));

    // then
    ArgumentCaptor<List> idsCaptor = ArgumentCaptor.forClass(List.class);
    verify(timetableHearingStatementClient, times(3)).getStatementsByIdAnonymized(idsCaptor.capture());

    assertThat(idsCaptor.getAllValues()).allSatisfy(ids -> assertThat(ids).hasSizeLessThanOrEqualTo(CHUNK_LIMIT));
    assertThat(idsCaptor.getAllValues().stream().flatMap(List::stream).distinct()).hasSize(205);
    assertThat(csvModels).hasSize(205);
  }

  @Test
  void shouldWriteCsvUsingConfiguredOutputDirectory() {
    // given
    TthDossierCsvExportService service = new TthDossierCsvExportService(fileService, tthDossierCsvTranslations,
        timetableHearingStatementClient);
    List<TthDossierTuCsvModel> csvData = List.of(TthDossierTuCsvModel.builder().dossierId(1L).build());
    when(fileService.getDir()).thenReturn("target/test-exports");

    File writtenFile = new File("target/test-exports/dossiers/export.csv");
    try (MockedStatic<CsvExportWriter> csvExportWriterMockedStatic = mockStatic(CsvExportWriter.class)) {
      csvExportWriterMockedStatic.when(() -> CsvExportWriter.writeCsv(any(CsvWriteConfig.class))).thenReturn(writtenFile);

      // when
      File result = service.writeCsv(csvData, TthDossierTuCsvModel.class, Locale.GERMAN);

      // then
      ArgumentCaptor<CsvWriteConfig> configCaptor = ArgumentCaptor.forClass(CsvWriteConfig.class);
      csvExportWriterMockedStatic.verify(() -> CsvExportWriter.writeCsv(configCaptor.capture()));

      CsvWriteConfig capturedConfig = configCaptor.getValue();
      assertThat(result).isEqualTo(writtenFile);
      assertThat(capturedConfig.elementClass()).isEqualTo(TthDossierTuCsvModel.class);
      assertThat(capturedConfig.csvData()).isEqualTo(csvData);
      assertThat(capturedConfig.locale()).isEqualTo(Locale.GERMAN);
      assertThat(capturedConfig.messageSource()).isEqualTo(tthDossierCsvTranslations);
      assertThat(capturedConfig.filePath()).isEqualTo(Path.of("target/test-exports", "dossiers"));
    }
  }

  private TthDossier dossier(Long dossierId, String topic, List<Long> statementIds) {
    return TthDossier.builder()
        .id(dossierId)
        .swissCanton(SwissCanton.BERN)
        .topic(topic)
        .dossierStatus(DossierStatus.ADDED)
        .statementIds(statementIds)
        .build();
  }

  private TimetableHearingAnonymStatementCsvModel statementModel(Long statementId) {
    return TimetableHearingAnonymStatementCsvModel.builder()
        .timetableHearingStatementId(statementId)
        .timetableFieldNumber("TFN-" + statementId)
        .timetableFieldNumberDescription("Description " + statementId)
        .stopPlace("Stop " + statementId)
        .transportCompanyAbbreviations("SBB")
        .transportCompanyDescriptions("Swiss Federal Railways")
        .statement("Statement " + statementId)
        .documentsPresent(Boolean.TRUE)
        .timetableHearingYear(2025L)
        .build();
  }
}
