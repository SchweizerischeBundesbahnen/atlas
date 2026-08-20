package ch.sbb.timetable.hearing.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ch.sbb.atlas.api.client.user.administration.UserAdministrationClient;
import ch.sbb.atlas.api.model.BoContactAssociated;
import ch.sbb.atlas.api.timetable.hearing.enumeration.HearingStatus;
import ch.sbb.atlas.api.timetable.hearing.enumeration.StatementStatus;
import ch.sbb.atlas.api.user.administration.PermissionModel;
import ch.sbb.atlas.api.user.administration.TransportCompanyDossierAnswerPermissionRestrictionModel;
import ch.sbb.atlas.api.user.administration.UserModel;
import ch.sbb.atlas.api.workflow.tth.dossier.DossierStatus;
import ch.sbb.atlas.kafka.model.SwissCanton;
import ch.sbb.atlas.kafka.model.user.admin.ApplicationType;
import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.atlas.model.exception.SimpleAtlasException;
import ch.sbb.atlas.user.administration.security.service.BoUserMailCheckService;
import ch.sbb.timetable.hearing.entity.Dossier;
import ch.sbb.timetable.hearing.entity.DossierQuestion;
import ch.sbb.timetable.hearing.entity.StatementSender;
import ch.sbb.timetable.hearing.entity.TimetableHearingStatement;
import ch.sbb.timetable.hearing.entity.TimetableHearingYear;
import ch.sbb.timetable.hearing.exception.NoHearingCurrentlyActiveException;
import ch.sbb.timetable.hearing.mail.DossierNotificationService;
import ch.sbb.timetable.hearing.repository.DossierRepository;
import ch.sbb.timetable.hearing.repository.TimetableHearingStatementRepository;
import ch.sbb.timetable.hearing.repository.TimetableHearingYearRepository;
import ch.sbb.timetable.hearing.search.DossierRequestParams;
import ch.sbb.timetable.hearing.search.DossierSearchRestrictions;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@IntegrationTest
@ExtendWith(MockitoExtension.class)
class DossierServiceTest {

  private static final long TIMETABLE_YEAR = 2024L;

  @Autowired
  private DossierService dossierService;

  @Autowired
  private DossierRepository dossierRepository;

  @Autowired
  private TimetableHearingYearRepository timetableHearingYearRepository;

  @Autowired
  private TimetableHearingStatementRepository timetableHearingStatementRepository;

  @MockitoBean
  private DossierNotificationService dossierNotificationService;

  @MockitoBean
  private UserAdministrationClient userAdministrationClient;

  @MockitoBean
  private BoUserMailCheckService boUserMailCheckService;

  private Dossier exampleDossier;
  private DossierQuestion question;
  private TimetableHearingYear year;
  private TimetableHearingStatement firstStatement;
  private TimetableHearingStatement secondStatement;

  @BeforeEach
  void setUp() {
    when(userAdministrationClient.getUserByMail(any())).thenReturn(UserModel.builder()
        .sbbUserId("u123456")
        .permissions(Set.of(PermissionModel.builder()
            .application(ApplicationType.TIMETABLE_HEARING)
            .permissionRestrictions(List.of(new TransportCompanyDossierAnswerPermissionRestrictionModel(true)))
            .build()))
        .build());

    when(boUserMailCheckService.isCurrentUserAssignedTo(any(BoContactAssociated.class))).thenReturn(true);

    year = timetableHearingYearRepository.saveAndFlush(TimetableHearingYear.builder()
        .timetableYear(TIMETABLE_YEAR)
        .hearingStatus(HearingStatus.ACTIVE)
        .hearingFrom(LocalDate.of(2023, 1, 1))
        .hearingTo(LocalDate.of(2023, 2, 1))
        .build());

    firstStatement = givenStatement();
    secondStatement = givenStatement();

    Dossier dossier = Dossier.builder()
        .swissCanton(SwissCanton.BERN)
        .topic("Bern, Salem - Takt")
        .internalComment("Noch mit Bernmobil abklären")
        .publicComment("In Abklärung mit GO")
        .boContactMail("bern@mobil.be")
        .boContactSbbuid("u123456")
        .dossierStatus(DossierStatus.ADDED)
        .statementIds(List.of(firstStatement.getId(), secondStatement.getId()))
        .boDeadlineToAnswer(LocalDate.now().plusDays(7))
        .timetableYear(year.getTimetableYear())
        .build();
    question = DossierQuestion.builder()
        .dossier(dossier)
        .question("Kann der Takt erhöht werden?")
        .build();
    dossier.setDossierQuestions(List.of(question));
    exampleDossier = dossierRepository.saveAndFlush(dossier);
  }

