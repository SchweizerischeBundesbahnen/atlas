package ch.sbb.workflow.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.atlas.workflow.model.WorkflowStatus;
import ch.sbb.workflow.entity.Person;
import ch.sbb.workflow.exception.StopPointWorkflowExaminantNotFoundException;
import ch.sbb.workflow.exception.StopPointWorkflowPinCodeInvalidException;
import ch.sbb.workflow.otp.repository.OtpRepository;
import ch.sbb.workflow.sepodi.hearing.controller.StopPointWorkflowController;
import ch.sbb.workflow.sepodi.hearing.enity.Decision;
import ch.sbb.workflow.sepodi.hearing.enity.JudgementType;
import ch.sbb.workflow.sepodi.hearing.enity.StopPointWorkflow;
import ch.sbb.workflow.sepodi.hearing.mail.StopPointWorkflowNotificationService;
import ch.sbb.workflow.sepodi.hearing.model.sepodi.DecisionModel;
import ch.sbb.workflow.sepodi.hearing.model.sepodi.OtpRequestModel;
import ch.sbb.workflow.sepodi.hearing.model.sepodi.OtpVerificationModel;
import ch.sbb.workflow.sepodi.hearing.model.sepodi.OverrideDecisionModel;
import ch.sbb.workflow.sepodi.hearing.model.sepodi.ReadDecisionModel;
import ch.sbb.workflow.sepodi.hearing.model.sepodi.ReadStopPointWorkflowModel;
import ch.sbb.workflow.sepodi.hearing.model.sepodi.StopPointClientPersonModel;
import ch.sbb.workflow.sepodi.hearing.repository.DecisionRepository;
import ch.sbb.workflow.sepodi.hearing.repository.StopPointWorkflowRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

@IntegrationTest
@Transactional
class StopPointWorkflowControllerVotingTest {

  private static final String MAIL_ADDRESS = "marek@hamsik.com";

  @Autowired
  private StopPointWorkflowController controller;

  @Autowired
  private StopPointWorkflowRepository workflowRepository;

  @Autowired
  private DecisionRepository decisionRepository;

  @Autowired
  private OtpRepository otpRepository;

  @MockitoBean
  private StopPointWorkflowNotificationService notificationService;

  @Captor
  private ArgumentCaptor<String> pincodeCaptor;

  private StopPointWorkflow workflowInHearing;

  @AfterEach
  void tearDown() {
    otpRepository.deleteAll();
    decisionRepository.deleteAll();
    workflowRepository.deleteAll();
  }

  @BeforeEach
  void setUp() {
    Person marek = Person.builder()
        .firstName("Marek")
        .lastName("Hamsik")
        .function("Centrocampista")
        .mail(MAIL_ADDRESS).build();
    Person judith = Person.builder()
        .firstName("Judith")
        .lastName("Bollhalder")
        .function("Fachstelle")
        .mail("judith.bollhalder@sbb.ch").build();
    StopPointWorkflow workflow = StopPointWorkflow.builder()
        .sloid("ch:1:sloid:1234")
        .sboid("ch:1:sboid:666")
        .designationOfficial("Biel/Bienne Bözingenfeld/Champ")
        .localityName("Biel/Bienne")
        .workflowComment("WF comment")
        .examinants(Set.of(marek, judith))
        .startDate(LocalDate.of(2000, 1, 1))
        .endDate(LocalDate.of(2000, 12, 31))
        .versionId(123456L)
        .status(WorkflowStatus.HEARING)
        .build();
    marek.setStopPointWorkflow(workflow);
    judith.setStopPointWorkflow(workflow);

    workflowInHearing = workflowRepository.save(workflow);
  }

  @Test
  void shouldObtainOtpViaMailWithCorrectMailAddress() {
    controller.obtainOtp(workflowInHearing.getId(), OtpRequestModel.builder().examinantMail(MAIL_ADDRESS).build());

    assertThat(otpRepository.findAll()).isNotEmpty();
    verify(notificationService, times(1)).sendPinCodeMail(any(), eq(MAIL_ADDRESS), pincodeCaptor.capture());
    assertThat(pincodeCaptor.getValue()).isNotEmpty();
  }

