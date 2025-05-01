package ch.sbb.workflow.sepodi.termination.service;

import static ch.sbb.atlas.kafka.model.mail.MailType.START_TERMINATION_STOP_POINT_WORKFLOW_NOTIFICATION;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import ch.sbb.atlas.kafka.model.mail.MailNotification;
import ch.sbb.workflow.sepodi.termination.entity.TerminationStopPointWorkflow;
import ch.sbb.workflow.sepodi.termination.entity.TerminationWorkflowStatus;
import ch.sbb.workflow.sepodi.termination.model.TerminationExaminants;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class TerminationStopPointWorkflowBuilderNotificationServiceTest {

  private TerminationStopPointWorkflowBuilderNotificationService builderNotificationService;

  @Mock
  private TerminationExaminants terminationExaminants;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    builderNotificationService = new TerminationStopPointWorkflowBuilderNotificationService(terminationExaminants);
  }

  @Test
  void shouldBuildStartTerminationNotificationMailForInfoPlus() {
    //given
    String mail = "a@b-ch";
    TerminationStopPointWorkflow terminationStopPointWorkflow = TerminationStopPointWorkflow.builder()
        .sloid("ch:1:sloid:1")
        .versionId(1234L)
        .boTerminationDate(LocalDate.of(2000, 1, 1))
        .infoPlusTerminationDate(LocalDate.of(2000, 1, 2))
        .novaTerminationDate(LocalDate.of(2000, 1, 3))
        .applicantMail("a@b.com")
        .designationOfficial("Heimsiswil Zentrum")
        .sboid("ch:sboid:1")
        .status(TerminationWorkflowStatus.STARTED)
        .build();
    when(terminationExaminants.getInfoPlus()).thenReturn(mail);
    //when
    MailNotification result = builderNotificationService.buildStartTerminationNotificationMailForInfoPlus(
        terminationStopPointWorkflow);
    //then
    assertThat(result).isNotNull();
    assertThat(result.getMailType()).isEqualTo(START_TERMINATION_STOP_POINT_WORKFLOW_NOTIFICATION);
    assertThat(result.getSubject()).isEqualTo(TerminationWorkflowSubject.START_TERMINATION_WORKFLOW_SUBJECT);
    assertThat(result.getTo()).hasSize(1).contains(mail);
    assertThat(result.getCc()).isNull();
  }
}