  @AfterEach
  void tearDown() {
    dossierRepository.deleteAll();
    timetableHearingStatementRepository.deleteAll();
    timetableHearingYearRepository.deleteAll();
  }

  private TimetableHearingStatement givenStatement() {
    return timetableHearingStatementRepository.saveAndFlush(TimetableHearingStatement.builder()
        .timetableYear(TIMETABLE_YEAR)
        .statementStatus(StatementStatus.RECEIVED)
        .swissCanton(SwissCanton.BERN)
        .statement("Statement")
        .statementSender(StatementSender.builder().emails(List.of("statement@sender.ch")).build())
        .build());
  }

  private TimetableHearingStatement reloadStatement(TimetableHearingStatement statement) {
    return timetableHearingStatementRepository.findById(statement.getId()).orElseThrow();
  }

  @Test
  void shouldGetDossier() {
    // when
    Dossier dossier = dossierService.getDossierById(exampleDossier.getId());

    // then
    assertThat(dossier.getId()).isNotNull();
  }

  @Test
  void shouldSaveDossier() {
    TimetableHearingStatement statement = givenStatement();

    Dossier dossier = dossierService.createDossier(Dossier.builder()
        .swissCanton(SwissCanton.BERN)
        .topic("Bern, Salem - Takt")
        .internalComment("Noch mit Bernmobil abklären")
        .publicComment("In Abklärung mit GO")
        .statementIds(List.of(statement.getId()))
        .boContactMail("bern@mobil.be")
        .boContactSbbuid("u123456")
        .dossierStatus(DossierStatus.ADDED)
        .boDeadlineToAnswer(LocalDate.now().plusDays(7))
        .build());
    dossier.setDossierQuestions(List.of(DossierQuestion.builder()
        .dossier(dossier)
        .question("Kann der Takt erhöht werden?")
        .build()));

    assertThat(dossier.getId()).isNotNull();
    assertThat(dossier.getTimetableYear()).isEqualTo(TIMETABLE_YEAR);
    assertThat(dossier.getDossierQuestions()).hasSize(1);
    assertThat(reloadStatement(statement).getStatementStatus()).isEqualTo(StatementStatus.IN_REVIEW);
  }

  @Test
  void shouldThrowExceptionWhenActiveTimetableHearingYearNotFound() {
    dossierRepository.deleteAll();
    timetableHearingYearRepository.deleteAll();

    Dossier dossier = Dossier.builder()
        .swissCanton(SwissCanton.BERN)
        .topic("Test Topic")
        .boContactMail("test@example.com")
        .statementIds(List.of(firstStatement.getId()))
        .boDeadlineToAnswer(LocalDate.now().plusDays(7))
        .build();

    assertThatThrownBy(() -> dossierService.createDossier(dossier))
        .isInstanceOf(NoHearingCurrentlyActiveException.class);
  }

  @Test
  void shouldCancelDossier() {
    // when
    dossierService.completeDossier(exampleDossier, DossierStatus.CANCELED);

    // then
    Dossier canceledDossier = dossierService.getDossierById(exampleDossier.getId());
    assertThat(canceledDossier.getDossierStatus()).isEqualTo(DossierStatus.CANCELED);

    assertThat(reloadStatement(firstStatement).getStatementStatus()).isEqualTo(StatementStatus.RECEIVED);
    assertThat(reloadStatement(secondStatement).getStatementStatus()).isEqualTo(StatementStatus.RECEIVED);
  }

