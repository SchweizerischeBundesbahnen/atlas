package ch.sbb.line.directory.module.tth.controller;

import static ch.sbb.line.directory.helper.PdfFiles.MULTIPART_FILES;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ch.sbb.atlas.api.bodi.TransportCompanyModel;
import ch.sbb.atlas.api.client.bodi.TransportCompanyClient;
import ch.sbb.atlas.api.client.user.administration.UserAdministrationClient;
import ch.sbb.atlas.api.lidi.enumaration.TtfnMeanOfTransport;
import ch.sbb.atlas.api.timetable.hearing.TimetableHearingStatementDataProtectionModel;
import ch.sbb.atlas.api.timetable.hearing.TimetableHearingStatementDocumentModel;
import ch.sbb.atlas.api.timetable.hearing.TimetableHearingStatementModelV2;
import ch.sbb.atlas.api.timetable.hearing.TimetableHearingStatementModelV2.Fields;
import ch.sbb.atlas.api.timetable.hearing.TimetableHearingStatementResponsibleTransportCompanyModel;
import ch.sbb.atlas.api.timetable.hearing.TimetableHearingStatementSenderModelV2;
import ch.sbb.atlas.api.timetable.hearing.TimetableHearingYearModel;
import ch.sbb.atlas.api.timetable.hearing.enumeration.HearingStatus;
import ch.sbb.atlas.api.timetable.hearing.enumeration.StatementStatus;
import ch.sbb.atlas.api.timetable.hearing.model.BatchUpdateTimetableHearingStatementsModel;
import ch.sbb.atlas.api.timetable.hearing.model.UpdateHearingCantonModel;
import ch.sbb.atlas.api.timetable.hearing.model.UpdateHearingStatementStatusModel;
import ch.sbb.atlas.export.CsvExportWriter;
import ch.sbb.atlas.kafka.model.SwissCanton;
import ch.sbb.atlas.kafka.model.user.admin.ApplicationRole;
import ch.sbb.atlas.kafka.model.user.admin.ApplicationType;
import ch.sbb.atlas.kafka.model.user.admin.PermissionRestrictionType;
import ch.sbb.atlas.model.Status;
import ch.sbb.atlas.model.controller.AtlasMockMultipartFile;
import ch.sbb.atlas.model.controller.BaseControllerApiTest;
import ch.sbb.atlas.model.controller.WithMockJwtAuthentication;
import ch.sbb.atlas.model.controller.WithMockJwtAuthentication.MockAccountType;
import ch.sbb.atlas.model.controller.WithMockJwtAuthentication.MockRole;
import ch.sbb.atlas.model.exception.NotFoundException.FileNotFoundException;
import ch.sbb.atlas.redact.StringRedactor;
import ch.sbb.atlas.user.administration.security.entity.Permission;
import ch.sbb.atlas.user.administration.security.entity.PermissionRestriction;
import ch.sbb.atlas.user.administration.security.repository.PermissionRepository;
import ch.sbb.line.directory.module.ttfn.entity.TimetableFieldNumber;
import ch.sbb.line.directory.module.ttfn.entity.TimetableFieldNumberVersion;
import ch.sbb.line.directory.module.ttfn.repository.TimetableFieldNumberVersionRepository;
import ch.sbb.line.directory.module.ttfn.service.TimetableFieldNumberService;
import ch.sbb.line.directory.module.tth.entity.StatementSender;
import ch.sbb.line.directory.module.tth.entity.TimetableHearingStatement;
import ch.sbb.line.directory.module.tth.entity.TimetableHearingYear;
import ch.sbb.line.directory.module.tth.exception.ForbiddenDueToHearingYearSettingsException;
import ch.sbb.line.directory.module.tth.exception.PdfDocumentConstraintViolationException;
import ch.sbb.line.directory.module.tth.mapper.ResponsibleTransportCompanyMapper;
import ch.sbb.line.directory.module.tth.repository.TimetableHearingStatementRepository;
import ch.sbb.line.directory.module.tth.repository.TimetableHearingYearRepository;
import ch.sbb.line.directory.shared.transportcompany.entity.SharedTransportCompany;
import ch.sbb.line.directory.shared.transportcompany.repository.SharedTransportCompanyRepository;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

class TimetableHearingStatementControllerInternalApiTest extends BaseControllerApiTest {

  private static final long YEAR = 2022L;
  private static final String TTFNID = "ch:1:ttfnid:123123123";
  private static final String SBOID = "ch:1:sboid:123451";

  @Autowired
  private TimetableHearingYearRepository timetableHearingYearRepository;

  @Autowired
  private TimetableHearingYearControllerInternal timetableHearingYearController;

  @Autowired
  private TimetableHearingStatementControllerInternal timetableHearingStatementControllerInternal;

  @Autowired
  private TimetableHearingStatementRepository timetableHearingStatementRepository;

  @Autowired
  private SharedTransportCompanyRepository sharedTransportCompanyRepository;

  @Autowired
  private TimetableFieldNumberVersionRepository timetableFieldNumberVersionRepository;

  @Autowired
  private PermissionRepository permissionRepository;

  @MockitoBean
  private TimetableFieldNumberService timetableFieldNumberService;

