package ch.sbb.workflow.module.sepodi.hearing.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ch.sbb.atlas.api.servicepoint.LocalityMunicipalityModel;
import ch.sbb.atlas.api.servicepoint.ReadServicePointVersionModel;
import ch.sbb.atlas.api.servicepoint.ServicePointGeolocationReadModel;
import ch.sbb.atlas.api.servicepoint.SwissLocation;
import ch.sbb.atlas.kafka.model.SwissCanton;
import ch.sbb.atlas.kafka.model.user.admin.ApplicationRole;
import ch.sbb.atlas.kafka.model.user.admin.ApplicationType;
import ch.sbb.atlas.model.Status;
import ch.sbb.atlas.model.controller.BaseControllerApiTest;
import ch.sbb.atlas.model.controller.WithMockJwtAuthentication;
import ch.sbb.atlas.model.controller.WithMockJwtAuthentication.MockRole;
import ch.sbb.atlas.servicepoint.enumeration.Category;
import ch.sbb.atlas.servicepoint.enumeration.MeanOfTransport;
import ch.sbb.atlas.servicepoint.enumeration.StopPointType;
import ch.sbb.atlas.user.administration.security.entity.Permission;
import ch.sbb.atlas.user.administration.security.repository.PermissionRepository;
import ch.sbb.atlas.workflow.model.WorkflowStatus;
import ch.sbb.workflow.entity.Person;
import ch.sbb.workflow.module.sepodi.hearing.api.StopPointWorkflowApiInternal;
import ch.sbb.workflow.module.sepodi.hearing.enity.Decision;
import ch.sbb.workflow.module.sepodi.hearing.enity.DecisionType;
import ch.sbb.workflow.module.sepodi.hearing.enity.JudgementType;
import ch.sbb.workflow.module.sepodi.hearing.enity.StopPointWorkflow;
import ch.sbb.workflow.module.sepodi.hearing.mail.StopPointWorkflowNotificationService;
import ch.sbb.workflow.module.sepodi.hearing.mapper.StopPointClientPersonMapper;
import ch.sbb.workflow.module.sepodi.hearing.model.sepodi.AddExaminantsModel;
import ch.sbb.workflow.module.sepodi.hearing.model.sepodi.DecisionModel;
import ch.sbb.workflow.module.sepodi.hearing.model.sepodi.EditStopPointWorkflowModel;
import ch.sbb.workflow.module.sepodi.hearing.model.sepodi.OtpRequestModel;
import ch.sbb.workflow.module.sepodi.hearing.model.sepodi.OtpVerificationModel;
import ch.sbb.workflow.module.sepodi.hearing.model.sepodi.OverrideDecisionModel;
import ch.sbb.workflow.module.sepodi.hearing.model.sepodi.StopPointClientPersonModel;
import ch.sbb.workflow.module.sepodi.hearing.model.sepodi.StopPointRejectWorkflowModel;
import ch.sbb.workflow.module.sepodi.hearing.model.sepodi.StopPointRestartWorkflowModel;
import ch.sbb.workflow.module.sepodi.hearing.repository.DecisionRepository;
import ch.sbb.workflow.module.sepodi.hearing.repository.StopPointWorkflowRepository;
import ch.sbb.workflow.module.sepodi.hearing.service.SePoDiClientService;
import ch.sbb.workflow.otp.entity.Otp;
import ch.sbb.workflow.otp.helper.OtpHelper;
import ch.sbb.workflow.otp.repository.OtpRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

class StopPointWorkflowApiInternalControllerTest extends BaseControllerApiTest {

  static final String MAIL_ADDRESS = "marek@hamsik.com";

  @Autowired
  private StopPointWorkflowApiV1Controller controller;

  @Autowired
  private StopPointWorkflowRepository workflowRepository;

  @Autowired
  private DecisionRepository decisionRepository;

  @Autowired
  private OtpRepository otpRepository;

  @Autowired
  private PermissionRepository permissionRepository;

  @MockitoBean
  private SePoDiClientService sePoDiClientService;

  @MockitoBean
  private StopPointWorkflowNotificationService notificationService;

  @AfterEach
  void tearDown() {
    otpRepository.deleteAll();
    decisionRepository.deleteAll();
    workflowRepository.deleteAll();
    permissionRepository.deleteAll();
  }

  @Nested
  @DisplayName("GET /internal/stop-point/workflows/{servicePointVersionId}/examinants")
  class GetExaminants {

    @Test
    void shouldGetExaminants() throws Exception {
      getExaminants()
          .andExpect(status().isOk())
          .andExpect(jsonPath("$", hasSize(2)))
          .andExpect(jsonPath("$[0].mail", is("testuser-atlas@sbb.ch")))
          .andExpect(jsonPath("$[0].firstName", is("atlas")))
          .andExpect(jsonPath("$[0].lastName", is("SKI")))
          .andExpect(jsonPath("$[0].organisation", is("SKI - Systemaufgaben Kundeninformation")))
          .andExpect(jsonPath("$[0].personFunction", is("Fachstelle atlas")));
    }

    private ResultActions getExaminants() throws Exception {
      long servicePointVersionId = 123456L;
      when(sePoDiClientService.getServicePointById(servicePointVersionId)).thenReturn(getUpdateServicePointVersionModel());

      return mvc.perform(get(StopPointWorkflowApiInternal.BASE_PATH + "/" + servicePointVersionId + "/examinants"));
    }

    @Test
    @WithMockJwtAuthentication(role = MockRole.UNAUTHORIZED)
    void shouldGetExaminantsAsUnauthorizedRedacted() throws Exception {
      getExaminants()
          .andExpect(status().isOk())
          .andExpect(jsonPath("$", hasSize(2)))
          .andExpect(jsonPath("$[0].mail", is("*****")))
          .andExpect(jsonPath("$[0].firstName", is("*****")))
          .andExpect(jsonPath("$[0].lastName", is("*****")))
          .andExpect(jsonPath("$[0].organisation", is("SKI - Systemaufgaben Kundeninformation")))
          .andExpect(jsonPath("$[0].personFunction", is("Fachstelle atlas")));
    }

