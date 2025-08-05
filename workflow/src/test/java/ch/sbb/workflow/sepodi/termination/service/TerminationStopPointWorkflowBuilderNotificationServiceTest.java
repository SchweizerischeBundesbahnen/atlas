package ch.sbb.workflow.sepodi.termination.service;

import static ch.sbb.atlas.kafka.model.mail.MailType.ABORT_TERMINATION_NOTIFICATION;
import static ch.sbb.atlas.kafka.model.mail.MailType.START_TERMINATION_STOP_POINT_WORKFLOW_NOTIFICATION;
import static ch.sbb.atlas.kafka.model.mail.MailType.TARIFF_STOP_APPROVED_NOTIFICATION;
import static ch.sbb.atlas.kafka.model.mail.MailType.TARIFF_STOP_NOT_APPROVED_NOTIFICATION;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import ch.sbb.atlas.kafka.model.mail.MailNotification;
import ch.sbb.workflow.sepodi.hearing.enity.JudgementType;
import ch.sbb.workflow.sepodi.termination.entity.TerminationDecision;
import ch.sbb.workflow.sepodi.termination.entity.TerminationDecisionPerson;
import ch.sbb.workflow.sepodi.termination.entity.TerminationStopPointWorkflow;
import ch.sbb.workflow.sepodi.termination.entity.TerminationWorkflowStatus;
import ch.sbb.workflow.sepodi.termination.model.TerminationExaminants;
import ch.sbb.workflow.sepodi.termination.model.TerminationExaminants.InfoPlus;
import ch.sbb.workflow.sepodi.termination.model.TerminationExaminants.Nova;
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
        .infoPlusDecision(TerminationDecision.builder()
            .terminationDecisionPerson(TerminationDecisionPerson.INFO_PLUS).judgement(JudgementType.YES).build())
        .novaTerminationDate(LocalDate.of(2000, 1, 3))
        .novaDecision(TerminationDecision.builder()
            .terminationDecisionPerson(TerminationDecisionPerson.NOVA).judgement(JudgementType.YES).build())
        .applicantMail("a@b.com")
        .designationOfficial("Heimsiswil Zentrum")
        .sboid("ch:sboid:1")
        .status(TerminationWorkflowStatus.STARTED)
        .build();
    when(terminationExaminants.getInfoPlus()).thenReturn(InfoPlus.builder().email(mail).build());
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

  @Test
  void shouldBuildTariffStopNotApprovedNotification() {
    //given
    TerminationStopPointWorkflow terminationStopPointWorkflow = TerminationStopPointWorkflow.builder()
        .sloid("ch:1:sloid:1")
        .versionId(1234L)
        .boTerminationDate(LocalDate.of(2000, 1, 1))
        .infoPlusTerminationDate(LocalDate.of(2000, 1, 2))
        .infoPlusDecision(TerminationDecision.builder()
            .terminationDecisionPerson(TerminationDecisionPerson.INFO_PLUS).judgement(JudgementType.YES).build())
        .novaTerminationDate(LocalDate.of(2000, 1, 3))
        .novaDecision(TerminationDecision.builder()
            .terminationDecisionPerson(TerminationDecisionPerson.NOVA).judgement(JudgementType.YES).build())
        .applicantMail("a@b.com")
        .designationOfficial("Heimsiswil Zentrum")
        .sboid("ch:sboid:1")
        .status(TerminationWorkflowStatus.STARTED)
        .build();
    when(terminationExaminants.getInfoPlus()).thenReturn(InfoPlus.builder().email("a@b-ch").build());
    //when
    MailNotification result = builderNotificationService.buildTariffStopNotApprovedNotification(
        terminationStopPointWorkflow, "No");
    //then
    assertThat(result).isNotNull();
    assertThat(result.getMailType()).isEqualTo(TARIFF_STOP_NOT_APPROVED_NOTIFICATION);
    assertThat(result.getSubject()).isEqualTo(TerminationWorkflowSubject.TARIFF_STOP_NOT_APPROVED_SUBJECT);
    assertThat(result.getTo()).hasSize(1).contains("a@b.com");
    assertThat(result.getCc()).isNull();
  }

  @Test
  void shouldBuildTariffStopApprovedNotification() {
    //given
    TerminationStopPointWorkflow terminationStopPointWorkflow = TerminationStopPointWorkflow.builder()
        .sloid("ch:1:sloid:1")
        .versionId(1234L)
        .boTerminationDate(LocalDate.of(2000, 1, 1))
        .infoPlusTerminationDate(LocalDate.of(2000, 1, 2))
        .infoPlusDecision(TerminationDecision.builder()
            .terminationDecisionPerson(TerminationDecisionPerson.INFO_PLUS).judgement(JudgementType.YES).build())
        .novaTerminationDate(LocalDate.of(2000, 1, 3))
        .novaDecision(TerminationDecision.builder()
            .terminationDecisionPerson(TerminationDecisionPerson.NOVA).judgement(JudgementType.YES).build())
        .applicantMail("a@b.com")
        .designationOfficial("Heimsiswil Zentrum")
        .sboid("ch:sboid:1")
        .status(TerminationWorkflowStatus.STARTED)
        .build();
    when(terminationExaminants.getNova()).thenReturn(Nova.builder().email("a@b-ch").build());
    //when
    MailNotification result = builderNotificationService.buildTariffStopApprovedNotification(
        terminationStopPointWorkflow);
    //then
    assertThat(result).isNotNull();
    assertThat(result.getMailType()).isEqualTo(TARIFF_STOP_APPROVED_NOTIFICATION);
    assertThat(result.getSubject()).isEqualTo(TerminationWorkflowSubject.TARIFF_STOP_APPROVED_SUBJECT);
    assertThat(result.getTo()).hasSize(1).contains("a@b-ch");
    assertThat(result.getCc()).isNull();
  }

  @Test
  void shouldBuildAbortNotification() {
    //given
    TerminationStopPointWorkflow terminationStopPointWorkflow = TerminationStopPointWorkflow.builder()
        .sloid("ch:1:sloid:1")
        .versionId(1234L)
        .boTerminationDate(LocalDate.of(2000, 1, 1))
        .infoPlusTerminationDate(LocalDate.of(2000, 1, 2))
        .infoPlusDecision(TerminationDecision.builder()
            .terminationDecisionPerson(TerminationDecisionPerson.INFO_PLUS).judgement(JudgementType.YES).build())
        .novaTerminationDate(LocalDate.of(2000, 1, 3))
        .novaDecision(TerminationDecision.builder()
            .terminationDecisionPerson(TerminationDecisionPerson.NOVA).judgement(JudgementType.YES).build())
        .applicantMail("a@b.com")
        .designationOfficial("Heimsiswil Zentrum")
        .sboid("ch:sboid:1")
        .status(TerminationWorkflowStatus.STARTED)
        .build();
    when(terminationExaminants.getInfoPlus()).thenReturn(InfoPlus.builder().email("a@b-ch").build());
    //when
    MailNotification result = builderNotificationService.buildAbortNotification(
        terminationStopPointWorkflow);
    //then
    assertThat(result).isNotNull();
    assertThat(result.getMailType()).isEqualTo(ABORT_TERMINATION_NOTIFICATION);
    assertThat(result.getSubject()).isEqualTo(TerminationWorkflowSubject.ABORT_TERMINATION_SUBJECT);
    assertThat(result.getTo()).hasSize(2).containsExactlyInAnyOrder("a@b-ch", "a@b.com");
    assertThat(result.getCc()).isNull();
  }
}