  @MockitoBean
  private TransportCompanyClient transportCompanyClient;

  @MockitoBean
  private UserAdministrationClient userAdministrationClient;

  private SharedTransportCompany sharedTransportCompany;
  private SharedTransportCompany sharedTransportCompany1;

  @BeforeEach
  void setUp() {
    timetableHearingYearRepository.saveAndFlush(TimetableHearingYear.builder()
        .timetableYear(YEAR)
        .hearingStatus(HearingStatus.PLANNED)
        .hearingFrom(LocalDate.of(2021, 1, 1))
        .hearingTo(LocalDate.of(2021, 2, 1))
        .statementCreatableExternal(true)
        .statementCreatableInternal(true)
        .statementEditable(true)
        .build());

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
        .number("5678")
        .descriptionOutwardLine1("Description")
        .descriptionReturnLine1("Description")
        .meanOfTransport(TtfnMeanOfTransport.TRAIN)
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
    permissionRepository.deleteAll();
  }

  @Nested
  @DisplayName("PUT internal/timetable-hearing/statements/update-statement-status")
  class UpdateHearingStatementStatus {

    @Test
    void shouldUpdateHearingStatementStatus() throws Exception {
      timetableHearingYearController.startHearingYear(YEAR);

      //given
      TimetableHearingStatement statement1 = TimetableHearingStatement.builder()
          .timetableYear(YEAR)
          .swissCanton(SwissCanton.BERN)
          .statementStatus(StatementStatus.RECEIVED)
          .statementSender(StatementSender.builder()
              .emails(List.of("mike@thebike.com"))
              .build())
          .statement("Ich mag bitte mehr Bös fahren")
          .build();
      TimetableHearingStatement statement2 = TimetableHearingStatement.builder()
          .timetableYear(YEAR)
          .swissCanton(SwissCanton.BERN)
          .statementStatus(StatementStatus.JUNK)
          .statementSender(StatementSender.builder()
              .emails(List.of("mike@thebike.com"))
              .build())
          .statement("Ich mag bitte mehr Bös fahren")
          .build();
      statement1 = timetableHearingStatementRepository.saveAndFlush(statement1);
      statement2 = timetableHearingStatementRepository.saveAndFlush(statement2);
      List<Long> ids = Stream.of(statement1, statement2).map(TimetableHearingStatement::getId).toList();
      UpdateHearingStatementStatusModel updateHearingStatementStatusModel =
          UpdateHearingStatementStatusModel.builder().ids(ids).justification("Forza Napoli")
              .statementStatus(StatementStatus.ACCEPTED).build();

      //when
      mvc.perform(put("/internal/timetable-hearing/statements/update-statement-status")
              .contentType(contentType)
              .content(mapper.writeValueAsString(updateHearingStatementStatusModel)))
          .andExpect(status().isOk());

      statement1 = timetableHearingStatementRepository.findById(statement1.getId()).orElseThrow();
      assertThat(statement1.getInternalComment()).isEqualTo("Forza Napoli");
      assertThat(statement1.getPublicComment()).isNull();
      assertThat(statement1.getStatementStatus()).isEqualTo(StatementStatus.ACCEPTED);

      statement2 = timetableHearingStatementRepository.findById(statement2.getId()).orElseThrow();
      assertThat(statement2.getInternalComment()).isEqualTo("Forza Napoli");
      assertThat(statement2.getPublicComment()).isNull();
      assertThat(statement2.getStatementStatus()).isEqualTo(StatementStatus.ACCEPTED);
    }

    @Test
    void shouldUpdateHearingStatementStatusAsAdmin() throws Exception {
      updateHearingStatementStatus().andExpect(status().isOk());
    }

    private ResultActions updateHearingStatementStatus() throws Exception {
      TimetableHearingYear timetableHearingYear = timetableHearingYearRepository.findById(YEAR).orElseThrow();
      timetableHearingYear.setHearingStatus(HearingStatus.ACTIVE);
      timetableHearingYearRepository.saveAndFlush(timetableHearingYear);

      //given
      TimetableHearingStatement statement = TimetableHearingStatement.builder()
          .timetableYear(YEAR)
          .swissCanton(SwissCanton.BERN)
          .statementStatus(StatementStatus.RECEIVED)
          .statementSender(StatementSender.builder()
              .emails(List.of("mike@thebike.com"))
              .build())
          .statement("Ich mag bitte mehr Bös fahren")
          .build();
      statement = timetableHearingStatementRepository.saveAndFlush(statement);
      UpdateHearingStatementStatusModel updateHearingStatementStatusModel =
          UpdateHearingStatementStatusModel.builder().ids(List.of(statement.getId())).justification("Forza Napoli")
              .statementStatus(StatementStatus.ACCEPTED).build();

      //when
      return mvc.perform(put("/internal/timetable-hearing/statements/update-statement-status")
          .contentType(contentType)
          .content(mapper.writeValueAsString(updateHearingStatementStatusModel)));
    }

    @Test
    @WithMockJwtAuthentication(role = MockRole.UNAUTHORIZED)
    void shouldNotUpdateHearingStatementStatusAsUnauthorized() throws Exception {
      updateHearingStatementStatus().andExpect(status().isForbidden());
    }

