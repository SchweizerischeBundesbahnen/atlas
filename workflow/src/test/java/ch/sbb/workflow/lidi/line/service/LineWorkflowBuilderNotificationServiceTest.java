package ch.sbb.workflow.lidi.line.service;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.kafka.model.mail.MailNotification;
import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.atlas.workflow.model.WorkflowStatus;
import ch.sbb.workflow.entity.Person;
import ch.sbb.workflow.lidi.line.entity.LineWorkflow;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.test.context.EmbeddedKafka;

@IntegrationTest
@EmbeddedKafka(topics = {"atlas.mail"})
class LineWorkflowBuilderNotificationServiceTest {

  @Autowired
  private LineWorkflowBuilderNotificationService lineWorkflowBuilderNotificationService;

  @Test
  void buildWorkflowStartedNotification() {
    MailNotification mailNotification = lineWorkflowBuilderNotificationService.buildWorkflowMailNotification(
        LineWorkflow.builder()
            .status(WorkflowStatus.STARTED)
            .workflowComment("Wee need this workflow")
            .swissId("ch:1:slnid:123")
            .businessObjectId(1100L)
            .description("Linie 1")
            .client(Person.builder().firstName("Hai").lastName("Fisch").function("Schwimmer").mail("hai@meer.com").build())
            .build());

    assertThat(mailNotification.getFrom()).isEqualTo("no-reply-atlas@sbb.ch");
    assertThat(mailNotification.getSubject()).isEqualTo(
        "Antrag prüfen zu / vérifier la demande de  / controllare la richiesta per: ch:1:slnid:123");
    Map<String, Object> properties = mailNotification.getTemplateProperties().get(0);
    assertThat(properties)
        .containsEntry("description", "Linie 1")
        .containsEntry("url", "http://localhost:4200/line-directory/lines/ch:1:slnid:123?id=1100");
  }

  @Test
  void buildWorkflowCompletedNotification() {
    MailNotification mailNotification = lineWorkflowBuilderNotificationService.buildWorkflowMailNotification(
        LineWorkflow.builder()
            .status(WorkflowStatus.APPROVED)
            .workflowComment("Wee need this workflow")
            .swissId("ch:1:slnid:123")
            .businessObjectId(1100L)
            .description("Linie 1")
            .checkComment("This is great")
            .examinant(
                Person.builder().firstName("Mr").lastName("Crabbs").function("Dinerbesitzer").mail("crabbs@meer.com").build())
            .client(Person.builder().firstName("Hai").lastName("Fisch").function("Schwimmer").mail("hai@meer.com").build())
            .build());

    assertThat(mailNotification.getFrom()).isEqualTo("no-reply-atlas@sbb.ch");
    assertThat(mailNotification.getSubject()).isEqualTo(
        "Antrag genehmigt / demande approuvée / richiesta approvata: ch:1:slnid:123");
    Map<String, Object> properties = mailNotification.getTemplateProperties().get(0);
    assertThat(properties)
        .containsEntry("description", "Linie 1")
        .containsEntry("url", "http://localhost:4200/line-directory/lines/ch:1:slnid:123?id=1100");
  }

  @Test
  void buildWorkflowRejectedNotification() {
    MailNotification mailNotification = lineWorkflowBuilderNotificationService.buildWorkflowMailNotification(
        LineWorkflow.builder()
            .status(WorkflowStatus.REJECTED)
            .workflowComment("Wee need this workflow")
            .swissId("ch:1:slnid:123")
            .businessObjectId(1100L)
            .description("Linie 1")
            .checkComment("This is great")
            .examinant(
                Person.builder().firstName("Mr").lastName("Crabbs").function("Dinerbesitzer").mail("crabbs@meer.com").build())
            .client(Person.builder().firstName("Hai").lastName("Fisch").function("Schwimmer").mail("hai@meer.com").build())
            .build());

    assertThat(mailNotification.getFrom()).isEqualTo("no-reply-atlas@sbb.ch");
    assertThat(mailNotification.getSubject()).isEqualTo(
        "Antrag zurückgewiesen / Demande rejetée / Domanda respinta: ch:1:slnid:123");
    Map<String, Object> properties = mailNotification.getTemplateProperties().get(0);
    assertThat(properties)
        .containsEntry("description", "Linie 1")
        .containsEntry("url", "http://localhost:4200/line-directory/lines/ch:1:slnid:123?id=1100");
  }
}