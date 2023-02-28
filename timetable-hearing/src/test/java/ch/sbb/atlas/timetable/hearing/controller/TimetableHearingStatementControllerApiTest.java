package ch.sbb.atlas.timetable.hearing.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ch.sbb.atlas.api.client.lidi.TimetableFieldNumberClient;
import ch.sbb.atlas.api.lidi.TimetableFieldNumberModel;
import ch.sbb.atlas.api.model.Container;
import ch.sbb.atlas.api.timetable.hearing.StatementSenderModel;
import ch.sbb.atlas.api.timetable.hearing.TimetableHearingStatementModel;
import ch.sbb.atlas.api.timetable.hearing.TimetableHearingStatementModel.Fields;
import ch.sbb.atlas.api.timetable.hearing.TimetableHearingYearModel;
import ch.sbb.atlas.api.timetable.hearing.enumeration.StatementStatus;
import ch.sbb.atlas.model.controller.BaseControllerApiTest;
import ch.sbb.atlas.timetable.hearing.repository.TimetableHearingYearRepository;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
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

  @MockBean
  private TimetableFieldNumberClient timetableFieldNumberClient;

  @BeforeEach
  void setUp() {
    timetableHearingYearController.createHearingYear(TIMETABLE_HEARING_YEAR);

    TimetableFieldNumberModel returnedTimetableFieldNumber = TimetableFieldNumberModel.builder()
        .number("1.1")
        .ttfnid("ch:1:ttfnid:123123123")
        .build();
    when(timetableFieldNumberClient.getOverview(any(), any(), eq(returnedTimetableFieldNumber.getNumber()), any(), any(), any())).thenReturn(
        Container.<TimetableFieldNumberModel>builder().objects(List.of(returnedTimetableFieldNumber)).totalCount(1L).build());
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
  void shouldCreateStatementExternalFromSkiWeb() throws Exception {
    timetableHearingYearController.startHearingYear(YEAR);
    String statement = """
         {
         	"statement": "I need some more busses please.",
         	"statementSender": {
         		"email": "maurer@post.ch",
         		"firstName": "Fabienne",
         		"lastName": "Maurer",
         		"organisation": "Post AG",
         		"street": "Bahnhofstrasse 12",
         		"zip": 3000,
         		"city": "Bern"
         	},
         	"timetableYear": 2024,
         	"timetableFieldNumber": "1.1",
         	"swissCanton": "BERN",
         	"stopPlace": "Bern, Wyleregg"
         }
        """;
    MockMultipartFile statementJson = new MockMultipartFile("statement", null,
        MediaType.APPLICATION_JSON_VALUE, statement.getBytes());

    mvc.perform(multipart(HttpMethod.POST, "/v1/timetable-hearing/statements/external")
            .file(statementJson)
            .file(new MockMultipartFile("documents", "doc1.pdf", MediaType.MULTIPART_FORM_DATA_VALUE, "Tolles PDF".getBytes()))
            .file(new MockMultipartFile("documents", "doc2.pdf", MediaType.MULTIPART_FORM_DATA_VALUE,
                "Noch ein tolles PDF".getBytes())))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$." + Fields.statementStatus, is(StatementStatus.RECEIVED.toString())))
        .andExpect(jsonPath("$." + Fields.documents, hasSize(2)))
        .andExpect(jsonPath("$." + Fields.documents + "[0].id", notNullValue()));
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