    @Test
    @WithMockJwtAuthentication(role = MockRole.STANDARD)
    void shouldNotUpdateHearingStatementStatusAsStandardUser() throws Exception {
      updateHearingStatementStatus().andExpect(status().isForbidden());
    }

    @Test
    void shouldThrowForbiddenExceptionWhenTimeTableYearOfStatementNotEqualAsHearingYear() throws Exception {
      timetableHearingYearController.startHearingYear(YEAR);

      //given
      TimetableHearingStatement statement1 = TimetableHearingStatement.builder()
          .timetableYear(YEAR)
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
          .timetableYear(YEAR)
          .swissCanton(SwissCanton.BERN)
          .statementStatus(StatementStatus.IN_REVIEW)
          .statementSender(StatementSender.builder()
              .emails(List.of("mike@thebike.com"))
              .build())
          .statement("Ich mag bitte mehr Bös fahren")
          .build();

      TimetableHearingStatement statement2 = TimetableHearingStatement.builder()
          .timetableYear(YEAR)
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
          .timetableYear(YEAR)
          .swissCanton(SwissCanton.BERN)
          .statementStatus(StatementStatus.IN_REVIEW)
          .statementSender(StatementSender.builder()
              .emails(List.of("mike@thebike.com"))
              .build())
          .statement("Ich mag bitte mehr Bös fahren")
          .build();

      TimetableHearingStatement statement2 = TimetableHearingStatement.builder()
          .timetableYear(YEAR)
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
  }

  @Nested
  @DisplayName("PUT internal/timetable-hearing/statements/update-canton")
  class UpdateHearingCanton {

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
    void shouldUpdateHearingStatementCantonAsAdmin() throws Exception {
      updateHearingStatementCanton().andExpect(status().isOk());
    }

    private ResultActions updateHearingStatementCanton() throws Exception {
      TimetableHearingYear timetableHearingYear = timetableHearingYearRepository.findById(YEAR).orElseThrow();
      timetableHearingYear.setHearingStatus(HearingStatus.ACTIVE);
      timetableHearingYearRepository.saveAndFlush(timetableHearingYear);

      //given
      TimetableHearingStatement statement = TimetableHearingStatement.builder()
          .timetableYear(YEAR)
          .swissCanton(SwissCanton.BERN)
          .statementStatus(StatementStatus.RECEIVED)
          .statementSender(StatementSender.builder()
              .emails(List.of("mike@thebike.com"))
              .build())
          .statement("Ich mag bitte mehr Bös fahren")
          .build();
      statement = timetableHearingStatementRepository.saveAndFlush(statement);
      UpdateHearingCantonModel updateHearingCantonModel =
          UpdateHearingCantonModel.builder().comment("Forza Napoli").ids(List.of(statement.getId())).swissCanton(SwissCanton.JURA)
              .build();

      //when
      return mvc.perform(put("/internal/timetable-hearing/statements/update-canton")
          .contentType(contentType)
          .content(mapper.writeValueAsString(updateHearingCantonModel)));
    }

    @Test
    @WithMockJwtAuthentication(role = MockRole.UNAUTHORIZED)
    void shouldNotUpdateHearingStatementCantonAsUnauthorized() throws Exception {
      updateHearingStatementCanton().andExpect(status().isForbidden());
    }

    @Test
    @WithMockJwtAuthentication(role = MockRole.STANDARD)
    void shouldNotUpdateHearingStatementCantonAsStandardUser() throws Exception {
      updateHearingStatementCanton().andExpect(status().isForbidden());
    }
  }

  @Nested
  @DisplayName("GET internal/timetable-hearing/statements")
  class GetStatements {

