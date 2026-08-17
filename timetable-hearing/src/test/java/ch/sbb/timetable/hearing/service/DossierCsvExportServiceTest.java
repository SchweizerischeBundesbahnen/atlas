package ch.sbb.timetable.hearing.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import ch.sbb.atlas.amazon.service.FileService;
import ch.sbb.atlas.api.timetable.hearing.model.TimetableHearingAnonymStatementCsvModel;
import ch.sbb.atlas.api.workflow.tth.dossier.DossierStatus;
import ch.sbb.atlas.export.CsvExportWriter;
import ch.sbb.atlas.export.CsvWriteConfig;
import ch.sbb.atlas.kafka.model.SwissCanton;
import ch.sbb.timetable.hearing.entity.Dossier;
import ch.sbb.timetable.hearing.entity.DossierQuestion;
import ch.sbb.timetable.hearing.model.DossierTuCsvModel;
import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.PageImpl;

@ExtendWith(MockitoExtension.class)
class DossierCsvExportServiceTest {

  @Mock
  private FileService fileService;

  @Mock
  private MessageSource tthDossierCsvTranslations;

  @Mock
  private TimetableHearingStatementService timetableHearingStatementService;

  @Test
  void shouldReturnCsvModelsByCombiningDossierAndStatementData() {
    // given
    Dossier dossierWithQuestion = dossier(11L, "Dossier 11", List.of(101L, 102L));
    dossierWithQuestion.setDossierQuestions(List.of(DossierQuestion.builder()
        .question("Question for TU")
        .answerToCanton("Answer from TU")
        .build()));
    Dossier dossierWithoutQuestion = dossier(22L, "Dossier 22", List.of(201L));

    when(timetableHearingStatementService.getStatementsByIdAnonymized(any())).thenAnswer(invocation -> {
      @SuppressWarnings("unchecked")
      List<Long> ids = invocation.getArgument(0, List.class);
      return ids.stream().map(this::statementModel).toList();
    });

    // when
    List<DossierTuCsvModel> csvModels = new DossierCsvExportService(fileService, tthDossierCsvTranslations,
        timetableHearingStatementService)
        .getTthDossierTuCsvModels(new PageImpl<>(List.of(dossierWithQuestion, dossierWithoutQuestion)));

    // then
    assertThat(csvModels).hasSize(3);
    assertThat(csvModels)
        .extracting(DossierTuCsvModel::getDossierId, DossierTuCsvModel::getTimetableHearingStatementId,
            DossierTuCsvModel::getQuestionForTU, DossierTuCsvModel::getAnswerFromTU)
        .containsExactly(
            tuple(11L, 101L, "Question for TU", "Answer from TU"),
            tuple(11L, 102L, "Question for TU", "Answer from TU"),
            tuple(22L, 201L, null, null)
        );
  }

  @Test
  void shouldSortCsvModelsByDossierIdAndStatementIdAscending() {
    // given
    Dossier dossier22 = dossier(22L, "Dossier 22", List.of(202L, 201L));
    Dossier dossier11 = dossier(11L, "Dossier 11", List.of(102L, 101L));

    when(timetableHearingStatementService.getStatementsByIdAnonymized(any())).thenAnswer(invocation -> {
      @SuppressWarnings("unchecked")
      List<Long> ids = invocation.getArgument(0, List.class);
      return ids.stream().map(this::statementModel).toList();
    });

    // when
    List<DossierTuCsvModel> csvModels = new DossierCsvExportService(fileService, tthDossierCsvTranslations,
        timetableHearingStatementService)
        .getTthDossierTuCsvModels(new PageImpl<>(List.of(dossier22, dossier11)));

    // then
    assertThat(csvModels)
        .extracting(DossierTuCsvModel::getDossierId, DossierTuCsvModel::getTimetableHearingStatementId)
        .containsExactly(
            tuple(11L, 101L),
            tuple(11L, 102L),
            tuple(22L, 201L),
            tuple(22L, 202L)
        );
  }

  @Test
  void shouldWriteCsvUsingConfiguredOutputDirectory() {
    // given
    DossierCsvExportService service = new DossierCsvExportService(fileService, tthDossierCsvTranslations,
        timetableHearingStatementService);
    List<DossierTuCsvModel> csvData = List.of(DossierTuCsvModel.builder().dossierId(1L).build());
    when(fileService.getDir()).thenReturn("target/test-exports");

    File writtenFile = new File("target/test-exports/dossiers/export.csv");
    try (MockedStatic<CsvExportWriter> csvExportWriterMockedStatic = mockStatic(CsvExportWriter.class)) {
      csvExportWriterMockedStatic.when(() -> CsvExportWriter.writeCsv(any(CsvWriteConfig.class))).thenReturn(writtenFile);

      // when
      File result = service.writeCsv(csvData, DossierTuCsvModel.class, Locale.GERMAN);

      // then
      ArgumentCaptor<CsvWriteConfig> configCaptor = ArgumentCaptor.forClass(CsvWriteConfig.class);
      csvExportWriterMockedStatic.verify(() -> CsvExportWriter.writeCsv(configCaptor.capture()));

      CsvWriteConfig capturedConfig = configCaptor.getValue();
      assertThat(result).isEqualTo(writtenFile);
      assertThat(capturedConfig.elementClass()).isEqualTo(DossierTuCsvModel.class);
      assertThat(capturedConfig.csvData()).isEqualTo(csvData);
      assertThat(capturedConfig.locale()).isEqualTo(Locale.GERMAN);
      assertThat(capturedConfig.messageSource()).isEqualTo(tthDossierCsvTranslations);
      assertThat(capturedConfig.filePath()).isEqualTo(Path.of("target/test-exports", "dossiers"));
    }
  }

  private Dossier dossier(Long dossierId, String topic, List<Long> statementIds) {
    return Dossier.builder()
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