  @Test
  void shouldDissolveDossier() {
    // when
    exampleDossier.setDossierStatus(DossierStatus.ACCEPTED);
    dossierService.completeDossier(exampleDossier, DossierStatus.DISSOLVED);

    // then
    Dossier dissolvedDossier = dossierService.getDossierById(exampleDossier.getId());
    assertThat(dissolvedDossier.getDossierStatus()).isEqualTo(DossierStatus.DISSOLVED);

    assertThat(reloadStatement(firstStatement).getStatementStatus()).isEqualTo(StatementStatus.ACCEPTED);
    assertThat(reloadStatement(secondStatement).getStatementStatus()).isEqualTo(StatementStatus.ACCEPTED);
  }

  @Test
  void shouldNotCompleteToAdded() {
    assertThatExceptionOfType(SimpleAtlasException.class).isThrownBy(
        () -> dossierService.completeDossier(exampleDossier, DossierStatus.ADDED));
  }

  @Test
  void shouldSendQuestionToBo() {
    // given
    TimetableHearingStatement statement = givenStatement();
    Dossier dossier = Dossier.builder()
        .swissCanton(SwissCanton.BERN)
        .topic("Bern, Salem - Takt")
        .internalComment("Noch mit Bernmobil abklären")
        .publicComment("In Abklärung mit GO")
        .boContactMail("bern@mobil.be")
        .boContactSbbuid("u123456")
        .dossierStatus(DossierStatus.DOSSIER_BO_CHECK)
        .statementIds(List.of(statement.getId()))
        .boDeadlineToAnswer(LocalDate.now().plusDays(7))
        .build();
    dossier = dossierService.createDossier(dossier);
    assertThat(dossier.getId()).isNotNull();

    // when
    dossierService.sendDossierToBo(dossier);

    // then
    Dossier updatedDossier = dossierService.getDossierById(dossier.getId());
    assertThat(updatedDossier.getDossierStatus()).isEqualTo(DossierStatus.DOSSIER_BO_CHECK);
    verify(dossierNotificationService).notifyBoAboutNewQuestion(any());
  }

  @Test
  void shouldUpdateDossier() {
    // when
    String newPublicComment = "Wir haben uns geeinigt, den Takt zu erhöhen";
    Dossier dossier = exampleDossier.toBuilder().publicComment(newPublicComment).build();
    Dossier updatedDossier = dossierService.updateDossier(exampleDossier.getId(), dossier);

    // then
    assertThat(updatedDossier.getDossierStatus()).isEqualTo(DossierStatus.ADDED);
    assertThat(updatedDossier.getPublicComment()).isEqualTo(newPublicComment);

    assertThat(reloadStatement(firstStatement).getPublicComment()).isEqualTo(newPublicComment);
    assertThat(reloadStatement(secondStatement).getPublicComment()).isEqualTo(newPublicComment);
  }

  @Test
  void shouldNotUpdateDossierInBoCheck() {
    exampleDossier.setDossierStatus(DossierStatus.DOSSIER_BO_CHECK);
    exampleDossier = dossierRepository.saveAndFlush(exampleDossier);

    Long dossierId = exampleDossier.getId();
    assertThatExceptionOfType(SimpleAtlasException.class).isThrownBy(
        () -> dossierService.updateDossier(dossierId, exampleDossier)
    );
  }

  @Test
  void shouldNotUpdateBoAnswerOnCantonUpdate() {
    // when
    String answerFromBo = "Answer from BO";
    exampleDossier.getDossierQuestions().getFirst().setAnswerToCanton(answerFromBo);
    exampleDossier = dossierRepository.saveAndFlush(exampleDossier);

    Dossier dossier = exampleDossier.toBuilder().build();
    dossier.getDossierQuestions().getFirst().setAnswerToCanton("Self edited :)");

    Long dossierId = exampleDossier.getId();
    assertThatExceptionOfType(SimpleAtlasException.class).isThrownBy(
        () -> dossierService.updateDossier(dossierId, dossier));
  }

