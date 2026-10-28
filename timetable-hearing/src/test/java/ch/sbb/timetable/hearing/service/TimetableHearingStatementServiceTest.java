package ch.sbb.timetable.hearing.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import ch.sbb.atlas.api.bodi.TransportCompanyModel;
import ch.sbb.atlas.api.client.bodi.TransportCompanyClient;
import ch.sbb.atlas.api.client.line.ttfn.TimetableFieldNumberApiV1Client;
import ch.sbb.atlas.api.lidi.TimetableFieldNumberApiInternal;
import ch.sbb.atlas.api.timetable.hearing.TimetableHearingStatementModelV2;
import ch.sbb.atlas.api.timetable.hearing.TimetableHearingStatementRequestParams;
import ch.sbb.atlas.api.timetable.hearing.TimetableHearingStatementResponsibleTransportCompanyModel;
import ch.sbb.atlas.api.timetable.hearing.TimetableHearingStatementSenderModelV2;
import ch.sbb.atlas.api.timetable.hearing.enumeration.StatementStatus;
import ch.sbb.atlas.api.timetable.hearing.model.BatchUpdateTimetableHearingStatementsModel;
import ch.sbb.atlas.kafka.model.SwissCanton;
import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.atlas.model.exception.BadRequestException;
import ch.sbb.atlas.model.exception.NotFoundException;
import ch.sbb.atlas.model.exception.NotFoundException.IdNotFoundException;
import ch.sbb.atlas.model.exception.SimpleAtlasException;
import ch.sbb.timetable.hearing.entity.StatementDocument;
import ch.sbb.timetable.hearing.entity.StatementSender;
import ch.sbb.timetable.hearing.entity.TimetableHearingStatement;
import ch.sbb.timetable.hearing.entity.TimetableHearingYear;
import ch.sbb.timetable.hearing.exception.StatementPartOfDossierException;
import ch.sbb.timetable.hearing.helper.PdfFiles;
import ch.sbb.timetable.hearing.mapper.TimetableHearingStatementMapperV2;
import ch.sbb.timetable.hearing.model.TimetableHearingStatementSearchRestrictions;
import ch.sbb.timetable.hearing.repository.TimetableHearingStatementRepository;
import ch.sbb.timetable.hearing.repository.TimetableHearingYearRepository;
import feign.FeignException;
import feign.Request;
import feign.RequestTemplate;
import feign.Response;
import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.web.multipart.MultipartFile;

@IntegrationTest
class TimetableHearingStatementServiceTest {

  private static final long YEAR = 2023L;

  @MockitoBean
  private TimetableFieldNumberApiV1Client timetableFieldNumberApiV1Client;

  @MockitoBean
  private TimetableFieldNumberApiInternal timetableFieldNumberApiInternal;

  @MockitoBean
  private TransportCompanyClient transportCompanyClient;

  @MockitoSpyBean
  private TimetableHearingPdfsAmazonService pdfsUploadAmazonService;

  private final TimetableHearingYearRepository timetableHearingYearRepository;
  private final TimetableHearingYearService timetableHearingYearService;
  private final TimetableHearingStatementRepository timetableHearingStatementRepository;
  private final TimetableHearingStatementService timetableHearingStatementService;
  private final TimetableHearingStatementMapperV2 timetableHearingStatementMapperV2;

  @Autowired
  TimetableHearingStatementServiceTest(TimetableHearingYearRepository timetableHearingYearRepository,
      TimetableHearingYearService timetableHearingYearService,
      TimetableHearingStatementRepository timetableHearingStatementRepository,
      TimetableHearingStatementService timetableHearingStatementService,
      TimetableHearingStatementMapperV2 timetableHearingStatementMapperV2) {
    this.timetableHearingYearRepository = timetableHearingYearRepository;
    this.timetableHearingYearService = timetableHearingYearService;
    this.timetableHearingStatementRepository = timetableHearingStatementRepository;
    this.timetableHearingStatementService = timetableHearingStatementService;
    this.timetableHearingStatementMapperV2 = timetableHearingStatementMapperV2;
  }

  private static TimetableHearingYear getTimetableHearingYear() {
    return TimetableHearingYear.builder()
        .timetableYear(YEAR)
        .hearingFrom(LocalDate.of(2022, 1, 1))
        .hearingTo(LocalDate.of(2022, 2, 1))
        .build();
  }

  private static TimetableHearingStatementModelV2 buildTimetableHearingStatementModelV2() {
    return TimetableHearingStatementModelV2.builder()
        .timetableYear(YEAR)
        .swissCanton(SwissCanton.BERN)
        .statementSender(TimetableHearingStatementSenderModelV2.builder()
            .emails(Set.of("fabienne.mueller@sbb.ch"))
            .build())
        .statement("Ich hätte gerne mehrere Verbindungen am Abend.")
        .build();
  }

  @AfterEach
  void tearDown() {
    timetableHearingStatementRepository.deleteAll();
    timetableHearingYearRepository.deleteAll();
  }

