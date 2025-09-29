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
import ch.sbb.atlas.model.Status;
import ch.sbb.atlas.model.controller.BaseControllerApiTest;
import ch.sbb.atlas.servicepoint.enumeration.Category;
import ch.sbb.atlas.servicepoint.enumeration.MeanOfTransport;
import ch.sbb.atlas.servicepoint.enumeration.StopPointType;
import ch.sbb.atlas.workflow.model.WorkflowStatus;
import ch.sbb.workflow.entity.Person;
import ch.sbb.workflow.module.sepodi.hearing.StopPointWorkflowTestData;
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
import ch.sbb.workflow.module.sepodi.hearing.model.sepodi.OverrideDecisionModel;
import ch.sbb.workflow.module.sepodi.hearing.model.sepodi.StopPointAddWorkflowModel;
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
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

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

  @MockitoBean
  private SePoDiClientService sePoDiClientService;

  @MockitoBean
  private StopPointWorkflowNotificationService notificationService;

  @AfterEach
  void tearDown() {
    otpRepository.deleteAll();
    decisionRepository.deleteAll();
    workflowRepository.deleteAll();
  }

  @Test
  void shouldGetExaminants() throws Exception {
    StopPointAddWorkflowModel workflowModel = StopPointWorkflowTestData.getAddStopPointWorkflow1();
    when(sePoDiClientService.updateStopPointStatusToInReview(workflowModel.getSloid(), workflowModel.getVersionId()))
        .thenReturn(getUpdateServicePointVersionModel());
    controller.addStopPointWorkflow(workflowModel);

    ReadServicePointVersionModel servicePointVersionModel = getUpdateServicePointVersionModel();

    when(sePoDiClientService.getServicePointById(servicePointVersionModel.getId()))
        .thenReturn(getUpdateServicePointVersionModel());

    mvc.perform(get(StopPointWorkflowApiInternal.BASE_PATH + "/123456/examinants"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(2)));
  }

  @Test
  void shouldStartWorkflow() throws Exception {
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
    mvc.perform(post(StopPointWorkflowApiInternal.BASE_PATH + "/start/" + stopPointWorkflow.getId())
            .contentType(contentType))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status", is("HEARING")));
    verify(notificationService).sendStartStopPointWorkflowMail(any(StopPointWorkflow.class));
  }

  @Test
  void shouldEditWorkflow() throws Exception {
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
    person.setStopPointWorkflow(stopPointWorkflow);
    workflowRepository.save(stopPointWorkflow);

    List<Person> examinant = new ArrayList<>(stopPointWorkflow.getExaminants());

    EditStopPointWorkflowModel editStopPointWorkflowModel = EditStopPointWorkflowModel.builder()
        .workflowComment("New Comment")
        .designationOfficial("Bern")
        .examinants(examinant.stream().map(StopPointClientPersonMapper::toModel).toList())
        .build();

    //given
    mvc.perform(post(StopPointWorkflowApiInternal.BASE_PATH + "/edit/" + stopPointWorkflow.getId())
            .contentType(contentType)
            .content(mapper.writeValueAsString(editStopPointWorkflowModel)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status", is("ADDED")));
    List<StopPointWorkflow> workflows =
        workflowRepository.findAll().stream().filter(spw -> spw.getVersionId().equals(versionId))
            .sorted(Comparator.comparing(StopPointWorkflow::getId)).toList();
    assertThat(workflows).hasSize(1);
    assertThat(workflows.getFirst().getStatus()).isEqualTo(WorkflowStatus.ADDED);
    assertThat(workflows.getFirst().getWorkflowComment()).isEqualTo(editStopPointWorkflowModel.getWorkflowComment());
  }

  @Test
  void shouldRejectWorkflow() throws Exception {
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

    StopPointRejectWorkflowModel stopPointRejectWorkflowModel = StopPointRejectWorkflowModel.builder()
        .motivationComment("No Comment")
        .firstName("Marek")
        .lastName("Hamsik")
        .organisation("YB")
        .mail(MAIL_ADDRESS)
        .build();

    //given
    mvc.perform(post(StopPointWorkflowApiInternal.BASE_PATH + "/reject/" + stopPointWorkflow.getId())
            .contentType(contentType)
            .content(mapper.writeValueAsString(stopPointRejectWorkflowModel)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status", is("REJECTED")));

    List<StopPointWorkflow> workflows =
        workflowRepository.findAll().stream().filter(spw -> spw.getVersionId().equals(versionId))
            .sorted(Comparator.comparing(StopPointWorkflow::getId)).toList();
    assertThat(workflows).hasSize(1);
    assertThat(workflows.getFirst().getStatus()).isEqualTo(WorkflowStatus.REJECTED);

    Decision decisionResult = decisionRepository.findAll().stream()
        .filter(decision -> decision.getExaminant().getStopPointWorkflow().getId().equals(stopPointWorkflow.getId())).findFirst()
        .orElse(null);
    assertThat(decisionResult).isNotNull();
    Person examinant = decisionResult.getExaminant();
    assertThat(examinant.getMail()).isEqualTo(MAIL_ADDRESS);
    assertThat(decisionResult.getMotivation()).isEqualTo(stopPointRejectWorkflowModel.getMotivationComment());
    assertThat(decisionResult.getDecisionType()).isEqualTo(DecisionType.REJECTED);

    verify(notificationService).sendRejectStopPointWorkflowMail(any(StopPointWorkflow.class), anyString());
  }

  @Test
  void shouldCancelWorkflow() throws Exception {
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
    workflowRepository.save(stopPointWorkflow);

    StopPointRejectWorkflowModel stopPointCancelWorkflowModel = StopPointRejectWorkflowModel.builder()
        .motivationComment("I don't like it!")
        .firstName("Marek")
        .lastName("Hamsik")
        .organisation("YB")
        .mail(MAIL_ADDRESS)
        .build();

    //given
    mvc.perform(post(StopPointWorkflowApiInternal.BASE_PATH + "/cancel/" + stopPointWorkflow.getId())
            .contentType(contentType)
            .content(mapper.writeValueAsString(stopPointCancelWorkflowModel)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status", is("CANCELED")));

    List<StopPointWorkflow> workflows =
        workflowRepository.findAll().stream().filter(spw -> spw.getVersionId().equals(versionId))
            .sorted(Comparator.comparing(StopPointWorkflow::getId)).toList();
    assertThat(workflows).hasSize(1);
    assertThat(workflows.getFirst().getStatus()).isEqualTo(WorkflowStatus.CANCELED);

    Decision decisionResult = decisionRepository.findAll().stream()
        .filter(decision -> decision.getExaminant().getStopPointWorkflow().getId().equals(stopPointWorkflow.getId())).findFirst()
        .orElse(null);
    assertThat(decisionResult).isNotNull();
    Person examinant = decisionResult.getExaminant();
    assertThat(examinant.getMail()).isEqualTo(MAIL_ADDRESS);
    assertThat(decisionResult.getMotivation()).isEqualTo(stopPointCancelWorkflowModel.getMotivationComment());
    assertThat(decisionResult.getDecisionType()).isEqualTo(DecisionType.CANCELED);
    stopPointWorkflow.setStatus(WorkflowStatus.CANCELED);
    verify(sePoDiClientService).updateStopPointStatusToDraftAsAdmin(any(StopPointWorkflow.class));
  }

  @Test
  void shouldGetOtpWorkflow() throws Exception {
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

    OtpRequestModel otpRequest = OtpRequestModel.builder().examinantMail(MAIL_ADDRESS).build();

    //given
    mvc.perform(post(StopPointWorkflowApiInternal.BASE_PATH + "/obtain-otp/" + stopPointWorkflow.getId())
            .contentType(contentType)
            .content(mapper.writeValueAsString(otpRequest)))
        .andExpect(status().isAccepted());

    Otp otpResult = otpRepository.findAll().stream().filter(otp -> otp.getPerson().getId().equals(person.getId())).findFirst()
        .orElse(null);

    assertThat(otpResult).isNotNull();
    assertThat(otpResult.getPerson().getId()).isEqualTo(person.getId());
  }

  @Test
  void shouldVoteToWorkflow() throws Exception {
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
    StopPointWorkflow workflow = workflowRepository.saveAndFlush(stopPointWorkflow);
    person.setStopPointWorkflow(workflow);
    workflowRepository.saveAndFlush(workflow);

    Otp otp = Otp.builder().code(OtpHelper.hashPinCode("12345")).person(person).build();
    otpRepository.saveAndFlush(otp);
    DecisionModel decisionModel = DecisionModel.builder()
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
    mvc.perform(post(StopPointWorkflowApiInternal.BASE_PATH + "/vote/" + stopPointWorkflow.getId() + "/" + person.getId())
            .contentType(contentType)
            .content(mapper.writeValueAsString(decisionModel)))
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

  @Test
  void shouldRestartWorkflow() throws Exception {
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
    mvc.perform(post(StopPointWorkflowApiInternal.BASE_PATH + "/restart/" + stopPointWorkflow.getId())
            .contentType(contentType)
            .content(mapper.writeValueAsString(restartWorkflowModel)))
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

  @Test
  void shouldOverrideVoteWithoutDecisionToWorkflow() throws Exception {
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
    StopPointWorkflow workflow = workflowRepository.saveAndFlush(stopPointWorkflow);
    person.setStopPointWorkflow(workflow);
    workflowRepository.saveAndFlush(workflow);

    OverrideDecisionModel overrideDecisionModel = OverrideDecisionModel.builder()
        .firstName("***REMOVED***")
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
    Set<Person> examinants = workflows.getFirst().getExaminants();
    assertThat(examinants).hasSize(1);
    Decision decisionByExaminantId = decisionRepository.findDecisionByExaminantId(person.getId());
    assertThat(decisionByExaminantId).isNotNull();
    assertThat(decisionByExaminantId.getFotMotivation()).isEqualTo(overrideDecisionModel.getFotMotivation());
    assertThat(decisionByExaminantId.getFotJudgement()).isEqualTo(overrideDecisionModel.getFotJudgement());
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

    Otp otp = Otp.builder().code("12345").person(person).build();
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
        .firstName("***REMOVED***")
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

  @Test
  void shouldAddExaminantsToWorkflowInHearing() throws Exception {
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

    AddExaminantsModel addExaminantsModel = AddExaminantsModel.builder()
        .examinants(List.of(StopPointClientPersonModel.builder().organisation("Sample").mail("someguy@sbb.ch").build()))
        .ccEmails(List.of("additionalDude@bern.be"))
        .build();

    //given
    mvc.perform(post(StopPointWorkflowApiInternal.BASE_PATH + "/add-examinants/" + stopPointWorkflow.getId())
            .contentType(contentType)
            .content(mapper.writeValueAsString(addExaminantsModel)))
        .andExpect(status().isOk());

    StopPointWorkflow workflow =
        workflowRepository.findAll().stream().filter(spw -> spw.getVersionId().equals(versionId))
            .sorted(Comparator.comparing(StopPointWorkflow::getId)).toList().getFirst();
    assertThat(workflow.getExaminants()).hasSize(2);
    assertThat(workflow.getCcEmails()).hasSize(2);

    verify(notificationService).sendStartToAddedExaminant(any(StopPointWorkflow.class), eq(List.of("someguy@sbb.ch")));
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