    @Test
    @WithMockJwtAuthentication(role = MockRole.STANDARD)
    void shouldGetExaminantsAsStandardUser() throws Exception {
      getExaminants()
          .andExpect(status().isOk())
          .andExpect(jsonPath("$", hasSize(2)))
          .andExpect(jsonPath("$[0].mail", is("testuser-atlas@sbb.ch")))
          .andExpect(jsonPath("$[0].firstName", is("atlas")))
          .andExpect(jsonPath("$[0].lastName", is("SKI")))
          .andExpect(jsonPath("$[0].organisation", is("SKI - Systemaufgaben Kundeninformation")))
          .andExpect(jsonPath("$[0].personFunction", is("Fachstelle atlas")));
    }
  }

  @Nested
  @DisplayName("POST /internal/stop-point/workflows/start/{id}")
  class StartStopPointWorkflow {

    @Test
    void shouldStartWorkflow() throws Exception {
      startWorkflow()
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.status", is("HEARING")));
      verify(notificationService).sendStartStopPointWorkflowMail(any(StopPointWorkflow.class));
    }

    private ResultActions startWorkflow() throws Exception {
      //when
      Person person = Person.builder()
          .firstName("Marek")
          .lastName("Hamsik")
          .function("Centrocampista")
          .mail(MAIL_ADDRESS).build();

      Long versionId = 123456L;
      StopPointWorkflow stopPointWorkflow = StopPointWorkflow.builder()
          .sloid("ch:1:sloid:1234")
          .sboid("ch:1:sboid:666")
          .designationOfficial("Biel/Bienne Bözingenfeld/Champ")
          .localityName("Biel/Bienne")
          .ccEmails(List.of(MAIL_ADDRESS))
          .workflowComment("WF comment")
          .status(WorkflowStatus.ADDED)
          .examinants(Set.of(person))
          .startDate(LocalDate.of(2000, 1, 1))
          .endDate(LocalDate.of(2000, 12, 31))
          .versionId(versionId)
          .build();
      workflowRepository.save(stopPointWorkflow);

      //given
      return mvc.perform(post(StopPointWorkflowApiInternal.BASE_PATH + "/start/" + stopPointWorkflow.getId())
          .contentType(contentType));
    }

    @Test
    @WithMockJwtAuthentication(role = MockRole.STANDARD)
    void shouldStartWorkflowAsSupervisor() throws Exception {
      // given sepodi supervisor
      setUserPermissionToSupervisor();

      startWorkflow().andExpect(status().isOk());
    }

    @Test
    @WithMockJwtAuthentication(role = MockRole.STANDARD)
    void shouldNotStartWorkflowAsStandardUser() throws Exception {
      startWorkflow().andExpect(status().isForbidden());
    }

    @Test
    @WithMockJwtAuthentication(role = MockRole.UNAUTHORIZED)
    void shouldNotStartWorkflowAsUnauthorized() throws Exception {
      startWorkflow().andExpect(status().isForbidden());
    }
  }

  @Nested
  @DisplayName("POST /internal/stop-point/workflows/edit/{id}")
  class EditStopPointWorkflow {

    private final Long versionId = 123456L;
    private EditStopPointWorkflowModel editStopPointWorkflowModel;

    @Test
    void shouldEditWorkflow() throws Exception {
      editWorkflow()
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.status", is("ADDED")));
      List<StopPointWorkflow> workflows =
          workflowRepository.findAll().stream().filter(spw -> spw.getVersionId().equals(versionId))
              .sorted(Comparator.comparing(StopPointWorkflow::getId)).toList();
      assertThat(workflows).hasSize(1);
      assertThat(workflows.getFirst().getStatus()).isEqualTo(WorkflowStatus.ADDED);
      assertThat(workflows.getFirst().getWorkflowComment()).isEqualTo(editStopPointWorkflowModel.getWorkflowComment());
    }

    private ResultActions editWorkflow() throws Exception {
      //when
      Person person = Person.builder()
          .firstName("Marek")
          .lastName("Hamsik")
          .function("Centrocampista")
          .mail(MAIL_ADDRESS).build();

      StopPointWorkflow stopPointWorkflow = StopPointWorkflow.builder()
          .sloid("ch:1:sloid:1234")
          .sboid("ch:1:sboid:666")
          .designationOfficial("Biel/Bienne Bözingenfeld/Champ")
          .localityName("Biel/Bienne")
          .ccEmails(List.of(MAIL_ADDRESS))
          .workflowComment("WF comment")
          .status(WorkflowStatus.ADDED)
          .examinants(Set.of(person))
          .startDate(LocalDate.of(2000, 1, 1))
          .endDate(LocalDate.of(2000, 12, 31))
          .versionId(versionId)
          .build();
      person.setStopPointWorkflow(stopPointWorkflow);
      workflowRepository.save(stopPointWorkflow);

      List<Person> examinant = new ArrayList<>(stopPointWorkflow.getExaminants());

      editStopPointWorkflowModel = EditStopPointWorkflowModel.builder()
          .workflowComment("New Comment")
          .designationOfficial("Bern")
          .examinants(examinant.stream().map(StopPointClientPersonMapper::toModel).toList())
          .build();

      //given
      return mvc.perform(post(StopPointWorkflowApiInternal.BASE_PATH + "/edit/" + stopPointWorkflow.getId())
          .contentType(contentType)
          .content(mapper.writeValueAsString(editStopPointWorkflowModel)));
    }

    @Test
    @WithMockJwtAuthentication(role = MockRole.UNAUTHORIZED)
    void shouldNotEditWorkflowAsUnauthorized() throws Exception {
      editWorkflow().andExpect(status().isForbidden());
    }

    @Test
    @WithMockJwtAuthentication(role = MockRole.STANDARD)
    void shouldNotEditWorkflowAsStandardUserWithoutPermissions() throws Exception {
      editWorkflow().andExpect(status().isForbidden());
    }

