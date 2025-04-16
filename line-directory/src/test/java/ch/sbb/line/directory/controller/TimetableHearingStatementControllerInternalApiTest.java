package ch.sbb.line.directory.controller;

import static ch.sbb.line.directory.helper.PdfFiles.MULTIPART_FILES;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
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
import ch.sbb.atlas.api.timetable.hearing.TimetableHearingStatementModel.Fields;
import ch.sbb.atlas.api.timetable.hearing.TimetableHearingStatementModelV1;
import ch.sbb.atlas.api.timetable.hearing.TimetableHearingStatementModelV2;
import ch.sbb.atlas.api.timetable.hearing.TimetableHearingStatementResponsibleTransportCompanyModel;
import ch.sbb.atlas.api.timetable.hearing.TimetableHearingStatementSenderModelV1;
import ch.sbb.atlas.api.timetable.hearing.TimetableHearingStatementSenderModelV2;
import ch.sbb.atlas.api.timetable.hearing.TimetableHearingYearModel;
import ch.sbb.atlas.api.timetable.hearing.enumeration.StatementStatus;
import ch.sbb.atlas.api.timetable.hearing.model.UpdateHearingCantonModel;
import ch.sbb.atlas.api.timetable.hearing.model.UpdateHearingStatementStatusModel;
import ch.sbb.atlas.export.CsvExportWriter;
import ch.sbb.atlas.kafka.model.SwissCanton;
import ch.sbb.atlas.model.Status;
import ch.sbb.atlas.model.controller.AtlasMockMultipartFile;
import ch.sbb.atlas.model.controller.BaseControllerApiTest;
import ch.sbb.atlas.model.exception.NotFoundException.FileNotFoundException;
import ch.sbb.line.directory.entity.SharedTransportCompany;
import ch.sbb.line.directory.entity.StatementSender;
import ch.sbb.line.directory.entity.TimetableFieldNumber;
import ch.sbb.line.directory.entity.TimetableFieldNumberVersion;
import ch.sbb.line.directory.entity.TimetableHearingStatement;
import ch.sbb.line.directory.exception.ForbiddenDueToHearingYearSettingsException;
import ch.sbb.line.directory.exception.PdfDocumentConstraintViolationException;
import ch.sbb.line.directory.mapper.ResponsibleTransportCompanyMapper;
import ch.sbb.line.directory.repository.SharedTransportCompanyRepository;
import ch.sbb.line.directory.repository.TimetableFieldNumberVersionRepository;
import ch.sbb.line.directory.repository.TimetableHearingStatementRepository;
import ch.sbb.line.directory.repository.TimetableHearingYearRepository;
import ch.sbb.line.directory.service.TimetableFieldNumberService;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MvcResult;

class TimetableHearingStatementControllerInternalApiTest extends BaseControllerApiTest {

  private static final long YEAR = 2022L;
  private static final TimetableHearingYearModel TIMETABLE_HEARING_YEAR = TimetableHearingYearModel.builder()
      .timetableYear(YEAR)
      .hearingFrom(LocalDate.of(2021, 1, 1))
      .hearingTo(LocalDate.of(2021, 2, 1))
      .build();
  private static final String TTFNID = "ch:1:ttfnid:123123123";
  private static final String SBOID = "ch:1:sboid:123451";

  private final TimetableHearingYearRepository timetableHearingYearRepository;
  private final TimetableHearingYearControllerInternal timetableHearingYearController;
  private final TimetableHearingStatementControllerInternal timetableHearingStatementControllerInternal;
  private final TimetableHearingStatementRepository timetableHearingStatementRepository;
  private final SharedTransportCompanyRepository sharedTransportCompanyRepository;
  private final TimetableFieldNumberVersionRepository timetableFieldNumberVersionRepository;

  @MockitoBean
  private TimetableFieldNumberService timetableFieldNumberService;

  @MockitoBean
  private TransportCompanyClient transportCompanyClient;

  @MockitoBean
  private UserAdministrationClient userAdministrationClient;

