package ch.sbb.line.directory.controller;

import static ch.sbb.atlas.model.controller.WithAdminMockJwtAuthentication.MockJwtAuthenticationFactory.createJwtWithoutSbbUid;
import static ch.sbb.line.directory.helper.PdfFiles.MULTIPART_FILES;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ch.sbb.atlas.api.bodi.TransportCompanyModel;
import ch.sbb.atlas.api.client.bodi.TransportCompanyClient;
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
import java.util.Optional;
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

class TimetableHearingStatementControllerV1ApiTest extends BaseControllerApiTest {

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
 private TimetableHearingStatementRepository timetableHearingStatementRepository;

 @Autowired
 private TimetableFieldNumberVersionRepository timetableFieldNumberVersionRepository;

 @MockBean
 private TimetableFieldNumberService timetableFieldNumberService;

 @MockBean
 private TransportCompanyClient transportCompanyClient;

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

}
