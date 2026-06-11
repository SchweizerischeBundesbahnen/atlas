package ch.sbb.workflow.module.lidi.tth.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ch.sbb.atlas.api.client.line.workflow.TimetableHearingStatementClient;
import ch.sbb.atlas.api.timetable.hearing.enumeration.HearingStatus;
import ch.sbb.atlas.api.workflow.tth.dossier.BoAnswerModel;
import ch.sbb.atlas.api.workflow.tth.dossier.DossierStatus;
import ch.sbb.atlas.api.workflow.tth.dossier.TthDossierModel;
import ch.sbb.atlas.kafka.model.SwissCanton;
import ch.sbb.atlas.kafka.model.user.admin.ApplicationRole;
import ch.sbb.atlas.kafka.model.user.admin.ApplicationType;
import ch.sbb.atlas.kafka.model.user.admin.PermissionRestrictionType;
import ch.sbb.atlas.model.controller.BaseControllerApiTest;
import ch.sbb.atlas.model.controller.WithMockJwtAuthentication;
import ch.sbb.atlas.model.controller.WithMockJwtAuthentication.MockAccountType;
import ch.sbb.atlas.model.controller.WithMockJwtAuthentication.MockRole;
import ch.sbb.atlas.user.administration.security.entity.Permission;
import ch.sbb.atlas.user.administration.security.entity.PermissionRestriction;
import ch.sbb.atlas.user.administration.security.repository.PermissionRepository;
import ch.sbb.workflow.module.lidi.tth.entity.TthDossier;
import ch.sbb.workflow.module.lidi.tth.entity.TthDossierQuestion;
import ch.sbb.workflow.module.lidi.tth.entity.TthDossierYear;
import ch.sbb.workflow.module.lidi.tth.mail.TthDossierNotificationService;
import ch.sbb.workflow.module.lidi.tth.mapper.TthDossierMapper;
import ch.sbb.workflow.module.lidi.tth.repository.TthDossierRepository;
import ch.sbb.workflow.module.lidi.tth.repository.TthDossierYearRepository;
import ch.sbb.workflow.module.lidi.tth.service.BoContactPermissionService;
import ch.sbb.workflow.module.lidi.tth.service.TthDossierCsvExportService;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.ResultActions;

class TthDossierApiInternalControllerTest extends BaseControllerApiTest {

  @Autowired
  private TthDossierRepository tthDossierRepository;

  @Autowired
  private TthDossierYearRepository tthDossierYearRepository;

  @Autowired
  private PermissionRepository permissionRepository;

  @MockitoBean
  private BoContactPermissionService boContactPermissionService;

  @MockitoBean
  private TimetableHearingStatementClient TimetableHearingStatementClient;

  @MockitoBean
  private TthDossierCsvExportService tthDossierCsvExportService;

  @MockitoBean
  private TthDossierNotificationService notificationService;

  private TthDossier tthDossier;
  private TthDossierYear tthDossierYear;

  @BeforeEach
  void setUp() {
    tthDossierYear = TthDossierYear.builder()
        .timetableYear(2024L)
        .hearingStatus(HearingStatus.ACTIVE)
        .build();
    tthDossierYear = tthDossierYearRepository.save(tthDossierYear);

    tthDossier = TthDossier.builder()
        .topic("TOPIC")
        .statementIds(List.of(1001L))
        .swissCanton(SwissCanton.BERN)
        .dossierStatus(DossierStatus.ADDED)
        .tthDossierYear(tthDossierYear).build();
    tthDossier = tthDossierRepository.saveAndFlush(tthDossier);
  }

  @AfterEach
  void tearDown() {
    tthDossierRepository.deleteAll();
    permissionRepository.deleteAll();
  }

  @Nested
  @DisplayName("GET /internal/tth/dossier")
  class GetDossiers {

