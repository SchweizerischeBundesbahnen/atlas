package ch.sbb.line.directory.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ch.sbb.atlas.api.timetable.hearing.TimetableHearingStatementModel;
import ch.sbb.atlas.api.timetable.hearing.TimetableHearingStatementSenderModel;
import ch.sbb.atlas.api.timetable.hearing.enumeration.StatementStatus;
import ch.sbb.atlas.kafka.model.SwissCanton;
import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.atlas.model.exception.NotFoundException.FileNotFoundException;
import ch.sbb.atlas.model.exception.NotFoundException.IdNotFoundException;
import ch.sbb.line.directory.entity.TimetableHearingStatement;
import ch.sbb.line.directory.entity.TimetableHearingYear;
import ch.sbb.line.directory.helper.PdfFiles;
import ch.sbb.line.directory.mapper.TimeTableHearingStatementMapper;
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
import org.springframework.web.multipart.MultipartFile;

@IntegrationTest
public class TimetableHearingStatementServiceTest {

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

  @Autowired
  public TimetableHearingStatementServiceTest(TimetableHearingYearRepository timetableHearingYearRepository,
    TimetableHearingYearService timetableHearingYearService,
    TimetableHearingStatementRepository timetableHearingStatementRepository,
    TimetableHearingStatementService timetableHearingStatementService) {
    this.timetableHearingYearRepository = timetableHearingYearRepository;
    this.timetableHearingYearService = timetableHearingYearService;
    this.timetableHearingStatementRepository = timetableHearingStatementRepository;
    this.timetableHearingStatementService = timetableHearingStatementService;
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
    TimetableHearingStatementModel createdStatement = timetableHearingStatementService.createHearingStatement(timetableHearingStatementModel, Collections.emptyList());

    TimetableHearingStatement hearingStatement = timetableHearingStatementService.getTimetableHearingStatementById(createdStatement.getId());

    assertThat(hearingStatement).isNotNull();
    assertThat(hearingStatement.getStatementStatus()).isEqualTo(StatementStatus.RECEIVED);
    assertThat(hearingStatement.getStatement()).isEqualTo(createdStatement.getStatement());
  }

  @Test
  void shouldNotGetHearingStatementIfIdIsNotValid() {
    timetableHearingYearService.createTimetableHearing(TIMETABLE_HEARING_YEAR);
    TimetableHearingStatementModel timetableHearingStatementModel = buildTimetableHearingStatementModel();

    TimetableHearingStatementModel createdStatement = timetableHearingStatementService.createHearingStatement(timetableHearingStatementModel, Collections.emptyList());

    assertThatThrownBy(() -> timetableHearingStatementService.getTimetableHearingStatementById(createdStatement.getId() + 1)).isInstanceOf(
      IdNotFoundException.class);
  }

  @Test
  void shouldGetDocumentFromHearingStatement() {
    timetableHearingYearService.createTimetableHearing(TIMETABLE_HEARING_YEAR);
    TimetableHearingStatementModel timetableHearingStatementModel = buildTimetableHearingStatementModel();

    List<MultipartFile> documents = new ArrayList<>();
    documents.add(PdfFiles.multipartFiles.get(0));
    documents.add(PdfFiles.multipartFiles.get(1));

    TimetableHearingStatementModel createdStatement = timetableHearingStatementService.createHearingStatement(timetableHearingStatementModel, documents);

    var originalFilename = PdfFiles.multipartFiles.get(0).getOriginalFilename();
    File statementDocument = timetableHearingStatementService.getStatementDocument(createdStatement.getId(), originalFilename);
    assertTrue(statementDocument.getName().contains("dummy.pdf"));
    assertEquals(PdfFiles.multipartFiles.get(0).getSize(), statementDocument.length());
  }

  @Test
  void shouldDeleteDocumentFromHearingStatement() {
    timetableHearingYearService.createTimetableHearing(TIMETABLE_HEARING_YEAR);
    TimetableHearingStatementModel timetableHearingStatementModel = buildTimetableHearingStatementModel();

    TimetableHearingStatementModel createdStatement = timetableHearingStatementService.createHearingStatement(timetableHearingStatementModel, Collections.emptyList());
    TimetableHearingStatement createdStatementEntity = TimeTableHearingStatementMapper.toEntity(createdStatement);

    timetableHearingStatementService.deleteStatementDocument(createdStatementEntity, PdfFiles.multipartFiles.get(0).getOriginalFilename());
    assertThatThrownBy(() -> timetableHearingStatementService.getStatementDocument(createdStatement.getId(), PdfFiles.multipartFiles.get(0).getOriginalFilename())).isInstanceOf(
      FileNotFoundException.class);
  }

  @Test
  void shouldThrowIllegalArgumentExceptionWhenDeletingDocument() {
    timetableHearingYearService.createTimetableHearing(TIMETABLE_HEARING_YEAR);

    assertThatThrownBy(() -> timetableHearingStatementService.deleteStatementDocument(new TimetableHearingStatement(), PdfFiles.multipartFiles.get(0).getOriginalFilename())).isInstanceOf(
      IllegalArgumentException.class);
  }

