package ch.sbb.workflow.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import ch.sbb.atlas.model.controller.WithUnauthorizedMockJwtAuthentication;
import ch.sbb.atlas.workflow.model.WorkflowStatus;
import ch.sbb.workflow.entity.Decision;
import ch.sbb.workflow.entity.DecisionType;
import ch.sbb.workflow.entity.JudgementType;
import ch.sbb.workflow.entity.Person;
import ch.sbb.workflow.entity.StopPointWorkflow;
import ch.sbb.workflow.kafka.StopPointWorkflowNotificationService;
import ch.sbb.workflow.model.sepodi.DecisionModel;
import ch.sbb.workflow.model.sepodi.OtpRequestModel;
import ch.sbb.workflow.model.sepodi.OtpVerificationModel;
import ch.sbb.workflow.model.sepodi.ReadStopPointWorkflowModel;
import ch.sbb.workflow.model.sepodi.StopPointClientPersonModel;
import ch.sbb.workflow.repository.DecisionRepository;
import ch.sbb.workflow.repository.OtpRepository;
import ch.sbb.workflow.repository.StopPointWorkflowRepository;
import ch.sbb.workflow.service.sepodi.SePoDiClientService;
import java.time.LocalDate;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@WithUnauthorizedMockJwtAuthentication
@ActiveProfiles("integration-test")
@EmbeddedKafka
@Transactional
class StopPointWorkflowControllerVotingUnauthorizedTest {

  private static final String MAIL_ADDRESS = "marek@hamsik.com";

  @Autowired
  private StopPointWorkflowController controller;

  @Autowired
  private StopPointWorkflowRepository workflowRepository;

  @Autowired
  private DecisionRepository decisionRepository;

  @Autowired
  private OtpRepository otpRepository;

  @MockBean
  private StopPointWorkflowNotificationService notificationService;

  @MockBean
  private SePoDiClientService sePoDiClientService;

  @Captor
  private ArgumentCaptor<String> pincodeCaptor;

  private StopPointWorkflow workflowInHearing;
  private Person judith;

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
    judith = Person.builder()
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
        .examinants(new HashSet<>(Set.of(marek, judith)))
        .startDate(LocalDate.of(2000, 1, 1))
        .endDate(LocalDate.of(2000, 12, 31))
        .versionId(123456L)
        .status(WorkflowStatus.HEARING)
        .ccEmails(Collections.emptyList())
        .build();
    marek.setStopPointWorkflow(workflow);
    judith.setStopPointWorkflow(workflow);

    workflowInHearing = workflowRepository.save(workflow);
  }

  @Test
  void shouldObtainOtpViaMailAndVoteAsUnauthorizedUserCorrectly() {
    controller.obtainOtp(workflowInHearing.getId(), OtpRequestModel.builder().examinantMail(MAIL_ADDRESS).build());
    verify(notificationService, times(1)).sendPinCodeMail(any(), eq(MAIL_ADDRESS), pincodeCaptor.capture());

    StopPointClientPersonModel verifiedExaminant = controller.verifyOtp(workflowInHearing.getId(),
        OtpVerificationModel.builder().examinantMail(MAIL_ADDRESS).pinCode(pincodeCaptor.getValue()).build());

    controller.voteWorkflow(workflowInHearing.getId(), verifiedExaminant.getId(),
        DecisionModel.builder().judgement(JudgementType.YES).examinantMail(MAIL_ADDRESS).pinCode(pincodeCaptor.getValue())
            .build());

    Decision decision = decisionRepository.findDecisionByExaminantId(verifiedExaminant.getId());
    assertThat(decision.getJudgement()).isEqualTo(JudgementType.YES);
  }

  @Test
  void shouldApproveWorkflowWithLastVoteAsUnauthorizedUserCorrectly() {
    // Given judith voted:
    Decision judithsDecision = Decision.builder()
        .judgement(JudgementType.YES)
        .decisionType(DecisionType.VOTED)
        .examinant(judith)
        .build();
    decisionRepository.save(judithsDecision);

    // When Marek votes
    controller.obtainOtp(workflowInHearing.getId(), OtpRequestModel.builder().examinantMail(MAIL_ADDRESS).build());
    verify(notificationService, times(1)).sendPinCodeMail(any(), eq(MAIL_ADDRESS), pincodeCaptor.capture());

    StopPointClientPersonModel verifiedExaminant = controller.verifyOtp(workflowInHearing.getId(),
        OtpVerificationModel.builder().examinantMail(MAIL_ADDRESS).pinCode(pincodeCaptor.getValue()).build());

    controller.voteWorkflow(workflowInHearing.getId(), verifiedExaminant.getId(),
        DecisionModel.builder().judgement(JudgementType.YES).examinantMail(MAIL_ADDRESS).pinCode(pincodeCaptor.getValue())
            .build());

    verify(sePoDiClientService).updateStopPointStatusToValidatedAsAdmin(any());

    // Then decision is given
    Decision decision = decisionRepository.findDecisionByExaminantId(verifiedExaminant.getId());
    assertThat(decision.getJudgement()).isEqualTo(JudgementType.YES);

    // Workflow is approved
    ReadStopPointWorkflowModel stopPointWorkflow = controller.getStopPointWorkflow(workflowInHearing.getId());
    assertThat(stopPointWorkflow.getStatus()).isEqualTo(WorkflowStatus.APPROVED);
  }

}