  @Test
  void shouldGetHearingStatement() {
    timetableHearingYearService.createTimetableHearing(getTimetableHearingYear());

    TimetableHearingStatementModelV2 timetableHearingStatementModel = buildTimetableHearingStatementModelV2();
    TimetableHearingStatementModelV2 createdStatement = timetableHearingStatementService.createHearingStatementV2(
        timetableHearingStatementModel, Collections.emptyList());

    TimetableHearingStatement hearingStatement = timetableHearingStatementService.getTimetableHearingStatementById(
        createdStatement.getId());

    assertThat(hearingStatement).isNotNull();
    assertThat(hearingStatement.getStatementStatus()).isEqualTo(StatementStatus.RECEIVED);
    assertThat(hearingStatement.getStatement()).isEqualTo(createdStatement.getStatement());
  }

  @Test
  void shouldNotGetHearingStatementIfIdIsNotValid() {
    timetableHearingYearService.createTimetableHearing(getTimetableHearingYear());
    TimetableHearingStatementModelV2 timetableHearingStatementModel = buildTimetableHearingStatementModelV2();

    TimetableHearingStatementModelV2 createdStatement = timetableHearingStatementService.createHearingStatementV2(
        timetableHearingStatementModel, Collections.emptyList());

    long unknownId = createdStatement.getId() + 1;
    assertThatThrownBy(
        () -> timetableHearingStatementService.getTimetableHearingStatementById(unknownId)).isInstanceOf(
        IdNotFoundException.class);
  }

  @Test
  void shouldGetDocumentFromHearingStatement() {
    //given
    timetableHearingYearService.createTimetableHearing(getTimetableHearingYear());
    TimetableHearingStatementModelV2 timetableHearingStatementModel = buildTimetableHearingStatementModelV2();

    List<MultipartFile> documents = new ArrayList<>();
    documents.add(PdfFiles.MULTIPART_FILES.getFirst());
    documents.add(PdfFiles.MULTIPART_FILES.get(1));
    StatementDocument.builder().fileName("dummy.pdf").build();

    TimetableHearingStatementModelV2 createdStatement = timetableHearingStatementService.createHearingStatementV2(
        timetableHearingStatementModel, documents);
    String originalFilename = PdfFiles.MULTIPART_FILES.getFirst().getOriginalFilename();
    TimetableHearingStatement timetableHearingStatement = timetableHearingStatementService.getTimetableHearingStatementById(
        createdStatement.getId());
    //when
    File statementDocument = timetableHearingStatementService.getStatementDocument(timetableHearingStatement,
        originalFilename);
    //then
    assertThat(statementDocument.getName()).contains("dummy.pdf");
  }

  @Test
  void shouldCreateHearingStatement() {
    timetableHearingYearService.createTimetableHearing(getTimetableHearingYear());
    TimetableHearingStatementModelV2 timetableHearingStatementModel = buildTimetableHearingStatementModelV2();

    TimetableHearingStatementModelV2 hearingStatement = timetableHearingStatementService.createHearingStatementV2(
        timetableHearingStatementModel, Collections.emptyList());

    assertThat(hearingStatement).isNotNull();
    assertThat(hearingStatement.getStatementStatus()).isEqualTo(StatementStatus.RECEIVED);
    verifyNoInteractions(transportCompanyClient);
  }

  @Test
  void shouldResolveTransportCompaniesOnceAndUseBodiDataOnCreate() {
    timetableHearingYearService.createTimetableHearing(getTimetableHearingYear());
    TimetableHearingStatementModelV2 statement = buildTimetableHearingStatementModelV2();
    setUpTransportCompanies(statement);

    TimetableHearingStatementModelV2 result = timetableHearingStatementService.createHearingStatementV2(statement, List.of());

    assertTransportCompaniesResolvedOnceWithBodiData(result);
  }

  @Test
  void shouldResolveTransportCompaniesOnceAndUseBodiDataOnUpdate() {
    timetableHearingYearService.createTimetableHearing(getTimetableHearingYear());
    TimetableHearingStatementModelV2 statement = timetableHearingStatementService.createHearingStatementV2(
        buildTimetableHearingStatementModelV2(), List.of());
    TimetableHearingStatement existingStatement =
        timetableHearingStatementService.getTimetableHearingStatementsById(statement.getId());
    setUpTransportCompanies(statement);

    TimetableHearingStatementModelV2 result =
        timetableHearingStatementService.updateHearingStatement(existingStatement, statement, List.of());

    assertTransportCompaniesResolvedOnceWithBodiData(result);
  }

  private void setUpTransportCompanies(TimetableHearingStatementModelV2 statement) {
    when(transportCompanyClient.getTransportCompany(1L)).thenReturn(TransportCompanyModel.builder()
        .id(1L).abbreviation("SBB").number("#0001").businessRegisterName("SBB AG").build());
    when(transportCompanyClient.getTransportCompany(2L)).thenReturn(TransportCompanyModel.builder()
        .id(2L).abbreviation("BLS").number("#0002").businessRegisterName("BLS AG").build());
    TimetableHearingStatementResponsibleTransportCompanyModel untrustedCompany =
        TimetableHearingStatementResponsibleTransportCompanyModel.builder()
            .id(1L).abbreviation("Wrong").number("Wrong").businessRegisterName("Wrong").build();
    statement.setResponsibleTransportCompanies(List.of(untrustedCompany, untrustedCompany,
        TimetableHearingStatementResponsibleTransportCompanyModel.builder().id(2L).build()));
  }

