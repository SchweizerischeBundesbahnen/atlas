package ch.sbb.line.directory.controller;

import static ch.sbb.atlas.model.controller.WithAdminMockJwtAuthentication.MockJwtAuthenticationFactory.createJwtWithoutSbbUid;
import static ch.sbb.line.directory.helper.PdfFiles.MULTIPART_FILES;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ch.sbb.atlas.api.bodi.TransportCompanyModel;
import ch.sbb.atlas.api.client.bodi.TransportCompanyClient;
import ch.sbb.atlas.api.client.user.administration.UserAdministrationClient;
import ch.sbb.atlas.api.timetable.hearing.TimetableHearingStatementModel.Fields;
import ch.sbb.atlas.api.timetable.hearing.TimetableHearingYearModel;
import ch.sbb.atlas.api.timetable.hearing.enumeration.StatementStatus;
import ch.sbb.atlas.model.Status;
import ch.sbb.atlas.model.controller.AtlasMockMultipartFile;
import ch.sbb.atlas.model.controller.BaseControllerApiTest;
import ch.sbb.line.directory.entity.SharedTransportCompany;
import ch.sbb.line.directory.entity.TimetableFieldNumber;
import ch.sbb.line.directory.entity.TimetableFieldNumberVersion;
import ch.sbb.line.directory.exception.ForbiddenDueToHearingYearSettingsException;
import ch.sbb.line.directory.exception.NoClientCredentialAuthUsedException;
import ch.sbb.line.directory.repository.SharedTransportCompanyRepository;
import ch.sbb.line.directory.repository.TimetableFieldNumberVersionRepository;
import ch.sbb.line.directory.repository.TimetableHearingStatementRepository;
import ch.sbb.line.directory.repository.TimetableHearingYearRepository;
import ch.sbb.line.directory.service.TimetableFieldNumberService;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
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
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@MockitoBean(types = UserAdministrationClient.class)
class TimetableHearingStatementControllerV2ApiTest extends BaseControllerApiTest {

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
  private final TimetableHearingStatementRepository timetableHearingStatementRepository;
  private final SharedTransportCompanyRepository sharedTransportCompanyRepository;
  private final TimetableFieldNumberVersionRepository timetableFieldNumberVersionRepository;

  @MockitoBean
  private TimetableFieldNumberService timetableFieldNumberService;

  @MockitoBean
  private TransportCompanyClient transportCompanyClient;

  @Autowired
  TimetableHearingStatementControllerV2ApiTest(
      TimetableHearingYearRepository timetableHearingYearRepository,
      TimetableHearingYearControllerInternal timetableHearingYearController,
      TimetableHearingStatementRepository timetableHearingStatementRepository,
      SharedTransportCompanyRepository sharedTransportCompanyRepository,
      TimetableFieldNumberVersionRepository timetableFieldNumberVersionRepository) {
    this.timetableHearingYearRepository = timetableHearingYearRepository;
    this.timetableHearingYearController = timetableHearingYearController;
    this.timetableHearingStatementRepository = timetableHearingStatementRepository;
    this.sharedTransportCompanyRepository = sharedTransportCompanyRepository;
    this.timetableFieldNumberVersionRepository = timetableFieldNumberVersionRepository;
  }

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

    SharedTransportCompany sharedTransportCompany = SharedTransportCompany.builder()
        .id(1L)
        .number("#0001")
        .description("SBB description")
        .abbreviation("SBB")
        .businessRegisterName("Schweizerische Bundesbahnen SBB")
        .businessRegisterNumber("SBB register number")
        .build();
    sharedTransportCompanyRepository.saveAndFlush(sharedTransportCompany);

    SharedTransportCompany sharedTransportCompany1 = SharedTransportCompany.builder()
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
  void shouldThrowExceptionWhenNotClientCredentialsAuthUsedForExternalEndpointV2() throws Exception {
    final MockMultipartFile statementJson = getMockMultipartFile();

    mvc.perform(multipart(HttpMethod.POST, "/v2/timetable-hearing/statements/external")
            .file(statementJson))
        .andExpect(status().isBadRequest())
        .andExpect(result -> assertInstanceOf(NoClientCredentialAuthUsedException.class, result.getResolvedException()))
        .andExpect(result -> assertEquals("Bad authentication used",
            ((NoClientCredentialAuthUsedException) Objects.requireNonNull(result.getResolvedException())).getErrorResponse()
                .getMessage()));
  }

  @Test
  void shouldThrowForbiddenExceptionWhenStatementCreatableExternalV2IsFalse() throws Exception {
    // For Client-Credential Auth
    SecurityContext context = SecurityContextHolder.getContext();
    Authentication authentication = new JwtAuthenticationToken(createJwtWithoutSbbUid(),
        AuthorityUtils.createAuthorityList("ROLE_atlas-admin"));
    authentication.setAuthenticated(true);
    context.setAuthentication(authentication);

    TimetableHearingYearModel hearingYearModel = timetableHearingYearController.startHearingYear(YEAR);
    hearingYearModel.setStatementCreatableExternal(false);
    timetableHearingYearController.updateTimetableHearingSettings(YEAR, hearingYearModel);

    final MockMultipartFile statementJson = getMockMultipartFile();

    mvc.perform(multipart(HttpMethod.POST, "/v2/timetable-hearing/statements/external")
            .file(statementJson))
        .andExpect(status().isForbidden())
        .andExpect(result -> assertInstanceOf(ForbiddenDueToHearingYearSettingsException.class, result.getResolvedException()))
        .andExpect(result -> assertEquals("Operation not allowed",
            ((ForbiddenDueToHearingYearSettingsException) Objects.requireNonNull(
                result.getResolvedException())).getErrorResponse()
                .getMessage()));
  }

  @Test
  void shouldCreateStatementExternalV2FromSkiWeb() throws Exception {
    // For Client-Credential Auth
    SecurityContext context = SecurityContextHolder.getContext();
    Authentication authentication = new JwtAuthenticationToken(createJwtWithoutSbbUid(),
        AuthorityUtils.createAuthorityList("ROLE_atlas-admin"));
    authentication.setAuthenticated(true);
    context.setAuthentication(authentication);

    timetableHearingYearController.startHearingYear(YEAR);
    final MockMultipartFile statementJson = getMockMultipartFile();

    mvc.perform(multipart(HttpMethod.POST, "/v2/timetable-hearing/statements/external")
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

  private static @NotNull MockMultipartFile getMockMultipartFile() {
    final String statement = """
         {
         	"statement": "I need some more busses please.",
         	"statementSender": {
         		"emails": ["maurer@post.ch", "max@post.ch"],
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
    return new AtlasMockMultipartFile("statement", null,
        MediaType.APPLICATION_JSON_VALUE, statement);
  }

}
