package ch.sbb.workflow.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import ch.sbb.atlas.model.controller.WithUnauthorizedMockJwtAuthentication;
import ch.sbb.atlas.workflow.model.WorkflowStatus;
import ch.sbb.workflow.entity.Decision;
import ch.sbb.workflow.entity.JudgementType;
import ch.sbb.workflow.entity.Person;
import ch.sbb.workflow.entity.StopPointWorkflow;
import ch.sbb.workflow.kafka.StopPointWorkflowNotificationService;
import ch.sbb.workflow.model.sepodi.DecisionModel;
import ch.sbb.workflow.model.sepodi.OtpRequestModel;
import ch.sbb.workflow.model.sepodi.OtpVerificationModel;
import ch.sbb.workflow.model.sepodi.StopPointClientPersonModel;
import ch.sbb.workflow.repository.DecisionRepository;
import ch.sbb.workflow.repository.OtpRepository;
import ch.sbb.workflow.repository.StopPointWorkflowRepository;
import java.time.LocalDate;
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

@SpringBootTest
@WithUnauthorizedMockJwtAuthentication
@ActiveProfiles("integration-test")
@EmbeddedKafka
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
    Person person = Person.builder()
        .firstName("Marek")
        .lastName("Hamsik")
        .function("Centrocampista")
        .mail(MAIL_ADDRESS).build();
    StopPointWorkflow workflow = StopPointWorkflow.builder()
        .sloid("ch:1:sloid:1234")
        .sboid("ch:1:sboid:666")
        .designationOfficial("Biel/Bienne Bözingenfeld/Champ")
        .localityName("Biel/Bienne")
        .workflowComment("WF comment")
        .examinants(Set.of(person))
        .startDate(LocalDate.of(2000, 1, 1))
        .endDate(LocalDate.of(2000, 12, 31))
        .versionId(123456L)
        .status(WorkflowStatus.HEARING)
        .build();
    person.setStopPointWorkflow(workflow);

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

}