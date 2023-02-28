package ch.sbb.atlas.timetable.hearing.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ch.sbb.atlas.api.timetable.hearing.StatementSenderModel;
import ch.sbb.atlas.api.timetable.hearing.TimetableHearingStatementModel;
import ch.sbb.atlas.api.timetable.hearing.TimetableHearingStatementModel.Fields;
import ch.sbb.atlas.api.timetable.hearing.TimetableHearingYearModel;
import ch.sbb.atlas.api.timetable.hearing.enumeration.StatementStatus;
import ch.sbb.atlas.model.controller.BaseControllerApiTest;
import ch.sbb.atlas.timetable.hearing.repository.TimetableHearingYearRepository;
import java.time.LocalDate;
import java.util.Collections;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

public class TimetableHearingStatementControllerApiTest extends BaseControllerApiTest {

  private static final long YEAR = 2022L;
  private static final TimetableHearingYearModel TIMETABLE_HEARING_YEAR = TimetableHearingYearModel.builder()
      .timetableYear(YEAR)
      .hearingFrom(LocalDate.of(2021, 1, 1))
      .hearingTo(LocalDate.of(2021, 2, 1))
      .build();

  @Autowired
  private TimetableHearingYearRepository timetableHearingYearRepository;

  @Autowired
  private TimetableHearingYearController timetableHearingYearController;

  @Autowired
  private TimetableHearingStatementController timetableHearingStatementController;

  @BeforeEach
  void setUp() {
    timetableHearingYearController.createHearingYear(TIMETABLE_HEARING_YEAR);
  }

  @AfterEach
  void tearDown() {
    timetableHearingYearRepository.deleteAll();
  }

  @Test
  void shouldCreateStatementWithoutDocuments() throws Exception {
    TimetableHearingStatementModel statement = TimetableHearingStatementModel.builder()
        .timetableYear(TIMETABLE_HEARING_YEAR.getTimetableYear())
        .ttfnid("ch:1:ttfnid:12341241")
        .statementSender(StatementSenderModel.builder()
            .email("mike@thebike.com")
            .build())
        .statement("Ich mag bitte mehr Bös fahren")
        .build();

    MockMultipartFile statementJson = new MockMultipartFile("statement", null,
        MediaType.APPLICATION_JSON_VALUE, mapper.writeValueAsString(statement).getBytes());

    mvc.perform(multipart(HttpMethod.POST, "/v1/timetable-hearing/statements")
            .file(statementJson))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$." + Fields.statementStatus, is(StatementStatus.RECEIVED.toString())))
        .andExpect(jsonPath("$." + Fields.ttfnid, is("ch:1:ttfnid:12341241")))
        .andExpect(jsonPath("$." + Fields.documents, hasSize(0)));
  }

  @Test
  void shouldReportInvalidJsonInStatementWithoutDocuments() throws Exception {
    TimetableHearingStatementModel statement = TimetableHearingStatementModel.builder()
        .timetableYear(TIMETABLE_HEARING_YEAR.getTimetableYear())
        .statementSender(StatementSenderModel.builder()
            .build())
        .statement("Ich mag bitte mehr Bös fahren")
        .build();

    MockMultipartFile statementJson = new MockMultipartFile("statement", null,
        MediaType.APPLICATION_JSON_VALUE, mapper.writeValueAsString(statement).getBytes());

    mvc.perform(multipart(HttpMethod.POST, "/v1/timetable-hearing/statements")
            .file(statementJson))
        .andExpect(status().isBadRequest());
  }

  @Test
  void shouldCreateStatementWithTwoDocuments() throws Exception {
    TimetableHearingStatementModel statement = TimetableHearingStatementModel.builder()
        .timetableYear(TIMETABLE_HEARING_YEAR.getTimetableYear())
        .statementSender(StatementSenderModel.builder()
            .email("mike@thebike.com")
            .build())
        .statement("Ich mag bitte mehr Bös fahren")
        .build();

    MockMultipartFile statementJson = new MockMultipartFile("statement", null,
        MediaType.APPLICATION_JSON_VALUE, mapper.writeValueAsString(statement).getBytes());

    mvc.perform(multipart(HttpMethod.POST, "/v1/timetable-hearing/statements")
            .file(statementJson)
            .file(new MockMultipartFile("documents", "doc1.pdf", MediaType.MULTIPART_FORM_DATA_VALUE, "Tolles PDF".getBytes()))
            .file(new MockMultipartFile("documents", "doc2.pdf", MediaType.MULTIPART_FORM_DATA_VALUE,
                "Noch ein tolles PDF".getBytes())))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$." + Fields.statementStatus, is(StatementStatus.RECEIVED.toString())))
        .andExpect(jsonPath("$." + Fields.documents, hasSize(2)));
  }

