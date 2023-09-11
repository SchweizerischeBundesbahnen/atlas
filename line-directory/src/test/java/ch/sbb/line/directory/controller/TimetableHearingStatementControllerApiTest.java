package ch.sbb.line.directory.controller;

import static ch.sbb.atlas.model.controller.WithMockJwtAuthentication.MockJwtAuthenticationFactory.createJwtWithoutSbbUid;
import static ch.sbb.line.directory.helper.PdfFiles.MULTIPART_FILES;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ch.sbb.atlas.api.bodi.TransportCompanyModel;
import ch.sbb.atlas.api.client.bodi.TransportCompanyClient;
import ch.sbb.atlas.api.client.user.administration.UserAdministrationClient;
import ch.sbb.atlas.api.timetable.hearing.TimetableHearingStatementModel;
import ch.sbb.atlas.api.timetable.hearing.TimetableHearingStatementModel.Fields;
import ch.sbb.atlas.api.timetable.hearing.TimetableHearingStatementResponsibleTransportCompanyModel;
import ch.sbb.atlas.api.timetable.hearing.TimetableHearingStatementSenderModel;
import ch.sbb.atlas.api.timetable.hearing.TimetableHearingYearModel;
import ch.sbb.atlas.api.timetable.hearing.enumeration.StatementStatus;
import ch.sbb.atlas.api.timetable.hearing.model.UpdateHearingCantonModel;
import ch.sbb.atlas.api.timetable.hearing.model.UpdateHearingStatementStatusModel;
import ch.sbb.atlas.export.CsvExportWriter;
import ch.sbb.atlas.kafka.model.SwissCanton;
import ch.sbb.atlas.model.controller.AtlasMockMultipartFile;
import ch.sbb.atlas.model.controller.BaseControllerApiTest;
import ch.sbb.atlas.model.exception.NotFoundException.FileNotFoundException;
import ch.sbb.line.directory.entity.SharedTransportCompany;
import ch.sbb.line.directory.entity.StatementSender;
import ch.sbb.line.directory.entity.TimetableFieldNumber;
import ch.sbb.line.directory.entity.TimetableFieldNumberVersion;
import ch.sbb.line.directory.entity.TimetableHearingStatement;
import ch.sbb.line.directory.exception.ForbiddenDueToHearingYearSettingsException;
import ch.sbb.line.directory.exception.NoClientCredentialAuthUsedException;
import ch.sbb.line.directory.exception.PdfDocumentConstraintViolationException;
import ch.sbb.line.directory.repository.SharedTransportCompanyRepository;
import ch.sbb.line.directory.repository.TimetableHearingStatementRepository;
import ch.sbb.line.directory.repository.TimetableHearingYearRepository;
import ch.sbb.line.directory.service.TimetableFieldNumberService;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.test.web.servlet.MvcResult;

public class TimetableHearingStatementControllerApiTest extends BaseControllerApiTest {

  private static final long YEAR = 2022L;
  private static final TimetableHearingYearModel TIMETABLE_HEARING_YEAR = TimetableHearingYearModel.builder()
      .timetableYear(YEAR)
      .hearingFrom(LocalDate.of(2021, 1, 1))
      .hearingTo(LocalDate.of(2021, 2, 1))
      .build();
  private static final String TTFNID = "ch:1:ttfnid:123123123";
  private static final String SBOID = "ch:1:sboid:123451";

  @Autowired
  private TimetableHearingYearRepository timetableHearingYearRepository;

  @Autowired
  private TimetableHearingYearController timetableHearingYearController;

  @Autowired
  private TimetableHearingStatementController timetableHearingStatementController;

  @Autowired
  private TimetableHearingStatementRepository timetableHearingStatementRepository;

  @MockBean
  private TimetableFieldNumberService timetableFieldNumberService;

  @MockBean
  private TransportCompanyClient transportCompanyClient;

  @MockBean
  private UserAdministrationClient userAdministrationClient;

  @MockBean
  private SharedTransportCompanyRepository sharedTransportCompanyRepository;