    @Test
    void shouldGetStatementByHearingYear() throws Exception {
      getStatements().andExpect(status().isOk())
          .andExpect(jsonPath("$.totalCount", is(1)));

      mvc.perform(get("/internal/timetable-hearing/statements?timetableHearingYear=2010"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.totalCount", is(0)));
    }

    private ResultActions getStatements() throws Exception {
      TimetableHearingStatement statement = timetableHearingStatementRepository.save(
          TimetableHearingStatement.builder()
              .timetableYear(YEAR)
              .swissCanton(SwissCanton.BERN)
              .statementStatus(StatementStatus.RECEIVED)
              .statementSender(StatementSender.builder()
                  .emails(List.of("fabienne.mueller@sbb.ch"))
                  .build())
              .statement("Ich hätte gerne mehrere Verbindungen am Abend.")
              .dossierContactSbbuid("e456789")
              .build());

      return mvc.perform(get("/internal/timetable-hearing/statements?timetableHearingYear=" + statement.getTimetableYear()));
    }

    @Test
    @WithMockJwtAuthentication(role = MockRole.UNAUTHORIZED)
    void shouldNotGetStatementsAsUnauthorized() throws Exception {
      getStatements().andExpect(status().isForbidden());
    }

    @Test
    @WithMockJwtAuthentication(role = MockRole.STANDARD)
    void shouldNotGetStatementsAsStandardUser() throws Exception {
      getStatements().andExpect(status().isForbidden());
    }
  }

  @Nested
  @DisplayName("GET internal/timetable-hearing/statements/csv/{language}")
  class GetStatementsAsCsv {

    @Test
    void shouldGetStatementsAsCsv() throws Exception {
      // Given
      String expectedCsvHeader = """
          ID;Kanton;Status;"Fahrplanfeld-Nr.";Fahrplanfeldbezeichnung;Haltestelle;"Abkürzung Transportunternehmung";"Name Transportunternehmung";Stellungnahme;"Anonyme Stellungnahme";"Anonymisierte Stellungnahme";Anhang;Vorname;Nachname;Organisation;Strasse;"PLZ/Ort";"E-Mails";Fahrplanjahr;"Öffentliche Begründung";"Interne Begründung";Thema
          """;

      MvcResult mvcResult = getStatementsAsCsv()
          .andExpect(status().isOk())
          .andReturn();

      // Then
      String response = mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8);
      assertThat(response).startsWith(CsvExportWriter.UTF_8_BYTE_ORDER_MARK + expectedCsvHeader);
      assertThat(response).contains("Ich hätte gerne mehrere Verbindungen am Abend.");
    }

    private ResultActions getStatementsAsCsv() throws Exception {
      TimetableHearingStatement statement = timetableHearingStatementRepository.save(
          TimetableHearingStatement.builder()
              .timetableYear(YEAR)
              .swissCanton(SwissCanton.BERN)
              .statementStatus(StatementStatus.RECEIVED)
              .statementSender(StatementSender.builder()
                  .emails(List.of("fabienne.mueller@sbb.ch"))
                  .build())
              .statement("Ich hätte gerne mehrere Verbindungen am Abend.")
              .dossierContactSbbuid("e456789")
              .build());

      // When
      return mvc.perform(
          get("/internal/timetable-hearing/statements/csv/de?timetableHearingYear=" + statement.getTimetableYear() +
              "&anonymized=false"));
    }

    @Test
    @WithMockJwtAuthentication(role = MockRole.UNAUTHORIZED)
    void shouldNotGetStatementsAsCsvAsUnauthorized() throws Exception {
      getStatementsAsCsv().andExpect(status().isForbidden());
    }

    @Test
    void shouldGetStatementsAsCsvAnonymized() throws Exception {
      // Given
      String expectedCsvHeader = """
          ID;Kanton;Status;"Fahrplanfeld-Nr.";Fahrplanfeldbezeichnung;Haltestelle;"Abkürzung Transportunternehmung";"Name Transportunternehmung";Stellungnahme;Anhang;Fahrplanjahr;Thema
          """;

      TimetableHearingStatementModelV2 statement = timetableHearingStatementControllerInternal.createStatement(
          TimetableHearingStatementModelV2.builder()
              .timetableYear(YEAR)
              .swissCanton(SwissCanton.BERN)
              .statementSender(TimetableHearingStatementSenderModelV2.builder()
                  .firstName("Fabienne")
                  .lastName("Mueller")
                  .zip(3001)
                  .city("Bern")
                  .street("Musterstrasse 1")
                  .organisation("SBB")
                  .emails(Set.of("fabienne.mueller@sbb.ch", "flo.mueller@sbb.ch"))
                  .build())
              .statement("Ich hätte gerne mehrere Verbindungen am Abend.")
              .anonymousStatement("Anonyme Stellungnahme")
              .statementAnonymous(false)
              .internalComment("Einfach eine interne Begründung")
              .publicComment("Einfach eine öffentliche Begründung")
              .build(),
          Collections.emptyList());

      // When
      MvcResult mvcResult = mvc.perform(
              get("/internal/timetable-hearing/statements/csv/de?timetableHearingYear=" + statement.getTimetableYear() +
                  "&anonymized=true"))
          .andExpect(status().isOk())
          .andReturn();

      // Then
      String response = mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8);
      assertThat(response).startsWith(CsvExportWriter.UTF_8_BYTE_ORDER_MARK + expectedCsvHeader);
      assertThat(response).contains(statement.getAnonymousStatement());
      assertThat(response).doesNotContain(statement.getStatement());
      assertThat(response).doesNotContain("fabienne.mueller@sbb.ch");
      assertThat(response).doesNotContain("flo.mueller@sbb.ch");
      assertThat(response).doesNotContain("Fabienne");
      assertThat(response).doesNotContain("Mueller");
      assertThat(response).doesNotContain("3001");
      assertThat(response).doesNotContain("Bern");
      assertThat(response).doesNotContain("Musterstrasse 1");
      assertThat(response).doesNotContain("SBB");
      assertThat(response).doesNotContain("Einfach eine interne Begründung");
      assertThat(response).doesNotContain("Einfach eine öffentliche Begründung");
    }

