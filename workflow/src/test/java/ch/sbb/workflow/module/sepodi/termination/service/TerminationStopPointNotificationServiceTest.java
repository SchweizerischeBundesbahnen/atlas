package ch.sbb.workflow.module.sepodi.termination.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ch.sbb.atlas.api.mail.MailNotification;
import ch.sbb.workflow.mail.MailClient;
import ch.sbb.workflow.module.sepodi.termination.entity.TerminationStopPointWorkflow;
import ch.sbb.workflow.module.sepodi.termination.entity.TerminationWorkflowStatus;
import ch.sbb.workflow.module.sepodi.termination.model.TerminationAbortModel;
import ch.sbb.workflow.module.sepodi.termination.model.TerminationDecisionModel;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class TerminationStopPointNotificationServiceTest {

  private TerminationStopPointNotificationService notificationService;

  @Mock
  private TerminationStopPointWorkflowBuilderNotificationService builderNotificationService;

  @Mock
  private MailClient mailClient;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    notificationService = new TerminationStopPointNotificationService(mailClient, builderNotificationService);
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
    MailNotification infoPlusNotification = MailNotification.builder().to(List.of("info-plus@example.com")).build();
    MailNotification applicantNotification = MailNotification.builder().to(List.of("a@b.com")).build();
    when(builderNotificationService.buildStartTerminationNotificationMailForInfoPlus(any())).thenReturn(
        infoPlusNotification);
    when(builderNotificationService.buildStartTerminationNotificationMailForApplicant(any())).thenReturn(
        applicantNotification);
    notificationService.sendStartTerminationNotificationToInfoPlusAndBo(terminationStopPointWorkflow);

    verify(builderNotificationService).buildStartTerminationNotificationMailForInfoPlus(terminationStopPointWorkflow);
    verify(builderNotificationService).buildStartTerminationNotificationMailForApplicant(terminationStopPointWorkflow);
    verify(mailClient).sendMailRequest(infoPlusNotification);
    verify(mailClient).sendMailRequest(applicantNotification);
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
    verify(mailClient).sendMailRequest(any());
  }

  @Test
  void shouldSendTariffStopApprovedNotificationToNova() {
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
    notificationService.sendTariffStopApprovedNotificationToNova(terminationStopPointWorkflow);

    verify(builderNotificationService).buildTariffStopApprovedNotification(terminationStopPointWorkflow);
    verify(mailClient, times(1)).sendMailRequest(any());
  }

  @Test
  void shouldSendAbortNotificationToBoAndInfoPlus() {
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

    when(builderNotificationService.buildAbortNotification(any(), any(), any())).thenReturn(MailNotification.builder().build());
    TerminationAbortModel abortModel = TerminationAbortModel.builder().abortComment("abort").build();
    //when
    notificationService.sendAbortNotificationToBoAndInfoPlus(terminationStopPointWorkflow, abortModel);

    verify(builderNotificationService).buildAbortNotificationForBoAndInfoPlus(terminationStopPointWorkflow, abortModel);

    verify(mailClient).sendMailRequest(any());
  }

  @Test
  void shouldSendAbortNotificationToBoInfoPlusNova() {
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

    when(builderNotificationService.buildAbortNotification(any(), any(), any())).thenReturn(MailNotification.builder().build());
    TerminationAbortModel abortModel = TerminationAbortModel.builder().abortComment("abort").build();
    //when
    notificationService.sendAbortNotificationToBoInfoPlusAndNova(terminationStopPointWorkflow, abortModel);

    verify(builderNotificationService).buildAbortNotificationForBoInfoPlusAndNova(terminationStopPointWorkflow, abortModel);
    verify(mailClient).sendMailRequest(any());
  }

}