  @Test
  void shouldNotObtainOtpViaMailWithIncorrectMailAddress() {
    assertThatExceptionOfType(StopPointWorkflowExaminantNotFoundException.class).isThrownBy(() ->
        controller.obtainOtp(workflowInHearing.getId(), OtpRequestModel.builder().examinantMail("iwantmail@here.ch").build()));

    assertThat(otpRepository.findAll()).isEmpty();
    verify(notificationService, times(0)).sendPinCodeMail(any(), anyString(), anyString());
  }

  @Test
  void shouldObtainOtpViaMailAndValidateIt() {
    controller.obtainOtp(workflowInHearing.getId(), OtpRequestModel.builder().examinantMail(MAIL_ADDRESS).build());
    verify(notificationService, times(1)).sendPinCodeMail(any(), eq(MAIL_ADDRESS), pincodeCaptor.capture());

    StopPointClientPersonModel verifiedExaminant = controller.verifyOtp(workflowInHearing.getId(),
        OtpVerificationModel.builder().examinantMail(MAIL_ADDRESS).pinCode(pincodeCaptor.getValue()).build());

    assertThat(verifiedExaminant.getId()).isNotNull();
    assertThat(verifiedExaminant.getMail()).isEqualTo(MAIL_ADDRESS);
  }

  @Test
  void shouldObtainOtpViaMailAndFailValidate() {
    controller.obtainOtp(workflowInHearing.getId(), OtpRequestModel.builder().examinantMail(MAIL_ADDRESS).build());
    verify(notificationService, times(1)).sendPinCodeMail(any(), eq(MAIL_ADDRESS), pincodeCaptor.capture());

    assertThatExceptionOfType(StopPointWorkflowPinCodeInvalidException.class).isThrownBy(
        () -> controller.verifyOtp(workflowInHearing.getId(),
            OtpVerificationModel.builder().examinantMail(MAIL_ADDRESS).pinCode("incorrectPin").build()));
  }

  @Test
  void shouldObtainOtpViaMailAndVoteCorrectly() {
    // Read workflow details
    ReadStopPointWorkflowModel stopPointWorkflow = controller.getStopPointWorkflow(workflowInHearing.getId());
    assertThat(stopPointWorkflow.getExaminants().getFirst().getJudgement()).isNull();

    // Obtain OTP
    controller.obtainOtp(workflowInHearing.getId(), OtpRequestModel.builder().examinantMail(MAIL_ADDRESS).build());
    verify(notificationService, times(1)).sendPinCodeMail(any(), eq(MAIL_ADDRESS), pincodeCaptor.capture());

    // Verify examinant
    StopPointClientPersonModel verifiedExaminant = controller.verifyOtp(workflowInHearing.getId(),
        OtpVerificationModel.builder().examinantMail(MAIL_ADDRESS).pinCode(pincodeCaptor.getValue()).build());

    // Vote
    controller.voteWorkflow(workflowInHearing.getId(), verifiedExaminant.getId(),
        DecisionModel.builder().judgement(JudgementType.YES).examinantMail(MAIL_ADDRESS).pinCode(pincodeCaptor.getValue())
            .build());

    // Verify correct handling
    Decision decision = decisionRepository.findDecisionByExaminantId(verifiedExaminant.getId());
    assertThat(decision.getJudgement()).isEqualTo(JudgementType.YES);

    stopPointWorkflow = controller.getStopPointWorkflow(workflowInHearing.getId());
    assertThat(stopPointWorkflow.getExaminants().stream().filter(i -> i.getMail().equals(MAIL_ADDRESS)).findFirst().orElseThrow()
        .getJudgement()).isEqualTo(JudgementType.YES);
  }

