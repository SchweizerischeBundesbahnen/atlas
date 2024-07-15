package ch.sbb.workflow.service.sepodi;

import static ch.sbb.atlas.kafka.model.mail.MailType.REJECT_STOP_POINT_WORKFLOW_NOTIFICATION;
import static ch.sbb.atlas.kafka.model.mail.MailType.START_STOP_POINT_WORKFLOW_CC_NOTIFICATION;
import static ch.sbb.atlas.kafka.model.mail.MailType.START_STOP_POINT_WORKFLOW_EXAMINANT_NOTIFICATION;
import static ch.sbb.atlas.kafka.model.mail.MailType.STOP_POINT_WORKFLOW_PINCODE_NOTIFICATION;
import static ch.sbb.atlas.kafka.model.mail.MailType.STOP_POINT_WORKFLOW_RESTART_CC_NOTIFICATION;
import static ch.sbb.atlas.kafka.model.mail.MailType.STOP_POINT_WORKFLOW_RESTART_NOTIFICATION;
import static ch.sbb.workflow.service.sepodi.StopPointWorkflowBuilderNotificationService.APPROVED_WORKFLOW_SUBJECT;
import static ch.sbb.workflow.service.sepodi.StopPointWorkflowBuilderNotificationService.CANCEL_WORKFLOW_SUBJECT;
import static ch.sbb.workflow.service.sepodi.StopPointWorkflowBuilderNotificationService.PINCODE_SUBJECT;
import static ch.sbb.workflow.service.sepodi.StopPointWorkflowBuilderNotificationService.REJECT_WORKFLOW_SUBJECT;
import static ch.sbb.workflow.service.sepodi.StopPointWorkflowBuilderNotificationService.RESTART_WORKFLOW_SUBJECT;
import static ch.sbb.workflow.service.sepodi.StopPointWorkflowBuilderNotificationService.START_WORKFLOW_SUBJECT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import ch.sbb.atlas.configuration.Role;
import ch.sbb.atlas.kafka.model.mail.MailNotification;
import ch.sbb.atlas.kafka.model.mail.MailType;
import ch.sbb.workflow.entity.Person;
import ch.sbb.workflow.entity.StopPointWorkflow;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

class StopPointWorkflowBuilderNotificationServiceTest {

  private StopPointWorkflowBuilderNotificationService notificationService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    notificationService = new StopPointWorkflowBuilderNotificationService();

