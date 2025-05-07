package ch.sbb.line.directory.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ch.sbb.atlas.api.timetable.hearing.TimetableHearingStatementModelV1;
import ch.sbb.atlas.api.timetable.hearing.TimetableHearingStatementModelV2;
import ch.sbb.atlas.api.timetable.hearing.TimetableHearingStatementRequestParams;
import ch.sbb.atlas.api.timetable.hearing.TimetableHearingStatementResponsibleTransportCompanyModel;
import ch.sbb.atlas.api.timetable.hearing.TimetableHearingStatementSenderModelV1;
import ch.sbb.atlas.api.timetable.hearing.TimetableHearingStatementSenderModelV2;
import ch.sbb.atlas.api.timetable.hearing.enumeration.StatementStatus;
import ch.sbb.atlas.kafka.model.SwissCanton;
import ch.sbb.atlas.kafka.model.transport.company.SharedTransportCompanyModel;
import ch.sbb.atlas.model.Status;
import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.atlas.model.exception.NotFoundException.FileNotFoundException;
import ch.sbb.atlas.model.exception.NotFoundException.IdNotFoundException;
import ch.sbb.line.directory.entity.TimetableFieldNumberVersion;
import ch.sbb.line.directory.entity.TimetableHearingStatement;
import ch.sbb.line.directory.entity.TimetableHearingYear;
import ch.sbb.line.directory.exception.TtfnidNotFoundException;
import ch.sbb.line.directory.helper.PdfFiles;
import ch.sbb.line.directory.mapper.TimetableHearingStatementMapperV2;
import ch.sbb.line.directory.model.TimetableHearingStatementSearchRestrictions;
import ch.sbb.line.directory.repository.SharedTransportCompanyRepository;
import ch.sbb.line.directory.repository.TimetableFieldNumberVersionRepository;
import ch.sbb.line.directory.repository.TimetableHearingStatementRepository;
import ch.sbb.line.directory.repository.TimetableHearingYearRepository;
import ch.sbb.line.directory.service.hearing.TimetableHearingStatementService;
import ch.sbb.line.directory.service.hearing.TimetableHearingYearService;
import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

@IntegrationTest
class TimetableHearingStatementServiceTest {

  private static final long YEAR = 2023L;

  private final TimetableHearingYearRepository timetableHearingYearRepository;
  private final TimetableHearingYearService timetableHearingYearService;
  private final TimetableHearingStatementRepository timetableHearingStatementRepository;
  private final TimetableHearingStatementService timetableHearingStatementService;
  private final TimetableHearingStatementMapperV2 timetableHearingStatementMapperV2;
  private final SharedTransportCompanyRepository sharedTransportCompanyRepository;

  private final TimetableFieldNumberVersionRepository timetableFieldNumberVersionRepository;

  @Autowired
  TimetableHearingStatementServiceTest(TimetableHearingYearRepository timetableHearingYearRepository,
      TimetableHearingYearService timetableHearingYearService,
      TimetableHearingStatementRepository timetableHearingStatementRepository,
      TimetableHearingStatementService timetableHearingStatementService,
      TimetableHearingStatementMapperV2 timetableHearingStatementMapperV2,
      SharedTransportCompanyRepository sharedTransportCompanyRepository,
      TimetableFieldNumberVersionRepository timetableFieldNumberVersionRepository) {
    this.timetableHearingYearRepository = timetableHearingYearRepository;
    this.timetableHearingYearService = timetableHearingYearService;
    this.timetableHearingStatementRepository = timetableHearingStatementRepository;
    this.timetableHearingStatementService = timetableHearingStatementService;
    this.timetableHearingStatementMapperV2 = timetableHearingStatementMapperV2;
    this.sharedTransportCompanyRepository = sharedTransportCompanyRepository;
    this.timetableFieldNumberVersionRepository = timetableFieldNumberVersionRepository;
  }

  private static TimetableHearingYear getTimetableHearingYear() {
    return TimetableHearingYear.builder()
        .timetableYear(YEAR)
        .hearingFrom(LocalDate.of(2022, 1, 1))
        .hearingTo(LocalDate.of(2022, 2, 1))
        .build();
  }

