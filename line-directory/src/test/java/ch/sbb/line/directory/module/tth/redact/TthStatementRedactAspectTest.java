package ch.sbb.line.directory.module.tth.redact;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doReturn;

import ch.sbb.atlas.api.timetable.hearing.TimetableHearingStatementModelV2;
import ch.sbb.atlas.api.timetable.hearing.enumeration.StatementStatus;
import ch.sbb.atlas.kafka.model.SwissCanton;
import ch.sbb.atlas.kafka.model.user.admin.ApplicationType;
import ch.sbb.atlas.user.administration.security.service.BoUserMailCheckService;
import ch.sbb.atlas.user.administration.security.service.CantonBasedUserAdministrationService;
import ch.sbb.line.directory.module.tth.entity.StatementDocument;
import ch.sbb.line.directory.module.tth.entity.StatementSender;
import ch.sbb.line.directory.module.tth.entity.TimetableHearingStatement;
import ch.sbb.line.directory.shared.transportcompany.entity.SharedTransportCompany;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class TthStatementRedactAspectTest {

  @Mock
  private BoUserMailCheckService boUserMailCheckService;

  @Mock
  private CantonBasedUserAdministrationService cantonBasedUserAdministrationService;

  private TthStatementRedactAspect tthStatementRedactAspect;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    tthStatementRedactAspect = new TthStatementRedactAspect(boUserMailCheckService, cantonBasedUserAdministrationService);
  }

  @Test
  void shouldNotRedactForBoUserWhenMailIsAddignesToHimAndStatementIsAnonymus() {
    //given
    String mailTo = "to@example.com";
    TimetableHearingStatement resultObject = getSensitiveStatement();
    resultObject.setDossierContactMail(mailTo);
    resultObject.setStatementAnonymous(true);
    TimetableHearingStatement redactObject = resultObject.toBuilder().build();
    doReturn(true).when(boUserMailCheckService).isCurrentUserAssignedTo(resultObject);
    //when
    tthStatementRedactAspect.redactStatementForBoUser(resultObject, redactObject);

    //then
    assertThat(redactObject.getStatement()).isEqualTo(resultObject.getStatement());
  }

  @Test
  void shouldRedactForBoUserWhenMailIsAssignedToHimAndStatementIsNotAnonymus() {
    //given
    String mailTo = "to@example.com";
    TimetableHearingStatement resultObject = getSensitiveStatement();
    resultObject.setDossierContactMail(mailTo);
    resultObject.setStatementAnonymous(false);
    TimetableHearingStatement redactObject = (TimetableHearingStatement) TthStatementRedactAspect.redactObject(resultObject);
    doReturn(true).when(boUserMailCheckService).isCurrentUserAssignedTo(resultObject);
    //when
    tthStatementRedactAspect.redactStatementForBoUser(resultObject, redactObject);

    //then
    assertThat(redactObject.getStatement()).isEqualTo("*****");
  }

  @Test
  void shouldRedactForBoUserWhenMailIsNotAssingedToHimAndStatementIsAnonymus() {
    //given
    String mailTo = "to@example.com";
    TimetableHearingStatement resultObject = getSensitiveStatement();
    resultObject.setDossierContactMail(mailTo);
    resultObject.setStatementAnonymous(false);
    TimetableHearingStatement redactObject = (TimetableHearingStatement) TthStatementRedactAspect.redactObject(resultObject);
    doReturn(true).when(boUserMailCheckService).isCurrentUserAssignedTo(resultObject);
    //when
    tthStatementRedactAspect.redactStatementForBoUser(resultObject, redactObject);

    //then
    assertThat(redactObject.getStatement()).isEqualTo("*****");
  }

  @Test
  void shouldRedactForBoUserWhenMailIsNotAssingedToHimAndStatementIsNotAnonymus() {
    //given
    String mailTo = "to@example.com";
    TimetableHearingStatement resultObject = getSensitiveStatement();
    resultObject.setDossierContactMail(mailTo);
    resultObject.setStatementAnonymous(false);
    TimetableHearingStatement redactObject = (TimetableHearingStatement) TthStatementRedactAspect.redactObject(resultObject);
    doReturn(false).when(boUserMailCheckService).isCurrentUserAssignedTo(resultObject);
    //when
    tthStatementRedactAspect.redactStatementForBoUser(resultObject, redactObject);

    //then
    assertThat(redactObject.getStatement()).isEqualTo("*****");
  }

  @Test
  void shouldThowrExceptionWhenRedactObjectIsNotTypeOfTimetableHearingStatement() {
    //given
    String mailTo = "to@example.com";
    TimetableHearingStatement resultObject = getSensitiveStatement();
    resultObject.setDossierContactMail(mailTo);
    resultObject.setStatementAnonymous(true);
    TimetableHearingStatementModelV2 redactObject = TimetableHearingStatementModelV2.builder().build();
    doReturn(true).when(boUserMailCheckService).isCurrentUserAssignedTo(resultObject);
    //when && then
    assertThrows(IllegalStateException.class,
        () -> tthStatementRedactAspect.redactStatementForBoUser(resultObject, redactObject));
  }

  @Test
  void shouldRedactForCantonUser() {
    //given
    TimetableHearingStatement resultObject = getSensitiveStatement();
    doReturn(true).when(cantonBasedUserAdministrationService).isAtLeastExplicitReader(ApplicationType.TIMETABLE_HEARING);
    //when
    Object result = tthStatementRedactAspect.doRedact(resultObject);

    //then
    assertThat(result).isInstanceOf(TimetableHearingStatement.class);
    assertThat(result).usingRecursiveComparison().isEqualTo(resultObject);
  }

  @Test
  void shouldNotRedactForCantonUser() {
    //given
    TimetableHearingStatement resultObject = getSensitiveStatement();
    doReturn(false).when(cantonBasedUserAdministrationService).isAtLeastExplicitReader(ApplicationType.TIMETABLE_HEARING);
    //when
    Object result = tthStatementRedactAspect.doRedact(resultObject);

    //then
    assertThat(result).isInstanceOf(TimetableHearingStatement.class);
    assertThat(result).usingRecursiveComparison().isEqualTo(TthStatementRedactAspect.redactObject(resultObject));
  }

  @Test
  void shouldRedactStatementCorrectly() {
    // given
    TimetableHearingStatement sensitiveStatement = getSensitiveStatement();

    TimetableHearingStatement redactedStatement = getRedactedSensitiveStatement();

    // when & then
    Object redactObject = TthStatementRedactAspect.redactObject(sensitiveStatement);
    assertThat(redactObject).usingRecursiveComparison().isEqualTo(redactedStatement);
  }

  @Test
  void shouldRedactZipIntegerWhenNull() {
    // given
    TimetableHearingStatement sensitiveStatement = TimetableHearingStatement.builder()
        .timetableYear(2023L)
        .statementStatus(StatementStatus.RECEIVED)
        .statement("Ich mag bitte mehr Bös fahren")
        .statementSender(StatementSender.builder()
            .zip(null)
            .emails(List.of("mike@thebike.com"))
            .build())
        .build();

    TimetableHearingStatement redactedStatement = TimetableHearingStatement.builder()
        .timetableYear(2023L)
        .statementStatus(StatementStatus.RECEIVED)
        .statement("*****")
        .statementSender(StatementSender.builder()
            .zip(null)
            .emails(List.of("*****"))
            .build())
        .build();

    // when & then
    Object redactObject = TthStatementRedactAspect.redactObject(sensitiveStatement);
    assertThat(redactObject).usingRecursiveComparison().isEqualTo(redactedStatement);
  }

  @Test
  void shouldRedactNonAnonymousFile() {
    // given
    TimetableHearingStatement sensitiveStatement = TimetableHearingStatement.builder()
        .timetableYear(2023L)
        .statementStatus(StatementStatus.RECEIVED)
        .statement("Ich mag bitte mehr Bös fahren")
        .statementSender(StatementSender.builder()
            .emails(List.of("mike@thebike.com"))
            .build())
        .documents(Set.of(StatementDocument.builder()
            .fileName("doc1")
            .fileSize(6454L)
            .anonymous(false)
            .build()))
        .dossierContactMail("mail@tu.ch")
        .build();

    // when & then
    //given
    TimetableHearingStatement redactedObject = (TimetableHearingStatement) TthStatementRedactAspect.redactObject(sensitiveStatement);
    doReturn(true).when(boUserMailCheckService).isCurrentUserAssignedTo(sensitiveStatement);
    //when
    tthStatementRedactAspect.redactStatementForBoUser(sensitiveStatement, redactedObject);
    assertThat(redactedObject.getDocuments()).isEmpty();
  }

  private TimetableHearingStatement getSensitiveStatement() {
    return TimetableHearingStatement.builder()
        .timetableYear(2023L)
        .statementStatus(StatementStatus.RECEIVED)
        .statement("Ich mag bitte mehr Bös fahren")
        .version(1)
        .ttfnid("ch:1:ttfnid:1235234")
        .swissCanton(SwissCanton.BERN)
        .stopPlace("Erste Haltestelle ... weisst ja")
        .responsibleTransportCompanies(Set.of(SharedTransportCompany.builder()
            .id(1L)
            .number("#0001")
            .abbreviation("SBB")
            .businessRegisterName("Schweizerische Bundesbahnen")
            .build()))
        .statementSender(StatementSender.builder()
            .firstName("Mike")
            .lastName("von Bike")
            .organisation("Bewerber")
            .street("Hauptstrasse 1")
            .zip(39012)
            .city("Algund")
            .emails(List.of("mike@thebike.com"))
            .build())
        .publicComment("Weil ich mag")
        .documents(Set.of(StatementDocument.builder()
            .fileName("doc1")
            .fileSize(6454L)
            .build()))
        .build();
  }

  private static TimetableHearingStatement getRedactedSensitiveStatement() {
    return TimetableHearingStatement.builder()
        .timetableYear(2023L)
        .statementStatus(StatementStatus.RECEIVED)
        .statement("*****")
        .version(1)
        .ttfnid("ch:1:ttfnid:1235234")
        .swissCanton(SwissCanton.BERN)
        .stopPlace("Erste Haltestelle ... weisst ja")
        .responsibleTransportCompanies(Set.of(SharedTransportCompany.builder()
            .id(1L)
            .number("#0001")
            .abbreviation("SBB")
            .businessRegisterName("Schweizerische Bundesbahnen")
            .build()))
        .statementSender(StatementSender.builder()
            .firstName("*****")
            .lastName("*****")
            .organisation("*****")
            .street("*****")
            .zip(0)
            .city("*****")
            .emails(List.of("*****"))
            .build())
        .publicComment("*****")
        .documents(Set.of(StatementDocument.builder()
            .fileName("doc1")
            .fileSize(6454L)
            .build()))
        .build();
  }

}