  private void assertTransportCompaniesResolvedOnceWithBodiData(TimetableHearingStatementModelV2 result) {
    assertThat(result.getResponsibleTransportCompanies()).hasSize(2);
    assertThat(result.getResponsibleTransportCompanies().getFirst())
        .usingRecursiveComparison()
        .isEqualTo(TimetableHearingStatementResponsibleTransportCompanyModel.builder()
            .id(1L).abbreviation("SBB").number("#0001").businessRegisterName("SBB AG").build());
    assertThat(result.getResponsibleTransportCompaniesDisplay()).isEqualTo("BLS, SBB");
    TimetableHearingStatement persisted = timetableHearingStatementService.getTimetableHearingStatementsById(result.getId());
    assertThat(persisted.getResponsibleTransportCompanyIds()).containsExactlyInAnyOrder(1L, 2L);
    assertThat(persisted.getResponsibleTransportCompaniesDisplay()).isEqualTo("BLS, SBB");
    assertThat(result.getEtagVersion()).isEqualTo(persisted.getVersion());
    verify(transportCompanyClient).getTransportCompany(1L);
    verify(transportCompanyClient).getTransportCompany(2L);
    verifyNoMoreInteractions(transportCompanyClient);
  }

  @ParameterizedTest
  @ValueSource(ints = {404, 503})
  void shouldRejectCreateBeforeUploadingDocumentsWhenTransportCompanyCannotBeResolved(int status) {
    timetableHearingYearService.createTimetableHearing(getTimetableHearingYear());
    TimetableHearingStatementModelV2 statement = buildTimetableHearingStatementModelV2();
    statement.setResponsibleTransportCompanies(List.of(
        TimetableHearingStatementResponsibleTransportCompanyModel.builder().id(99L).build()));
    FeignException failure = transportCompanyFailure(status);
    when(transportCompanyClient.getTransportCompany(99L)).thenThrow(failure);
    List<MultipartFile> documents = List.of(PdfFiles.MULTIPART_FILES.getFirst());

    assertThatThrownBy(() -> timetableHearingStatementService.createHearingStatementV2(statement, documents))
        .satisfies(exception -> assertTransportCompanyFailure(exception, failure));

    assertThat(timetableHearingStatementRepository.count()).isZero();
    verifyNoInteractions(pdfsUploadAmazonService);
  }

  @ParameterizedTest
  @ValueSource(ints = {404, 503})
  void shouldRejectUpdateBeforeChangingStatementOrDocumentsWhenTransportCompanyCannotBeResolved(int status) {
    timetableHearingYearService.createTimetableHearing(getTimetableHearingYear());
    TimetableHearingStatementModelV2 statement = timetableHearingStatementService.createHearingStatementV2(
        buildTimetableHearingStatementModelV2(), List.of(PdfFiles.MULTIPART_FILES.getFirst()));
    TimetableHearingStatement existingStatement =
        timetableHearingStatementService.getTimetableHearingStatementsById(statement.getId());
    statement.setResponsibleTransportCompanies(List.of(
        TimetableHearingStatementResponsibleTransportCompanyModel.builder().id(99L).build()));
    statement.setStatement("Changed statement");
    statement.setDocuments(List.of());
    clearInvocations(pdfsUploadAmazonService);
    FeignException failure = transportCompanyFailure(status);
    when(transportCompanyClient.getTransportCompany(99L)).thenThrow(failure);
    List<MultipartFile> documents = List.of();

    assertThatThrownBy(() -> timetableHearingStatementService.updateHearingStatement(existingStatement, statement, documents))
        .satisfies(exception -> assertTransportCompanyFailure(exception, failure));

    TimetableHearingStatement persisted = timetableHearingStatementService.getTimetableHearingStatementsById(statement.getId());
    assertThat(persisted.getStatement()).isEqualTo(existingStatement.getStatement());
    assertThat(persisted.getResponsibleTransportCompanyIds()).isEmpty();
    assertThat(persisted.getDocuments()).hasSize(1);
    assertThat(persisted.getVersion()).isEqualTo(existingStatement.getVersion());
    verifyNoInteractions(pdfsUploadAmazonService);
  }

  @Test
  void shouldRejectTransportCompanyWithoutIdBeforeCallingBodi() {
    timetableHearingYearService.createTimetableHearing(getTimetableHearingYear());
    TimetableHearingStatementModelV2 statement = buildTimetableHearingStatementModelV2();
    statement.setResponsibleTransportCompanies(List.of(
        TimetableHearingStatementResponsibleTransportCompanyModel.builder().build()));
    List<MultipartFile> documents = List.of();

    assertThatThrownBy(() -> timetableHearingStatementService.createHearingStatementV2(statement, documents))
        .isInstanceOf(BadRequestException.class);

    assertThat(timetableHearingStatementRepository.count()).isZero();
    verifyNoInteractions(transportCompanyClient, pdfsUploadAmazonService);
  }