    @Test
    void shouldGetStatementsAsCsvAnonymizedByBoolean() throws Exception {
      // Given
      String expectedCsvHeader = """
          ID;Kanton;Status;"Fahrplanfeld-Nr.";Fahrplanfeldbezeichnung;Haltestelle;"Abkürzung Transportunternehmung";"Name Transportunternehmung";Stellungnahme;Anhang;Fahrplanjahr;Thema
          """;

      TimetableHearingStatementModelV2 statement = timetableHearingStatementControllerInternal.createStatement(
          TimetableHearingStatementModelV2.builder()
              .timetableYear(YEAR)
              .swissCanton(SwissCanton.BERN)
              .statementSender(TimetableHearingStatementSenderModelV2.builder()
                  .firstName("Fabienne")
                  .lastName("Mueller")
                  .zip(3001)
                  .city("Bern")
                  .street("Musterstrasse 1")
                  .organisation("SBB")
                  .emails(Set.of("fabienne.mueller@sbb.ch", "flo.mueller@sbb.ch"))
                  .build())
              .statement("Ich hätte gerne mehrere Verbindungen am Abend.")
              .statementAnonymous(true)
              .internalComment("Einfach eine interne Begründung")
              .publicComment("Einfach eine öffentliche Begründung")
              .build(),
          Collections.emptyList());

      // When
      MvcResult mvcResult = mvc.perform(
              get("/internal/timetable-hearing/statements/csv/de?timetableHearingYear=" + statement.getTimetableYear() +
                  "&anonymized=true"))
          .andExpect(status().isOk())
          .andReturn();

      // Then
      String response = mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8);
      assertThat(response).startsWith(CsvExportWriter.UTF_8_BYTE_ORDER_MARK + expectedCsvHeader);
      assertThat(statement.getStatementAnonymous()).isTrue();
      assertThat(response).contains(statement.getStatement());
      assertThat(response).doesNotContain("fabienne.mueller@sbb.ch");
      assertThat(response).doesNotContain("flo.mueller@sbb.ch");
      assertThat(response).doesNotContain("Fabienne");
      assertThat(response).doesNotContain("Mueller");
      assertThat(response).doesNotContain("3001");
      assertThat(response).doesNotContain("Bern");
      assertThat(response).doesNotContain("Musterstrasse 1");
      assertThat(response).doesNotContain("SBB");
      assertThat(response).doesNotContain("Einfach eine interne Begründung");
      assertThat(response).doesNotContain("Einfach eine öffentliche Begründung");
    }
  }

  @Nested
  @DisplayName("GET internal/timetable-hearing/statements/{id}")
  class GetStatement {

    @Test
    void shouldGetStatementById() throws Exception {
      getStatementById()
          .andExpect(status().isOk())
          .andExpect(jsonPath("$." + Fields.statementStatus, is(StatementStatus.RECEIVED.toString())))
          .andExpect(jsonPath("$." + TimetableHearingStatementDataProtectionModel.Fields.documents, hasSize(0)));
    }

    private ResultActions getStatementById() throws Exception {
      TimetableHearingStatement statement = timetableHearingStatementRepository.save(
          TimetableHearingStatement.builder()
              .timetableYear(YEAR)
              .swissCanton(SwissCanton.BERN)
              .statementStatus(StatementStatus.RECEIVED)
              .statementSender(StatementSender.builder()
                  .emails(List.of("fabienne.mueller@sbb.ch"))
                  .build())
              .statement("Ich hätte gerne mehrere Verbindungen am Abend.")
              .dossierContactSbbuid("e456789")
              .build());

      return mvc.perform(get("/internal/timetable-hearing/statements/" + statement.getId()));
    }

    @Test
    @WithMockJwtAuthentication(role = MockRole.UNAUTHORIZED)
    void shouldNotBeAuthorizedToGetStatementByIdAsUnauthorized() throws Exception {
      getStatementById().andExpect(status().isForbidden());
    }

    @Test
    @WithMockJwtAuthentication(role = MockRole.STANDARD)
    void shouldBeAuthorizedToGetStatementByIdAsExplicitReader() throws Exception {
      Permission permission = Permission.builder()
          .identifier(WithMockJwtAuthentication.MOCKUSER_SBB_UID)
          .application(ApplicationType.TIMETABLE_HEARING)
          .role(ApplicationRole.EXPLICIT_READER)
          .build();
      permissionRepository.saveAndFlush(permission);

      getStatementById().andExpect(status().isOk())
          // Mails are visible for canton user
          .andExpect(jsonPath("$.statementSender.emails[0]", is("fabienne.mueller@sbb.ch")));
    }

    @Test
    @WithMockJwtAuthentication(sbbuid = "e456789", role = MockRole.STANDARD, accountType = MockAccountType.GUEST)
    void shouldBeAuthorizedToGetStatementByIdAsBoUser() throws Exception {
      Permission permission = Permission.builder()
          .identifier("e456789")
          .application(ApplicationType.TIMETABLE_HEARING)
          .role(ApplicationRole.READER)
          .build();
      permission.setPermissionRestrictions(Set.of(PermissionRestriction.builder()
          .permission(permission)
          .type(PermissionRestrictionType.TRANSPORT_COMPANY_DOSSIER_ANSWER)
          .restriction("true")
          .build()));
      permissionRepository.saveAndFlush(permission);

      getStatementById().andExpect(status().isOk())
          // Mails are redacted for bo user
          .andExpect(jsonPath("$.statementSender.emails[0]", is(StringRedactor.REPLACEMENT)));
    }
  }

  @Nested
  @DisplayName("GET internal/timetable-hearing/statements/{id}/documents/{filename}")
  class GetStatementDocument {