    @Test
    @WithMockJwtAuthentication(role = MockRole.STANDARD)
    void shouldEditWorkflowAsSupervisor() throws Exception {
      setUserPermissionToSupervisor();
      editWorkflow().andExpect(status().isOk());
    }

  }

  @Nested
  @DisplayName("POST /internal/stop-point/workflows/add-examinants/{id}")
  class AddExaminantsToStopPointWorkflow {

    private Long versionId;

    @Test
    void shouldAddExaminantsToWorkflowInHearing() throws Exception {
      addExaminantsToWorkflowInHearing()
          .andExpect(status().isOk());

      StopPointWorkflow workflow =
          workflowRepository.findAll().stream().filter(spw -> spw.getVersionId().equals(versionId))
              .sorted(Comparator.comparing(StopPointWorkflow::getId)).toList().getFirst();
      assertThat(workflow.getExaminants()).hasSize(2);
      assertThat(workflow.getCcEmails()).hasSize(2);

      verify(notificationService).sendStartToAddedExaminant(any(StopPointWorkflow.class), eq(List.of("someguy@sbb.ch")));
    }

    private ResultActions addExaminantsToWorkflowInHearing() throws Exception {
      //when
      Person person = Person.builder()
          .firstName("Marek")
          .lastName("Hamsik")
          .function("Centrocampista")
          .mail(MAIL_ADDRESS).build();

      versionId = 123456L;
      StopPointWorkflow stopPointWorkflow = StopPointWorkflow.builder()
          .sloid("ch:1:sloid:1234")
          .sboid("ch:1:sboid:666")
          .designationOfficial("Biel/Bienne Bözingenfeld/Champ")
          .localityName("Biel/Bienne")
          .ccEmails(List.of(MAIL_ADDRESS))
          .workflowComment("WF comment")
          .status(WorkflowStatus.HEARING)
          .examinants(Set.of(person))
          .startDate(LocalDate.of(2000, 1, 1))
          .endDate(LocalDate.of(2000, 12, 31))
          .versionId(versionId)
          .build();
      person.setStopPointWorkflow(stopPointWorkflow);
      workflowRepository.save(stopPointWorkflow);

      AddExaminantsModel addExaminantsModel = AddExaminantsModel.builder()
          .examinants(List.of(StopPointClientPersonModel.builder().organisation("Sample").mail("someguy@sbb.ch").build()))
          .ccEmails(List.of("additionalDude@bern.be"))
          .build();

      //given
      return mvc.perform(post(StopPointWorkflowApiInternal.BASE_PATH + "/add-examinants/" + stopPointWorkflow.getId())
          .contentType(contentType)
          .content(mapper.writeValueAsString(addExaminantsModel)));
    }

    @Test
    @WithMockJwtAuthentication(role = MockRole.UNAUTHORIZED)
    void shouldNotAddExaminantsAsUnauthorized() throws Exception {
      addExaminantsToWorkflowInHearing().andExpect(status().isForbidden());
    }

    @Test
    @WithMockJwtAuthentication(role = MockRole.STANDARD)
    void shouldNotAddExaminantsAsStandardUserWithoutPermissions() throws Exception {
      addExaminantsToWorkflowInHearing().andExpect(status().isForbidden());
    }

    @Test
    @WithMockJwtAuthentication(role = MockRole.STANDARD)
    void shouldAddExaminantsAsSupervisor() throws Exception {
      setUserPermissionToSupervisor();
      addExaminantsToWorkflowInHearing().andExpect(status().isOk());
    }
  }

  @Nested
  @DisplayName("POST /internal/stop-point/workflows/reject/{id}")
  class RejectStopPointWorkflow {

    private Long versionId;
    private StopPointWorkflow stopPointWorkflow;
    private StopPointRejectWorkflowModel stopPointRejectWorkflowModel;

    @Test
    void shouldRejectWorkflow() throws Exception {
      rejectWorkflow()
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.status", is("REJECTED")));

      List<StopPointWorkflow> workflows =
          workflowRepository.findAll().stream().filter(spw -> spw.getVersionId().equals(versionId))
              .sorted(Comparator.comparing(StopPointWorkflow::getId)).toList();
      assertThat(workflows).hasSize(1);
      assertThat(workflows.getFirst().getStatus()).isEqualTo(WorkflowStatus.REJECTED);

      Decision decisionResult = decisionRepository.findAll().stream()
          .filter(decision -> decision.getExaminant().getStopPointWorkflow().getId().equals(stopPointWorkflow.getId()))
          .findFirst()
          .orElse(null);
      assertThat(decisionResult).isNotNull();
      Person examinant = decisionResult.getExaminant();
      assertThat(examinant.getMail()).isEqualTo(MAIL_ADDRESS);
      assertThat(decisionResult.getMotivation()).isEqualTo(stopPointRejectWorkflowModel.getMotivationComment());
      assertThat(decisionResult.getDecisionType()).isEqualTo(DecisionType.REJECTED);

      verify(notificationService).sendRejectStopPointWorkflowMail(any(StopPointWorkflow.class), anyString());
    }

    private ResultActions rejectWorkflow() throws Exception {
      //when
      Person person = Person.builder()
          .firstName("Marek")
          .lastName("Hamsik")
          .function("Centrocampista")
          .mail(MAIL_ADDRESS).build();

      versionId = 123456L;
      stopPointWorkflow = StopPointWorkflow.builder()
          .sloid("ch:1:sloid:1234")
          .sboid("ch:1:sboid:666")
          .designationOfficial("Biel/Bienne Bözingenfeld/Champ")
          .localityName("Biel/Bienne")
          .ccEmails(List.of(MAIL_ADDRESS))
          .workflowComment("WF comment")
          .status(WorkflowStatus.ADDED)
          .examinants(Set.of(person))
          .startDate(LocalDate.of(2000, 1, 1))
          .endDate(LocalDate.of(2000, 12, 31))
          .versionId(versionId)
          .build();
      stopPointWorkflow = workflowRepository.save(stopPointWorkflow);

      stopPointRejectWorkflowModel = StopPointRejectWorkflowModel.builder()
          .motivationComment("No Comment")
          .firstName("Marek")
          .lastName("Hamsik")
          .organisation("YB")
          .mail(MAIL_ADDRESS)
          .build();

      //given
      return mvc.perform(post(StopPointWorkflowApiInternal.BASE_PATH + "/reject/" + stopPointWorkflow.getId())
          .contentType(contentType)
          .content(mapper.writeValueAsString(stopPointRejectWorkflowModel)));
    }