  @BeforeEach
  void setUp() {
    timetableHearingYearController.createHearingYear(TIMETABLE_HEARING_YEAR);

    TimetableFieldNumber returnedTimetableFieldNumber = TimetableFieldNumber.builder()
        .number("1.1")
        .ttfnid(TTFNID)
        .businessOrganisation(SBOID)
        .validFrom(LocalDate.of(2000, 1, 1))
        .validTo(LocalDate.of(9999, 12, 31))
        .build();
    when(timetableFieldNumberService.getVersionsSearched(any())).thenReturn(new PageImpl<>(List.of(returnedTimetableFieldNumber),
        Pageable.unpaged(), 1L));

    TimetableFieldNumberVersion returnedTimetableFieldNumberVersion = TimetableFieldNumberVersion.builder()
        .number("1.1")
        .ttfnid(TTFNID)
        .businessOrganisation(SBOID)
        .validFrom(LocalDate.of(2000, 1, 1))
        .validTo(LocalDate.of(9999, 12, 31))
        .build();
    when(timetableFieldNumberService.getAllVersionsVersioned(TTFNID)).thenReturn(List.of(returnedTimetableFieldNumberVersion));

    TransportCompanyModel transportCompanyModel = TransportCompanyModel.builder()
        .id(1L)
        .number("#0001")
        .abbreviation("SBB")
        .businessRegisterName("Schweizerische Bundesbahnen SBB")
        .build();
    when(transportCompanyClient.getTransportCompaniesBySboid(SBOID)).thenReturn(List.of(transportCompanyModel));

    when(sharedTransportCompanyRepository.findById(1L)).thenReturn(Optional.of(SharedTransportCompany.builder()
        .id(1L)
        .number("#0001")
        .abbreviation("SBB")
        .businessRegisterName("Schweizerische Bundesbahnen SBB")
        .build()));
    when(sharedTransportCompanyRepository.findById(2L)).thenReturn(Optional.of(SharedTransportCompany.builder()
        .id(2L)
        .number("#0001")
        .abbreviation("BLS")
        .businessRegisterName("Berner Land Seilbahnen")
        .build()));
  }

  @AfterEach
  void tearDown() {
    timetableHearingYearRepository.deleteAll();
    timetableHearingStatementRepository.deleteAll();
  }

  @Test
  void shouldCreateStatementWithoutDocuments() throws Exception {
    TimetableHearingStatementModel statement = TimetableHearingStatementModel.builder()
        .timetableYear(TIMETABLE_HEARING_YEAR.getTimetableYear())
        .swissCanton(SwissCanton.BERN)
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
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$." + Fields.statementStatus, is(StatementStatus.RECEIVED.toString())))
        .andExpect(jsonPath("$." + Fields.ttfnid, is("ch:1:ttfnid:12341241")))
        .andExpect(jsonPath("$." + Fields.documents, hasSize(0)));
  }

  @Test
  void shouldReportInvalidJsonInStatementWithoutDocuments() throws Exception {
    TimetableHearingStatementModel statement = TimetableHearingStatementModel.builder()
        .timetableYear(TIMETABLE_HEARING_YEAR.getTimetableYear())
        .swissCanton(SwissCanton.BERN)
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
        .swissCanton(SwissCanton.BERN)
        .statementSender(TimetableHearingStatementSenderModel.builder()
            .email("fabienne.mueller@sbb.ch")
            .build())
        .statement("Ich hätte gerne mehrere Verbindungen am Abend.")
        .build();

    MockMultipartFile statementJson = new AtlasMockMultipartFile("statement", null, MediaType.APPLICATION_JSON_VALUE,
        mapper.writeValueAsString(statement));

    mvc.perform(multipart(HttpMethod.POST, "/v1/timetable-hearing/statements")
            .file(statementJson)
            .file(new MockMultipartFile(MULTIPART_FILES.get(0).getName(), MULTIPART_FILES.get(0).getOriginalFilename(),
                MULTIPART_FILES.get(0).getContentType(), MULTIPART_FILES.get(0).getBytes()))
            .file(
                new MockMultipartFile(MULTIPART_FILES.get(1).getName(), MULTIPART_FILES.get(1).getOriginalFilename(),
                    MULTIPART_FILES.get(1).getContentType(), MULTIPART_FILES.get(1).getBytes())))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$." + Fields.statementStatus, is(StatementStatus.RECEIVED.toString())))
        .andExpect(jsonPath("$.creationDate", notNullValue()))
        .andExpect(jsonPath("$.editionDate", notNullValue()))
        .andExpect(jsonPath("$." + Fields.documents, hasSize(2)));
  }

