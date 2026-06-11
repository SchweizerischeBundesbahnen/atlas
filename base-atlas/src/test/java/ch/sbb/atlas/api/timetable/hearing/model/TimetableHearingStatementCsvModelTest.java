package ch.sbb.atlas.api.timetable.hearing.model;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.api.timetable.hearing.TimetableHearingStatementDocumentModel;
import ch.sbb.atlas.api.timetable.hearing.TimetableHearingStatementModelV2;
import ch.sbb.atlas.api.timetable.hearing.TimetableHearingStatementResponsibleTransportCompanyModel;
import ch.sbb.atlas.api.timetable.hearing.TimetableHearingStatementSenderModelV2;
import ch.sbb.atlas.api.timetable.hearing.enumeration.StatementStatus;
import ch.sbb.atlas.kafka.model.SwissCanton;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;

class TimetableHearingStatementCsvModelTest {

  @Test
  void shouldMapStatementModelToCsvModel() {
    // Given
    TimetableHearingStatementSenderModelV2 sender = TimetableHearingStatementSenderModelV2.builder()
        .firstName("John")
        .lastName("Doe")
        .organisation("SBB")
        .street("Bahnhofstrasse 1")
        .zip(3000)
        .city("Bern")
        .emails(Set.of("z@example.ch", "a@example.ch"))
        .build();

    TimetableHearingStatementModelV2 statementModel = TimetableHearingStatementModelV2.builder()
        .id(77L)
        .swissCanton(SwissCanton.BERN)
        .statementStatus(StatementStatus.IN_REVIEW)
        .timetableFieldNumber("80.099")
        .timetableFieldDescription("Field description")
        .stopPlace("Bern")
        .responsibleTransportCompanies(List.of(
            TimetableHearingStatementResponsibleTransportCompanyModel.builder()
                .abbreviation("SBB")
                .businessRegisterName("Swiss Federal Railways")
                .build(),
            TimetableHearingStatementResponsibleTransportCompanyModel.builder()
                .abbreviation("BLS")
                .businessRegisterName("BLS AG")
                .build()
        ))
        .documents(List.of(TimetableHearingStatementDocumentModel.builder().fileName("doc.pdf").fileSize(100L).build()))
        .timetableYear(2026L)
        .topic("Timetable topic")
        .statementAnonymous(Boolean.TRUE)
        .statement("Original statement")
        .anonymousStatement("Anonymized")
        .publicComment("Public")
        .internalComment("Internal")
        .statementSender(sender)
        .build();

    // When
    TimetableHearingStatementCsvModel csvModel = TimetableHearingStatementCsvModel.fromModel(statementModel);

    // Then
    assertThat(csvModel.getTimetableHearingStatementId()).isEqualTo(77L);
    assertThat(csvModel.getCantonAbbreviation()).isEqualTo("BE");
    assertThat(csvModel.getTimetableFieldNumber()).isEqualTo("80.099");
    assertThat(csvModel.getTimetableFieldNumberDescription()).isEqualTo("Field description");
    assertThat(csvModel.getStopPlace()).isEqualTo("Bern");
    assertThat(csvModel.getTransportCompanyAbbreviations()).isEqualTo("BLS,SBB");
    assertThat(csvModel.getTransportCompanyDescriptions()).isEqualTo("BLS AG,Swiss Federal Railways");
    assertThat(csvModel.getDocumentsPresent()).isTrue();
    assertThat(csvModel.getStatus()).isEqualTo(StatementStatus.IN_REVIEW);
    assertThat(csvModel.getTimetableHearingYear()).isEqualTo(2026L);
    assertThat(csvModel.getTopic()).isEqualTo("Timetable topic");
    assertThat(csvModel.getStatementAnonymous()).isTrue();
    assertThat(csvModel.getStatement()).isEqualTo("Original statement");
    assertThat(csvModel.getAnonymousStatement()).isEqualTo("Anonymized");
    assertThat(csvModel.getPublicComment()).isEqualTo("Public");
    assertThat(csvModel.getInternalComment()).isEqualTo("Internal");
    assertThat(csvModel.getFirstName()).isEqualTo("John");
    assertThat(csvModel.getLastName()).isEqualTo("Doe");
    assertThat(csvModel.getOrganisation()).isEqualTo("SBB");
    assertThat(csvModel.getStreet()).isEqualTo("Bahnhofstrasse 1");
    assertThat(csvModel.getZipAndCity()).isEqualTo("3000/Bern");
    assertThat(csvModel.getEmails()).isEqualTo("a@example.ch,z@example.ch");
  }

  @Test
  void shouldReturnExpectedZipAndCityCombinations() {
    assertThat(TimetableHearingStatementCsvModel.getZipAndCity(null, "Bern")).isEqualTo("Bern");
    assertThat(TimetableHearingStatementCsvModel.getZipAndCity(3005, "")).isEqualTo("3005");
    assertThat(TimetableHearingStatementCsvModel.getZipAndCity(3005, null)).isEqualTo("3005");
    assertThat(TimetableHearingStatementCsvModel.getZipAndCity(3005, "Bern")).isEqualTo("3005/Bern");
    assertThat(TimetableHearingStatementCsvModel.getZipAndCity(null, null)).isEmpty();
    assertThat(TimetableHearingStatementCsvModel.getZipAndCity(null, "")).isEmpty();
  }
}