    @Test
    @WithMockJwtAuthentication(role = MockRole.UNAUTHORIZED)
    void shouldNotRejectWorkflowAsUnauthorized() throws Exception {
      rejectWorkflow().andExpect(status().isForbidden());
    }

    @Test
    @WithMockJwtAuthentication(role = MockRole.UNAUTHORIZED)
    void shouldNotRejectWorkflowAsUserWithoutPermissions() throws Exception {
      rejectWorkflow().andExpect(status().isForbidden());
    }

    @Test
    @WithMockJwtAuthentication(role = MockRole.STANDARD)
    void shouldRejectWorkflowAsSupervisor() throws Exception {
      setUserPermissionToSupervisor();
      rejectWorkflow().andExpect(status().isOk());
    }
  }

  @Nested
  @DisplayName("POST /internal/stop-point/workflows/obtain-otp/{id}")
  class ObtainOtp {

    private Person person;

    @Test
    void shouldGetOtpWorkflow() throws Exception {
      getOtpWorkflow().andExpect(status().isAccepted());

      Otp otpResult = otpRepository.findAll().stream().filter(otp -> otp.getPerson().getId().equals(person.getId())).findFirst()
          .orElse(null);

      assertThat(otpResult).isNotNull();
      assertThat(otpResult.getPerson().getId()).isEqualTo(person.getId());
    }

    private ResultActions getOtpWorkflow() throws Exception {
      //when
      person = Person.builder()
          .firstName("Marek")
          .lastName("Hamsik")
          .function("Centrocampista")
          .mail(MAIL_ADDRESS).build();

      Long versionId = 123456L;
      StopPointWorkflow stopPointWorkflow = StopPointWorkflow.builder()
          .sloid("ch:1:sloid:1234")
          .sboid("ch:1:sboid:666")
          .designationOfficial("Biel/Bienne Bözingenfeld/Champ")
          .localityName("Biel/Bienne")
          .ccEmails(List.of(MAIL_ADDRESS))
          .workflowComment("WF comment")
          .status(WorkflowStatus.HEARING)
          .examinants(Set.of(person))
          .startDate(LocalDate.of(2000, 1, 1))
          .endDate(LocalDate.of(2000, 12, 31))
          .versionId(versionId)
          .build();
      StopPointWorkflow workflow = workflowRepository.save(stopPointWorkflow);
      person.setStopPointWorkflow(workflow);
      workflowRepository.save(workflow);

      OtpRequestModel otpRequest = OtpRequestModel.builder().examinantMail(MAIL_ADDRESS).build();

      //given
      return mvc.perform(post(StopPointWorkflowApiInternal.BASE_PATH + "/obtain-otp/" + stopPointWorkflow.getId())
          .contentType(contentType)
          .content(mapper.writeValueAsString(otpRequest)));
    }

    @Test
    @WithMockJwtAuthentication(role = MockRole.UNAUTHORIZED)
    void shouldGetOtpWorkflowAsUnauthorized() throws Exception {
      getOtpWorkflow().andExpect(status().isAccepted());

      Otp otpResult = otpRepository.findAll().stream().filter(otp -> otp.getPerson().getId().equals(person.getId())).findFirst()
          .orElse(null);

      assertThat(otpResult).isNotNull();
      assertThat(otpResult.getPerson().getId()).isEqualTo(person.getId());
    }
  }

  @Nested
  @DisplayName("POST /internal/stop-point/workflows/verify-otp/{id}")
  class VerifyOtp {

    @Test
    void shouldVerifyOtpWorkflow() throws Exception {
      verifyOtp()
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.mail", is(MAIL_ADDRESS)))
          .andExpect(jsonPath("$.firstName", is("Marek")))
          .andExpect(jsonPath("$.lastName", is("Hamsik")));
    }

    private ResultActions verifyOtp() throws Exception {
      //when
      Person person = Person.builder()
          .firstName("Marek")
          .lastName("Hamsik")
          .function("Centrocampista")
          .mail(MAIL_ADDRESS).build();

      Long versionId = 123456L;
      StopPointWorkflow stopPointWorkflow = StopPointWorkflow.builder()
          .sloid("ch:1:sloid:1234")
          .sboid("ch:1:sboid:666")
          .designationOfficial("Biel/Bienne Bözingenfeld/Champ")
          .localityName("Biel/Bienne")
          .ccEmails(List.of(MAIL_ADDRESS))
          .workflowComment("WF comment")
          .status(WorkflowStatus.HEARING)
          .examinants(Set.of(person))
          .startDate(LocalDate.of(2000, 1, 1))
          .endDate(LocalDate.of(2000, 12, 31))
          .versionId(versionId)
          .build();
      person.setStopPointWorkflow(stopPointWorkflow);
      workflowRepository.save(stopPointWorkflow);

      String pinCode = "123456";
      otpRepository.save(Otp.builder()
          .person(person)
          .code(OtpHelper.hashPinCode(pinCode))
          .creationTime(LocalDateTime.now())
          .build());

      OtpVerificationModel otpRequest = OtpVerificationModel.builder().pinCode(pinCode).examinantMail(MAIL_ADDRESS).build();

      //given
      return mvc.perform(post(StopPointWorkflowApiInternal.BASE_PATH + "/verify-otp/" + stopPointWorkflow.getId())
          .contentType(contentType)
          .content(mapper.writeValueAsString(otpRequest)));
    }