    Authentication authentication = Mockito.mock(Authentication.class);
    Jwt jwt = Mockito.mock(Jwt.class);
    when(jwt.getClaim(Role.ROLES_JWT_KEY)).thenReturn(List.of("role1", "role2", "role3"));
    when(authentication.getPrincipal()).thenReturn(jwt);
    SecurityContext securityContext = Mockito.mock(SecurityContext.class);
    when(securityContext.getAuthentication()).thenReturn(authentication);
    SecurityContextHolder.setContext(securityContext);
  }


  @Test
  void shouldBuildWorkflowStartedExaminantMail() {
    //given
    StopPointWorkflow stopPointWorkflow = getStopPointWorkflow();
    //when
    MailNotification result = notificationService.buildWorkflowStartedExaminantMail(stopPointWorkflow);
    //then
    assertThat(result).isNotNull();
    assertThat(result.getMailType()).isEqualTo(START_STOP_POINT_WORKFLOW_EXAMINANT_NOTIFICATION);
    assertThat(result.getSubject()).isEqualTo(START_WORKFLOW_SUBJECT);
    assertThat(result.getTo()).hasSize(2).contains("p@a.ch","t@a.ch");
    assertThat(result.getCc()).isNull();
  }

  @Test
  void shouldBuildWorkflowStartedCCMail() {
    //given
    StopPointWorkflow stopPointWorkflow = getStopPointWorkflow();
    //when
    MailNotification result = notificationService.buildWorkflowStartedCCMail(stopPointWorkflow);
    //then
    assertThat(result).isNotNull();
    assertThat(result.getMailType()).isEqualTo(START_STOP_POINT_WORKFLOW_CC_NOTIFICATION);
    assertThat(result.getSubject()).isEqualTo(START_WORKFLOW_SUBJECT);
    assertThat(result.getTo()).hasSize(3).contains("a@b.ch","b@c.dh","app@lica.ma");
    assertThat(result.getCc()).isNull();
  }

  @Test
  void shouldBuildWorkflowRejectMail() {
    //given
    StopPointWorkflow stopPointWorkflow = getStopPointWorkflow();
    //when
    MailNotification result = notificationService.buildWorkflowRejectMail(stopPointWorkflow, "reject comment");
    //then
    assertThat(result).isNotNull();
    assertThat(result.getMailType()).isEqualTo(REJECT_STOP_POINT_WORKFLOW_NOTIFICATION);
    assertThat(result.getSubject()).isEqualTo(REJECT_WORKFLOW_SUBJECT);
    assertThat(result.getTo()).hasSize(1).contains("app@lica.ma");
    assertThat(result.getCc()).hasSize(4).contains("a@b.ch", "b@c.dh", "p@a.ch", "t@a.ch");
  }

  @Test
  void shouldContainsBuildMailProperties() {
    //given
    StopPointWorkflow stopPointWorkflow = getStopPointWorkflow();
    //when
    List<Map<String, Object>> result = notificationService.buildMailProperties(stopPointWorkflow, REJECT_WORKFLOW_SUBJECT);
    //then
    assertThat(result).hasSize(1);
    Map<String, Object> properties = result.getFirst();
    assertThat(properties).hasSize(6).containsKeys("title", "designationOfficial", "sloid", "comment",
        "endDate", "url");
  }

  @Test
  void shouldBuildWorkflowPinCodeMail() {
    //given
    StopPointWorkflow stopPointWorkflow = getStopPointWorkflow();
    //when
    MailNotification result = notificationService.buildPinCodeMail(stopPointWorkflow, "luca@bayern.munchen", "648966");
    //then
    assertThat(result).isNotNull();
    assertThat(result.getMailType()).isEqualTo(STOP_POINT_WORKFLOW_PINCODE_NOTIFICATION);
    assertThat(result.getSubject()).isEqualTo(PINCODE_SUBJECT);
    assertThat(result.getTo()).hasSize(1).contains("luca@bayern.munchen");
    assertThat(result.getCc()).isNull();
    assertThat(result.getTemplateProperties().getFirst()).containsKeys("pincode");
  }

  @Test
  void shouldBuildWorkflowApprovedMail() {
    //given
    StopPointWorkflow stopPointWorkflow = getStopPointWorkflow();
    //when
    MailNotification result = notificationService.buildWorkflowApprovedMail(stopPointWorkflow);
    //then
    assertThat(result).isNotNull();
    assertThat(result.getMailType()).isEqualTo(MailType.APPROVED_STOP_POINT_WORKFLOW_NOTIFICATION);
    assertThat(result.getSubject()).isEqualTo(APPROVED_WORKFLOW_SUBJECT);
    assertThat(result.getTo()).hasSize(3);
    assertThat(result.getCc()).hasSize(2);
  }

  @Test
  void shouldBuildWorkflowCancelMail() {
    //given
    StopPointWorkflow stopPointWorkflow = getStopPointWorkflow();
    //when
    MailNotification result = notificationService.buildWorkflowCanceledMail(stopPointWorkflow, "cancel");
    //then
    assertThat(result).isNotNull();
    assertThat(result.getMailType()).isEqualTo(MailType.CANCEL_STOP_POINT_WORKFLOW_NOTIFICATION);
    assertThat(result.getSubject()).isEqualTo(CANCEL_WORKFLOW_SUBJECT);
    assertThat(result.getTo()).hasSize(3);
    assertThat(result.getCc()).hasSize(2);
  }

  @Test
  void shouldBuildWorkflowRestartedMail() {
    //given
    StopPointWorkflow existingStopPointWorkflow = getStopPointWorkflow();
    StopPointWorkflow newStopPointWorkflow = getStopPointWorkflow();
    newStopPointWorkflow.setDesignationOfficial("Bern 1");
    //when
    MailNotification result = notificationService.buildWorkflowRestartedMail(existingStopPointWorkflow, newStopPointWorkflow);
    //then
    assertThat(result).isNotNull();
    assertThat(result.getMailType()).isEqualTo(STOP_POINT_WORKFLOW_RESTART_NOTIFICATION);
    assertThat(result.getSubject()).isEqualTo(RESTART_WORKFLOW_SUBJECT);
    assertThat(result.getTo()).hasSize(2).contains("p@a.ch","t@a.ch");
    assertThat(result.getCc()).isNull();
  }

  @Test
  void shouldBuildWorkflowRestartedCCMail() {
    //given
    StopPointWorkflow existingStopPointWorkflow = getStopPointWorkflow();
    StopPointWorkflow newStopPointWorkflow = getStopPointWorkflow();
    newStopPointWorkflow.setDesignationOfficial("Bern 1");
    //when
    MailNotification result = notificationService.buildWorkflowRestartedCCMail(existingStopPointWorkflow, newStopPointWorkflow);
    //then
    assertThat(result).isNotNull();
    assertThat(result.getMailType()).isEqualTo(STOP_POINT_WORKFLOW_RESTART_CC_NOTIFICATION);
    assertThat(result.getSubject()).isEqualTo(RESTART_WORKFLOW_SUBJECT);
    assertThat(result.getTo()).hasSize(3).contains("a@b.ch","b@c.dh","app@lica.ma");
    assertThat(result.getCc()).isNull();
  }

  private static StopPointWorkflow getStopPointWorkflow() {
    Person person1 = Person.builder().organisation("PostAuto").mail("p@a.ch").firstName("Ciovanni").firstName("Ciannini")
        .function("ploischler").build();
    Person person2 = Person.builder().organisation("PostAuto").mail("t@a.ch").firstName("Tscanni").firstName("Versacce")
        .function("ploischler").build();

    List<String> ccMails = new ArrayList<>();
    ccMails.add("a@b.ch");
    ccMails.add("b@c.dh");
    return StopPointWorkflow.builder()
        .versionValidFrom(LocalDate.of(2000, 1, 1))
        .designationOfficial("Bern")
        .localityName("Bern")
        .sboid("ch:1:sloid:7000")
        .applicantMail("app@lica.ma")
        .endDate(LocalDate.of(2000, 2, 1))
        .startDate(LocalDate.of(2000, 1, 1))
        .versionId(123456L)
        .ccEmails(ccMails)
        .workflowComment("Forza Napoli")
        .examinants(Set.of(person1, person2))
        .build();
  }

}