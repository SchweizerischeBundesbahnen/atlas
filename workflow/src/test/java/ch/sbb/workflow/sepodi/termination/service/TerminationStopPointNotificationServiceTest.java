package ch.sbb.workflow.sepodi.termination.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ch.sbb.atlas.kafka.model.mail.MailNotification;
import ch.sbb.workflow.mail.MailProducerService;
import ch.sbb.workflow.sepodi.termination.entity.TerminationStopPointWorkflow;
import ch.sbb.workflow.sepodi.termination.entity.TerminationWorkflowStatus;
import ch.sbb.workflow.sepodi.termination.model.TerminationDecisionModel;
import ch.sbb.workflow.sepodi.termination.model.TerminationExaminants;
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

  @Mock
  private TerminationExaminants terminationExaminants;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    notificationService = new TerminationStopPointNotificationService(mailProducerService, builderNotificationService,
        terminationExaminants);
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
    when(builderNotificationService.buildStartTerminationNotificationMailForInfoPlus(any())).thenReturn(
        MailNotification.builder().build());
    notificationService.sendStartTerminationNotificationToInfoPlusAndBo(terminationStopPointWorkflow);

    verify(builderNotificationService).buildStartTerminationNotificationMailForInfoPlus(terminationStopPointWorkflow);
    verify(mailProducerService, times(2)).produceMailNotification(any());
  }

  @Test
  void shouldSendTariffStopNotApprovedNotificationToBo() {
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
        .status(TerminationWorkflowStatus.TERMINATION_NOT_APPROVED)
        .build();
    //when
    notificationService.sendTariffStopNotApprovedNotificationToBo(terminationStopPointWorkflow,
        TerminationDecisionModel.builder().motivation("Not approved").build());

    verify(builderNotificationService).buildTariffStopNotApprovedNotification(terminationStopPointWorkflow, "Not approved");
    verify(mailProducerService).produceMailNotification(any());
  }

  @Test
  void shouldSendTariffStopApprovedNotificationToNovaAndBo() {
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
        .status(TerminationWorkflowStatus.TERMINATION_APPROVED)
        .build();
    //when
    when(builderNotificationService.buildTariffStopApprovedNotification(any())).thenReturn(MailNotification.builder().build());
    notificationService.sendTariffStopApprovedNotificationToNovaAndBo(terminationStopPointWorkflow);

    verify(builderNotificationService).buildTariffStopApprovedNotification(terminationStopPointWorkflow);
    verify(mailProducerService, times(2)).produceMailNotification(any());
  }

  @Test
  void shouldSendCancelNotificationToInfoPlusAndBoAndNova() {
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
        .status(TerminationWorkflowStatus.CANCELED)
        .build();
    //when
    notificationService.sendCancelNotificationToInfoPlusAndBoAndNova(terminationStopPointWorkflow);

    verify(builderNotificationService).buildCancelNotification(terminationStopPointWorkflow);
    verify(mailProducerService).produceMailNotification(any());
  }

}