    @Test
    @WithMockJwtAuthentication(role = MockRole.UNAUTHORIZED)
    void shouldVerifyOtpWorkflowAsUnauthorized() throws Exception {
      // Should return data as it has been verified by pin
      verifyOtp().andExpect(status().isOk())
          .andExpect(jsonPath("$.mail", is(MAIL_ADDRESS)))
          .andExpect(jsonPath("$.firstName", is("Marek")))
          .andExpect(jsonPath("$.lastName", is("Hamsik")));
    }

    @Test
    void shouldNotVerifyPinCodeWhenNoOtpPresent() throws Exception {
      //given
      Person person = Person.builder()
          .firstName("Marek")
          .lastName("Hamsik")
          .function("Centrocampista")
          .mail(MAIL_ADDRESS).build();

      Long versionId = 123456L;
      StopPointWorkflow stopPointWorkflow = StopPointWorkflow.builder()
          .sloid("ch:1:sloid:1234")
          .sboid("ch:1:sboid:666")
          .designationOfficial("Biel/Bienne Bözingenfeld/Champ")
          .localityName("Biel/Bienne")
          .ccEmails(List.of(MAIL_ADDRESS))
          .workflowComment("WF comment")
          .status(WorkflowStatus.HEARING)
          .examinants(Set.of(person))
          .startDate(LocalDate.of(2000, 1, 1))
          .endDate(LocalDate.of(2000, 12, 31))
          .versionId(versionId)
          .build();
      person.setStopPointWorkflow(stopPointWorkflow);
      workflowRepository.save(stopPointWorkflow);

      String pinCode = "123456";

      OtpVerificationModel otpRequest = OtpVerificationModel.builder().pinCode(pinCode).examinantMail(MAIL_ADDRESS).build();

      //when & then
      mvc.perform(post(StopPointWorkflowApiInternal.BASE_PATH + "/verify-otp/" + stopPointWorkflow.getId())
              .contentType(contentType)
              .content(mapper.writeValueAsString(otpRequest)))
          .andExpect(status().isForbidden());
    }
  }

  @Nested
  @DisplayName("GET /internal/stop-point/workflows/decisions/{personId}")
  @Transactional
  class GetDecision {

    @Test
    void shouldGetDecisionByPerson() throws Exception {
      getDecisionByPerson()
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.judgement", is("YES")))
          .andExpect(jsonPath("$.motivation", is("I agree")))
          .andExpect(jsonPath("$.examinant.mail", is(MAIL_ADDRESS)))
          .andExpect(jsonPath("$.examinant.firstName", is("Marek")))
          .andExpect(jsonPath("$.examinant.lastName", is("Hamsik")));
    }

    private ResultActions getDecisionByPerson() throws Exception {
      //when
      Person person = Person.builder()
          .firstName("Marek")
          .lastName("Hamsik")
          .function("Centrocampista")
          .mail(MAIL_ADDRESS).build();

      Long versionId = 123456L;
      StopPointWorkflow stopPointWorkflow = StopPointWorkflow.builder()
          .sloid("ch:1:sloid:1234")
          .sboid("ch:1:sboid:666")
          .designationOfficial("Biel/Bienne Bözingenfeld/Champ")
          .localityName("Biel/Bienne")
          .ccEmails(List.of(MAIL_ADDRESS))
          .workflowComment("WF comment")
          .status(WorkflowStatus.HEARING)
          .examinants(Set.of(person))
          .startDate(LocalDate.of(2000, 1, 1))
          .endDate(LocalDate.of(2000, 12, 31))
          .versionId(versionId)
          .build();
      person.setStopPointWorkflow(stopPointWorkflow);
      stopPointWorkflow = workflowRepository.saveAndFlush(stopPointWorkflow);
      person = workflowRepository.findById(stopPointWorkflow.getId()).orElseThrow().getExaminants().iterator().next();

      Decision decision = Decision.builder()
          .examinant(person)
          .judgement(JudgementType.YES)
          .motivation("I agree")
          .decisionType(DecisionType.VOTED)
          .build();
      decisionRepository.saveAndFlush(decision);

      //given
      return mvc.perform(get(StopPointWorkflowApiInternal.BASE_PATH + "/decisions/" + person.getId()));
    }

    @Test
    @WithMockJwtAuthentication(role = MockRole.UNAUTHORIZED)
    void shouldGetDecisionAsUnauthorizedRedacted() throws Exception {
      getDecisionByPerson()
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.judgement", is("YES")))
          .andExpect(jsonPath("$.motivation", is("I agree")))
          .andExpect(jsonPath("$.examinant.mail", is("m*****")))
          .andExpect(jsonPath("$.examinant.firstName", is("M*****")))
          .andExpect(jsonPath("$.examinant.lastName", is("H*****")));
    }
  }

  @Nested
  @DisplayName("GET /internal/stop-point/workflows/vote/{id}/{personId}")
  class VoteWorkflow {

    private final Long versionId = 123456L;
    private DecisionModel decisionModel;
    private Person person;

    @Test
    void shouldVoteForWorkflow() throws Exception {
      voteForWorkflow()
          .andExpect(status().isOk());

      List<StopPointWorkflow> workflows =
          workflowRepository.findAll().stream().filter(spw -> spw.getVersionId().equals(versionId))
              .sorted(Comparator.comparing(StopPointWorkflow::getId)).toList();
      assertThat(workflows).hasSize(1);
      assertThat(workflows.getFirst().getExaminants()).hasSize(1);
      Decision decisionByExaminantId = decisionRepository.findDecisionByExaminantId(person.getId());
      assertThat(decisionByExaminantId).isNotNull();
      assertThat(decisionByExaminantId.getMotivation()).isEqualTo(decisionModel.getMotivation());
      assertThat(decisionByExaminantId.getJudgement()).isEqualTo(decisionModel.getJudgement());
      assertThat(decisionByExaminantId.getDecisionType()).isEqualTo(DecisionType.VOTED);
    }