  @Test
  void shouldFailCreatingStatementWithFourDocuments() throws Exception {
    TimetableHearingStatementModel statement = TimetableHearingStatementModel.builder()
        .timetableYear(TIMETABLE_HEARING_YEAR.getTimetableYear())
        .swissCanton(SwissCanton.BERN)
        .statementSender(TimetableHearingStatementSenderModel.builder()
            .email("fabienne.mueller@sbb.ch")
            .build())
        .statement("Ich hätte gerne mehrere Verbindungen am Abend.")
        .build();

    MockMultipartFile statementJson = new AtlasMockMultipartFile("statement", null, MediaType.APPLICATION_JSON_VALUE,
        mapper.writeValueAsString(statement));

    mvc.perform(multipart(HttpMethod.POST, "/v1/timetable-hearing/statements")
            .file(statementJson)
            .file(new MockMultipartFile(MULTIPART_FILES.get(0).getName(), MULTIPART_FILES.get(0).getOriginalFilename(),
                MULTIPART_FILES.get(0).getContentType(), MULTIPART_FILES.get(0).getBytes()))
            .file(new MockMultipartFile(MULTIPART_FILES.get(1).getName(), MULTIPART_FILES.get(1).getOriginalFilename(),
                MULTIPART_FILES.get(1).getContentType(), MULTIPART_FILES.get(1).getBytes()))
            .file(new MockMultipartFile(MULTIPART_FILES.get(2).getName(), MULTIPART_FILES.get(2).getOriginalFilename(),
                MULTIPART_FILES.get(2).getContentType(), MULTIPART_FILES.get(2).getBytes()))
            .file(new MockMultipartFile(MULTIPART_FILES.get(3).getName(), MULTIPART_FILES.get(3).getOriginalFilename(),
                MULTIPART_FILES.get(3).getContentType(), MULTIPART_FILES.get(3).getBytes()))
        )
        .andExpect(status().isBadRequest())
        .andExpect(result -> assertTrue(result.getResolvedException() instanceof PdfDocumentConstraintViolationException))
        .andExpect(
            result -> assertEquals("Overall number of documents is: 4 which exceeds the number of allowed documents of 3.",
                Objects.requireNonNull(result.getResolvedException()).getMessage()));
  }

  @Test
  void shouldThrowForbiddenExceptionWhenStatementCreatableInternalIsFalse() throws Exception {
    TimetableHearingStatementModel statement = TimetableHearingStatementModel.builder()
        .timetableYear(YEAR)
        .swissCanton(SwissCanton.BERN)
        .ttfnid("ch:1:ttfnid:12341241")
        .statementSender(TimetableHearingStatementSenderModel.builder()
            .email("fabienne.mueller@sbb.ch")
            .build())
        .statement("Ich hätte gerne mehrere Verbindungen am Abend.")
        .build();

    MockMultipartFile statementJson = new AtlasMockMultipartFile("statement", null,
        MediaType.APPLICATION_JSON_VALUE, mapper.writeValueAsString(statement));

    TimetableHearingYearModel hearingYearModel = timetableHearingYearController.startHearingYear(YEAR);
    hearingYearModel.setStatementCreatableInternal(false);
    timetableHearingYearController.updateTimetableHearingSettings(YEAR, hearingYearModel);

    mvc.perform(multipart(HttpMethod.POST, "/v1/timetable-hearing/statements")
            .file(statementJson))
        .andExpect(status().isForbidden())
        .andExpect(result -> assertTrue(result.getResolvedException() instanceof ForbiddenDueToHearingYearSettingsException))
        .andExpect(result -> assertEquals("Operation not allowed",
            ((ForbiddenDueToHearingYearSettingsException) Objects.requireNonNull(
                result.getResolvedException())).getErrorResponse()
                .getMessage()));
  }

  @Test
  void shouldThrowExceptionWhenNotClientCredentialsAuthUsedForExternalEndpoint() throws Exception {
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
         	"timetableFieldNumber": "1.1",
         	"swissCanton": "BERN",
         	"stopPlace": "Bern, Wyleregg"
         }
        """;
    MockMultipartFile statementJson = new AtlasMockMultipartFile("statement", null,
        MediaType.APPLICATION_JSON_VALUE, statement);

    mvc.perform(multipart(HttpMethod.POST, "/v1/timetable-hearing/statements/external")
            .file(statementJson))
        .andExpect(status().isBadRequest())
        .andExpect(result -> assertTrue(result.getResolvedException() instanceof NoClientCredentialAuthUsedException))
        .andExpect(result -> assertEquals("Bad authentication used",
            ((NoClientCredentialAuthUsedException) Objects.requireNonNull(result.getResolvedException())).getErrorResponse()
                .getMessage()));
  }

  @Test
  void shouldThrowForbiddenExceptionWhenStatementCreatableExternalIsFalse() throws Exception {
    // For Client-Credential Auth
    SecurityContext context = SecurityContextHolder.getContext();
    Authentication authentication = new JwtAuthenticationToken(createJwtWithoutSbbUid(),
        AuthorityUtils.createAuthorityList("ROLE_atlas-admin"));
    authentication.setAuthenticated(true);
    context.setAuthentication(authentication);

    TimetableHearingYearModel hearingYearModel = timetableHearingYearController.startHearingYear(YEAR);
    hearingYearModel.setStatementCreatableExternal(false);
    timetableHearingYearController.updateTimetableHearingSettings(YEAR, hearingYearModel);

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
         	"timetableFieldNumber": "1.1",
         	"swissCanton": "BERN",
         	"stopPlace": "Bern, Wyleregg"
         }
        """;
    MockMultipartFile statementJson = new AtlasMockMultipartFile("statement", null,
        MediaType.APPLICATION_JSON_VALUE, statement);