  @Test
  void shouldRejectNullTransportCompanyBeforeCallingBodi() {
    timetableHearingYearService.createTimetableHearing(getTimetableHearingYear());
    TimetableHearingStatementModelV2 statement = buildTimetableHearingStatementModelV2();
    statement.setResponsibleTransportCompanies(Collections.singletonList(null));
    List<MultipartFile> documents = List.of();

    assertThatThrownBy(() -> timetableHearingStatementService.createHearingStatementV2(statement, documents))
        .isInstanceOf(BadRequestException.class);

    assertThat(timetableHearingStatementRepository.count()).isZero();
    verifyNoInteractions(transportCompanyClient, pdfsUploadAmazonService);
  }

  private static FeignException transportCompanyFailure(int status) {
    return FeignException.errorStatus("getTransportCompany", Response.builder()
        .status(status)
        .reason("BODI error")
        .request(Request.create(Request.HttpMethod.GET, "http://bodi/internal/transport-companies/99",
            Map.of(), null, new RequestTemplate()))
        .build());
  }

  private static void assertTransportCompanyFailure(Throwable exception, FeignException failure) {
    if (failure.status() == 404) {
      assertThat(exception).hasCause(failure)
          .isInstanceOfSatisfying(NotFoundException.class, notFound -> {
            assertThat(notFound.getErrorResponse().getStatus()).isEqualTo(404);
            assertThat(notFound.getErrorResponse().getDetails()).singleElement()
                .satisfies(detail -> assertThat(detail.getField()).isEqualTo("responsibleTransportCompanies.id"));
          });
    } else {
      assertThat(exception).isSameAs(failure);
    }
  }

  @Test
  void shouldNotCreateHearingStatementIfYearIsUnknown() {
    TimetableHearingStatementModelV2 timetableHearingStatementModel = buildTimetableHearingStatementModelV2();

    List<MultipartFile> documents = Collections.emptyList();
    assertThatThrownBy(() -> timetableHearingStatementService.createHearingStatementV2(timetableHearingStatementModel,
        documents)).isInstanceOf(
        IdNotFoundException.class);
  }

  @Test
  void shouldNotCreateHearingStatementIfTtfnidNotExists() {
    timetableHearingYearService.createTimetableHearing(getTimetableHearingYear());
    TimetableHearingStatementModelV2 timetableHearingStatementModel = buildTimetableHearingStatementModelV2();
    timetableHearingStatementModel.setTtfnid("ABC");
    List<MultipartFile> emptyList = Collections.emptyList();

    when(timetableFieldNumberApiV1Client.getAllVersionsVersioned(any())).thenThrow(
        new NotFoundException("ttfnid", "ABC") {
        });

    assertThatException().isThrownBy(
        () -> timetableHearingStatementService.createHearingStatementV2(timetableHearingStatementModel, emptyList));
  }

  @Test
  void shouldUpdateHearingStatement() {
    timetableHearingYearService.createTimetableHearing(getTimetableHearingYear());
    List<MultipartFile> docs = Collections.emptyList();

    TimetableHearingStatementSenderModelV2 timetableHearingStatementSenderModelV2 = new TimetableHearingStatementSenderModelV2();
    timetableHearingStatementSenderModelV2.setFirstName("Jack");
    timetableHearingStatementSenderModelV2.setLastName("Smith");
    timetableHearingStatementSenderModelV2.setCity("Bern");
    timetableHearingStatementSenderModelV2.setOrganisation("BigCompany");
    timetableHearingStatementSenderModelV2.setStreet("MyStreet");
    timetableHearingStatementSenderModelV2.setEmails(Set.of("hello@op.com", "test@test.com"));
    TimetableHearingStatementModelV2 timetableHearingStatementModel = buildTimetableHearingStatementModelV2();
    timetableHearingStatementModel.setStatementSender(timetableHearingStatementSenderModelV2);
    TimetableHearingStatement timetableHearingStatement =
        timetableHearingStatementMapperV2.toEntity(timetableHearingStatementModel);

    TimetableHearingStatementModelV2 updatingStatement = timetableHearingStatementService.createHearingStatementV2(
        timetableHearingStatementModel, docs);
    updatingStatement.setStatementStatus(StatementStatus.JUNK);
    timetableHearingStatementSenderModelV2.setEmails(new HashSet<>(Set.of("antohertest@test.com")));
    updatingStatement.setStatementSender(timetableHearingStatementSenderModelV2);

    TimetableHearingStatementModelV2 updatedStatement = timetableHearingStatementService.updateHearingStatement(
        timetableHearingStatement,
        updatingStatement, docs);

    assertThat(updatedStatement).isNotNull();
    assertThat(updatedStatement.getStatementStatus()).isEqualTo(StatementStatus.JUNK);
  }