    private ResultActions voteForWorkflow() throws Exception {
      //when
      person = Person.builder()
          .firstName("Marek")
          .lastName("Hamsik")
          .function("Centrocampista")
          .mail(MAIL_ADDRESS).build();

      StopPointWorkflow stopPointWorkflow = StopPointWorkflow.builder()
          .sloid("ch:1:sloid:1234")
          .sboid("ch:1:sboid:666")
          .designationOfficial("Biel/Bienne Bözingenfeld/Champ")
          .localityName("Biel/Bienne")
          .ccEmails(List.of(MAIL_ADDRESS))
          .workflowComment("WF comment")
          .status(WorkflowStatus.HEARING)
          .examinants(Set.of(person))
          .startDate(LocalDate.of(2000, 1, 1))
          .endDate(LocalDate.of(2000, 12, 31))
          .versionId(versionId)
          .build();
      StopPointWorkflow workflow = workflowRepository.saveAndFlush(stopPointWorkflow);
      person.setStopPointWorkflow(workflow);
      workflowRepository.saveAndFlush(workflow);

      Otp otp = Otp.builder().code(OtpHelper.hashPinCode("12345")).person(person).creationTime(LocalDateTime.now()).build();
      otpRepository.saveAndFlush(otp);
      decisionModel = DecisionModel.builder()
          .judgement(JudgementType.NO)
          .motivation("Perfetto")
          .pinCode("12345")
          .examinantMail(MAIL_ADDRESS)
          .firstName("Marek")
          .lastName("Hamsik")
          .personFunction("Centrocampista")
          .organisation("Napoli")
          .build();

      //given
      return mvc.perform(
          post(StopPointWorkflowApiInternal.BASE_PATH + "/vote/" + stopPointWorkflow.getId() + "/" + person.getId())
              .contentType(contentType)
              .content(mapper.writeValueAsString(decisionModel)));
    }

    @Test
    @WithMockJwtAuthentication(role = MockRole.UNAUTHORIZED)
    void shouldVoteForWorkflowAsUnauthorized() throws Exception {
      voteForWorkflow().andExpect(status().isOk());
    }
  }

  @Nested
  @DisplayName("GET /internal/stop-point/workflows/override-vote/{id}/{personId}")
  class OverrideVoteWorkflow {

    private final Long versionId = 123456L;
    private Person person;
    private OverrideDecisionModel overrideDecisionModel;

    @Test
    void shouldOverrideVoteWithoutDecisionToWorkflow() throws Exception {
      overrideVoteWithoutDecisionToWorkflow()
          .andExpect(status().isOk());

      List<StopPointWorkflow> workflows =
          workflowRepository.findAll().stream().filter(spw -> spw.getVersionId().equals(versionId))
              .sorted(Comparator.comparing(StopPointWorkflow::getId)).toList();
      assertThat(workflows).hasSize(1);
      Set<Person> examinants = workflows.getFirst().getExaminants();
      assertThat(examinants).hasSize(1);
      Decision decisionByExaminantId = decisionRepository.findDecisionByExaminantId(person.getId());
      assertThat(decisionByExaminantId).isNotNull();
      assertThat(decisionByExaminantId.getFotMotivation()).isEqualTo(overrideDecisionModel.getFotMotivation());
      assertThat(decisionByExaminantId.getFotJudgement()).isEqualTo(overrideDecisionModel.getFotJudgement());
    }

    private ResultActions overrideVoteWithoutDecisionToWorkflow() throws Exception {
      //when
      person = Person.builder()
          .firstName("Marek")
          .lastName("Hamsik")
          .function("Centrocampista")
          .mail(MAIL_ADDRESS).build();

      StopPointWorkflow stopPointWorkflow = StopPointWorkflow.builder()
          .sloid("ch:1:sloid:1234")
          .sboid("ch:1:sboid:666")
          .designationOfficial("Biel/Bienne Bözingenfeld/Champ")
          .localityName("Biel/Bienne")
          .ccEmails(List.of(MAIL_ADDRESS))
          .workflowComment("WF comment")
          .status(WorkflowStatus.HEARING)
          .examinants(Set.of(person))
          .startDate(LocalDate.of(2000, 1, 1))
          .endDate(LocalDate.of(2000, 12, 31))
          .versionId(versionId)
          .build();
      StopPointWorkflow workflow = workflowRepository.saveAndFlush(stopPointWorkflow);
      person.setStopPointWorkflow(workflow);
      workflowRepository.saveAndFlush(workflow);

      overrideDecisionModel = OverrideDecisionModel.builder()
          .firstName("Firtsname")
          .lastName("Fix")
          .fotJudgement(JudgementType.NO)
          .fotMotivation("Ja save")
          .build();

      //given
      return mvc.perform(
          post(StopPointWorkflowApiInternal.BASE_PATH + "/override-vote/" + stopPointWorkflow.getId() + "/" + person.getId())
              .contentType(contentType)
              .content(mapper.writeValueAsString(overrideDecisionModel)));
    }

    @Test
    @WithMockJwtAuthentication(role = MockRole.UNAUTHORIZED)
    void shouldNotOverrideVoteAsUnauthorized() throws Exception {
      overrideVoteWithoutDecisionToWorkflow().andExpect(status().isForbidden());
    }

    @Test
    @WithMockJwtAuthentication(role = MockRole.STANDARD)
    void shouldNotOverrideVoteAsStandardUserWithoutPermissions() throws Exception {
      overrideVoteWithoutDecisionToWorkflow().andExpect(status().isForbidden());
    }

    @Test
    @WithMockJwtAuthentication(role = MockRole.STANDARD)
    void shouldOverrideVoteAsSupervisor() throws Exception {
      setUserPermissionToSupervisor();
      overrideVoteWithoutDecisionToWorkflow().andExpect(status().isOk());
    }