  @Test
  void shouldUpdateStatement() throws Exception {
    TimetableHearingStatementModel statement = timetableHearingStatementController.createStatement(
        TimetableHearingStatementModel.builder()
            .timetableYear(TIMETABLE_HEARING_YEAR.getTimetableYear())
            .statementSender(StatementSenderModel.builder()
                .email("mike@thebike.com")
                .build())
            .statement("Ich mag bitte mehr Bös fahren")
            .build(),
        Collections.emptyList());

    statement.setStatementStatus(StatementStatus.JUNK);

    MockMultipartFile statementJson = new MockMultipartFile("statement", null,
        MediaType.APPLICATION_JSON_VALUE, mapper.writeValueAsString(statement).getBytes());

    mvc.perform(multipart(HttpMethod.PUT, "/v1/timetable-hearing/statements/" + statement.getId())
            .file(statementJson))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$." + Fields.statementStatus, is(StatementStatus.JUNK.toString())))
        .andExpect(jsonPath("$." + Fields.documents, hasSize(0)));
  }

  @Test
  void shouldUpdateStatementWithAdditionalDocuments() throws Exception {
    TimetableHearingStatementModel statement = timetableHearingStatementController.createStatement(
        TimetableHearingStatementModel.builder()
            .timetableYear(TIMETABLE_HEARING_YEAR.getTimetableYear())
            .statementSender(StatementSenderModel.builder()
                .email("mike@thebike.com")
                .build())
            .statement("Ich mag bitte mehr Bös fahren")
            .build(),
        Collections.emptyList());

    MockMultipartFile statementJson = new MockMultipartFile("statement", null,
        MediaType.APPLICATION_JSON_VALUE, mapper.writeValueAsString(statement).getBytes());

    mvc.perform(multipart(HttpMethod.PUT, "/v1/timetable-hearing/statements/" + statement.getId())
            .file(statementJson)
            .file(new MockMultipartFile("documents", "doc1.pdf", MediaType.MULTIPART_FORM_DATA_VALUE, "Tolles PDF".getBytes())))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$." + Fields.statementStatus, is(StatementStatus.RECEIVED.toString())))
        .andExpect(jsonPath("$." + Fields.documents, hasSize(1)));
  }

  @Test
  void shouldGetStatementById() throws Exception {
    TimetableHearingStatementModel statement = timetableHearingStatementController.createStatement(
        TimetableHearingStatementModel.builder()
            .timetableYear(TIMETABLE_HEARING_YEAR.getTimetableYear())
            .statementSender(StatementSenderModel.builder()
                .email("mike@thebike.com")
                .build())
            .statement("Ich mag bitte mehr Bös fahren")
            .build(),
        Collections.emptyList());

    mvc.perform(get("/v1/timetable-hearing/statements/" + statement.getId()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$." + Fields.statementStatus, is(StatementStatus.RECEIVED.toString())))
        .andExpect(jsonPath("$." + Fields.documents, hasSize(0)));
  }

  @Test
  void shouldGetStatementByHearingYear() throws Exception {
    TimetableHearingStatementModel statement = timetableHearingStatementController.createStatement(
        TimetableHearingStatementModel.builder()
            .timetableYear(TIMETABLE_HEARING_YEAR.getTimetableYear())
            .statementSender(StatementSenderModel.builder()
                .email("mike@thebike.com")
                .build())
            .statement("Ich mag bitte mehr Bös fahren")
            .build(),
        Collections.emptyList());

    mvc.perform(get("/v1/timetable-hearing/statements?timetableHearingYear=" + statement.getTimetableYear()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.totalCount", is(1)));

    mvc.perform(get("/v1/timetable-hearing/statements?timetableHearingYear=2010"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.totalCount", is(0)));
  }

}