  @Autowired
  TimetableHearingStatementControllerInternalApiTest(
      TimetableHearingYearRepository timetableHearingYearRepository,
      TimetableHearingYearControllerInternal timetableHearingYearController,
      TimetableHearingStatementControllerInternal timetableHearingStatementControllerInternal,
      TimetableHearingStatementRepository timetableHearingStatementRepository,
      SharedTransportCompanyRepository sharedTransportCompanyRepository,
      TimetableFieldNumberVersionRepository timetableFieldNumberVersionRepository) {
    this.timetableHearingYearRepository = timetableHearingYearRepository;
    this.timetableHearingYearController = timetableHearingYearController;
    this.timetableHearingStatementControllerInternal = timetableHearingStatementControllerInternal;
    this.timetableHearingStatementRepository = timetableHearingStatementRepository;
    this.sharedTransportCompanyRepository = sharedTransportCompanyRepository;
    this.timetableFieldNumberVersionRepository = timetableFieldNumberVersionRepository;
  }

  private SharedTransportCompany sharedTransportCompany;
  private SharedTransportCompany sharedTransportCompany1;

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

    sharedTransportCompany = SharedTransportCompany.builder()
        .id(1L)
        .number("#0001")
        .description("SBB description")
        .abbreviation("SBB")
        .businessRegisterName("Schweizerische Bundesbahnen SBB")
        .businessRegisterNumber("SBB register number")
        .build();
    sharedTransportCompanyRepository.saveAndFlush(sharedTransportCompany);

    sharedTransportCompany1 = SharedTransportCompany.builder()
        .id(2L)
        .number("#0002")
        .description("BLS description")
        .abbreviation("BLS")
        .businessRegisterName("Berner Land Seilbahnen")
        .businessRegisterNumber("BLS register number")
        .build();
    sharedTransportCompanyRepository.saveAndFlush(sharedTransportCompany1);

    TimetableFieldNumberVersion timetableFieldNumber = TimetableFieldNumberVersion.builder()
        .ttfnid(TTFNID)
        .swissTimetableFieldNumber("1234")
        .number("5678")
        .description("Description")
        .status(Status.VALIDATED)
        .businessOrganisation("Business Organisation")
        .validFrom(LocalDate.now())
        .validTo(LocalDate.now().plusYears(1))
        .build();