    @Test
    void shouldOverrideVoteWithDecisionToWorkflow() throws Exception {
      //when
      Person person = Person.builder()
          .firstName("Marek")
          .lastName("Hamsik")
          .function("Centrocampista")
          .mail(MAIL_ADDRESS).build();

      Long versionId = 123456L;
      StopPointWorkflow stopPointWorkflow = StopPointWorkflow.builder()
          .sloid("ch:1:sloid:1234")
          .sboid("ch:1:sboid:666")
          .designationOfficial("Biel/Bienne Bözingenfeld/Champ")
          .localityName("Biel/Bienne")
          .ccEmails(List.of(MAIL_ADDRESS))
          .workflowComment("WF comment")
          .status(WorkflowStatus.HEARING)
          .examinants(Set.of(person))
          .startDate(LocalDate.of(2000, 1, 1))
          .endDate(LocalDate.of(2000, 12, 31))
          .versionId(versionId)
          .build();
      StopPointWorkflow workflow = workflowRepository.save(stopPointWorkflow);
      person.setStopPointWorkflow(workflow);
      workflowRepository.save(workflow);

      Otp otp = Otp.builder().code("12345").person(person).creationTime(LocalDateTime.now()).build();
      otpRepository.save(otp);
      Decision decision = Decision.builder()
          .judgement(JudgementType.YES)
          .motivation("Perfetto")
          .motivationDate(LocalDateTime.now())
          .build();
      decisionRepository.save(decision);
      decision.setExaminant(person);
      decisionRepository.save(decision);
      OverrideDecisionModel overrideDecisionModel = OverrideDecisionModel.builder()
          .firstName("Firtsname")
          .lastName("Fix")
          .fotJudgement(JudgementType.NO)
          .fotMotivation("Ja save")
          .build();

      //given
      mvc.perform(
              post(StopPointWorkflowApiInternal.BASE_PATH + "/override-vote/" + stopPointWorkflow.getId() + "/" + person.getId())
                  .contentType(contentType)
                  .content(mapper.writeValueAsString(overrideDecisionModel)))
          .andExpect(status().isOk());

      List<StopPointWorkflow> workflows =
          workflowRepository.findAll().stream().filter(spw -> spw.getVersionId().equals(versionId))
              .sorted(Comparator.comparing(StopPointWorkflow::getId)).toList();
      assertThat(workflows).hasSize(1);
      assertThat(workflows.getFirst().getExaminants()).hasSize(1);
      Decision decisionByExaminantId = decisionRepository.findDecisionByExaminantId(person.getId());
      assertThat(decisionByExaminantId).isNotNull();
      assertThat(decisionByExaminantId.getMotivation()).isEqualTo(decision.getMotivation());
      assertThat(decisionByExaminantId.getJudgement()).isEqualTo(decision.getJudgement());
      assertThat(decisionByExaminantId.getFotMotivation()).isEqualTo(overrideDecisionModel.getFotMotivation());
      assertThat(decisionByExaminantId.getFotJudgement()).isEqualTo(overrideDecisionModel.getFotJudgement());
    }
  }

  @Nested
  @DisplayName("GET /internal/stop-point/workflows/restart/{id}")
  class RestartStopPointWorkflow {

    private final Long versionId = 123456L;

    @Test
    void shouldRestartWorkflow() throws Exception {
      restartWorkflow()
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.status", is("HEARING")));
      List<StopPointWorkflow> workflows =
          workflowRepository.findAll().stream().filter(spw -> spw.getVersionId().equals(versionId))
              .sorted(Comparator.comparing(StopPointWorkflow::getId)).toList();
      assertThat(workflows).hasSize(2);
      assertThat(workflows.getFirst().getStatus()).isEqualTo(WorkflowStatus.REJECTED);
      assertThat(workflows.getFirst().getFollowUpWorkflow()).isNotNull();
      assertThat(workflows.getFirst().getExaminants()).hasSize(2);

      assertThat(workflows.get(1).getStatus()).isEqualTo(WorkflowStatus.HEARING);
      assertThat(workflows.get(1).getApplicantMail()).isEqualTo(workflows.getFirst().getApplicantMail());
      assertThat(workflows.get(1).getCreator()).isEqualTo(workflows.getFirst().getCreator());
      assertThat(workflows.get(1).getExaminants()).hasSize(1);

      verify(notificationService).sendRestartStopPointWorkflowMail(any(StopPointWorkflow.class), any(StopPointWorkflow.class));
      verify(sePoDiClientService).updateDesignationOfficialServicePointAsAdmin(any(StopPointWorkflow.class));
    }

    private ResultActions restartWorkflow() throws Exception {
      //when
      Person person = Person.builder()
          .firstName("Marek")
          .lastName("Hamsik")
          .function("Centrocampista")
          .mail(MAIL_ADDRESS).build();

      StopPointWorkflow stopPointWorkflow = StopPointWorkflow.builder()
          .sloid("ch:1:sloid:1234")
          .sboid("ch:1:sboid:666")
          .designationOfficial("Biel/Bienne Bözingenfeld/Champ")
          .localityName("Biel/Bienne")
          .ccEmails(List.of(MAIL_ADDRESS))
          .workflowComment("WF comment")
          .status(WorkflowStatus.HEARING)
          .startDate(LocalDate.of(2000, 1, 1))
          .endDate(LocalDate.of(2000, 12, 31))
          .versionId(versionId)
          .build();
      stopPointWorkflow.setExaminants(Set.of(person));
      workflowRepository.saveAndFlush(stopPointWorkflow);

      StopPointRestartWorkflowModel restartWorkflowModel = StopPointRestartWorkflowModel.builder()
          .designationOfficial("Bern")
          .firstName("marek")
          .lastName("hamsik")
          .motivationComment("Bern is better")
          .mail("chef@chef.ch")
          .organisation("sbb")
          .build();

      //given
      return mvc.perform(post(StopPointWorkflowApiInternal.BASE_PATH + "/restart/" + stopPointWorkflow.getId())
          .contentType(contentType)
          .content(mapper.writeValueAsString(restartWorkflowModel)));
    }

    @Test
    @WithMockJwtAuthentication(role = MockRole.UNAUTHORIZED)
    void shouldNotRestartWorkflowAsUnauthorized() throws Exception {
      restartWorkflow().andExpect(status().isForbidden());
    }

