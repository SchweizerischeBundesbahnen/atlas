package ch.sbb.line.directory.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ch.sbb.atlas.api.timetable.hearing.TimetableHearingStatementModel;
import ch.sbb.atlas.api.timetable.hearing.TimetableHearingStatementRequestParams;
import ch.sbb.atlas.api.timetable.hearing.TimetableHearingStatementResponsibleTransportCompanyModel;
import ch.sbb.atlas.api.timetable.hearing.TimetableHearingStatementSenderModel;
import ch.sbb.atlas.api.timetable.hearing.enumeration.StatementStatus;
import ch.sbb.atlas.kafka.model.SwissCanton;
import ch.sbb.atlas.kafka.model.transport.company.SharedTransportCompanyModel;
import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.atlas.model.exception.NotFoundException.FileNotFoundException;
import ch.sbb.atlas.model.exception.NotFoundException.IdNotFoundException;
import ch.sbb.atlas.transport.company.repository.TransportCompanySharingDataAccessor;
import ch.sbb.line.directory.entity.TimetableHearingStatement;
import ch.sbb.line.directory.entity.TimetableHearingYear;
import ch.sbb.line.directory.helper.PdfFiles;
import ch.sbb.line.directory.mapper.TimetableHearingStatementMapper;
import ch.sbb.line.directory.model.TimetableHearingStatementSearchRestrictions;
import ch.sbb.line.directory.repository.TimetableHearingStatementRepository;
import ch.sbb.line.directory.repository.TimetableHearingYearRepository;
import ch.sbb.line.directory.service.hearing.TimetableHearingStatementService;
import ch.sbb.line.directory.service.hearing.TimetableHearingYearService;
import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