  @Test
  void shouldThrowIllegalArgumentExceptionWhenDeletingEmptyStringDocumentName() {
    timetableHearingYearService.createTimetableHearing(TIMETABLE_HEARING_YEAR);
    TimetableHearingStatementModel timetableHearingStatementModel = buildTimetableHearingStatementModel();

    List<MultipartFile> documents = new ArrayList<>();
    documents.add(PdfFiles.multipartFiles.get(0));
    documents.add(PdfFiles.multipartFiles.get(1));

    TimetableHearingStatementModel createdStatement = timetableHearingStatementService.createHearingStatement(timetableHearingStatementModel, documents);
    TimetableHearingStatement createdStatementEntity = TimeTableHearingStatementMapper.toEntity(createdStatement);

    assertThatThrownBy(() -> timetableHearingStatementService.deleteStatementDocument(createdStatementEntity, "")).isInstanceOf(
      IllegalArgumentException.class);
  }

  @Test
  void shouldNotDoAnythingIfDeleteUnknownDocument() {
    timetableHearingYearService.createTimetableHearing(TIMETABLE_HEARING_YEAR);
    TimetableHearingStatementModel timetableHearingStatementModel = buildTimetableHearingStatementModel();

    TimetableHearingStatementModel createdStatement = timetableHearingStatementService.createHearingStatement(timetableHearingStatementModel, Collections.emptyList());
    TimetableHearingStatement createdStatementEntity = TimeTableHearingStatementMapper.toEntity(createdStatement);

    timetableHearingStatementService.deleteStatementDocument(createdStatementEntity, PdfFiles.multipartFiles.get(0).getOriginalFilename());
  }

  @Test
  void shouldNotGetHearingStatementIfIdIsNotValissd() {
    timetableHearingYearService.createTimetableHearing(TIMETABLE_HEARING_YEAR);
    TimetableHearingStatementModel timetableHearingStatementModel = buildTimetableHearingStatementModel();

    TimetableHearingStatementModel createdStatement = timetableHearingStatementService.createHearingStatement(timetableHearingStatementModel, Collections.emptyList());

    assertThatThrownBy(() -> timetableHearingStatementService.getTimetableHearingStatementById(createdStatement.getId() + 1)).isInstanceOf(
      IdNotFoundException.class);
  }

  @Test
  void shouldCreateHearingStatement() {
    timetableHearingYearService.createTimetableHearing(TIMETABLE_HEARING_YEAR);
    TimetableHearingStatementModel timetableHearingStatementModel = buildTimetableHearingStatementModel();

    TimetableHearingStatementModel hearingStatement = timetableHearingStatementService.createHearingStatement(timetableHearingStatementModel, Collections.emptyList());

    assertThat(hearingStatement).isNotNull();
    assertThat(hearingStatement.getStatementStatus()).isEqualTo(StatementStatus.RECEIVED);
  }

  @Test
  void shouldNotCreateHearingStatementIfYearIsUnknown() {
    TimetableHearingStatementModel timetableHearingStatementModel = buildTimetableHearingStatementModel();

    assertThatThrownBy(() -> timetableHearingStatementService.createHearingStatement(timetableHearingStatementModel, Collections.emptyList())).isInstanceOf(
      IdNotFoundException.class);
  }

  @Test
  void shouldUpdateHearingStatement() {
    timetableHearingYearService.createTimetableHearing(TIMETABLE_HEARING_YEAR);

    TimetableHearingStatementModel timetableHearingStatementModel = buildTimetableHearingStatementModel();

    TimetableHearingStatementModel updatingStatement = timetableHearingStatementService.createHearingStatement(timetableHearingStatementModel, Collections.emptyList());
    updatingStatement.setStatementStatus(StatementStatus.JUNK);

    TimetableHearingStatement updatedStatement = timetableHearingStatementService.updateHearingStatement(updatingStatement, Collections.emptyList());

    assertThat(updatedStatement).isNotNull();
    assertThat(updatedStatement.getStatementStatus()).isEqualTo(StatementStatus.JUNK);
  }

  @Test
  void shouldNotUpdateHearingStatementIfYearIsUnknown() {
    timetableHearingYearService.createTimetableHearing(TIMETABLE_HEARING_YEAR);
    TimetableHearingStatementModel timetableHearingStatementModel1 = buildTimetableHearingStatementModel();

    TimetableHearingStatementModel updatingStatement = timetableHearingStatementService.createHearingStatement(timetableHearingStatementModel1, Collections.emptyList());
    updatingStatement.setTimetableYear(2020L);

    assertThatThrownBy(() -> timetableHearingStatementService.updateHearingStatement(updatingStatement, Collections.emptyList())).isInstanceOf(IdNotFoundException.class);
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
}