    @Test
    void shouldGetStatementDocumentByDocumentId() throws Exception {
      TimetableHearingStatementModelV2 statement = timetableHearingStatementControllerInternal.createStatement(
          TimetableHearingStatementModelV2.builder()
              .timetableYear(YEAR)
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
              .timetableYear(YEAR)
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
    void shouldGetStatementDocumentNotFoundWhenNoDocument() throws Exception {
      TimetableHearingStatementModelV2 statement = timetableHearingStatementControllerInternal.createStatement(
          TimetableHearingStatementModelV2.builder()
              .timetableYear(YEAR)
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
  }

  @Nested
  @DisplayName("POST internal/timetable-hearing/statements")
  class CreateStatement {

    @Test
    void shouldCreateStatementWithoutDocuments() throws Exception {
      TimetableFieldNumberVersion timetableFieldNumber = TimetableFieldNumberVersion.builder()
          .ttfnid("ch:1:ttfnid:12341241")
          .number("5678")
          .descriptionOutwardLine1("Description")
          .descriptionReturnLine1("Description")
          .meanOfTransport(TtfnMeanOfTransport.TRAIN)
          .status(Status.VALIDATED)
          .businessOrganisation("Business Organisation")
          .validFrom(LocalDate.now())
          .validTo(LocalDate.now().plusYears(1))
          .build();

      timetableFieldNumberVersionRepository.saveAndFlush(timetableFieldNumber);

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

      mvc.perform(multipart(HttpMethod.POST, "/internal/timetable-hearing/statements")
              .file(statementJson))
          .andExpect(status().isCreated())
          .andExpect(jsonPath("$." + Fields.statementStatus, is(StatementStatus.RECEIVED.toString())))
          .andExpect(jsonPath("$." + Fields.ttfnid, is("ch:1:ttfnid:12341241")))
          .andExpect(jsonPath("$." + TimetableHearingStatementDataProtectionModel.Fields.documents, hasSize(0)));
    }

    @Test
    void shouldThrowExceptionOnCreateWhenIdNotNull() throws Exception {
      TimetableFieldNumberVersion timetableFieldNumber = TimetableFieldNumberVersion.builder()
          .ttfnid("ch:1:ttfnid:12341241")
          .number("5678")
          .descriptionOutwardLine1("Description")
          .descriptionReturnLine1("Description")
          .meanOfTransport(TtfnMeanOfTransport.TRAIN)
          .status(Status.VALIDATED)
          .businessOrganisation("Business Organisation")
          .validFrom(LocalDate.now())
          .validTo(LocalDate.now().plusYears(1))
          .build();

      timetableFieldNumberVersionRepository.saveAndFlush(timetableFieldNumber);

      TimetableHearingStatementModelV2 statement = TimetableHearingStatementModelV2.builder()
          .id(1111L)
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

      mvc.perform(multipart(HttpMethod.POST, "/internal/timetable-hearing/statements")
              .file(statementJson))
          .andExpect(status().isBadRequest())
          .andExpect(jsonPath("$.status", is(400)))
          .andExpect(jsonPath("$.error", is("Constraint violation")))
          .andExpect(jsonPath("$.details[0].displayInfo.code", is("ERROR.CONSTRAINT_VIOLATION.CREATE_ID_CHECK")))
          .andExpect(jsonPath("$.details[0].message", is("ID must be null when creating a new element")));
    }

    @Test
    void shouldCreateStatementWithTwoDocuments() throws Exception {
      TimetableHearingStatementModelV2 statement = TimetableHearingStatementModelV2.builder()
          .timetableYear(YEAR)
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
          .andExpect(jsonPath("$." + TimetableHearingStatementDataProtectionModel.Fields.documents, hasSize(2)));
    }

    @Test
    void shouldFailCreatingStatementWithFourDocuments() throws Exception {
      TimetableHearingStatementModelV2 statement = TimetableHearingStatementModelV2.builder()
          .timetableYear(YEAR)
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
  }

  @Nested
  @DisplayName("PUT internal/timetable-hearing/statements/{id}")
  class UpdateHearingStatement {

    @Test
    void shouldUpdateStatement() throws Exception {
      TimetableHearingStatementModelV2 statement = timetableHearingStatementControllerInternal.createStatement(
          TimetableHearingStatementModelV2.builder()
              .timetableYear(YEAR)
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
          .andExpect(jsonPath("$." + TimetableHearingStatementDataProtectionModel.Fields.documents, hasSize(0)));
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
              .timetableYear(YEAR)
              .swissCanton(SwissCanton.BERN)
              .statementSender(statementSenderModelV2)
              .responsibleTransportCompaniesDisplay(sharedTransportCompany.getAbbreviation())
              .responsibleTransportCompanies(List.of(thsrtcm))
              .statement("Ich hätte gerne mehrere Verbindungen am Abend.")
              .build(),
          Collections.emptyList());
      timetableHearingStatementControllerInternal.createStatement(
          TimetableHearingStatementModelV2.builder()
              .timetableYear(YEAR)
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
          .andExpect(jsonPath("$." + TimetableHearingStatementDataProtectionModel.Fields.documents, hasSize(0)));
    }

    @Test
    void shouldThrowForbiddenExceptionWhenStatementUpdatableIsFalse() throws Exception {
      TimetableHearingYearModel hearingYear = timetableHearingYearController.getHearingYear(YEAR);
      hearingYear.setStatementEditable(false);
      timetableHearingYearController.updateTimetableHearingSettings(YEAR, hearingYear);

      TimetableHearingStatementModelV2 statement = timetableHearingStatementControllerInternal.createStatement(
          TimetableHearingStatementModelV2.builder()
              .timetableYear(YEAR)
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
              .timetableYear(YEAR)
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
          .andExpect(jsonPath("$." + TimetableHearingStatementDataProtectionModel.Fields.documents, hasSize(1)));
    }

    @Test
    void shouldUpdateStatementWithDocumentsWithAdditionalDocuments() throws Exception {
      TimetableHearingStatementModelV2 timetableHearingStatementModel = TimetableHearingStatementModelV2.builder()
          .timetableYear(YEAR)
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
          .andExpect(jsonPath("$." + TimetableHearingStatementDataProtectionModel.Fields.documents, hasSize(3)));
    }

    @Test
    void shouldUpdateStatementWithDocumentsWithAdditionalDocumentAndRemoveExisting() throws Exception {
      TimetableHearingStatementModelV2 timetableHearingStatementModel = TimetableHearingStatementModelV2.builder()
          .timetableYear(YEAR)
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
          .andExpect(jsonPath("$." + TimetableHearingStatementDataProtectionModel.Fields.documents, hasSize(1)))
          .andExpect(jsonPath("$." + TimetableHearingStatementDataProtectionModel.Fields.documents + "[0].fileName",
              is(MULTIPART_FILES.get(2).getOriginalFilename())));
    }

    @Test
    void shouldUpdateStatementWithReplacingTransportCompany() throws Exception {
      TimetableHearingStatementModelV2 timetableHearingStatementModel = TimetableHearingStatementModelV2.builder()
          .timetableYear(YEAR)
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

  @Nested
  @DisplayName("GET internal/timetable-hearing/statements/responsible-transport-companies/{ttfnid}/{year}")
  class GetResponsibleTransportCompanies {

    @Test
    void shouldGetResponsibleTransportCompanies() throws Exception {
      mvc.perform(get("/internal/timetable-hearing/statements/responsible-transport-companies/" + TTFNID + "/" + YEAR))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$", hasSize(1)))
          .andExpect(jsonPath("$[0].id", is(sharedTransportCompany.getId().intValue())));
    }
  }

  @Nested
  @DisplayName("POST internal/timetable-hearing/statements/batch-update-statements")
  class UpdateStatements {

    @Test
    void shouldUpdateStatementsInBatchForDossier() throws Exception {
      //given
      TimetableHearingStatement statement = TimetableHearingStatement.builder()
          .timetableYear(2023L)
          .swissCanton(SwissCanton.BERN)
          .statementStatus(StatementStatus.RECEIVED)
          .statementSender(StatementSender.builder()
              .emails(List.of("mike@thebike.com"))
              .build())
          .statement("Ich mag bitte mehr Bös fahren")
          .build();
      statement = timetableHearingStatementRepository.saveAndFlush(statement);
      BatchUpdateTimetableHearingStatementsModel updateModel =
          BatchUpdateTimetableHearingStatementsModel.builder()
              .ids(List.of(statement.getId()))
              .dossierCanton(SwissCanton.BERN)
              .dossierId(1L)
              .dossierContactMail("uerli@bernmobil.ch")
              .dossierContactSbbuid("u123456")
              .statementStatus(StatementStatus.IN_REVIEW)
              .build();

      //when
      mvc.perform(post("/internal/timetable-hearing/statements/batch-update-statements")
              .contentType(contentType)
              .content(mapper.writeValueAsString(updateModel)))
          .andExpect(status().isOk());

      TimetableHearingStatement statementAfterUpdate = timetableHearingStatementRepository.findById(statement.getId())
          .orElseThrow();
      assertThat(statementAfterUpdate.getDossierId()).isEqualTo(1L);
      assertThat(statementAfterUpdate.getDossierContactMail()).isEqualTo("uerli@bernmobil.ch");
      assertThat(statementAfterUpdate.getDossierContactSbbuid()).isEqualTo("u123456");
      assertThat(statementAfterUpdate.getStatementStatus()).isEqualTo(StatementStatus.IN_REVIEW);
    }

    @Test
    @WithMockJwtAuthentication(role = MockRole.UNAUTHORIZED)
    void shouldNotUpdateStatementsInBatchForDossierIfUnauthorized() throws Exception {
      BatchUpdateTimetableHearingStatementsModel updateModel = getBatchUpdateTimetableHearingStatementsModel();

      //when
      mvc.perform(post("/internal/timetable-hearing/statements/batch-update-statements")
              .contentType(contentType)
              .content(mapper.writeValueAsString(updateModel)))
          .andExpect(status().isForbidden());
    }

    @Test
    @WithMockJwtAuthentication(role = MockRole.STANDARD)
    void shouldNotUpdateStatementsInBatchForDossierIfStandardUser() throws Exception {
      BatchUpdateTimetableHearingStatementsModel updateModel = getBatchUpdateTimetableHearingStatementsModel();

      //when
      mvc.perform(post("/internal/timetable-hearing/statements/batch-update-statements")
              .contentType(contentType)
              .content(mapper.writeValueAsString(updateModel)))
          .andExpect(status().isForbidden());
    }

    @Test
    @WithMockJwtAuthentication(role = MockRole.STANDARD)
    void shouldUpdateStatementsInBatchForDossierIfWriter() throws Exception {
      Permission permission = Permission.builder()
          .identifier(WithMockJwtAuthentication.MOCKUSER_SBB_UID)
          .application(ApplicationType.TIMETABLE_HEARING)
          .role(ApplicationRole.WRITER)
          .build();
      permission.setPermissionRestrictions(Set.of(PermissionRestriction.builder()
          .permission(permission)
          .type(PermissionRestrictionType.CANTON)
          .restriction(SwissCanton.BERN.name())
          .build()));
      permissionRepository.saveAndFlush(permission);

      BatchUpdateTimetableHearingStatementsModel updateModel = getBatchUpdateTimetableHearingStatementsModel();

      //when
      mvc.perform(post("/internal/timetable-hearing/statements/batch-update-statements")
              .contentType(contentType)
              .content(mapper.writeValueAsString(updateModel)))
          .andExpect(status().isOk());
    }

    private BatchUpdateTimetableHearingStatementsModel getBatchUpdateTimetableHearingStatementsModel() {
      TimetableHearingStatement statement = TimetableHearingStatement.builder()
          .timetableYear(2023L)
          .swissCanton(SwissCanton.BERN)
          .statementStatus(StatementStatus.RECEIVED)
          .statementSender(StatementSender.builder()
              .emails(List.of("mike@thebike.com"))
              .build())
          .statement("Ich mag bitte mehr Bös fahren")
          .build();
      statement = timetableHearingStatementRepository.saveAndFlush(statement);
      return BatchUpdateTimetableHearingStatementsModel.builder()
          .ids(List.of(statement.getId()))
          .dossierCanton(SwissCanton.BERN)
          .dossierId(1L)
          .dossierContactMail("uerli@bernmobil.ch")
          .dossierContactSbbuid("u123456")
          .statementStatus(StatementStatus.IN_REVIEW)
          .build();
    }
  }

  @Nested
  @DisplayName("POST internal/timetable-hearing/statements/check-data-protection")
  class CheckDataProtection {

    @Test
    void shouldCheckDataProtectionForExistingStatement() {
      // Given
      TimetableHearingStatementModelV2 statement = timetableHearingStatementControllerInternal.createStatement(
          TimetableHearingStatementModelV2.builder()
              .timetableYear(YEAR)
              .swissCanton(SwissCanton.BERN)
              .statementSender(TimetableHearingStatementSenderModelV2.builder()
                  .emails(Set.of("fabienne.mueller@sbb.ch"))
                  .build())
              .statement("Ich hätte gerne mehrere Verbindungen am Abend.")
              .build(),
          Collections.emptyList());
      assertThat(statement.isDataProtectionChecked()).isFalse();

      // when
      timetableHearingStatementControllerInternal.checkDataProtection(TimetableHearingStatementDataProtectionModel.builder()
          .id(statement.getId())
          .statementAnonymous(true)
          .build());

      // then
      TimetableHearingStatementModelV2 updatedStatement = timetableHearingStatementControllerInternal.getStatement(
          statement.getId());
      assertThat(updatedStatement.isDataProtectionChecked()).isTrue();
    }

    @Test
    void shouldCheckDataProtectionForExistingStatementWithDocuments() {
      // Given
      TimetableHearingStatementModelV2 statement = timetableHearingStatementControllerInternal.createStatement(
          TimetableHearingStatementModelV2.builder()
              .timetableYear(YEAR)
              .swissCanton(SwissCanton.BERN)
              .statementSender(TimetableHearingStatementSenderModelV2.builder()
                  .emails(Set.of("fabienne.mueller@sbb.ch"))
                  .build())
              .statement("Ich hätte gerne mehrere Verbindungen am Abend.")
              .build(),
          List.of(MULTIPART_FILES.getFirst()));
      assertThat(statement.isDataProtectionChecked()).isFalse();

      // when
      timetableHearingStatementControllerInternal.checkDataProtection(TimetableHearingStatementDataProtectionModel.builder()
          .id(statement.getId())
          .statementAnonymous(false)
          .anonymousStatement("Anonymisierte Stellungnahme")
          .documents(List.of(TimetableHearingStatementDocumentModel.builder()
              .id(statement.getDocuments().getFirst().getId())
              .anonymous(true)
              .build()))
          .build());

      // then
      TimetableHearingStatementModelV2 updatedStatement = timetableHearingStatementControllerInternal.getStatement(
          statement.getId());
      assertThat(updatedStatement.isDataProtectionChecked()).isTrue();
      assertThat(updatedStatement.getStatementAnonymous()).isFalse();
      assertThat(updatedStatement.getAnonymousStatement()).isNotNull();
      assertThat(updatedStatement.getDocuments().getFirst().getAnonymous()).isTrue();
    }
  }
}
