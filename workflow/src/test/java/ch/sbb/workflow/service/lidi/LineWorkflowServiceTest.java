package ch.sbb.workflow.service.lidi;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.kafka.model.mail.MailNotification;
import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.atlas.workflow.model.WorkflowStatus;
import ch.sbb.workflow.entity.LineWorkflow;
import ch.sbb.workflow.entity.Person;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.test.context.EmbeddedKafka;

@IntegrationTest
@EmbeddedKafka(topics = {"atlas.mail"})
class LineWorkflowServiceTest {

  @Autowired
  private LineWorkflowService lineWorkflowService;

  @Test
  void buildWorkflowStartedNotification() {
    MailNotification mailNotification = lineWorkflowService.buildWorkflowStartedMailNotification(LineWorkflow.builder()
        .status(WorkflowStatus.STARTED)
        .workflowComment("Wee need this workflow")
        .swissId("ch:1:slnid:123")
        .businessObjectId(1100L)
        .description("Linie 1")
        .client(Person.builder().firstName("Hai").lastName("Fisch").function("Schwimmer").mail("hai@meer.com").build())
        .build());

    assertThat(mailNotification.getFrom()).isEqualTo("no-reply-atlas@sbb.ch");
    assertThat(mailNotification.getSubject()).isEqualTo("Antrag zu ch:1:slnid:123 Linie 1 prüfen");
    assertThat(mailNotification.getTemplateProperties().get(0).get("description")).isEqualTo("Linie 1");
    assertThat(mailNotification.getTemplateProperties().get(0).get("url")).isEqualTo(
        "http://localhost:4200/line-directory/lines/ch:1:slnid:123?id=1100");
  }

  @Test
  void buildWorkflowCompletedNotification() {
    MailNotification mailNotification = lineWorkflowService.buildWorkflowCompletedMailNotification(LineWorkflow.builder()
        .status(WorkflowStatus.APPROVED)
        .workflowComment("Wee need this workflow")
        .swissId("ch:1:slnid:123")
        .businessObjectId(1100L)
        .description("Linie 1")
        .checkComment("This is great")
        .examinant(Person.builder().firstName("Mr").lastName("Crabbs").function("Dinerbesitzer").mail("crabbs@meer.com").build())
        .client(Person.builder().firstName("Hai").lastName("Fisch").function("Schwimmer").mail("hai@meer.com").build())
        .build());

    assertThat(mailNotification.getFrom()).isEqualTo("no-reply-atlas@sbb.ch");
    assertThat(mailNotification.getSubject()).isEqualTo("Antrag zu ch:1:slnid:123 Linie 1 genehmigt");
    assertThat(mailNotification.getTemplateProperties().get(0).get("description")).isEqualTo("Linie 1");
    assertThat(mailNotification.getTemplateProperties().get(0).get("url")).isEqualTo(
        "http://localhost:4200/line-directory/lines/ch:1:slnid:123?id=1100");
  }
}