    timetableFieldNumberVersionRepository.saveAndFlush(timetableFieldNumber);
  }

  @AfterEach
  void tearDown() {
    timetableHearingYearRepository.deleteAll();
    timetableHearingStatementRepository.deleteAll();
    timetableFieldNumberVersionRepository.deleteAll();
    sharedTransportCompanyRepository.deleteAll();
  }

  @Test
  void shouldCreateStatementWithoutDocuments() throws Exception {
    TimetableFieldNumberVersion timetableFieldNumber = TimetableFieldNumberVersion.builder()
        .ttfnid("ch:1:ttfnid:12341241")
        .swissTimetableFieldNumber("1234")
        .number("5678")
        .description("Description")
        .status(Status.VALIDATED)
        .businessOrganisation("Business Organisation")
        .validFrom(LocalDate.now())
        .validTo(LocalDate.now().plusYears(1))
        .build();

    timetableFieldNumberVersionRepository.saveAndFlush(timetableFieldNumber);

    TimetableHearingStatementModelV2 statement = TimetableHearingStatementModelV2.builder()
        .timetableYear(TIMETABLE_HEARING_YEAR.getTimetableYear())
        .swissCanton(SwissCanton.BERN)
        .ttfnid("ch:1:ttfnid:12341241")
        .statementSender(TimetableHearingStatementSenderModelV2.builder()
            .emails(Set.of("fabienne.mueller@sbb.ch"))
            .build())
        .statement("Ich hätte gerne mehrere Verbindungen am Abend.")
        .build();

    MockMultipartFile statementJson = new AtlasMockMultipartFile("statement", null,
        MediaType.APPLICATION_JSON_VALUE, mapper.writeValueAsString(statement));

    mvc.perform(multipart(HttpMethod.POST, "/internal/timetable-hearing/statements")
            .file(statementJson))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$." + Fields.statementStatus, is(StatementStatus.RECEIVED.toString())))
        .andExpect(jsonPath("$." + Fields.ttfnid, is("ch:1:ttfnid:12341241")))
        .andExpect(jsonPath("$." + Fields.documents, hasSize(0)));
  }

  @Test
  void shouldReportInvalidJsonInStatementWithoutDocuments() throws Exception {
    TimetableHearingStatementModelV1 statement = TimetableHearingStatementModelV1.builder()
        .timetableYear(TIMETABLE_HEARING_YEAR.getTimetableYear())
        .swissCanton(SwissCanton.BERN)
        .statementSender(TimetableHearingStatementSenderModelV1.builder()
            .build())
        .statement("Ich hätte gerne mehrere Verbindungen am Abend.")
        .build();

    MockMultipartFile statementJson = new AtlasMockMultipartFile("statement", null,
        MediaType.APPLICATION_JSON_VALUE, mapper.writeValueAsString(statement));

    mvc.perform(multipart(HttpMethod.POST, "/internal/timetable-hearing/statements")
            .file(statementJson))
        .andExpect(status().isBadRequest());
  }

  @Test
  void shouldCreateStatementWithTwoDocuments() throws Exception {
    TimetableHearingStatementModelV2 statement = TimetableHearingStatementModelV2.builder()
        .timetableYear(TIMETABLE_HEARING_YEAR.getTimetableYear())
        .swissCanton(SwissCanton.BERN)
        .statementSender(TimetableHearingStatementSenderModelV2.builder()
            .emails(Set.of("fabienne.mueller@sbb.ch"))
            .build())
        .statement("Ich hätte gerne mehrere Verbindungen am Abend.")
        .build();

    MockMultipartFile statementJson = new AtlasMockMultipartFile("statement", null, MediaType.APPLICATION_JSON_VALUE,
        mapper.writeValueAsString(statement));

    mvc.perform(multipart(HttpMethod.POST, "/internal/timetable-hearing/statements")
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
    TimetableHearingStatementModelV2 statement = TimetableHearingStatementModelV2.builder()
        .timetableYear(TIMETABLE_HEARING_YEAR.getTimetableYear())
        .swissCanton(SwissCanton.BERN)
        .statementSender(TimetableHearingStatementSenderModelV2.builder()
            .emails(Set.of("fabienne.mueller@sbb.ch"))
            .build())
        .statement("Ich hätte gerne mehrere Verbindungen am Abend.")
        .build();

    MockMultipartFile statementJson = new AtlasMockMultipartFile("statement", null, MediaType.APPLICATION_JSON_VALUE,
        mapper.writeValueAsString(statement));

    mvc.perform(multipart(HttpMethod.POST, "/internal/timetable-hearing/statements")
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
        .andExpect(result -> assertInstanceOf(PdfDocumentConstraintViolationException.class, result.getResolvedException()))
        .andExpect(
            result -> assertEquals("Overall number of documents is: 4 which exceeds the number of allowed documents of 3.",
                Objects.requireNonNull(result.getResolvedException()).getMessage()));
  }

  @Test
  void shouldThrowForbiddenExceptionWhenStatementCreatableInternalIsFalse() throws Exception {
    TimetableHearingStatementModelV2 statement = TimetableHearingStatementModelV2.builder()
        .timetableYear(YEAR)
        .swissCanton(SwissCanton.BERN)
        .ttfnid("ch:1:ttfnid:12341241")
        .statementSender(TimetableHearingStatementSenderModelV2.builder()
            .emails(Set.of("fabienne.mueller@sbb.ch"))
            .build())
        .statement("Ich hätte gerne mehrere Verbindungen am Abend.")
        .build();

    MockMultipartFile statementJson = new AtlasMockMultipartFile("statement", null,
        MediaType.APPLICATION_JSON_VALUE, mapper.writeValueAsString(statement));

    TimetableHearingYearModel hearingYearModel = timetableHearingYearController.startHearingYear(YEAR);
    hearingYearModel.setStatementCreatableInternal(false);
    timetableHearingYearController.updateTimetableHearingSettings(YEAR, hearingYearModel);

    mvc.perform(multipart(HttpMethod.POST, "/internal/timetable-hearing/statements")
            .file(statementJson))
        .andExpect(status().isForbidden())
        .andExpect(result -> assertInstanceOf(ForbiddenDueToHearingYearSettingsException.class, result.getResolvedException()))
        .andExpect(result -> assertEquals("Operation not allowed",
            ((ForbiddenDueToHearingYearSettingsException) Objects.requireNonNull(
                result.getResolvedException())).getErrorResponse()
                .getMessage()));
  }

  @Test
  void shouldUpdateStatement() throws Exception {
    TimetableHearingStatementModelV2 statement = timetableHearingStatementControllerInternal.createStatement(
        TimetableHearingStatementModelV2.builder()
            .timetableYear(TIMETABLE_HEARING_YEAR.getTimetableYear())
            .swissCanton(SwissCanton.BERN)
            .statementSender(TimetableHearingStatementSenderModelV2.builder()
                .emails(Set.of("fabienne.mueller@sbb.ch"))
                .build())
            .statement("Ich hätte gerne mehrere Verbindungen am Abend.")
            .build(),
        Collections.emptyList());

    statement.setStatementStatus(StatementStatus.JUNK);

    MockMultipartFile statementJson = new AtlasMockMultipartFile("statement", null,
        MediaType.APPLICATION_JSON_VALUE, mapper.writeValueAsString(statement));

    mvc.perform(multipart(HttpMethod.PUT, "/internal/timetable-hearing/statements/" + statement.getId())
            .file(statementJson))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$." + Fields.statementStatus, is(StatementStatus.JUNK.toString())))
        .andExpect(jsonPath("$." + Fields.documents, hasSize(0)));
  }

  @Test
  void shouldCreateTwoStatementsWithTheSameCompanyAndThenUpdateOneStatementWithAnotherCompany() throws Exception {
    TimetableHearingStatementResponsibleTransportCompanyModel thsrtcm =
        ResponsibleTransportCompanyMapper.toModel(sharedTransportCompany);
    TimetableHearingStatementSenderModelV2 statementSenderModelV2 = TimetableHearingStatementSenderModelV2.builder()
        .firstName("Fabienne")
        .emails(Set.of("fabienne.mueller@sbb.ch"))
        .build();
    TimetableHearingStatementModelV2 statement = timetableHearingStatementControllerInternal.createStatement(
        TimetableHearingStatementModelV2.builder()
            .timetableYear(TIMETABLE_HEARING_YEAR.getTimetableYear())
            .swissCanton(SwissCanton.BERN)
            .statementSender(statementSenderModelV2)
            .responsibleTransportCompaniesDisplay(sharedTransportCompany.getAbbreviation())
            .responsibleTransportCompanies(List.of(thsrtcm))
            .statement("Ich hätte gerne mehrere Verbindungen am Abend.")
            .build(),
        Collections.emptyList());
    timetableHearingStatementControllerInternal.createStatement(
        TimetableHearingStatementModelV2.builder()
            .timetableYear(TIMETABLE_HEARING_YEAR.getTimetableYear())
            .swissCanton(SwissCanton.BERN)
            .statementSender(statementSenderModelV2)
            .responsibleTransportCompaniesDisplay(sharedTransportCompany.getAbbreviation())
            .responsibleTransportCompanies(List.of(thsrtcm))
            .statement("Ich hätte gerne mehrere Verbindungen am Abend1.")
            .build(),
        Collections.emptyList());

    statementSenderModelV2.setFirstName("Fabienne2");
    statement.setStatementSender(statementSenderModelV2);
    TimetableHearingStatementResponsibleTransportCompanyModel thsrtcm1 =
        ResponsibleTransportCompanyMapper.toModel(sharedTransportCompany1);
    statement.setResponsibleTransportCompanies(List.of(thsrtcm1));

    MockMultipartFile statementJson = new AtlasMockMultipartFile("statement", null,
        MediaType.APPLICATION_JSON_VALUE, mapper.writeValueAsString(statement));

    mvc.perform(multipart(HttpMethod.PUT, "/internal/timetable-hearing/statements/" + statement.getId())
            .file(statementJson))
        .andExpect(status().isOk())
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
            .emails(List.of("mike@thebike.com"))
            .build())
        .statement("Ich mag bitte mehr Bös fahren")
        .build();
    TimetableHearingStatement statement2 = TimetableHearingStatement.builder()
        .timetableYear(TIMETABLE_HEARING_YEAR.getTimetableYear())
        .swissCanton(SwissCanton.BERN)
        .statementStatus(StatementStatus.JUNK)
        .statementSender(StatementSender.builder()
            .emails(List.of("mike@thebike.com"))
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
    mvc.perform(put("/internal/timetable-hearing/statements/update-statement-status")
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
            .emails(List.of("mike@thebike.com"))
            .build())
        .statement("Ich mag bitte mehr Bös fahren")
        .build();

    TimetableHearingStatement statement2 = TimetableHearingStatement.builder()
        .timetableYear(2055L)
        .swissCanton(SwissCanton.BERN)
        .statementStatus(StatementStatus.JUNK)
        .statementSender(StatementSender.builder()
            .emails(List.of("mike@thebike.com"))
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
    mvc.perform(put("/internal/timetable-hearing/statements/update-statement-status")
            .contentType(contentType)
            .content(mapper.writeValueAsString(updateHearingStatementStatusModel)))
        .andExpect(status().isForbidden());
  }

  @Test
  void shouldThrowForbiddenWhenHearingYearIsNotActive() throws Exception {
    //given
    TimetableHearingStatement statement1 = TimetableHearingStatement.builder()
        .timetableYear(TIMETABLE_HEARING_YEAR.getTimetableYear())
        .swissCanton(SwissCanton.BERN)
        .statementStatus(StatementStatus.IN_REVIEW)
        .statementSender(StatementSender.builder()
            .emails(List.of("mike@thebike.com"))
            .build())
        .statement("Ich mag bitte mehr Bös fahren")
        .build();

    TimetableHearingStatement statement2 = TimetableHearingStatement.builder()
        .timetableYear(TIMETABLE_HEARING_YEAR.getTimetableYear())
        .swissCanton(SwissCanton.BERN)
        .statementStatus(StatementStatus.JUNK)
        .statementSender(StatementSender.builder()
            .emails(List.of("mike@thebike.com"))
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
    mvc.perform(put("/internal/timetable-hearing/statements/update-statement-status")
            .contentType(contentType)
            .content(mapper.writeValueAsString(updateHearingStatementStatusModel)))
        .andExpect(status().isForbidden());
  }

  @Test
  void shouldThrowForbiddenWhenHearingYearStatementEditableIsDisabled() throws Exception {
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
            .emails(List.of("mike@thebike.com"))
            .build())
        .statement("Ich mag bitte mehr Bös fahren")
        .build();

    TimetableHearingStatement statement2 = TimetableHearingStatement.builder()
        .timetableYear(TIMETABLE_HEARING_YEAR.getTimetableYear())
        .swissCanton(SwissCanton.BERN)
        .statementStatus(StatementStatus.JUNK)
        .statementSender(StatementSender.builder()
            .emails(List.of("mike@thebike.com"))
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
    mvc.perform(put("/internal/timetable-hearing/statements/update-statement-status")
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
            .emails(List.of("mike@thebike.com"))
            .build())
        .statement("Ich mag bitte mehr Bös fahren")
        .build();
    TimetableHearingStatement statement2 = TimetableHearingStatement.builder()
        .timetableYear(2024L)
        .swissCanton(SwissCanton.AARGAU)
        .statementStatus(StatementStatus.JUNK)
        .statementSender(StatementSender.builder()
            .emails(List.of("mike@thebike.com"))
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
    mvc.perform(put("/internal/timetable-hearing/statements/update-canton")
            .contentType(contentType)
            .content(mapper.writeValueAsString(updateHearingCantonModel)))
        .andExpect(status().isOk());
  }

  @Test
  void shouldThrowForbiddenExceptionWhenStatementUpdatableIsFalse() throws Exception {
    TimetableHearingYearModel hearingYear = timetableHearingYearController.getHearingYear(YEAR);
    hearingYear.setStatementEditable(false);
    timetableHearingYearController.updateTimetableHearingSettings(YEAR, hearingYear);

    TimetableHearingStatementModelV2 statement = timetableHearingStatementControllerInternal.createStatement(
        TimetableHearingStatementModelV2.builder()
            .timetableYear(TIMETABLE_HEARING_YEAR.getTimetableYear())
            .swissCanton(SwissCanton.BERN)
            .statementSender(TimetableHearingStatementSenderModelV2.builder()
                .emails(Set.of("fabienne.mueller@sbb.ch"))
                .build())
            .statement("Ich hätte gerne mehrere Verbindungen am Abend.")
            .build(),
        Collections.emptyList());

    statement.setStatementStatus(StatementStatus.JUNK);

    MockMultipartFile statementJson = new AtlasMockMultipartFile("statement", null,
        MediaType.APPLICATION_JSON_VALUE, mapper.writeValueAsString(statement));

    mvc.perform(multipart(HttpMethod.PUT, "/internal/timetable-hearing/statements/" + statement.getId())
            .file(statementJson))
        .andExpect(status().isForbidden())
        .andExpect(result -> assertInstanceOf(ForbiddenDueToHearingYearSettingsException.class, result.getResolvedException()))
        .andExpect(result -> assertEquals("Operation not allowed",
            ((ForbiddenDueToHearingYearSettingsException) Objects.requireNonNull(
                result.getResolvedException())).getErrorResponse()
                .getMessage()));
  }

  @Test
  void shouldAddDocumentsToExistingStatementWithoutDocuments() throws Exception {
    TimetableHearingStatementModelV2 statement = timetableHearingStatementControllerInternal.createStatement(
        TimetableHearingStatementModelV2.builder()
            .timetableYear(TIMETABLE_HEARING_YEAR.getTimetableYear())
            .swissCanton(SwissCanton.BERN)
            .statementSender(TimetableHearingStatementSenderModelV2.builder()
                .emails(Set.of("fabienne.mueller@sbb.ch"))
                .build())
            .statement("Ich hätte gerne mehrere Verbindungen am Abend.")
            .build(),
        Collections.emptyList());

    MockMultipartFile statementJson = new AtlasMockMultipartFile("statement", null,
        MediaType.APPLICATION_JSON_VALUE, mapper.writeValueAsString(statement));

    mvc.perform(multipart(HttpMethod.PUT, "/internal/timetable-hearing/statements/" + statement.getId())
            .file(statementJson)
            .file(
                new MockMultipartFile(MULTIPART_FILES.get(2).getName(), MULTIPART_FILES.get(2).getOriginalFilename(),
                    MULTIPART_FILES.get(2).getContentType(), MULTIPART_FILES.get(2).getBytes())))

        .andExpect(jsonPath("$." + Fields.statementStatus, is(StatementStatus.RECEIVED.toString())))
        .andExpect(jsonPath("$." + Fields.documents, hasSize(1)));
  }

  @Test
  void shouldUpdateStatementWithDocumentsWithAdditionalDocuments() throws Exception {
    TimetableHearingStatementModelV2 timetableHearingStatementModel = TimetableHearingStatementModelV2.builder()
        .timetableYear(TIMETABLE_HEARING_YEAR.getTimetableYear())
        .swissCanton(SwissCanton.BERN)
        .statementSender(TimetableHearingStatementSenderModelV2.builder()
            .emails(Set.of("fabienne.mueller@sbb.ch"))
            .build())
        .statement("Ich haette gerne mehrere Verbindungen am Abend.")
        .build();

    TimetableHearingStatementModelV2 statement = timetableHearingStatementControllerInternal.createStatement(
        timetableHearingStatementModel,
        List.of(MULTIPART_FILES.get(1)));

    MockMultipartFile statementJson = new AtlasMockMultipartFile("statement", null,
        MediaType.APPLICATION_JSON_VALUE, mapper.writeValueAsString(statement));

    mvc.perform(multipart(HttpMethod.PUT, "/internal/timetable-hearing/statements/" + statement.getId())
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
    TimetableHearingStatementModelV2 timetableHearingStatementModel = TimetableHearingStatementModelV2.builder()
        .timetableYear(TIMETABLE_HEARING_YEAR.getTimetableYear())
        .swissCanton(SwissCanton.BERN)
        .statementSender(TimetableHearingStatementSenderModelV2.builder()
            .emails(Set.of("fabienne.mueller@sbb.ch"))
            .build())
        .statement("Ich haette gerne mehrere Verbindungen am Abend.")
        .build();

    TimetableHearingStatementModelV2 statement = timetableHearingStatementControllerInternal.createStatement(
        timetableHearingStatementModel,
        List.of(MULTIPART_FILES.get(1)));

    statement.setDocuments(Collections.emptyList());
    MockMultipartFile statementJson = new AtlasMockMultipartFile("statement", null,
        MediaType.APPLICATION_JSON_VALUE, mapper.writeValueAsString(statement));

    mvc.perform(multipart(HttpMethod.PUT, "/internal/timetable-hearing/statements/" + statement.getId())
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
    TimetableHearingStatementModelV2 statement = timetableHearingStatementControllerInternal.createStatement(
        TimetableHearingStatementModelV2.builder()
            .timetableYear(TIMETABLE_HEARING_YEAR.getTimetableYear())
            .swissCanton(SwissCanton.BERN)
            .statementSender(TimetableHearingStatementSenderModelV2.builder()
                .emails(Set.of("fabienne.mueller@sbb.ch"))
                .build())
            .statement("Ich hätte gerne mehrere Verbindungen am Abend.")
            .build(),
        Collections.emptyList());

    mvc.perform(get("/internal/timetable-hearing/statements/" + statement.getId()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$." + Fields.statementStatus, is(StatementStatus.RECEIVED.toString())))
        .andExpect(jsonPath("$." + Fields.documents, hasSize(0)));
  }

  @Test
  void shouldGetStatementByHearingYear() throws Exception {
    TimetableHearingStatementModelV2 statement = timetableHearingStatementControllerInternal.createStatement(
        TimetableHearingStatementModelV2.builder()
            .timetableYear(TIMETABLE_HEARING_YEAR.getTimetableYear())
            .swissCanton(SwissCanton.BERN)
            .statementSender(TimetableHearingStatementSenderModelV2.builder()
                .emails(Set.of("fabienne.mueller@sbb.ch"))
                .build())
            .statement("Ich hätte gerne mehrere Verbindungen am Abend.")
            .build(),
        Collections.emptyList());

    mvc.perform(get("/internal/timetable-hearing/statements?timetableHearingYear=" + statement.getTimetableYear()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.totalCount", is(1)));

    mvc.perform(get("/internal/timetable-hearing/statements?timetableHearingYear=2010"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.totalCount", is(0)));
  }

  @Test
  void shouldGetStatementDocumentByDocumentId() throws Exception {
    TimetableHearingStatementModelV2 statement = timetableHearingStatementControllerInternal.createStatement(
        TimetableHearingStatementModelV2.builder()
            .timetableYear(TIMETABLE_HEARING_YEAR.getTimetableYear())
            .swissCanton(SwissCanton.BERN)
            .statementSender(TimetableHearingStatementSenderModelV2.builder()
                .emails(Set.of("fabienne.mueller@sbb.ch"))
                .build())
            .statement("Ich hätte gerne mehrere Verbindungen am Abend.")
            .build(),
        List.of(MULTIPART_FILES.getFirst()));

    mvc.perform(get("/internal/timetable-hearing/statements/" + statement.getId() + "/documents/" + MULTIPART_FILES.getFirst()
            .getOriginalFilename()))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_PDF_VALUE));
  }

  @Test
  void shouldThrowExceptionOnGetStatementDocumentByDocumentId() throws Exception {
    TimetableHearingStatementModelV2 statement = timetableHearingStatementControllerInternal.createStatement(
        TimetableHearingStatementModelV2.builder()
            .timetableYear(TIMETABLE_HEARING_YEAR.getTimetableYear())
            .swissCanton(SwissCanton.BERN)
            .statementSender(TimetableHearingStatementSenderModelV2.builder()
                .emails(Set.of("fabienne.mueller@sbb.ch"))
                .build())
            .statement("Ich hätte gerne mehrere Verbindungen am Abend.")
            .build(),
        List.of(MULTIPART_FILES.getFirst()));

    mvc.perform(get("/internal/timetable-hearing/statements/" + statement.getId() + "/documents/" + "nonexistingfilename"))
        .andExpect(status().isNotFound())
        .andExpect(result -> assertInstanceOf(FileNotFoundException.class, result.getResolvedException()));
  }

  @Test
  void shouldDeleteStatementDocumentByDocumentId() throws Exception {
    TimetableHearingStatementModelV2 statement = timetableHearingStatementControllerInternal.createStatement(
        TimetableHearingStatementModelV2.builder()
            .timetableYear(TIMETABLE_HEARING_YEAR.getTimetableYear())
            .swissCanton(SwissCanton.BERN)
            .statementSender(TimetableHearingStatementSenderModelV2.builder()
                .emails(Set.of("fabienne.mueller@sbb.ch"))
                .build())
            .statement("Ich hätte gerne mehrere Verbindungen am Abend.")
            .build(),
        List.of(MULTIPART_FILES.getFirst()));

    mvc.perform(delete(
            "/internal/timetable-hearing/statements/" + statement.getId() + "/documents/" + MULTIPART_FILES.getFirst()
                .getOriginalFilename()))
        .andExpect(status().isOk());
  }

  @Test
  void shouldGetStatementDocumentNotFoundWhenNoDocument() throws Exception {
    TimetableHearingStatementModelV2 statement = timetableHearingStatementControllerInternal.createStatement(
        TimetableHearingStatementModelV2.builder()
            .timetableYear(TIMETABLE_HEARING_YEAR.getTimetableYear())
            .swissCanton(SwissCanton.BERN)
            .statementSender(TimetableHearingStatementSenderModelV2.builder()
                .emails(Set.of("fabienne.mueller@sbb.ch"))
                .build())
            .statement("Ich hätte gerne mehrere Verbindungen am Abend.")
            .build(),
        Collections.emptyList());

    mvc.perform(get("/internal/timetable-hearing/statements/" + statement.getId() + "/documents/" + "nonexistingfilename"))
        .andExpect(status().isNotFound());
  }

  @Test
  void shouldGetStatementsAsCsv() throws Exception {
    // Given
    String expectedCsvHeader = """
        Kanton;"Fahrplanfeld-Nr.";Fahrplanfeldbezeichnung;Haltestelle;ID;"Abkürzung Transportunternehmung";"Name Transportunternehmung";Stellungnahme;Anhang;Status;Begründung;Vorname;Nachname;Organisation;Strasse;"PLZ/Ort";"E-Mails";Bearbeiter;"Zuletzt bearbeitet";Fahrplanjahr
        """;

    TimetableHearingStatementModelV2 statement = timetableHearingStatementControllerInternal.createStatement(
        TimetableHearingStatementModelV2.builder()
            .timetableYear(TIMETABLE_HEARING_YEAR.getTimetableYear())
            .swissCanton(SwissCanton.BERN)
            .statementSender(TimetableHearingStatementSenderModelV2.builder()
                .emails(Set.of("fabienne.mueller@sbb.ch", "flo.mueller@sbb.ch"))
                .build())
            .statement("Ich hätte gerne mehrere Verbindungen am Abend.")
            .build(),
        Collections.emptyList());

    // When
    MvcResult mvcResult = mvc.perform(
            get("/internal/timetable-hearing/statements/csv/de?timetableHearingYear=" + statement.getTimetableYear()))
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
    TimetableHearingStatementModelV2 timetableHearingStatementModel = TimetableHearingStatementModelV2.builder()
        .timetableYear(TIMETABLE_HEARING_YEAR.getTimetableYear())
        .swissCanton(SwissCanton.BERN)
        .statementSender(TimetableHearingStatementSenderModelV2.builder()
            .emails(Set.of("fabienne.mueller@sbb.ch"))
            .build())
        .responsibleTransportCompanies(List.of(TimetableHearingStatementResponsibleTransportCompanyModel.builder()
            .id(1L)
            .businessRegisterName("SBB")
            .build()))
        .statement("Ich haette gerne mehrere Verbindungen am Abend.")
        .build();

    TimetableHearingStatementModelV2 statement = timetableHearingStatementControllerInternal.createStatement(
        timetableHearingStatementModel, Collections.emptyList());
    assertThat(statement.getResponsibleTransportCompanies()).hasSize(1);
    assertThat(statement.getResponsibleTransportCompanies().getFirst().getId()).isEqualTo(1);

    statement.setResponsibleTransportCompanies(List.of(TimetableHearingStatementResponsibleTransportCompanyModel.builder()
        .id(2L)
        .businessRegisterName("BLS")
        .build()));
    MockMultipartFile statementJson = new AtlasMockMultipartFile("statement", null,
        MediaType.APPLICATION_JSON_VALUE, mapper.writeValueAsString(statement));

    mvc.perform(multipart(HttpMethod.PUT, "/internal/timetable-hearing/statements/" + statement.getId())
            .file(statementJson))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$." + Fields.statementStatus, is(StatementStatus.RECEIVED.toString())))
        .andExpect(jsonPath("$." + Fields.responsibleTransportCompanies, hasSize(1)))
        .andExpect(jsonPath("$." + Fields.responsibleTransportCompanies + "[0].id", is(2)));
  }
}