  @Test
  void shouldNotUpdateHearingStatementIfYearIsUnknown() {
    timetableHearingYearService.createTimetableHearing(getTimetableHearingYear());
    TimetableHearingStatementModelV2 timetableHearingStatementModel = buildTimetableHearingStatementModelV2();
    TimetableHearingStatement timetableHearingStatement =
        timetableHearingStatementMapperV2.toEntity(timetableHearingStatementModel);

    TimetableHearingStatementModelV2 updatingStatement = timetableHearingStatementService.createHearingStatementV2(
        timetableHearingStatementModel, Collections.emptyList());
    updatingStatement.setTimetableYear(2020L);

    List<MultipartFile> documents = Collections.emptyList();
    assertThatThrownBy(
        () -> timetableHearingStatementService.updateHearingStatement(timetableHearingStatement, updatingStatement,
            documents)).isInstanceOf(
        IdNotFoundException.class);
  }

  @Test
  void shouldNotUpdateHearingStatementIfItIsPartOfDossier() {
    timetableHearingYearService.createTimetableHearing(getTimetableHearingYear());

    TimetableHearingStatementModelV2 timetableHearingStatementModel = buildTimetableHearingStatementModelV2();
    List<MultipartFile> documents = Collections.emptyList();
    TimetableHearingStatementModelV2 createdStatement =
        timetableHearingStatementService.createHearingStatementV2(timetableHearingStatementModel, documents);
    TimetableHearingStatement statement =
        timetableHearingStatementService.getTimetableHearingStatementsById(createdStatement.getId());

    timetableHearingStatementService.updateStatementFromDossier(statement, BatchUpdateTimetableHearingStatementsModel.builder()
        .statementStatus(StatementStatus.IN_REVIEW)
        .dossierCanton(statement.getSwissCanton())
        .dossierId(1L)
        .build());

    assertThatThrownBy(
        () -> timetableHearingStatementService.updateHearingStatement(statement, timetableHearingStatementModel,
            documents)).isInstanceOf(StatementPartOfDossierException.class);
  }

  @Test
  void shouldThrowExceptionIfItIsAlreadyPartOfDossier() {
    timetableHearingYearService.createTimetableHearing(getTimetableHearingYear());

    TimetableHearingStatementModelV2 timetableHearingStatementModel = buildTimetableHearingStatementModelV2();
    List<MultipartFile> documents = Collections.emptyList();
    TimetableHearingStatementModelV2 createdStatement =
        timetableHearingStatementService.createHearingStatementV2(timetableHearingStatementModel, documents);
    TimetableHearingStatement statement =
        timetableHearingStatementService.getTimetableHearingStatementsById(createdStatement.getId());
    timetableHearingStatementService.updateStatementFromDossier(statement, BatchUpdateTimetableHearingStatementsModel.builder()
        .statementStatus(StatementStatus.IN_REVIEW)
        .dossierCanton(statement.getSwissCanton())
        .dossierId(1L)
        .build());

    BatchUpdateTimetableHearingStatementsModel model =
        BatchUpdateTimetableHearingStatementsModel.builder()
            .statementStatus(StatementStatus.IN_REVIEW)
            .dossierCanton(statement.getSwissCanton())
            .dossierId(2L)
            .build();

    assertThatThrownBy(
        () -> timetableHearingStatementService.updateStatementFromDossier(statement,
            model)).isInstanceOf(StatementPartOfDossierException.class);
  }

  @Test
  void shouldNotUpdateHearingStatementIfItIsDifferentCanton() {
    timetableHearingYearService.createTimetableHearing(getTimetableHearingYear());

    TimetableHearingStatementModelV2 timetableHearingStatementModel = buildTimetableHearingStatementModelV2();
    List<MultipartFile> documents = Collections.emptyList();
    TimetableHearingStatementModelV2 createdStatement =
        timetableHearingStatementService.createHearingStatementV2(timetableHearingStatementModel, documents);
    TimetableHearingStatement statement =
        timetableHearingStatementService.getTimetableHearingStatementsById(createdStatement.getId());

    BatchUpdateTimetableHearingStatementsModel model =
        BatchUpdateTimetableHearingStatementsModel.builder()
            .statementStatus(StatementStatus.IN_REVIEW)
            .dossierCanton(SwissCanton.ZUG)
            .dossierId(1L)
            .build();

    assertThatThrownBy(
        () -> timetableHearingStatementService.updateStatementFromDossier(statement, model)).isInstanceOf(
        SimpleAtlasException.class);
  }

  @Test
  void shouldNotUpdateHearingStatementIfTtfnidNotExists() {
    timetableHearingYearService.createTimetableHearing(getTimetableHearingYear());
    TimetableHearingStatementModelV2 timetableHearingStatementModel = buildTimetableHearingStatementModelV2();

    TimetableHearingStatement timetableHearingStatement =
        timetableHearingStatementMapperV2.toEntity(timetableHearingStatementModel);

    TimetableHearingStatementModelV2 updatingStatement = timetableHearingStatementService.createHearingStatementV2(
        timetableHearingStatementModel, Collections.emptyList());
    updatingStatement.setTtfnid("ungueltig");
    List<MultipartFile> emptyList = Collections.emptyList();

    when(timetableFieldNumberApiV1Client.getAllVersionsVersioned(any())).thenThrow(
        new NotFoundException("ttfnid", "ABC") {
        });

    assertThatException().isThrownBy(
        () -> timetableHearingStatementService.updateHearingStatement(timetableHearingStatement, updatingStatement,
            emptyList));
  }

