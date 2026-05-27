package ch.sbb.atlas.api.timetable.hearing.model;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.api.timetable.hearing.TimetableHearingStatementDocumentModel;
import ch.sbb.atlas.api.timetable.hearing.TimetableHearingStatementModelV2;
import ch.sbb.atlas.api.timetable.hearing.TimetableHearingStatementResponsibleTransportCompanyModel;
import ch.sbb.atlas.api.timetable.hearing.enumeration.StatementStatus;
import ch.sbb.atlas.kafka.model.SwissCanton;
import java.util.List;
import org.junit.jupiter.api.Test;

class TimetableHearingAnonymStatementCsvModelTest {

  @Test
  void shouldBuildCsvModelUsingOriginalStatementWhenAlreadyAnonymous() {
    // Given
    TimetableHearingStatementModelV2 statementModel = baseStatementModelBuilder()
        .statementAnonymous(Boolean.TRUE)
        .statement("original statement")
        .anonymousStatement("anonymized statement")
        .documents(List.of(TimetableHearingStatementDocumentModel.builder().fileName("document.pdf").fileSize(10L).build()))
        .build();

    // When
    TimetableHearingAnonymStatementCsvModel csvModel = TimetableHearingAnonymStatementCsvModel.fromModelAnonymized(statementModel);

    // Then
    assertThat(csvModel.getStatement()).isEqualTo("original statement");
    assertThat(csvModel.getCantonAbbreviation()).isEqualTo("BE");
    assertThat(csvModel.getTimetableFieldNumber()).isEqualTo("100");
    assertThat(csvModel.getTimetableFieldNumberDescription()).isEqualTo("Bern - Thun");
    assertThat(csvModel.getStopPlace()).isEqualTo("Bern");
    assertThat(csvModel.getTimetableHearingStatementId()).isEqualTo(99L);
    assertThat(csvModel.getTransportCompanyAbbreviations()).isEqualTo("BLS,SBB");
    assertThat(csvModel.getTransportCompanyDescriptions()).isEqualTo("BLS AG,Swiss Federal Railways");
    assertThat(csvModel.getDocumentsPresent()).isTrue();
    assertThat(csvModel.getStatus()).isEqualTo(StatementStatus.RECEIVED);
    assertThat(csvModel.getTimetableHearingYear()).isEqualTo(2025L);
    assertThat(csvModel.getTopic()).isEqualTo("Service quality");
  }

  @Test
  void shouldBuildCsvModelUsingAnonymizedStatementWhenStatementIsNotAnonymous() {
    // Given
    TimetableHearingStatementModelV2 statementModel = baseStatementModelBuilder()
        .statementAnonymous(Boolean.FALSE)
        .statement("original statement")
        .anonymousStatement("anonymized statement")
        .documents(null)
        .build();

    // When
    TimetableHearingAnonymStatementCsvModel csvModel = TimetableHearingAnonymStatementCsvModel.fromModelAnonymized(statementModel);

    // Then
    assertThat(csvModel.getStatement()).isEqualTo("anonymized statement");
    assertThat(csvModel.getDocumentsPresent()).isFalse();
  }

  private TimetableHearingStatementModelV2.TimetableHearingStatementModelV2Builder<?, ?> baseStatementModelBuilder() {
    return TimetableHearingStatementModelV2.builder()
        .id(99L)
        .swissCanton(SwissCanton.BERN)
        .statementStatus(StatementStatus.RECEIVED)
        .timetableFieldNumber("100")
        .timetableFieldDescription("Bern - Thun")
        .stopPlace("Bern")
        .responsibleTransportCompanies(List.of(
            TimetableHearingStatementResponsibleTransportCompanyModel.builder()
                .abbreviation("SBB")
                .businessRegisterName("Swiss Federal Railways")
                .build(),
            TimetableHearingStatementResponsibleTransportCompanyModel.builder()
                .abbreviation(null)
                .businessRegisterName(null)
                .build(),
            TimetableHearingStatementResponsibleTransportCompanyModel.builder()
                .abbreviation("BLS")
                .businessRegisterName("BLS AG")
                .build()
        ))
        .timetableYear(2025L)
        .topic("Service quality");
  }
}