    @Test
    @WithMockJwtAuthentication(role = MockRole.STANDARD)
    void shouldNotRestartWorkflowAsStandardUserWithoutPermissions() throws Exception {
      restartWorkflow().andExpect(status().isForbidden());
    }

    @Test
    @WithMockJwtAuthentication(role = MockRole.STANDARD)
    void shouldRestartWorkflowAsSupervisor() throws Exception {
      setUserPermissionToSupervisor();
      restartWorkflow().andExpect(status().isOk());
    }
  }

  @Nested
  @DisplayName("GET /internal/stop-point/workflows/cancel/{id}")
  class CancelStopPointWorkflow {

    private final Long versionId = 123456L;
    private StopPointWorkflow stopPointWorkflow;
    private StopPointRejectWorkflowModel stopPointCancelWorkflowModel;

    @Test
    void shouldCancelWorkflow() throws Exception {
      cancelWorkflow()
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.status", is("CANCELED")));

      List<StopPointWorkflow> workflows =
          workflowRepository.findAll().stream().filter(spw -> spw.getVersionId().equals(versionId))
              .sorted(Comparator.comparing(StopPointWorkflow::getId)).toList();
      assertThat(workflows).hasSize(1);
      assertThat(workflows.getFirst().getStatus()).isEqualTo(WorkflowStatus.CANCELED);

      Decision decisionResult = decisionRepository.findAll().stream()
          .filter(decision -> decision.getExaminant().getStopPointWorkflow().getId().equals(stopPointWorkflow.getId()))
          .findFirst()
          .orElse(null);
      assertThat(decisionResult).isNotNull();
      Person examinant = decisionResult.getExaminant();
      assertThat(examinant.getMail()).isEqualTo(MAIL_ADDRESS);
      assertThat(decisionResult.getMotivation()).isEqualTo(stopPointCancelWorkflowModel.getMotivationComment());
      assertThat(decisionResult.getDecisionType()).isEqualTo(DecisionType.CANCELED);
      stopPointWorkflow.setStatus(WorkflowStatus.CANCELED);
      verify(sePoDiClientService).updateStopPointStatusToDraftAsAdmin(any(StopPointWorkflow.class));
    }

    private ResultActions cancelWorkflow() throws Exception {
      //when
      Person person = Person.builder()
          .firstName("Marek")
          .lastName("Hamsik")
          .function("Centrocampista")
          .mail(MAIL_ADDRESS).build();

      stopPointWorkflow = StopPointWorkflow.builder()
          .sloid("ch:1:sloid:1234")
          .sboid("ch:1:sboid:666")
          .designationOfficial("Biel/Bienne Bözingenfeld/Champ")
          .localityName("Biel/Bienne")
          .ccEmails(List.of(MAIL_ADDRESS))
          .workflowComment("WF comment")
          .status(WorkflowStatus.HEARING)
          .examinants(Set.of(person))
          .startDate(LocalDate.of(2000, 1, 1))
          .endDate(LocalDate.of(2000, 12, 31))
          .versionId(versionId)
          .build();
      workflowRepository.save(stopPointWorkflow);

      stopPointCancelWorkflowModel = StopPointRejectWorkflowModel.builder()
          .motivationComment("I don't like it!")
          .firstName("Marek")
          .lastName("Hamsik")
          .organisation("YB")
          .mail(MAIL_ADDRESS)
          .build();

      //given
      return mvc.perform(post(StopPointWorkflowApiInternal.BASE_PATH + "/cancel/" + stopPointWorkflow.getId())
          .contentType(contentType)
          .content(mapper.writeValueAsString(stopPointCancelWorkflowModel)));
    }

    @Test
    @WithMockJwtAuthentication(role = MockRole.UNAUTHORIZED)
    void shouldNotCancelWorkflowAsUnauthorized() throws Exception {
      cancelWorkflow().andExpect(status().isForbidden());
    }

    @Test
    @WithMockJwtAuthentication(role = MockRole.STANDARD)
    void shouldNotCancelWorkflowAsStandardUserWithoutPermissions() throws Exception {
      cancelWorkflow().andExpect(status().isForbidden());
    }

    @Test
    @WithMockJwtAuthentication(role = MockRole.STANDARD)
    void shouldCancelWorkflowAsSupervisor() throws Exception {
      setUserPermissionToSupervisor();
      cancelWorkflow().andExpect(status().isOk());
    }
  }

  private void setUserPermissionToSupervisor() {
    Permission permission = Permission.builder()
        .identifier(WithMockJwtAuthentication.MOCKUSER_SBB_UID)
        .application(ApplicationType.SEPODI)
        .role(ApplicationRole.SUPERVISOR)
        .build();
    permissionRepository.saveAndFlush(permission);
  }

  private static ReadServicePointVersionModel getUpdateServicePointVersionModel() {
    long versionId = 123456L;
    String sloid = "ch:1:sloid:1234";
    ServicePointGeolocationReadModel geolocationReadModel = ServicePointGeolocationReadModel.builder()
        .swissLocation(SwissLocation.builder()
            .canton(SwissCanton.BERN)
            .localityMunicipality(LocalityMunicipalityModel.builder().localityName("Bern").build())
            .build())
        .build();
    return ReadServicePointVersionModel.builder()
        .designationLong("designation long 1")
        .designationOfficial("Aargau Strasse")
        .abbreviation("ABC")
        .id(versionId)
        .sloid(sloid)
        .freightServicePoint(false)
        .sortCodeOfDestinationStation("39136")
        .businessOrganisation("ch:1:sboid:100871")
        .categories(List.of(Category.POINT_OF_SALE))
        .status(Status.IN_REVIEW)
        .servicePointGeolocation(geolocationReadModel)
        .operatingPointRouteNetwork(true)
        .meansOfTransport(List.of(MeanOfTransport.TRAIN))
        .stopPointType(StopPointType.ON_REQUEST)
        .validFrom(LocalDate.of(2010, 12, 11))
        .validTo(LocalDate.of(2019, 8, 10))
        .build();
  }

}