  @Test
  void shouldMoveClosedStatementsToNextYearWithStatusUpdateFromMovedToReceived() {
    // given
    timetableHearingYearService.createTimetableHearing(getTimetableHearingYear());

    TimetableHearingStatementModelV2 statement;
    // Statement 1
    statement = buildTimetableHearingStatementModelV2();
    statement.setStatementStatus(StatementStatus.RECEIVED);
    statement.setTimetableYear(YEAR - 1);
    Long statement1Id = timetableHearingStatementRepository.save(timetableHearingStatementMapperV2.toEntity(statement)).getId();

    // Statement 2
    statement = buildTimetableHearingStatementModelV2();
    statement.setStatementStatus(StatementStatus.IN_REVIEW);
    Long statement2Id = timetableHearingStatementRepository.save(timetableHearingStatementMapperV2.toEntity(statement)).getId();

    // Statement 3
    statement = buildTimetableHearingStatementModelV2();
    statement.setStatementStatus(StatementStatus.RECEIVED);
    Long statement3Id = timetableHearingStatementRepository.save(timetableHearingStatementMapperV2.toEntity(statement)).getId();

    // Statement 4
    statement = buildTimetableHearingStatementModelV2();
    statement.setStatementStatus(StatementStatus.JUNK);
    Long statement4Id = timetableHearingStatementRepository.save(timetableHearingStatementMapperV2.toEntity(statement)).getId();

    // Statement 5
    statement = buildTimetableHearingStatementModelV2();
    statement.setStatementStatus(StatementStatus.MOVED);
    Long statement5Id = timetableHearingStatementRepository.save(timetableHearingStatementMapperV2.toEntity(statement)).getId();

    // when
    timetableHearingStatementService.moveClosedStatementsToNextYearWithStatusUpdates(YEAR);

    // then
    assertThat(timetableHearingStatementRepository.findAll()).hasSize(5);

    assertThat(timetableHearingStatementRepository.findById(statement1Id).orElseThrow().getStatementStatus()).isEqualTo(
        StatementStatus.RECEIVED);
    assertThat(timetableHearingStatementRepository.findById(statement1Id).orElseThrow().getTimetableYear()).isEqualTo(YEAR - 1);

    assertThat(timetableHearingStatementRepository.findById(statement2Id).orElseThrow().getStatementStatus()).isEqualTo(
        StatementStatus.IN_REVIEW);
    assertThat(timetableHearingStatementRepository.findById(statement2Id).orElseThrow().getTimetableYear()).isEqualTo(YEAR + 1);

    assertThat(timetableHearingStatementRepository.findById(statement3Id).orElseThrow().getStatementStatus()).isEqualTo(
        StatementStatus.RECEIVED);
    assertThat(timetableHearingStatementRepository.findById(statement3Id).orElseThrow().getTimetableYear()).isEqualTo(YEAR + 1);

    assertThat(timetableHearingStatementRepository.findById(statement4Id).orElseThrow().getStatementStatus()).isEqualTo(
        StatementStatus.JUNK);
    assertThat(timetableHearingStatementRepository.findById(statement4Id).orElseThrow().getTimetableYear()).isEqualTo(YEAR);

    assertThat(timetableHearingStatementRepository.findById(statement5Id).orElseThrow().getStatementStatus()).isEqualTo(
        StatementStatus.RECEIVED);
    assertThat(timetableHearingStatementRepository.findById(statement5Id).orElseThrow().getTimetableYear()).isEqualTo(YEAR + 1);
  }

  @Test
  void shouldFindStatementBySearchCriteria() {
    timetableHearingYearService.createTimetableHearing(getTimetableHearingYear());
    TimetableHearingStatementModelV2 timetableHearingStatementModel = TimetableHearingStatementModelV2.builder()
        .timetableYear(YEAR)
        .swissCanton(SwissCanton.BERN)
        .statementSender(TimetableHearingStatementSenderModelV2.builder()
            .firstName("Firtsname")
            .emails(Set.of("fabienne.mueller@sbb.ch"))
            .build())
        .statement("Ich hätte gerne mehrere Verbindungen am Abend.")
        .build();
    TimetableHearingStatementModelV2 created =
        timetableHearingStatementService.createHearingStatementV2(timetableHearingStatementModel,
            Collections.emptyList());

    TimetableHearingStatementSearchRestrictions searchRestrictions = TimetableHearingStatementSearchRestrictions.builder()
        .statementRequestParams(TimetableHearingStatementRequestParams.builder()
            .searchCriterias(List.of("gerne", "Firtsname", created.getId().toString()))
            .canton(SwissCanton.BERN)
            .timetableHearingYear(YEAR)
            .build())
        .pageable(Pageable.unpaged())
        .build();

    Page<TimetableHearingStatement> hearingStatements = timetableHearingStatementService.getHearingStatements(searchRestrictions);

    assertThat(hearingStatements.getTotalElements()).isEqualTo(1);
  }