  @Test
  void shouldUpdateDossierRemovingStatement() {
    // given
    TimetableHearingStatement addedStatement = givenStatement();

    // when
    List<Long> statementIds = List.of(addedStatement.getId());
    Dossier dossier = exampleDossier.toBuilder().statementIds(statementIds).build();
    Dossier updatedDossier = dossierService.updateDossier(exampleDossier.getId(), dossier);

    // then
    assertThat(updatedDossier.getDossierStatus()).isEqualTo(DossierStatus.ADDED);
    assertThat(updatedDossier.getStatementIds()).hasSameElementsAs(statementIds);

    // Removed statements got the dossier relation removed and the status set back to RECEIVED
    assertThat(reloadStatement(firstStatement)).satisfies(statement -> {
      assertThat(statement.getStatementStatus()).isEqualTo(StatementStatus.RECEIVED);
      assertThat(statement.getDossierId()).isNull();
      assertThat(statement.getTopic()).isEqualTo(exampleDossier.getTopic());
      assertThat(statement.getPublicComment()).isEqualTo(exampleDossier.getPublicComment());
      assertThat(statement.getInternalComment()).isEqualTo(exampleDossier.getInternalComment());
    });

    // Added statement got linked to the dossier and the status set to IN_REVIEW
    assertThat(reloadStatement(addedStatement)).satisfies(statement -> {
      assertThat(statement.getStatementStatus()).isEqualTo(StatementStatus.IN_REVIEW);
      assertThat(statement.getDossierId()).isEqualTo(updatedDossier.getId());
      assertThat(statement.getTopic()).isEqualTo(exampleDossier.getTopic());
      assertThat(statement.getPublicComment()).isEqualTo(exampleDossier.getPublicComment());
      assertThat(statement.getInternalComment()).isEqualTo(exampleDossier.getInternalComment());
    });
  }

  @Test
  void shouldAnswerQuestionAsBo() {
    dossierService.sendDossierToBo(exampleDossier);
    Dossier dossierInBoCheck = dossierService.getDossierById(exampleDossier.getId());

    // when
    String boAnswer = "Joa das geht schon.";
    assertThat(dossierInBoCheck.getDossierStatus()).isEqualTo(DossierStatus.DOSSIER_BO_CHECK);

    dossierService.answerQuestion(question.getId(), boAnswer, dossierInBoCheck);
    // then
    Dossier dossier = dossierService.getDossierById(exampleDossier.getId());

    assertThat(dossier.getDossierStatus()).isEqualTo(DossierStatus.DOSSIER_CANTON_CHECK);

    assertThat(dossier.getDossierQuestions()).hasSize(1);
    assertThat(dossier.getDossierQuestions().getFirst().getAnswerToCanton()).isEqualTo(boAnswer);

    assertThat(reloadStatement(firstStatement).getStatementStatus()).isEqualTo(StatementStatus.RECEIVED);
  }

  @Test
  void shouldNotBeAbleToAnswerQuestionInOtherStatus() {
    Long questionId = question.getId();
    assertThatExceptionOfType(SimpleAtlasException.class)
        .isThrownBy(() -> dossierService.answerQuestion(questionId, "Joa das geht schon.", exampleDossier))
        .withMessage("Dossier is not in status DOSSIER_BO_CHECK");
  }

  @Test
  void shouldGetDossierByQuestionId() {
    Long questionId = exampleDossier.getDossierQuestions().getFirst().getId();
    Dossier foundDossier = dossierService.getDossierByQuestionId(questionId);
    assertThat(foundDossier).usingRecursiveComparison().isEqualTo(exampleDossier);
  }

  @Test
  void shouldFindDossiersBySearchCriteria() {
    List<Dossier> dossiers =
        dossierService.getDossiers(DossierSearchRestrictions.builder()
            .requestParams(DossierRequestParams.builder()
                .searchCriteria("Bern")
                .build())
            .pageable(Pageable.unpaged())
            .build()).getContent();
    assertThat(dossiers).hasSize(1);

    dossiers =
        dossierService.getDossiers(DossierSearchRestrictions.builder()
            .requestParams(DossierRequestParams.builder()
                .searchCriteria("Zürich")
                .build())
            .pageable(Pageable.unpaged())
            .build()).getContent();
    assertThat(dossiers).isEmpty();
  }

