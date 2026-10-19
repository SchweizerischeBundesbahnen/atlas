package ch.sbb.exportservice.listener;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import ch.sbb.atlas.api.mail.MailNotification;
import ch.sbb.exportservice.service.MailClient;
import ch.sbb.exportservice.service.MailNotificationService;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.job.JobExecution;
import org.springframework.batch.core.job.JobInstance;
import org.springframework.batch.core.step.StepExecution;

class JobCompletionListenerTest {

  private final MailNotificationService mailNotificationService = mock(MailNotificationService.class);
  private final MailClient mailClient = mock(MailClient.class);
  private final JobCompletionListener listener = new JobCompletionListener(mailNotificationService, mailClient);

  @Test
  void shouldSendNotificationForFailedStep() {
    JobExecution jobExecution = mock(JobExecution.class);
    StepExecution stepExecution = mock(StepExecution.class);
    JobExecution stepJobExecution = mock(JobExecution.class);
    JobInstance jobInstance = mock(JobInstance.class);
    MailNotification mailNotification = mock(MailNotification.class);
    when(jobExecution.getExitStatus()).thenReturn(ExitStatus.FAILED);
    when(jobExecution.getStepExecutions()).thenReturn(Set.of(stepExecution));
    when(stepExecution.getStatus()).thenReturn(BatchStatus.FAILED);
    when(stepExecution.getJobExecution()).thenReturn(stepJobExecution);
    when(stepJobExecution.getJobInstance()).thenReturn(jobInstance);
    when(jobInstance.getJobName()).thenReturn("exportJob");
    when(mailNotificationService.buildMailErrorNotification("exportJob", stepExecution))
        .thenReturn(mailNotification);

    listener.afterJob(jobExecution);

    verify(mailClient).sendMailRequest(mailNotification);
  }

  @Test
  void shouldNotSendNotificationWhenJobDidNotFail() {
    JobExecution jobExecution = mock(JobExecution.class);
    when(jobExecution.getExitStatus()).thenReturn(ExitStatus.COMPLETED);

    listener.beforeJob(jobExecution);
    listener.afterJob(jobExecution);

    verifyNoInteractions(mailNotificationService, mailClient);
  }

  @Test
  void shouldNotSendNotificationWhenNoFailedStepExists() {
    JobExecution jobExecution = mock(JobExecution.class);
    StepExecution stepExecution = mock(StepExecution.class);
    when(jobExecution.getExitStatus()).thenReturn(ExitStatus.FAILED);
    when(jobExecution.getStepExecutions()).thenReturn(Set.of(stepExecution));
    when(stepExecution.getStatus()).thenReturn(BatchStatus.COMPLETED);

    listener.afterJob(jobExecution);

    verifyNoInteractions(mailNotificationService, mailClient);
  }
}