    @Test
    void shouldGetTthDossierOverview() throws Exception {
      //when
      mvc.perform(get("/internal/tth/dossier")
              .queryParam("page", "0")
              .queryParam("size", "5")
              .queryParam("sort", "id,asc"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.totalCount").value(1))
          .andExpect(jsonPath("$.objects", hasSize(1)));
    }

    @Test
    @WithMockJwtAuthentication(role = MockRole.UNAUTHORIZED)
    void shouldNotGetTthDossierOverviewAsUnauthorized() throws Exception {
      mvc.perform(get("/internal/tth/dossier")).andExpect(status().isForbidden());
    }
  }

  @Nested
  @DisplayName("GET /internal/tth/dossier/csv")
  class GetDossiersCsv {

    @Test
    void shouldGetDossiersCsv() throws Exception {
      File csvFile = File.createTempFile("tth-dossiers", ".csv");
      csvFile.deleteOnExit();
      Files.writeString(csvFile.toPath(), "id;topic\n1;TOPIC\n", StandardCharsets.UTF_8);

      when(tthDossierCsvExportService.getTthDossierTuCsvModels(any())).thenReturn(List.of());
      when(tthDossierCsvExportService.writeCsv(any(), any(), any())).thenReturn(csvFile);

      mvc.perform(get("/internal/tth/dossier/csv")
              .queryParam("lang", "DE"))
          .andExpect(status().isOk());
    }

    @Test
    @WithMockJwtAuthentication(role = MockRole.UNAUTHORIZED)
    void shouldNotGetDossiersCsvAsUnauthorized() throws Exception {
      mvc.perform(get("/internal/tth/dossier/csv")
              .queryParam("lang", "DE"))
          .andExpect(status().isForbidden());
    }
  }

  @Nested
  @DisplayName("GET /internal/tth/dossier/{dossierId}")
  class GetDossier {

    @Test
    void shouldGetTthDossierById() throws Exception {
      mvc.perform(get("/internal/tth/dossier/" + tthDossier.getId()))
          .andExpect(status().isOk());
    }

    @Test
    @WithMockJwtAuthentication(role = MockRole.UNAUTHORIZED)
    void shouldNotGetTthDossierByIdAsUnauthorized() throws Exception {
      mvc.perform(get("/internal/tth/dossier/" + tthDossier.getId()))
          .andExpect(status().isForbidden());
    }
  }

  @Nested
  @DisplayName("POST /internal/tth/dossier")
  class CreateDossier {

    @Test
    void shouldAllowCreationWithoutQuestion() throws Exception {
      creationWithoutQuestion().andExpect(status().isOk());
    }

    private ResultActions creationWithoutQuestion() throws Exception {
      return mvc.perform(post("/internal/tth/dossier")
          .contentType(contentType)
          .content("""
              {
                "topic": "no question",
                "statementIds": [123],
                "swissCanton": "BERN",
                "questions": []
              }
              """));
    }

    @Test
    @WithMockJwtAuthentication(role = MockRole.UNAUTHORIZED)
    void shouldNotAllowCreationWithoutQuestionToUnauthorized() throws Exception {
      creationWithoutQuestion().andExpect(status().isForbidden());
    }

    @Test
    @WithMockJwtAuthentication(role = MockRole.STANDARD)
    void shouldNotAllowCreationWithoutQuestionToStandardUserWithoutPermissions() throws Exception {
      creationWithoutQuestion().andExpect(status().isForbidden());
    }

    @Test
    @WithMockJwtAuthentication(role = MockRole.STANDARD)
    void shouldAllowCreationWithoutQuestionToWriter() throws Exception {
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

      creationWithoutQuestion().andExpect(status().isOk());
    }
  }

  @Nested
  @DisplayName("POST /internal/tth/dossier/{dossierId}/send-to-bo")
  class SendDossierToBo {

    @Test
    @WithMockJwtAuthentication(role = MockRole.STANDARD)
    void shouldSendToBoAsCantonWriter() throws Exception {
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

      sendToBo().andExpect(status().isOk());

      TthDossier updatedDossier = tthDossierRepository.findById(tthDossier.getId()).orElseThrow();
      assertThat(updatedDossier.getDossierStatus()).isEqualTo(DossierStatus.DOSSIER_BO_CHECK);
    }

    private ResultActions sendToBo() throws Exception {
      return mvc.perform(post("/internal/tth/dossier/" + tthDossier.getId() + "/send-to-bo"));
    }

    @Test
    @WithMockJwtAuthentication(role = MockRole.UNAUTHORIZED)
    void shouldNotSendToBoAsUnauthorized() throws Exception {
      sendToBo().andExpect(status().isForbidden());
    }
  }

  @Nested
  @DisplayName("POST /internal/tth/dossier/answer/{questionId}")
  class AnswerQuestion {

    @Test
    @WithMockJwtAuthentication(role = MockRole.STANDARD, accountType = MockAccountType.GUEST)
    void shouldAnswerQuestionAsBoUser() throws Exception {
      Permission permission = Permission.builder()
          .identifier(WithMockJwtAuthentication.MOCKUSER_SBB_UID)
          .application(ApplicationType.TIMETABLE_HEARING)
          .role(ApplicationRole.READER)
          .build();
      permission.setPermissionRestrictions(Set.of(PermissionRestriction.builder()
          .permission(permission)
          .type(PermissionRestrictionType.TRANSPORT_COMPANY_DOSSIER_ANSWER)
          .restriction("true")
          .build()));
      permissionRepository.saveAndFlush(permission);

      answerQuestionAsBoUser().andExpect(status().isOk());

      TthDossier updatedDossier = tthDossierRepository.findById(tthDossier.getId()).orElseThrow();
      assertThat(updatedDossier.getDossierStatus()).isEqualTo(DossierStatus.DOSSIER_CANTON_CHECK);
    }

    private ResultActions answerQuestionAsBoUser() throws Exception {
      tthDossierRepository.deleteAll();
      TthDossier dossierWithQuestion = TthDossier.builder()
          .topic("TOPIC")
          .statementIds(List.of(1001L))
          .swissCanton(SwissCanton.BERN)
          .dossierStatus(DossierStatus.DOSSIER_BO_CHECK)
          .tthDossierYear(tthDossierYear)
          .boContactSbbuid(WithMockJwtAuthentication.MOCKUSER_SBB_UID)
          .build();
      dossierWithQuestion.setDossierQuestions(List.of(TthDossierQuestion.builder()
          .tthDossier(dossierWithQuestion)
          .question("Könnt ihr das machen?").build()));
      tthDossier = tthDossierRepository.saveAndFlush(dossierWithQuestion);

      BoAnswerModel answer = BoAnswerModel.builder()
          .answerToCanton("We are working on it.")
          .build();
      return mvc.perform(post("/internal/tth/dossier/answer/" + tthDossier.getDossierQuestions().getFirst().getId())
          .contentType(contentType).content(mapper.writeValueAsString(answer)));
    }

    @Test
    @WithMockJwtAuthentication(role = MockRole.UNAUTHORIZED)
    void shouldNotAnswerQuestionAsUnauthorized() throws Exception {
      answerQuestionAsBoUser().andExpect(status().isForbidden());
    }

    @Test
    @WithMockJwtAuthentication(role = MockRole.STANDARD, sbbuid = "ue12345")
    void shouldNotAnswerQuestionAsStandardUserWithoutPermissions() throws Exception {
      answerQuestionAsBoUser().andExpect(status().isForbidden());
    }
  }

  @Nested
  @DisplayName("POST /internal/tth/dossier/{dossierId}/complete/{status}")
  class CompleteDossier {

    @Test
    @WithMockJwtAuthentication(role = MockRole.STANDARD)
    void shouldCompleteDossierAsCantonWriter() throws Exception {
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

      completeDossier().andExpect(status().isOk());

      TthDossier updatedDossier = tthDossierRepository.findById(tthDossier.getId()).orElseThrow();
      assertThat(updatedDossier.getDossierStatus()).isEqualTo(DossierStatus.ACCEPTED);
    }

    private ResultActions completeDossier() throws Exception {
      return mvc.perform(post("/internal/tth/dossier/" + tthDossier.getId() + "/complete/" + DossierStatus.ACCEPTED.name()));
    }

    @Test
    @WithMockJwtAuthentication(role = MockRole.UNAUTHORIZED)
    void shouldNotCompleteDossierAsUnauthorized() throws Exception {
      completeDossier().andExpect(status().isForbidden());
    }
  }

  @Nested
  @DisplayName("PUT /internal/tth/dossier/{dossierId}")
  class UpdateDossier {

    @Test
    @WithMockJwtAuthentication(role = MockRole.STANDARD)
    void shouldUpdateDossierAsCantonWriter() throws Exception {
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

      updateDossier().andExpect(status().isOk());

      TthDossier updatedDossier = tthDossierRepository.findById(tthDossier.getId()).orElseThrow();
      assertThat(updatedDossier.getTopic()).isEqualTo("New Request");
    }

    private ResultActions updateDossier() throws Exception {
      tthDossierRepository.deleteAll();
      TthDossier dossierWithQuestion = TthDossier.builder()
          .topic("TOPIC")
          .statementIds(List.of(1001L))
          .swissCanton(SwissCanton.BERN)
          .dossierStatus(DossierStatus.DOSSIER_CANTON_CHECK)
          .tthDossierYear(tthDossierYear)
          .boContactSbbuid(WithMockJwtAuthentication.MOCKUSER_SBB_UID)
          .build();
      dossierWithQuestion.setDossierQuestions(List.of(TthDossierQuestion.builder()
          .tthDossier(dossierWithQuestion)
          .question("Könnt ihr das machen?").build()));
      tthDossier = tthDossierRepository.saveAndFlush(dossierWithQuestion);

      TthDossierModel dossier = TthDossierMapper.toModel(tthDossier);
      dossier.setTopic("New Request");

      return mvc.perform(put("/internal/tth/dossier/" + tthDossier.getId())
          .contentType(contentType)
          .content(mapper.writeValueAsString(dossier)));
    }

    @Test
    @WithMockJwtAuthentication(role = MockRole.UNAUTHORIZED)
    void shouldNotUpdateDossierAsUnauthorized() throws Exception {
      updateDossier().andExpect(status().isForbidden());
    }
  }
}