  private static TimetableHearingStatementModelV1 buildTimetableHearingStatementModelV1() {
    return TimetableHearingStatementModelV1.builder()
        .timetableYear(YEAR)
        .swissCanton(SwissCanton.BERN)
        .statementSender(TimetableHearingStatementSenderModelV1.builder()
            .email("fabienne.mueller@sbb.ch")
            .build())
        .statement("Ich hätte gerne mehrere Verbindungen am Abend.")
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
    sharedTransportCompanyRepository.deleteAll();
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
  void shouldGetHearingStatementCreatedWithV1() {
    timetableHearingYearService.createTimetableHearing(getTimetableHearingYear());

    TimetableHearingStatementModelV1 timetableHearingStatementModel = buildTimetableHearingStatementModelV1();
    TimetableHearingStatementModelV1 createdStatement = timetableHearingStatementService.createHearingStatementV1(
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

    assertThatThrownBy(
        () -> timetableHearingStatementService.getTimetableHearingStatementById(createdStatement.getId() + 1)).isInstanceOf(
        IdNotFoundException.class);
  }

  @Test
  void shouldGetDocumentFromHearingStatement() {
    timetableHearingYearService.createTimetableHearing(getTimetableHearingYear());
    TimetableHearingStatementModelV2 timetableHearingStatementModel = buildTimetableHearingStatementModelV2();

    List<MultipartFile> documents = new ArrayList<>();
    documents.add(PdfFiles.MULTIPART_FILES.get(0));
    documents.add(PdfFiles.MULTIPART_FILES.get(1));

    TimetableHearingStatementModelV2 createdStatement = timetableHearingStatementService.createHearingStatementV2(
        timetableHearingStatementModel, documents);

    String originalFilename = PdfFiles.MULTIPART_FILES.get(0).getOriginalFilename();
    File statementDocument = timetableHearingStatementService.getStatementDocument(createdStatement.getId(), originalFilename);
    assertTrue(statementDocument.getName().contains("dummy.pdf"));
  }

  @Test
  void shouldDeleteDocumentFromHearingStatement() {
    timetableHearingYearService.createTimetableHearing(getTimetableHearingYear());
    TimetableHearingStatementModelV2 timetableHearingStatementModel = buildTimetableHearingStatementModelV2();

    TimetableHearingStatementModelV2 createdStatement = timetableHearingStatementService.createHearingStatementV2(
        timetableHearingStatementModel, Collections.emptyList());
    TimetableHearingStatement createdStatementEntity = timetableHearingStatementMapperV2.toEntity(createdStatement);

    timetableHearingStatementService.deleteStatementDocument(createdStatementEntity,
        PdfFiles.MULTIPART_FILES.getFirst().getOriginalFilename());
    assertThatThrownBy(() -> timetableHearingStatementService.getStatementDocument(createdStatement.getId(),
        PdfFiles.MULTIPART_FILES.getFirst().getOriginalFilename())).isInstanceOf(
        FileNotFoundException.class);
  }

  @Test
  void shouldThrowIllegalArgumentExceptionWhenDeletingDocument() {
    timetableHearingYearService.createTimetableHearing(getTimetableHearingYear());

    assertThatThrownBy(() -> timetableHearingStatementService.deleteStatementDocument(new TimetableHearingStatement(),
        PdfFiles.MULTIPART_FILES.getFirst().getOriginalFilename())).isInstanceOf(
        IllegalArgumentException.class);
  }

  @Test
  void shouldThrowIllegalArgumentExceptionWhenDeletingEmptyStringDocumentName() {
    timetableHearingYearService.createTimetableHearing(getTimetableHearingYear());
    TimetableHearingStatementModelV2 timetableHearingStatementModel = buildTimetableHearingStatementModelV2();

    List<MultipartFile> documents = new ArrayList<>();
    documents.add(PdfFiles.MULTIPART_FILES.get(0));
    documents.add(PdfFiles.MULTIPART_FILES.get(1));

    TimetableHearingStatementModelV2 createdStatement = timetableHearingStatementService.createHearingStatementV2(
        timetableHearingStatementModel, documents);
    TimetableHearingStatement createdStatementEntity = timetableHearingStatementMapperV2.toEntity(createdStatement);

    assertThatThrownBy(() -> timetableHearingStatementService.deleteStatementDocument(createdStatementEntity, "")).isInstanceOf(
        IllegalArgumentException.class);
  }

  @Test
  void shouldNotDoAnythingIfDeleteUnknownDocument() {
    timetableHearingYearService.createTimetableHearing(getTimetableHearingYear());
    TimetableHearingStatementModelV2 timetableHearingStatementModel = buildTimetableHearingStatementModelV2();

    TimetableHearingStatementModelV2 createdStatement = timetableHearingStatementService.createHearingStatementV2(
        timetableHearingStatementModel, Collections.emptyList());
    TimetableHearingStatement createdStatementEntity = timetableHearingStatementMapperV2.toEntity(createdStatement);

    assertDoesNotThrow(() -> timetableHearingStatementService.deleteStatementDocument(createdStatementEntity,
        PdfFiles.MULTIPART_FILES.getFirst().getOriginalFilename()));
  }

  @Test
  void shouldNotGetHearingStatementIfIdIsNotValissd() {
    timetableHearingYearService.createTimetableHearing(getTimetableHearingYear());
    TimetableHearingStatementModelV2 timetableHearingStatementModel = buildTimetableHearingStatementModelV2();

    TimetableHearingStatementModelV2 createdStatement = timetableHearingStatementService.createHearingStatementV2(
        timetableHearingStatementModel, Collections.emptyList());

    assertThatThrownBy(
        () -> timetableHearingStatementService.getTimetableHearingStatementById(createdStatement.getId() + 1)).isInstanceOf(
        IdNotFoundException.class);
  }

  @Test
  void shouldCreateHearingStatement() {
    timetableHearingYearService.createTimetableHearing(getTimetableHearingYear());
    TimetableHearingStatementModelV2 timetableHearingStatementModel = buildTimetableHearingStatementModelV2();

    TimetableHearingStatementModelV2 hearingStatement = timetableHearingStatementService.createHearingStatementV2(
        timetableHearingStatementModel, Collections.emptyList());

    assertThat(hearingStatement).isNotNull();
    assertThat(hearingStatement.getStatementStatus()).isEqualTo(StatementStatus.RECEIVED);
  }

  @Test
  void shouldNotCreateHearingStatementIfYearIsUnknown() {
    TimetableHearingStatementModelV2 timetableHearingStatementModel = buildTimetableHearingStatementModelV2();

    assertThatThrownBy(() -> timetableHearingStatementService.createHearingStatementV2(timetableHearingStatementModel,
        Collections.emptyList())).isInstanceOf(
        IdNotFoundException.class);
  }

  @Test
  void shouldNotCreateHearingStatementIfTtfnidNotExists() {
    timetableHearingYearService.createTimetableHearing(getTimetableHearingYear());
    TimetableHearingStatementModelV2 timetableHearingStatementModel = buildTimetableHearingStatementModelV2();
    timetableHearingStatementModel.setTtfnid("ABC");
    List<MultipartFile> emptyList = Collections.emptyList();

    assertThatThrownBy(() -> timetableHearingStatementService.createHearingStatementV2(timetableHearingStatementModel, emptyList))
        .isInstanceOf(TtfnidNotFoundException.class);
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

    TimetableHearingStatement updatedStatement = timetableHearingStatementService.updateHearingStatement(
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

    assertThatThrownBy(
        () -> timetableHearingStatementService.updateHearingStatement(timetableHearingStatement, updatingStatement,
            Collections.emptyList())).isInstanceOf(
        IdNotFoundException.class);
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

    assertThatThrownBy(
        () -> timetableHearingStatementService.updateHearingStatement(timetableHearingStatement, updatingStatement,
            emptyList)).isInstanceOf(
        TtfnidNotFoundException.class);
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
            .firstName("Luca")
            .emails(Set.of("fabienne.mueller@sbb.ch"))
            .build())
        .statement("Ich hätte gerne mehrere Verbindungen am Abend.")
        .build();
    TimetableHearingStatementModelV2 created =
        timetableHearingStatementService.createHearingStatementV2(timetableHearingStatementModel,
            Collections.emptyList());

    TimetableHearingStatementSearchRestrictions searchRestrictions = TimetableHearingStatementSearchRestrictions.builder()
        .statementRequestParams(TimetableHearingStatementRequestParams.builder()
            .searchCriterias(List.of("gerne", "Luca", created.getId().toString()))
            .canton(SwissCanton.BERN)
            .timetableHearingYear(YEAR)
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
            .firstName("Luca")
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
            .searchCriterias(List.of("gerne", "Luca", Long.toString(fakeId)))
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
    TimetableFieldNumberVersion timetableFieldNumber = TimetableFieldNumberVersion.builder()
        .ttfnid("ch:1:ttfnid:2341234")
        .swissTimetableFieldNumber("1234")
        .number("5678")
        .description("Description")
        .status(Status.VALIDATED)
        .businessOrganisation("Business Organisation")
        .validFrom(LocalDate.now())
        .validTo(LocalDate.now().plusYears(1))
        .build();

    timetableFieldNumberVersionRepository.saveAndFlush(timetableFieldNumber);

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
    TimetableFieldNumberVersion timetableFieldNumber = TimetableFieldNumberVersion.builder()
        .ttfnid("ch:1:ttfnid:2341234")
        .swissTimetableFieldNumber("1234")
        .number("5678")
        .description("Description")
        .status(Status.VALIDATED)
        .businessOrganisation("Business Organisation")
        .validFrom(LocalDate.now())
        .validTo(LocalDate.now().plusYears(1))
        .build();

    timetableFieldNumberVersionRepository.saveAndFlush(timetableFieldNumber);

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
    sharedTransportCompanyRepository.save(SharedTransportCompanyModel.builder()
        .id(4L)
        .abbreviation("SBB")
        .businessRegisterName("Schweizerische Bundesbahnen").build());
    sharedTransportCompanyRepository.save(SharedTransportCompanyModel.builder()
        .id(5L)
        .abbreviation("BLS")
        .businessRegisterName("Basel Land Stationen ? :D").build());
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
            .firstName("Luca")
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

}