    mvc.perform(multipart(HttpMethod.POST, "/v1/timetable-hearing/statements/external")
            .file(statementJson))
        .andExpect(status().isForbidden())
        .andExpect(result -> assertTrue(result.getResolvedException() instanceof ForbiddenDueToHearingYearSettingsException))
        .andExpect(result -> assertEquals("Operation not allowed",
            ((ForbiddenDueToHearingYearSettingsException) Objects.requireNonNull(
                result.getResolvedException())).getErrorResponse()
                .getMessage()));
  }

  @Test
  void shouldCreateStatementExternalFromSkiWeb() throws Exception {
    // For Client-Credential Auth
    SecurityContext context = SecurityContextHolder.getContext();
    Authentication authentication = new JwtAuthenticationToken(createJwtWithoutSbbUid(),
        AuthorityUtils.createAuthorityList("ROLE_atlas-admin"));
    authentication.setAuthenticated(true);
    context.setAuthentication(authentication);

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
         	"timetableFieldNumber": "1.1",
         	"swissCanton": "BERN",
         	"stopPlace": "Bern, Wyleregg"
         }
        """;
    MockMultipartFile statementJson = new AtlasMockMultipartFile("statement", null,
        MediaType.APPLICATION_JSON_VALUE, statement);

    mvc.perform(multipart(HttpMethod.POST, "/v1/timetable-hearing/statements/external")
            .file(statementJson)
            .file(new MockMultipartFile(MULTIPART_FILES.get(0).getName(), MULTIPART_FILES.get(0).getOriginalFilename(),
                MULTIPART_FILES.get(0).getContentType(), MULTIPART_FILES.get(0).getBytes()))
            .file(
                new MockMultipartFile(MULTIPART_FILES.get(1).getName(), MULTIPART_FILES.get(1).getOriginalFilename(),
                    MULTIPART_FILES.get(1).getContentType(), MULTIPART_FILES.get(1).getBytes())))
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
            .swissCanton(SwissCanton.BERN)
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
  void shouldUpdateHearingStatementStatus() throws Exception {
    timetableHearingYearController.startHearingYear(TIMETABLE_HEARING_YEAR.getTimetableYear());

    //given
    TimetableHearingStatement statement1 = TimetableHearingStatement.builder()
        .timetableYear(TIMETABLE_HEARING_YEAR.getTimetableYear())
        .swissCanton(SwissCanton.BERN)
        .statementStatus(StatementStatus.RECEIVED)
        .statementSender(StatementSender.builder()
            .email("mike@thebike.com")
            .build())
        .statement("Ich mag bitte mehr Bös fahren")
        .build();
    TimetableHearingStatement statement2 = TimetableHearingStatement.builder()
        .timetableYear(TIMETABLE_HEARING_YEAR.getTimetableYear())
        .swissCanton(SwissCanton.BERN)
        .statementStatus(StatementStatus.JUNK)
        .statementSender(StatementSender.builder()
            .email("mike@thebike.com")
            .build())
        .statement("Ich mag bitte mehr Bös fahren")
        .build();
    timetableHearingStatementRepository.saveAndFlush(statement1);
    timetableHearingStatementRepository.saveAndFlush(statement2);
    List<Long> ids = Stream.of(statement1, statement2).map(TimetableHearingStatement::getId).toList();
    UpdateHearingStatementStatusModel updateHearingStatementStatusModel =
        UpdateHearingStatementStatusModel.builder().ids(ids).justification("Forza Napoli")
            .statementStatus(StatementStatus.ACCEPTED).build();

    //when
    mvc.perform(put("/v1/timetable-hearing/statements/update-statement-status")
            .contentType(contentType)
            .content(mapper.writeValueAsString(updateHearingStatementStatusModel)))
        .andExpect(status().isOk());
  }

  @Test
  void shouldThrowForbiddenExceptionWhenTimeTableYearOfStatementNotEqualAsHearingYear() throws Exception {
    timetableHearingYearController.startHearingYear(TIMETABLE_HEARING_YEAR.getTimetableYear());

    //given
    TimetableHearingStatement statement1 = TimetableHearingStatement.builder()
        .timetableYear(TIMETABLE_HEARING_YEAR.getTimetableYear())
        .swissCanton(SwissCanton.BERN)
        .statementStatus(StatementStatus.IN_REVIEW)
        .statementSender(StatementSender.builder()
            .email("mike@thebike.com")
            .build())
        .statement("Ich mag bitte mehr Bös fahren")
        .build();

    TimetableHearingStatement statement2 = TimetableHearingStatement.builder()
        .timetableYear(2055L)
        .swissCanton(SwissCanton.BERN)
        .statementStatus(StatementStatus.JUNK)
        .statementSender(StatementSender.builder()
            .email("mike@thebike.com")
            .build())
        .statement("Ich mag bitte mehr Bös fahren")
        .build();

    timetableHearingStatementRepository.saveAndFlush(statement1);
    timetableHearingStatementRepository.saveAndFlush(statement2);
    List<Long> ids = Stream.of(statement1, statement2).map(TimetableHearingStatement::getId).toList();
    UpdateHearingStatementStatusModel updateHearingStatementStatusModel =
        UpdateHearingStatementStatusModel.builder().ids(ids).justification("Forza Napoli")
            .statementStatus(StatementStatus.ACCEPTED).build();

    //when
    mvc.perform(put("/v1/timetable-hearing/statements/update-statement-status")
            .contentType(contentType)
            .content(mapper.writeValueAsString(updateHearingStatementStatusModel)))
        .andExpect(status().isForbidden());
  }

  @Test
  void shouldThrowForbiddenWhenHearingYearIsNotActive() throws Exception{
    //given
    TimetableHearingStatement statement1 = TimetableHearingStatement.builder()
        .timetableYear(TIMETABLE_HEARING_YEAR.getTimetableYear())
        .swissCanton(SwissCanton.BERN)
        .statementStatus(StatementStatus.IN_REVIEW)
        .statementSender(StatementSender.builder()
            .email("mike@thebike.com")
            .build())
        .statement("Ich mag bitte mehr Bös fahren")
        .build();

    TimetableHearingStatement statement2 = TimetableHearingStatement.builder()
        .timetableYear(TIMETABLE_HEARING_YEAR.getTimetableYear())
        .swissCanton(SwissCanton.BERN)
        .statementStatus(StatementStatus.JUNK)
        .statementSender(StatementSender.builder()
            .email("mike@thebike.com")
            .build())
        .statement("Ich mag bitte mehr Bös fahren")
        .build();

    timetableHearingStatementRepository.saveAndFlush(statement1);
    timetableHearingStatementRepository.saveAndFlush(statement2);
    List<Long> ids = Stream.of(statement1, statement2).map(TimetableHearingStatement::getId).toList();
    UpdateHearingStatementStatusModel updateHearingStatementStatusModel =
        UpdateHearingStatementStatusModel.builder().ids(ids).justification("Forza Napoli")
            .statementStatus(StatementStatus.ACCEPTED).build();
    //when
    mvc.perform(put("/v1/timetable-hearing/statements/update-statement-status")
            .contentType(contentType)
            .content(mapper.writeValueAsString(updateHearingStatementStatusModel)))
        .andExpect(status().isForbidden());
  }
  @Test
  void shouldThrowForbiddenWhenHearingYearStatementEditableIsDisabled() throws Exception{
    timetableHearingYearController.startHearingYear(YEAR);
    timetableHearingYearController.updateTimetableHearingSettings(YEAR,
        TimetableHearingYearModel.builder()
            .statementEditable(false).build());
    //given
    TimetableHearingStatement statement1 = TimetableHearingStatement.builder()
        .timetableYear(TIMETABLE_HEARING_YEAR.getTimetableYear())
        .swissCanton(SwissCanton.BERN)
        .statementStatus(StatementStatus.IN_REVIEW)
        .statementSender(StatementSender.builder()
            .email("mike@thebike.com")
            .build())
        .statement("Ich mag bitte mehr Bös fahren")
        .build();

    TimetableHearingStatement statement2 = TimetableHearingStatement.builder()
        .timetableYear(TIMETABLE_HEARING_YEAR.getTimetableYear())
        .swissCanton(SwissCanton.BERN)
        .statementStatus(StatementStatus.JUNK)
        .statementSender(StatementSender.builder()
            .email("mike@thebike.com")
            .build())
        .statement("Ich mag bitte mehr Bös fahren")
        .build();

    timetableHearingStatementRepository.saveAndFlush(statement1);
    timetableHearingStatementRepository.saveAndFlush(statement2);
    List<Long> ids = Stream.of(statement1, statement2).map(TimetableHearingStatement::getId).toList();
    UpdateHearingStatementStatusModel updateHearingStatementStatusModel =
        UpdateHearingStatementStatusModel.builder().ids(ids).justification("Forza Napoli")
            .statementStatus(StatementStatus.ACCEPTED).build();
    //when
    mvc.perform(put("/v1/timetable-hearing/statements/update-statement-status")
            .contentType(contentType)
            .content(mapper.writeValueAsString(updateHearingStatementStatusModel)))
        .andExpect(status().isForbidden());
  }

  @Test
  void shouldUpdateHearingCanton() throws Exception {
    //given
    TimetableHearingStatement statement1 = TimetableHearingStatement.builder()
        .timetableYear(2023L)
        .swissCanton(SwissCanton.BERN)
        .statementStatus(StatementStatus.RECEIVED)
        .statementSender(StatementSender.builder()
            .email("mike@thebike.com")
            .build())
        .statement("Ich mag bitte mehr Bös fahren")
        .build();
    TimetableHearingStatement statement2 = TimetableHearingStatement.builder()
        .timetableYear(2024L)
        .swissCanton(SwissCanton.AARGAU)
        .statementStatus(StatementStatus.JUNK)
        .statementSender(StatementSender.builder()
            .email("mike@thebike.com")
            .build())
        .statement("Ich mag bitte mehr Bös fahren")
        .build();
    timetableHearingStatementRepository.saveAndFlush(statement1);
    timetableHearingStatementRepository.saveAndFlush(statement2);
    List<Long> ids = Stream.of(statement1, statement2).map(TimetableHearingStatement::getId).toList();
    UpdateHearingCantonModel updateHearingCantonModel =
        UpdateHearingCantonModel.builder().comment("Forza Napoli").ids(ids).swissCanton(SwissCanton.JURA)
            .build();

    //when
    mvc.perform(put("/v1/timetable-hearing/statements/update-canton")
            .contentType(contentType)
            .content(mapper.writeValueAsString(updateHearingCantonModel)))
        .andExpect(status().isOk());
  }

  @Test
  void shouldThrowForbiddenExceptionWhenStatementUpdatableIsFalse() throws Exception {
    TimetableHearingYearModel hearingYear = timetableHearingYearController.getHearingYear(YEAR);
    hearingYear.setStatementEditable(false);
    timetableHearingYearController.updateTimetableHearingSettings(YEAR, hearingYear);

    TimetableHearingStatementModel statement = timetableHearingStatementController.createStatement(
        TimetableHearingStatementModel.builder()
            .timetableYear(TIMETABLE_HEARING_YEAR.getTimetableYear())
            .swissCanton(SwissCanton.BERN)
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
        .andExpect(status().isForbidden())
        .andExpect(result -> assertTrue(result.getResolvedException() instanceof ForbiddenDueToHearingYearSettingsException))
        .andExpect(result -> assertEquals("Operation not allowed",
            ((ForbiddenDueToHearingYearSettingsException) Objects.requireNonNull(
                result.getResolvedException())).getErrorResponse()
                .getMessage()));
  }

  @Test
  void shouldAddDocumentsToExistingStatementWithoutDocuments() throws Exception {
    TimetableHearingStatementModel statement = timetableHearingStatementController.createStatement(
        TimetableHearingStatementModel.builder()
            .timetableYear(TIMETABLE_HEARING_YEAR.getTimetableYear())
            .swissCanton(SwissCanton.BERN)
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
            .file(
                new MockMultipartFile(MULTIPART_FILES.get(2).getName(), MULTIPART_FILES.get(2).getOriginalFilename(),
                    MULTIPART_FILES.get(2).getContentType(), MULTIPART_FILES.get(2).getBytes())))

        .andExpect(jsonPath("$." + Fields.statementStatus, is(StatementStatus.RECEIVED.toString())))
        .andExpect(jsonPath("$." + Fields.documents, hasSize(1)));
  }

  @Test
  void shouldUpdateStatementWithDocumentsWithAdditionalDocuments() throws Exception {
    TimetableHearingStatementModel timetableHearingStatementModel = TimetableHearingStatementModel.builder()
        .timetableYear(TIMETABLE_HEARING_YEAR.getTimetableYear())
        .swissCanton(SwissCanton.BERN)
        .statementSender(TimetableHearingStatementSenderModel.builder()
            .email("fabienne.mueller@sbb.ch")
            .build())
        .statement("Ich haette gerne mehrere Verbindungen am Abend.")
        .build();

    TimetableHearingStatementModel statement = timetableHearingStatementController.createStatement(
        timetableHearingStatementModel,
        List.of(MULTIPART_FILES.get(1)));

    MockMultipartFile statementJson = new AtlasMockMultipartFile("statement", null,
        MediaType.APPLICATION_JSON_VALUE, mapper.writeValueAsString(statement));

    mvc.perform(multipart(HttpMethod.PUT, "/v1/timetable-hearing/statements/" + statement.getId())
            .file(statementJson)
            .file(new MockMultipartFile(MULTIPART_FILES.get(0).getName(), MULTIPART_FILES.get(0).getOriginalFilename(),
                MULTIPART_FILES.get(0).getContentType(), MULTIPART_FILES.get(0).getBytes()))
            .file(
                new MockMultipartFile(MULTIPART_FILES.get(2).getName(), MULTIPART_FILES.get(2).getOriginalFilename(),
                    MULTIPART_FILES.get(2).getContentType(), MULTIPART_FILES.get(2).getBytes())))

        .andExpect(status().isOk())
        .andExpect(jsonPath("$." + Fields.statementStatus, is(StatementStatus.RECEIVED.toString())))
        .andExpect(jsonPath("$." + Fields.documents, hasSize(3)));
  }

  @Test
  void shouldUpdateStatementWithDocumentsWithAdditionalDocumentAndRemoveExisting() throws Exception {
    TimetableHearingStatementModel timetableHearingStatementModel = TimetableHearingStatementModel.builder()
        .timetableYear(TIMETABLE_HEARING_YEAR.getTimetableYear())
        .swissCanton(SwissCanton.BERN)
        .statementSender(TimetableHearingStatementSenderModel.builder()
            .email("fabienne.mueller@sbb.ch")
            .build())
        .statement("Ich haette gerne mehrere Verbindungen am Abend.")
        .build();

    TimetableHearingStatementModel statement = timetableHearingStatementController.createStatement(
        timetableHearingStatementModel,
        List.of(MULTIPART_FILES.get(1)));

    statement.setDocuments(Collections.emptyList());
    MockMultipartFile statementJson = new AtlasMockMultipartFile("statement", null,
        MediaType.APPLICATION_JSON_VALUE, mapper.writeValueAsString(statement));

    mvc.perform(multipart(HttpMethod.PUT, "/v1/timetable-hearing/statements/" + statement.getId())
            .file(statementJson)
            .file(
                new MockMultipartFile(MULTIPART_FILES.get(2).getName(), MULTIPART_FILES.get(2).getOriginalFilename(),
                    MULTIPART_FILES.get(2).getContentType(), MULTIPART_FILES.get(2).getBytes())))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$." + Fields.statementStatus, is(StatementStatus.RECEIVED.toString())))
        .andExpect(jsonPath("$." + Fields.documents, hasSize(1)))
        .andExpect(jsonPath("$." + Fields.documents + "[0].fileName", is(MULTIPART_FILES.get(2).getOriginalFilename())));
  }

  @Test
  void shouldGetStatementById() throws Exception {
    TimetableHearingStatementModel statement = timetableHearingStatementController.createStatement(
        TimetableHearingStatementModel.builder()
            .timetableYear(TIMETABLE_HEARING_YEAR.getTimetableYear())
            .swissCanton(SwissCanton.BERN)
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
            .swissCanton(SwissCanton.BERN)
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

  @Test
  void shouldGetStatementDocumentByDocumentId() throws Exception {
    TimetableHearingStatementModel statement = timetableHearingStatementController.createStatement(
        TimetableHearingStatementModel.builder()
            .timetableYear(TIMETABLE_HEARING_YEAR.getTimetableYear())
            .swissCanton(SwissCanton.BERN)
            .statementSender(TimetableHearingStatementSenderModel.builder()
                .email("fabienne.mueller@sbb.ch")
                .build())
            .statement("Ich hätte gerne mehrere Verbindungen am Abend.")
            .build(),
        List.of(MULTIPART_FILES.get(0)));

    mvc.perform(get("/v1/timetable-hearing/statements/" + statement.getId() + "/documents/" + MULTIPART_FILES.get(0)
            .getOriginalFilename()))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_PDF_VALUE));
  }

  @Test
  void shouldThrowExceptionOnGetStatementDocumentByDocumentId() throws Exception {
    TimetableHearingStatementModel statement = timetableHearingStatementController.createStatement(
        TimetableHearingStatementModel.builder()
            .timetableYear(TIMETABLE_HEARING_YEAR.getTimetableYear())
            .swissCanton(SwissCanton.BERN)
            .statementSender(TimetableHearingStatementSenderModel.builder()
                .email("fabienne.mueller@sbb.ch")
                .build())
            .statement("Ich hätte gerne mehrere Verbindungen am Abend.")
            .build(),
        List.of(MULTIPART_FILES.get(0)));

    mvc.perform(get("/v1/timetable-hearing/statements/" + statement.getId() + "/documents/" + "nonexistingfilename"))
        .andExpect(status().isNotFound())
        .andExpect(result -> assertTrue(result.getResolvedException() instanceof FileNotFoundException));
  }

  @Test
  void shouldDeleteStatementDocumentByDocumentId() throws Exception {
    TimetableHearingStatementModel statement = timetableHearingStatementController.createStatement(
        TimetableHearingStatementModel.builder()
            .timetableYear(TIMETABLE_HEARING_YEAR.getTimetableYear())
            .swissCanton(SwissCanton.BERN)
            .statementSender(TimetableHearingStatementSenderModel.builder()
                .email("fabienne.mueller@sbb.ch")
                .build())
            .statement("Ich hätte gerne mehrere Verbindungen am Abend.")
            .build(),
        List.of(MULTIPART_FILES.get(0)));

    mvc.perform(delete(
            "/v1/timetable-hearing/statements/" + statement.getId() + "/documents/" + MULTIPART_FILES.get(0).getOriginalFilename()))
        .andExpect(status().isOk());
  }

  @Test
  void shouldGetStatementDocumentNotFoundWhenNoDocument() throws Exception {
    TimetableHearingStatementModel statement = timetableHearingStatementController.createStatement(
        TimetableHearingStatementModel.builder()
            .timetableYear(TIMETABLE_HEARING_YEAR.getTimetableYear())
            .swissCanton(SwissCanton.BERN)
            .statementSender(TimetableHearingStatementSenderModel.builder()
                .email("fabienne.mueller@sbb.ch")
                .build())
            .statement("Ich hätte gerne mehrere Verbindungen am Abend.")
            .build(),
        Collections.emptyList());

    mvc.perform(get("/v1/timetable-hearing/statements/" + statement.getId() + "/documents/" + "nonexistingfilename"))
        .andExpect(status().isNotFound());
  }

  @Test
  void shouldGetStatementsAsCsv() throws Exception {
    // Given
    String expectedCsvHeader = """
        Kanton;"Fahrplanfeld-Nr.";Fahrplanfeldbezeichnung;Haltestelle;"Abkürzung Transportunternehmung";"Name Transportunternehmung";Stellungnahme;Anhang;Status;Begründung;Vorname;Nachname;Organisation;Strasse;"PLZ/Ort";"E-Mail";Bearbeiter;"Zuletzt bearbeitet";Fahrplanjahr
        """;

    TimetableHearingStatementModel statement = timetableHearingStatementController.createStatement(
        TimetableHearingStatementModel.builder()
            .timetableYear(TIMETABLE_HEARING_YEAR.getTimetableYear())
            .swissCanton(SwissCanton.BERN)
            .statementSender(TimetableHearingStatementSenderModel.builder()
                .email("fabienne.mueller@sbb.ch")
                .build())
            .statement("Ich hätte gerne mehrere Verbindungen am Abend.")
            .build(),
        Collections.emptyList());

    // When
    MvcResult mvcResult = mvc.perform(
            get("/v1/timetable-hearing/statements/csv/de?timetableHearingYear=" + statement.getTimetableYear()))
        .andExpect(status().isOk())
        .andReturn();

    // Then
    String response = mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8);
    assertThat(response).startsWith(CsvExportWriter.UTF_8_BYTE_ORDER_MARK + expectedCsvHeader);
    assertThat(response).contains(statement.getStatement());

    verify(userAdministrationClient, times(1)).getUserInformation(any());
  }

  @Test
  void shouldUpdateStatementWithReplacingTransportCompany() throws Exception {
    TimetableHearingStatementModel timetableHearingStatementModel = TimetableHearingStatementModel.builder()
        .timetableYear(TIMETABLE_HEARING_YEAR.getTimetableYear())
        .swissCanton(SwissCanton.BERN)
        .statementSender(TimetableHearingStatementSenderModel.builder()
            .email("fabienne.mueller@sbb.ch")
            .build())
        .responsibleTransportCompanies(List.of(TimetableHearingStatementResponsibleTransportCompanyModel.builder()
            .id(1L)
            .businessRegisterName("SBB")
            .build()))
        .statement("Ich haette gerne mehrere Verbindungen am Abend.")
        .build();

    TimetableHearingStatementModel statement = timetableHearingStatementController.createStatement(
        timetableHearingStatementModel, Collections.emptyList());
    assertThat(statement.getResponsibleTransportCompanies()).hasSize(1);
    assertThat(statement.getResponsibleTransportCompanies().get(0).getId()).isEqualTo(1);

    statement.setResponsibleTransportCompanies(List.of(TimetableHearingStatementResponsibleTransportCompanyModel.builder()
        .id(2L)
        .businessRegisterName("BLS")
        .build()));
    MockMultipartFile statementJson = new AtlasMockMultipartFile("statement", null,
        MediaType.APPLICATION_JSON_VALUE, mapper.writeValueAsString(statement));

    mvc.perform(multipart(HttpMethod.PUT, "/v1/timetable-hearing/statements/" + statement.getId())
            .file(statementJson))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$." + Fields.statementStatus, is(StatementStatus.RECEIVED.toString())))
        .andExpect(jsonPath("$." + Fields.responsibleTransportCompanies, hasSize(1)))
        .andExpect(jsonPath("$." + Fields.responsibleTransportCompanies + "[0].id", is(2)));
  }
}