  @Test
  void shouldOverridePreviousVoteBySelfCorrectly() {
    // First Vote: NO
    controller.obtainOtp(workflowInHearing.getId(), OtpRequestModel.builder().examinantMail(MAIL_ADDRESS).build());
    verify(notificationService, times(1)).sendPinCodeMail(any(), eq(MAIL_ADDRESS), pincodeCaptor.capture());

    StopPointClientPersonModel verifiedExaminant = controller.verifyOtp(workflowInHearing.getId(),
        OtpVerificationModel.builder().examinantMail(MAIL_ADDRESS).pinCode(pincodeCaptor.getValue()).build());

    controller.voteWorkflow(workflowInHearing.getId(), verifiedExaminant.getId(),
        DecisionModel.builder().judgement(JudgementType.NO).examinantMail(MAIL_ADDRESS).pinCode(pincodeCaptor.getValue())
            .build());

    Decision decision = decisionRepository.findDecisionByExaminantId(verifiedExaminant.getId());
    assertThat(decision.getJudgement()).isEqualTo(JudgementType.NO);

    // Prepare second vote
    clearInvocations(notificationService);

    // Second Vote: YES
    controller.obtainOtp(workflowInHearing.getId(), OtpRequestModel.builder().examinantMail(MAIL_ADDRESS).build());
    verify(notificationService, times(1)).sendPinCodeMail(any(), eq(MAIL_ADDRESS), pincodeCaptor.capture());

    verifiedExaminant = controller.verifyOtp(workflowInHearing.getId(),
        OtpVerificationModel.builder().examinantMail(MAIL_ADDRESS).pinCode(pincodeCaptor.getValue()).build());

    controller.voteWorkflow(workflowInHearing.getId(), verifiedExaminant.getId(),
        DecisionModel.builder().judgement(JudgementType.YES).examinantMail(MAIL_ADDRESS).pinCode(pincodeCaptor.getValue())
            .build());

    decision = decisionRepository.findDecisionByExaminantId(verifiedExaminant.getId());
    assertThat(decision.getJudgement()).isEqualTo(JudgementType.YES);
  }

  @Test
  void shouldOverridePendingVoteCorrectly() {
    //given
    Person examinantToOverride = workflowInHearing.getExaminants().stream().filter(i -> i.getMail().equals(MAIL_ADDRESS))
        .findFirst().orElseThrow();

    Decision examinantDecision = decisionRepository.findDecisionByExaminantId(examinantToOverride.getId());
    assertThat(examinantDecision).isNull();

    // when
    OverrideDecisionModel override = OverrideDecisionModel.builder()
        .firstName("Luca")
        .lastName("Ammann")
        .fotJudgement(JudgementType.YES)
        .fotMotivation("Nein, Müll")
        .build();

    controller.overrideVoteWorkflow(workflowInHearing.getId(), examinantToOverride.getId(), override);

    // then
    examinantDecision = decisionRepository.findDecisionByExaminantId(examinantToOverride.getId());
    assertThat(examinantDecision.getFotJudgement()).isEqualTo(JudgementType.YES);
  }

  @Test
  void shouldOverrideExistingVoteCorrectly() {
    // given
    Person examinantToOverride =
        workflowInHearing.getExaminants().stream().filter(i -> i.getMail().equals(MAIL_ADDRESS)).findFirst().orElseThrow();
    Decision decision = Decision.builder()
        .judgement(JudgementType.NO)
        .motivation("Bad stuff")
        .motivationDate(LocalDateTime.now())
        .examinant(examinantToOverride)
        .build();
    decisionRepository.save(decision);

    // when
    OverrideDecisionModel override = OverrideDecisionModel.builder()
        .firstName("Luca")
        .lastName("Ammann")
        .fotJudgement(JudgementType.YES)
        .fotMotivation("Good stuff")
        .build();

    controller.overrideVoteWorkflow(workflowInHearing.getId(), examinantToOverride.getId(), override);

    // then
    Decision examinantDecision = decisionRepository.findDecisionByExaminantId(examinantToOverride.getId());
    assertThat(examinantDecision.getFotJudgement()).isEqualTo(JudgementType.YES);

    ReadDecisionModel decisionModel = controller.getDecision(examinantToOverride.getId());
    assertThat(decisionModel.getJudgement()).isEqualTo(JudgementType.NO);
    assertThat(decisionModel.getFotJudgement()).isEqualTo(JudgementType.YES);
  }

}