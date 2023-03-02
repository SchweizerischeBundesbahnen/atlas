package ch.sbb.line.directory.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ch.sbb.atlas.api.bodi.TransportCompanyModel;
import ch.sbb.atlas.api.client.bodi.TransportCompanyClient;
import ch.sbb.atlas.api.client.lidi.TimetableFieldNumberClient;
import ch.sbb.atlas.api.lidi.TimetableFieldNumberModel;
import ch.sbb.atlas.api.lidi.TimetableFieldNumberVersionModel;
import ch.sbb.atlas.api.model.Container;
import ch.sbb.atlas.api.timetable.hearing.TimetableHearingStatementSenderModel;
import ch.sbb.atlas.api.timetable.hearing.TimetableHearingStatementModel;
import ch.sbb.atlas.api.timetable.hearing.TimetableHearingStatementModel.Fields;
import ch.sbb.atlas.api.timetable.hearing.TimetableHearingYearModel;
import ch.sbb.atlas.api.timetable.hearing.enumeration.StatementStatus;
import ch.sbb.atlas.model.controller.AtlasMockMultipartFile;
import ch.sbb.atlas.model.controller.BaseControllerApiTest;
import ch.sbb.line.directory.repository.TimetableHearingYearRepository;
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
  public static final String TTFNID = "ch:1:ttfnid:123123123";
  public static final String SBOID = "ch:1:sboid:123451";

  @Autowired
  private TimetableHearingYearRepository timetableHearingYearRepository;

  @Autowired
  private TimetableHearingYearController timetableHearingYearController;

  @Autowired
  private TimetableHearingStatementController timetableHearingStatementController;

  @MockBean
  private TimetableFieldNumberClient timetableFieldNumberClient;

  @MockBean
  private TransportCompanyClient transportCompanyClient;

  @BeforeEach
  void setUp() {
    timetableHearingYearController.createHearingYear(TIMETABLE_HEARING_YEAR);

    TimetableFieldNumberModel returnedTimetableFieldNumber = TimetableFieldNumberModel.builder()
        .number("1.1")
        .ttfnid(TTFNID)
        .businessOrganisation(SBOID)
        .validFrom(LocalDate.of(2000, 1, 1))
        .validTo(LocalDate.of(9999, 12, 31))
        .build();
    when(timetableFieldNumberClient.getOverview(any(), any(), eq(returnedTimetableFieldNumber.getNumber()), any(), any(),
        any())).thenReturn(
        Container.<TimetableFieldNumberModel>builder().objects(List.of(returnedTimetableFieldNumber)).totalCount(1L).build());

    TimetableFieldNumberVersionModel returnedTimetableFieldNumberVersion = TimetableFieldNumberVersionModel.builder()
        .number("1.1")
        .ttfnid(TTFNID)
        .businessOrganisation(SBOID)
        .validFrom(LocalDate.of(2000, 1, 1))
        .validTo(LocalDate.of(9999, 12, 31))
        .build();
    when(timetableFieldNumberClient.getAllVersionsVersioned(TTFNID)).thenReturn(List.of(returnedTimetableFieldNumberVersion));

    TransportCompanyModel transportCompanyModel = TransportCompanyModel.builder()
        .id(1L)
        .number("#0001")
        .abbreviation("SBB")
        .businessRegisterName("Schweizerische Bundesbahnen SBB")
        .build();
    when(transportCompanyClient.getTransportCompaniesBySboid(SBOID)).thenReturn(List.of(transportCompanyModel));
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
        .statementSender(TimetableHearingStatementSenderModel.builder()
            .email("fabienne.mueller@sbb.ch")
            .build())
        .statement("Ich hätte gerne mehrere Verbindungen am Abend.")
        .build();

    MockMultipartFile statementJson = new AtlasMockMultipartFile("statement", null,
        MediaType.APPLICATION_JSON_VALUE, mapper.writeValueAsString(statement));

    mvc.perform(multipart(HttpMethod.POST, "/v1/timetable-hearing/statements")
            .file(statementJson))
        .andDo(print())
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$." + Fields.statementStatus, is(StatementStatus.RECEIVED.toString())))
        .andExpect(jsonPath("$." + Fields.ttfnid, is("ch:1:ttfnid:12341241")))
        .andExpect(jsonPath("$." + Fields.documents, hasSize(0)));
  }

  @Test
  void shouldReportInvalidJsonInStatementWithoutDocuments() throws Exception {
    TimetableHearingStatementModel statement = TimetableHearingStatementModel.builder()
        .timetableYear(TIMETABLE_HEARING_YEAR.getTimetableYear())
        .statementSender(TimetableHearingStatementSenderModel.builder()
            .build())
        .statement("Ich hätte gerne mehrere Verbindungen am Abend.")
        .build();

    MockMultipartFile statementJson = new AtlasMockMultipartFile("statement", null,
        MediaType.APPLICATION_JSON_VALUE, mapper.writeValueAsString(statement));

    mvc.perform(multipart(HttpMethod.POST, "/v1/timetable-hearing/statements")
            .file(statementJson))
        .andExpect(status().isBadRequest());
  }

  @Test
  void shouldCreateStatementWithTwoDocuments() throws Exception {
    TimetableHearingStatementModel statement = TimetableHearingStatementModel.builder()
        .timetableYear(TIMETABLE_HEARING_YEAR.getTimetableYear())
        .statementSender(TimetableHearingStatementSenderModel.builder()
            .email("fabienne.mueller@sbb.ch")
            .build())
        .statement("Ich hätte gerne mehrere Verbindungen am Abend.")
        .build();

    MockMultipartFile statementJson = new AtlasMockMultipartFile("statement", null,
        MediaType.APPLICATION_JSON_VALUE, mapper.writeValueAsString(statement));

    mvc.perform(multipart(HttpMethod.POST, "/v1/timetable-hearing/statements")
            .file(statementJson)
            .file(new AtlasMockMultipartFile("documents", "doc1.pdf", MediaType.MULTIPART_FORM_DATA_VALUE, "Tolles PDF"))
            .file(new AtlasMockMultipartFile("documents", "doc2.pdf", MediaType.MULTIPART_FORM_DATA_VALUE, "Noch ein tolles "
                + "PDF")))
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
    MockMultipartFile statementJson = new AtlasMockMultipartFile("statement", null,
        MediaType.APPLICATION_JSON_VALUE, statement);

    mvc.perform(multipart(HttpMethod.POST, "/v1/timetable-hearing/statements/external")
            .file(statementJson)
            .file(new AtlasMockMultipartFile("documents", "doc1.pdf", MediaType.MULTIPART_FORM_DATA_VALUE, "Tolles PDF"))
            .file(new AtlasMockMultipartFile("documents", "doc2.pdf", MediaType.MULTIPART_FORM_DATA_VALUE,
                "Noch ein tolles PDF")))
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
            .statementSender(TimetableHearingStatementSenderModel.builder()
                .email("fabienne.mueller@sbb.ch")
                .build())
            .statement("Ich hätte gerne mehrere Verbindungen am Abend.")
            .build(),
        Collections.emptyList());

    statement.setStatementStatus(StatementStatus.JUNK);

    MockMultipartFile statementJson = new AtlasMockMultipartFile("statement", null,
        MediaType.APPLICATION_JSON_VALUE, mapper.writeValueAsString(statement));

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
            .statementSender(TimetableHearingStatementSenderModel.builder()
                .email("fabienne.mueller@sbb.ch")
                .build())
            .statement("Ich hätte gerne mehrere Verbindungen am Abend.")
            .build(),
        Collections.emptyList());

    MockMultipartFile statementJson = new AtlasMockMultipartFile("statement", null,
        MediaType.APPLICATION_JSON_VALUE, mapper.writeValueAsString(statement));

    mvc.perform(multipart(HttpMethod.PUT, "/v1/timetable-hearing/statements/" + statement.getId())
            .file(statementJson)
            .file(new AtlasMockMultipartFile("documents", "doc1.pdf", MediaType.MULTIPART_FORM_DATA_VALUE, "Tolles PDF")))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$." + Fields.statementStatus, is(StatementStatus.RECEIVED.toString())))
        .andExpect(jsonPath("$." + Fields.documents, hasSize(1)));
  }

  @Test
  void shouldGetStatementById() throws Exception {
    TimetableHearingStatementModel statement = timetableHearingStatementController.createStatement(
        TimetableHearingStatementModel.builder()
            .timetableYear(TIMETABLE_HEARING_YEAR.getTimetableYear())
            .statementSender(TimetableHearingStatementSenderModel.builder()
                .email("fabienne.mueller@sbb.ch")
                .build())
            .statement("Ich hätte gerne mehrere Verbindungen am Abend.")
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
            .statementSender(TimetableHearingStatementSenderModel.builder()
                .email("fabienne.mueller@sbb.ch")
                .build())
            .statement("Ich hätte gerne mehrere Verbindungen am Abend.")
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