  @Test
  void shouldFindDossiersBySearchCriteriaForBo() {
    Dossier dossier = Dossier.builder()
        .swissCanton(SwissCanton.BERN)
        .topic("Bern, Salem - Takt")
        .internalComment("Noch mit Bernmobil abklären")
        .publicComment("In Abklärung mit GO")
        .boContactMail("bern@mobil.be")
        .boContactSbbuid("u123456")
        .dossierStatus(DossierStatus.DOSSIER_BO_CHECK)
        .statementIds(List.of(givenStatement().getId()))
        .boDeadlineToAnswer(LocalDate.now().plusDays(7))
        .timetableYear(year.getTimetableYear())
        .build();

    Dossier dossier2 = Dossier.builder()
        .swissCanton(SwissCanton.BERN)
        .topic("Bern, Salem - Takt")
        .internalComment("Noch mit Bernmobil abklären")
        .publicComment("In Abklärung mit GO")
        .boContactMail("wrongMail@mail.com")
        .boContactSbbuid("u444332")
        .dossierStatus(DossierStatus.DOSSIER_BO_CHECK)
        .statementIds(List.of(givenStatement().getId()))
        .boDeadlineToAnswer(LocalDate.now().plusDays(7))
        .timetableYear(year.getTimetableYear())
        .build();

    Dossier dossier3 = Dossier.builder()
        .swissCanton(SwissCanton.BERN)
        .topic("Bern, Salem - Takt")
        .internalComment("Noch mit Bernmobil abklären")
        .publicComment("In Abklärung mit GO")
        .boContactMail("bern@mobil.be")
        .dossierStatus(DossierStatus.ADDED)
        .boContactSbbuid("u123456")
        .statementIds(List.of(givenStatement().getId()))
        .boDeadlineToAnswer(LocalDate.now().plusDays(7))
        .timetableYear(year.getTimetableYear())
        .build();

    dossierRepository.saveAndFlush(dossier);
    dossierRepository.saveAndFlush(dossier2);
    dossierRepository.saveAndFlush(dossier3);

    List<Dossier> dossiers =
        dossierService.getDossiers(DossierSearchRestrictions.builder()
            .requestParams(DossierRequestParams.builder()
                .boContactSbbuid("u123456")
                .statusRestriction(DossierStatus.DOSSIER_BO_CHECK)
                .build())
            .pageable(Pageable.unpaged())
            .build()).getContent();
    assertThat(dossiers).hasSize(1);
  }

  @Test
  void shouldFindDossiersByCanton() {
    List<Dossier> dossiers =
        dossierService.getDossiers(DossierSearchRestrictions.builder()
            .requestParams(DossierRequestParams.builder()
                .canton(SwissCanton.BERN)
                .build())
            .pageable(Pageable.unpaged())
            .build()).getContent();
    assertThat(dossiers).hasSize(1);

    dossiers =
        dossierService.getDossiers(DossierSearchRestrictions.builder()
            .requestParams(DossierRequestParams.builder()
                .canton(SwissCanton.ZUG)
                .build())
            .pageable(Pageable.unpaged())
            .build()).getContent();
    assertThat(dossiers).isEmpty();
  }

  @Test
  void shouldFindDossiersByStatus() {
    List<Dossier> dossiers =
        dossierService.getDossiers(DossierSearchRestrictions.builder()
            .requestParams(DossierRequestParams.builder()
                .statusRestriction(DossierStatus.ADDED)
                .build())
            .pageable(Pageable.unpaged())
            .build()).getContent();
    assertThat(dossiers).hasSize(1);

    dossiers =
        dossierService.getDossiers(DossierSearchRestrictions.builder()
            .requestParams(DossierRequestParams.builder()
                .statusRestriction(DossierStatus.DOSSIER_BO_CHECK)
                .build())
            .pageable(Pageable.unpaged())
            .build()).getContent();
    assertThat(dossiers).isEmpty();
  }

  @Test
  void shouldFindDossiersByTimetableHearingYear() {
    List<Dossier> dossiers =
        dossierService.getDossiers(DossierSearchRestrictions.builder()
            .requestParams(DossierRequestParams.builder()
                .timetableHearingYear(TIMETABLE_YEAR)
                .build())
            .pageable(Pageable.unpaged())
            .build()).getContent();
    assertThat(dossiers).hasSize(1);

    dossiers =
        dossierService.getDossiers(DossierSearchRestrictions.builder()
            .requestParams(DossierRequestParams.builder()
                .timetableHearingYear(2025L)
                .build())
            .pageable(Pageable.unpaged())
            .build()).getContent();
    assertThat(dossiers).isEmpty();
  }
}