  @Test
  void shouldFindStatementByTopicSearchCriteria() {
    timetableHearingYearService.createTimetableHearing(getTimetableHearingYear());
    TimetableHearingStatementModelV2 timetableHearingStatementModel = TimetableHearingStatementModelV2.builder()
        .timetableYear(YEAR)
        .swissCanton(SwissCanton.BERN)
        .statementSender(TimetableHearingStatementSenderModelV2.builder()
            .emails(Set.of("fabienne.mueller@sbb.ch"))
            .build())
        .statement("Ich hätte gerne mehrere Verbindungen am Abend.")
        .topic("Wichtiges Thema")
        .build();
    timetableHearingStatementService.createHearingStatementV2(timetableHearingStatementModel, Collections.emptyList());

    TimetableHearingStatementSearchRestrictions searchRestrictions = TimetableHearingStatementSearchRestrictions.builder()
        .statementRequestParams(TimetableHearingStatementRequestParams.builder()
            .searchCriterias(List.of("Thema"))
            .build())
        .pageable(Pageable.unpaged())
        .build();

    Page<TimetableHearingStatement> hearingStatements = timetableHearingStatementService.getHearingStatements(searchRestrictions);

    assertThat(hearingStatements.getTotalElements()).isEqualTo(1);
  }

  @Test
  void shouldNotFindStatementBySearchCriteria() {
    timetableHearingYearService.createTimetableHearing(getTimetableHearingYear());
    TimetableHearingStatementModelV2 timetableHearingStatementModel = TimetableHearingStatementModelV2.builder()
        .timetableYear(YEAR)
        .swissCanton(SwissCanton.BERN)
        .statementSender(TimetableHearingStatementSenderModelV2.builder()
            .firstName("Firtsname")
            .emails(Set.of("fabienne.mueller@sbb.ch"))
            .build())
        .statement("Ich hätte gerne mehrere Verbindungen am Abend.")
        .build();
    TimetableHearingStatementModelV2 created =
        timetableHearingStatementService.createHearingStatementV2(timetableHearingStatementModel,
            Collections.emptyList());

    long fakeId = created.getId() + 10L;

    TimetableHearingStatementSearchRestrictions searchRestrictions = TimetableHearingStatementSearchRestrictions.builder()
        .statementRequestParams(TimetableHearingStatementRequestParams.builder()
            .searchCriterias(List.of("gerne", "Firtsname", Long.toString(fakeId)))
            .canton(SwissCanton.BERN)
            .timetableHearingYear(YEAR)
            .build())
        .pageable(Pageable.unpaged())
        .build();

    Page<TimetableHearingStatement> hearingStatements = timetableHearingStatementService.getHearingStatements(searchRestrictions);

    assertThat(hearingStatements.getTotalElements()).isZero();
  }

  @Test
  void shouldFindStatementByTtfnid() {
    timetableHearingYearService.createTimetableHearing(getTimetableHearingYear());

    TimetableHearingStatementModelV2 timetableHearingStatementModel = TimetableHearingStatementModelV2.builder()
        .timetableYear(YEAR)
        .swissCanton(SwissCanton.BERN)
        .ttfnid("ch:1:ttfnid:2341234")
        .statementSender(TimetableHearingStatementSenderModelV2.builder()
            .emails(Set.of("fabienne.mueller@sbb.ch"))
            .build())
        .statement("Ich hätte gerne mehrere Verbindungen am Abend.")
        .build();
    timetableHearingStatementService.createHearingStatementV2(timetableHearingStatementModel, Collections.emptyList());

    TimetableHearingStatementSearchRestrictions searchRestrictions = TimetableHearingStatementSearchRestrictions.builder()
        .statementRequestParams(TimetableHearingStatementRequestParams.builder()
            .ttfnid("ch:1:ttfnid:2341234")
            .build())
        .pageable(Pageable.unpaged())
        .build();

    Page<TimetableHearingStatement> hearingStatements = timetableHearingStatementService.getHearingStatements(searchRestrictions);

    assertThat(hearingStatements.getTotalElements()).isEqualTo(1);

    // Negative Test
    searchRestrictions = TimetableHearingStatementSearchRestrictions.builder()
        .statementRequestParams(TimetableHearingStatementRequestParams.builder()
            .ttfnid("other bs")
            .build())
        .pageable(Pageable.unpaged())
        .build();

    hearingStatements = timetableHearingStatementService.getHearingStatements(searchRestrictions);

    assertThat(hearingStatements.getTotalElements()).isZero();
  }

  @Test
  void shouldFindStatementByStatus() {
    timetableHearingYearService.createTimetableHearing(getTimetableHearingYear());

    TimetableHearingStatementModelV2 timetableHearingStatementModel = TimetableHearingStatementModelV2.builder()
        .timetableYear(YEAR)
        .swissCanton(SwissCanton.BERN)
        .ttfnid("ch:1:ttfnid:2341234")
        .statementSender(TimetableHearingStatementSenderModelV2.builder()
            .emails(Set.of("fabienne.mueller@sbb.ch"))
            .build())
        .statement("Ich hätte gerne mehrere Verbindungen am Abend.")
        .build();
    timetableHearingStatementService.createHearingStatementV2(timetableHearingStatementModel, Collections.emptyList());

    TimetableHearingStatementSearchRestrictions searchRestrictions = TimetableHearingStatementSearchRestrictions.builder()
        .statementRequestParams(TimetableHearingStatementRequestParams.builder()
            .statusRestrictions(List.of(StatementStatus.RECEIVED))
            .build())
        .pageable(Pageable.unpaged())
        .build();

    Page<TimetableHearingStatement> hearingStatements = timetableHearingStatementService.getHearingStatements(searchRestrictions);

    assertThat(hearingStatements.getTotalElements()).isEqualTo(1);

    // Negative Test
    searchRestrictions = TimetableHearingStatementSearchRestrictions.builder()
        .statementRequestParams(TimetableHearingStatementRequestParams.builder()
            .statusRestrictions(List.of(StatementStatus.JUNK))
            .build())
        .pageable(Pageable.unpaged())
        .build();

    hearingStatements = timetableHearingStatementService.getHearingStatements(searchRestrictions);

    assertThat(hearingStatements.getTotalElements()).isZero();
  }