@IntegrationTest
 class TimetableHearingStatementServiceTest {

  private static final long YEAR = 2023L;
  private static final TimetableHearingYear TIMETABLE_HEARING_YEAR = TimetableHearingYear.builder()
      .timetableYear(YEAR)
      .hearingFrom(LocalDate.of(2022, 1, 1))
      .hearingTo(LocalDate.of(2022, 2, 1))
      .build();

  private final TimetableHearingYearRepository timetableHearingYearRepository;
  private final TimetableHearingYearService timetableHearingYearService;
  private final TimetableHearingStatementRepository timetableHearingStatementRepository;
  private final TimetableHearingStatementService timetableHearingStatementService;
  private final TimetableHearingStatementMapper timetableHearingStatementMapper;
  private final TransportCompanySharingDataAccessor transportCompanySharingDataAccessor;

  @Autowired
   TimetableHearingStatementServiceTest(TimetableHearingYearRepository timetableHearingYearRepository,
      TimetableHearingYearService timetableHearingYearService,
      TimetableHearingStatementRepository timetableHearingStatementRepository,
      TimetableHearingStatementService timetableHearingStatementService,
      TimetableHearingStatementMapper timetableHearingStatementMapper,
      TransportCompanySharingDataAccessor transportCompanySharingDataAccessor) {
    this.timetableHearingYearRepository = timetableHearingYearRepository;
    this.timetableHearingYearService = timetableHearingYearService;
    this.timetableHearingStatementRepository = timetableHearingStatementRepository;
    this.timetableHearingStatementService = timetableHearingStatementService;
    this.timetableHearingStatementMapper = timetableHearingStatementMapper;
    this.transportCompanySharingDataAccessor = transportCompanySharingDataAccessor;
  }

  private static TimetableHearingStatementModel buildTimetableHearingStatementModel() {
    return TimetableHearingStatementModel.builder()
        .timetableYear(TIMETABLE_HEARING_YEAR.getTimetableYear())
        .swissCanton(SwissCanton.BERN)
        .statementSender(TimetableHearingStatementSenderModel.builder()
            .email("fabienne.mueller@sbb.ch")
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
    timetableHearingYearService.createTimetableHearing(TIMETABLE_HEARING_YEAR);

    TimetableHearingStatementModel timetableHearingStatementModel = buildTimetableHearingStatementModel();
    TimetableHearingStatementModel createdStatement = timetableHearingStatementService.createHearingStatement(
        timetableHearingStatementModel, Collections.emptyList());

    TimetableHearingStatement hearingStatement = timetableHearingStatementService.getTimetableHearingStatementById(
        createdStatement.getId());

    assertThat(hearingStatement).isNotNull();
    assertThat(hearingStatement.getStatementStatus()).isEqualTo(StatementStatus.RECEIVED);
    assertThat(hearingStatement.getStatement()).isEqualTo(createdStatement.getStatement());
  }

  @Test
  void shouldNotGetHearingStatementIfIdIsNotValid() {
    timetableHearingYearService.createTimetableHearing(TIMETABLE_HEARING_YEAR);
    TimetableHearingStatementModel timetableHearingStatementModel = buildTimetableHearingStatementModel();

    TimetableHearingStatementModel createdStatement = timetableHearingStatementService.createHearingStatement(
        timetableHearingStatementModel, Collections.emptyList());

    assertThatThrownBy(
        () -> timetableHearingStatementService.getTimetableHearingStatementById(createdStatement.getId() + 1)).isInstanceOf(
        IdNotFoundException.class);
  }

  @Test
  void shouldGetDocumentFromHearingStatement() {
    timetableHearingYearService.createTimetableHearing(TIMETABLE_HEARING_YEAR);
    TimetableHearingStatementModel timetableHearingStatementModel = buildTimetableHearingStatementModel();

    List<MultipartFile> documents = new ArrayList<>();
    documents.add(PdfFiles.MULTIPART_FILES.get(0));
    documents.add(PdfFiles.MULTIPART_FILES.get(1));

    TimetableHearingStatementModel createdStatement = timetableHearingStatementService.createHearingStatement(
        timetableHearingStatementModel, documents);

    String originalFilename = PdfFiles.MULTIPART_FILES.get(0).getOriginalFilename();
    File statementDocument = timetableHearingStatementService.getStatementDocument(createdStatement.getId(), originalFilename);
    assertTrue(statementDocument.getName().contains("dummy.pdf"));
    assertEquals(PdfFiles.MULTIPART_FILES.get(0).getSize(), statementDocument.length());
  }

  @Test
  void shouldDeleteDocumentFromHearingStatement() {
    timetableHearingYearService.createTimetableHearing(TIMETABLE_HEARING_YEAR);
    TimetableHearingStatementModel timetableHearingStatementModel = buildTimetableHearingStatementModel();

    TimetableHearingStatementModel createdStatement = timetableHearingStatementService.createHearingStatement(
        timetableHearingStatementModel, Collections.emptyList());
    TimetableHearingStatement createdStatementEntity = timetableHearingStatementMapper.toEntity(createdStatement);

    timetableHearingStatementService.deleteStatementDocument(createdStatementEntity,
        PdfFiles.MULTIPART_FILES.get(0).getOriginalFilename());
    assertThatThrownBy(() -> timetableHearingStatementService.getStatementDocument(createdStatement.getId(),
        PdfFiles.MULTIPART_FILES.get(0).getOriginalFilename())).isInstanceOf(
        FileNotFoundException.class);
  }

  @Test
  void shouldThrowIllegalArgumentExceptionWhenDeletingDocument() {
    timetableHearingYearService.createTimetableHearing(TIMETABLE_HEARING_YEAR);

    assertThatThrownBy(() -> timetableHearingStatementService.deleteStatementDocument(new TimetableHearingStatement(),
        PdfFiles.MULTIPART_FILES.get(0).getOriginalFilename())).isInstanceOf(
        IllegalArgumentException.class);
  }

  @Test
  void shouldThrowIllegalArgumentExceptionWhenDeletingEmptyStringDocumentName() {
    timetableHearingYearService.createTimetableHearing(TIMETABLE_HEARING_YEAR);
    TimetableHearingStatementModel timetableHearingStatementModel = buildTimetableHearingStatementModel();

    List<MultipartFile> documents = new ArrayList<>();
    documents.add(PdfFiles.MULTIPART_FILES.get(0));
    documents.add(PdfFiles.MULTIPART_FILES.get(1));

    TimetableHearingStatementModel createdStatement = timetableHearingStatementService.createHearingStatement(
        timetableHearingStatementModel, documents);
    TimetableHearingStatement createdStatementEntity = timetableHearingStatementMapper.toEntity(createdStatement);

    assertThatThrownBy(() -> timetableHearingStatementService.deleteStatementDocument(createdStatementEntity, "")).isInstanceOf(
        IllegalArgumentException.class);
  }

  @Test
  void shouldNotDoAnythingIfDeleteUnknownDocument() {
    timetableHearingYearService.createTimetableHearing(TIMETABLE_HEARING_YEAR);
    TimetableHearingStatementModel timetableHearingStatementModel = buildTimetableHearingStatementModel();

    TimetableHearingStatementModel createdStatement = timetableHearingStatementService.createHearingStatement(
        timetableHearingStatementModel, Collections.emptyList());
    TimetableHearingStatement createdStatementEntity = timetableHearingStatementMapper.toEntity(createdStatement);

    assertDoesNotThrow(() -> timetableHearingStatementService.deleteStatementDocument(createdStatementEntity,
        PdfFiles.MULTIPART_FILES.get(0).getOriginalFilename()));
  }

  @Test
  void shouldNotGetHearingStatementIfIdIsNotValissd() {
    timetableHearingYearService.createTimetableHearing(TIMETABLE_HEARING_YEAR);
    TimetableHearingStatementModel timetableHearingStatementModel = buildTimetableHearingStatementModel();

    TimetableHearingStatementModel createdStatement = timetableHearingStatementService.createHearingStatement(
        timetableHearingStatementModel, Collections.emptyList());

    assertThatThrownBy(
        () -> timetableHearingStatementService.getTimetableHearingStatementById(createdStatement.getId() + 1)).isInstanceOf(
        IdNotFoundException.class);
  }

  @Test
  void shouldCreateHearingStatement() {
    timetableHearingYearService.createTimetableHearing(TIMETABLE_HEARING_YEAR);
    TimetableHearingStatementModel timetableHearingStatementModel = buildTimetableHearingStatementModel();

    TimetableHearingStatementModel hearingStatement = timetableHearingStatementService.createHearingStatement(
        timetableHearingStatementModel, Collections.emptyList());

    assertThat(hearingStatement).isNotNull();
    assertThat(hearingStatement.getStatementStatus()).isEqualTo(StatementStatus.RECEIVED);
  }

  @Test
  void shouldNotCreateHearingStatementIfYearIsUnknown() {
    TimetableHearingStatementModel timetableHearingStatementModel = buildTimetableHearingStatementModel();

    assertThatThrownBy(() -> timetableHearingStatementService.createHearingStatement(timetableHearingStatementModel,
        Collections.emptyList())).isInstanceOf(
        IdNotFoundException.class);
  }

  @Test
  void shouldUpdateHearingStatement() {
    timetableHearingYearService.createTimetableHearing(TIMETABLE_HEARING_YEAR);

    TimetableHearingStatementModel timetableHearingStatementModel = buildTimetableHearingStatementModel();
    TimetableHearingStatement timetableHearingStatement= timetableHearingStatementMapper.toEntity(timetableHearingStatementModel);

    TimetableHearingStatementModel updatingStatement = timetableHearingStatementService.createHearingStatement(
        timetableHearingStatementModel, Collections.emptyList());
    updatingStatement.setStatementStatus(StatementStatus.JUNK);

    TimetableHearingStatement updatedStatement = timetableHearingStatementService.updateHearingStatement(timetableHearingStatement, updatingStatement,
        Collections.emptyList());

    assertThat(updatedStatement).isNotNull();
    assertThat(updatedStatement.getStatementStatus()).isEqualTo(StatementStatus.JUNK);
  }

  @Test
  void shouldNotUpdateHearingStatementIfYearIsUnknown() {
    timetableHearingYearService.createTimetableHearing(TIMETABLE_HEARING_YEAR);
    TimetableHearingStatementModel timetableHearingStatementModel = buildTimetableHearingStatementModel();
    TimetableHearingStatement timetableHearingStatement= timetableHearingStatementMapper.toEntity(timetableHearingStatementModel);

    TimetableHearingStatementModel updatingStatement = timetableHearingStatementService.createHearingStatement(
        timetableHearingStatementModel, Collections.emptyList());
    updatingStatement.setTimetableYear(2020L);

    assertThatThrownBy(
        () -> timetableHearingStatementService.updateHearingStatement(timetableHearingStatement, updatingStatement, Collections.emptyList())).isInstanceOf(
        IdNotFoundException.class);
  }

  @Test
  void shouldMoveClosedStatementsToNextYearWithStatusUpdateFromMovedToReceived() {
    // given
    timetableHearingYearService.createTimetableHearing(TIMETABLE_HEARING_YEAR);

    TimetableHearingStatementModel statement;
    // Statement 1
    statement = buildTimetableHearingStatementModel();
    statement.setStatementStatus(StatementStatus.RECEIVED);
    statement.setTimetableYear(YEAR - 1);
    Long statement1Id = timetableHearingStatementRepository.save(timetableHearingStatementMapper.toEntity(statement)).getId();

    // Statement 2
    statement = buildTimetableHearingStatementModel();
    statement.setStatementStatus(StatementStatus.IN_REVIEW);
    Long statement2Id = timetableHearingStatementRepository.save(timetableHearingStatementMapper.toEntity(statement)).getId();

    // Statement 3
    statement = buildTimetableHearingStatementModel();
    statement.setStatementStatus(StatementStatus.RECEIVED);
    Long statement3Id = timetableHearingStatementRepository.save(timetableHearingStatementMapper.toEntity(statement)).getId();

    // Statement 4
    statement = buildTimetableHearingStatementModel();
    statement.setStatementStatus(StatementStatus.JUNK);
    Long statement4Id = timetableHearingStatementRepository.save(timetableHearingStatementMapper.toEntity(statement)).getId();

    // Statement 5
    statement = buildTimetableHearingStatementModel();
    statement.setStatementStatus(StatementStatus.MOVED);
    Long statement5Id = timetableHearingStatementRepository.save(timetableHearingStatementMapper.toEntity(statement)).getId();

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
    timetableHearingYearService.createTimetableHearing(TIMETABLE_HEARING_YEAR);
    TimetableHearingStatementModel timetableHearingStatementModel = TimetableHearingStatementModel.builder()
        .timetableYear(TIMETABLE_HEARING_YEAR.getTimetableYear())
        .swissCanton(SwissCanton.BERN)
        .statementSender(TimetableHearingStatementSenderModel.builder()
            .firstName("Luca")
            .email("fabienne.mueller@sbb.ch")
            .build())
        .statement("Ich hätte gerne mehrere Verbindungen am Abend.")
        .build();
    timetableHearingStatementService.createHearingStatement(timetableHearingStatementModel, Collections.emptyList());

    TimetableHearingStatementSearchRestrictions searchRestrictions = TimetableHearingStatementSearchRestrictions.builder()
        .statementRequestParams(TimetableHearingStatementRequestParams.builder()
            .searchCriterias(List.of("gerne", "Luca"))
            .canton(SwissCanton.BERN)
            .timetableHearingYear(TIMETABLE_HEARING_YEAR.getTimetableYear())
            .build())
        .pageable(Pageable.unpaged())
        .build();

    Page<TimetableHearingStatement> hearingStatements = timetableHearingStatementService.getHearingStatements(searchRestrictions);

    assertThat(hearingStatements.getTotalElements()).isEqualTo(1);
  }

  @Test
  void shouldFindStatementByTtfnid() {
    timetableHearingYearService.createTimetableHearing(TIMETABLE_HEARING_YEAR);
    TimetableHearingStatementModel timetableHearingStatementModel = TimetableHearingStatementModel.builder()
        .timetableYear(TIMETABLE_HEARING_YEAR.getTimetableYear())
        .swissCanton(SwissCanton.BERN)
        .ttfnid("ch:1:ttfnid:2341234")
        .statementSender(TimetableHearingStatementSenderModel.builder()
            .email("fabienne.mueller@sbb.ch")
            .build())
        .statement("Ich hätte gerne mehrere Verbindungen am Abend.")
        .build();
    timetableHearingStatementService.createHearingStatement(timetableHearingStatementModel, Collections.emptyList());

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
    timetableHearingYearService.createTimetableHearing(TIMETABLE_HEARING_YEAR);
    TimetableHearingStatementModel timetableHearingStatementModel = TimetableHearingStatementModel.builder()
        .timetableYear(TIMETABLE_HEARING_YEAR.getTimetableYear())
        .swissCanton(SwissCanton.BERN)
        .ttfnid("ch:1:ttfnid:2341234")
        .statementSender(TimetableHearingStatementSenderModel.builder()
            .email("fabienne.mueller@sbb.ch")
            .build())
        .statement("Ich hätte gerne mehrere Verbindungen am Abend.")
        .build();
    timetableHearingStatementService.createHearingStatement(timetableHearingStatementModel, Collections.emptyList());

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
    transportCompanySharingDataAccessor.save(SharedTransportCompanyModel.builder()
        .id(4L)
        .abbreviation("SBB")
        .businessRegisterName("Schweizerische Bundesbahnen").build());
    transportCompanySharingDataAccessor.save(SharedTransportCompanyModel.builder()
        .id(5L)
        .abbreviation("BLS")
        .businessRegisterName("Basel Land Stationen ? :D").build());
    timetableHearingYearService.createTimetableHearing(TIMETABLE_HEARING_YEAR);
    TimetableHearingStatementModel timetableHearingStatementModel = TimetableHearingStatementModel.builder()
        .timetableYear(TIMETABLE_HEARING_YEAR.getTimetableYear())
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
        .statementSender(TimetableHearingStatementSenderModel.builder()
            .firstName("Luca")
            .email("fabienne.mueller@sbb.ch")
            .build())
        .statement("Ich hätte gerne mehrere Verbindungen am Abend.")
        .build();
    timetableHearingStatementService.createHearingStatement(timetableHearingStatementModel, Collections.emptyList());

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
