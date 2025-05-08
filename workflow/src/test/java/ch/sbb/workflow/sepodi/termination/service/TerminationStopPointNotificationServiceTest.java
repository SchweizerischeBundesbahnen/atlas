package ch.sbb.workflow.sepodi.termination.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

import ch.sbb.workflow.mail.MailProducerService;
import ch.sbb.workflow.sepodi.termination.entity.TerminationStopPointWorkflow;
import ch.sbb.workflow.sepodi.termination.entity.TerminationWorkflowStatus;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class TerminationStopPointNotificationServiceTest {

  private TerminationStopPointNotificationService notificationService;

  @Mock
  private TerminationStopPointWorkflowBuilderNotificationService builderNotificationService;

  @Mock
  private MailProducerService mailProducerService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    notificationService = new TerminationStopPointNotificationService(mailProducerService, builderNotificationService);
  }

  @Test
  void shouldSendStartTerminationNotificationToInfoPlus() {
    //given
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
    //when
    notificationService.sendStartTerminationNotificationToInfoPlus(terminationStopPointWorkflow);

    verify(builderNotificationService).buildStartTerminationNotificationMailForInfoPlus(terminationStopPointWorkflow);
    verify(mailProducerService).produceMailNotification(any());
  }

}