  @Test
  void shouldFindStatementByTransportCompany() {
    TransportCompanyModel sbb = TransportCompanyModel.builder()
        .id(4L)
        .abbreviation("SBB")
        .businessRegisterName("Schweizerische Bundesbahnen").build();
    TransportCompanyModel bls = TransportCompanyModel.builder()
        .id(5L)
        .abbreviation("BLS")
        .businessRegisterName("Basel Land Stationen ? :D").build();
    when(transportCompanyClient.getTransportCompany(4L)).thenReturn(sbb);
    when(transportCompanyClient.getTransportCompany(5L)).thenReturn(bls);
    timetableHearingYearService.createTimetableHearing(getTimetableHearingYear());
    TimetableHearingStatementModelV2 timetableHearingStatementModel = TimetableHearingStatementModelV2.builder()
        .timetableYear(YEAR)
        .swissCanton(SwissCanton.BERN)
        .responsibleTransportCompanies(List.of(TimetableHearingStatementResponsibleTransportCompanyModel.builder()
                .id(4L)
                .abbreviation("SBB")
                .businessRegisterName("Schweizerische Bundesbahnen")
                .build(),
            TimetableHearingStatementResponsibleTransportCompanyModel.builder()
                .id(5L)
                .abbreviation("BLS")
                .businessRegisterName("Basel Land Stationen ? :D")
                .build()))
        .statementSender(TimetableHearingStatementSenderModelV2.builder()
            .firstName("Firtsname")
            .emails(Set.of("fabienne.mueller@sbb.ch"))
            .build())
        .statement("Ich hätte gerne mehrere Verbindungen am Abend.")
        .build();
    timetableHearingStatementService.createHearingStatementV2(timetableHearingStatementModel, Collections.emptyList());

    TimetableHearingStatementSearchRestrictions searchRestrictions = TimetableHearingStatementSearchRestrictions.builder()
        .statementRequestParams(TimetableHearingStatementRequestParams.builder()
            .transportCompanies(List.of(4L, 5L))
            .build())
        .pageable(Pageable.unpaged())
        .build();

    Page<TimetableHearingStatement> hearingStatements = timetableHearingStatementService.getHearingStatements(searchRestrictions);

    assertThat(hearingStatements.getTotalElements()).isEqualTo(1);

    //Negative Test
    searchRestrictions = TimetableHearingStatementSearchRestrictions.builder()
        .statementRequestParams(TimetableHearingStatementRequestParams.builder()
            .transportCompanies(List.of(3L))
            .build())
        .pageable(Pageable.unpaged())
        .build();

    hearingStatements = timetableHearingStatementService.getHearingStatements(searchRestrictions);
    assertThat(hearingStatements.getTotalElements()).isZero();
  }

  @Test
  void shouldFindStatementByPartOfDossier() {
    TimetableHearingStatement timetableHearingStatementModel = TimetableHearingStatement.builder()
        .statementStatus(StatementStatus.RECEIVED)
        .timetableYear(YEAR)
        .swissCanton(SwissCanton.BERN)
        .statementSender(StatementSender.builder()
            .emails(List.of("mail@be.ch"))
            .build())
        .statement("Ich hätte gerne mehrere Verbindungen am Abend.")
        .dossierId(1L)
        .build();
    timetableHearingStatementRepository.save(timetableHearingStatementModel);

    TimetableHearingStatementSearchRestrictions searchRestrictions = TimetableHearingStatementSearchRestrictions.builder()
        .statementRequestParams(TimetableHearingStatementRequestParams.builder()
            .partOfDossier(true)
            .build())
        .pageable(Pageable.unpaged())
        .build();

    Page<TimetableHearingStatement> hearingStatements = timetableHearingStatementService.getHearingStatements(searchRestrictions);

    assertThat(hearingStatements.getTotalElements()).isEqualTo(1);

    // Negative Test
    searchRestrictions = TimetableHearingStatementSearchRestrictions.builder()
        .statementRequestParams(TimetableHearingStatementRequestParams.builder()
            .partOfDossier(false)
            .build())
        .pageable(Pageable.unpaged())
        .build();

    hearingStatements = timetableHearingStatementService.getHearingStatements(searchRestrictions);

    assertThat(hearingStatements.getTotalElements()).isZero();
  }
}
