package ch.sbb.workflow.module.sepodi.hearing.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.atlas.workflow.model.WorkflowStatus;
import ch.sbb.workflow.entity.Person;
import ch.sbb.workflow.module.sepodi.hearing.enity.StopPointWorkflow;
import ch.sbb.workflow.module.sepodi.hearing.exception.StopPointWorkflowExaminantNotFoundException;
import ch.sbb.workflow.module.sepodi.hearing.exception.StopPointWorkflowNotInHearingException;
import ch.sbb.workflow.module.sepodi.hearing.exception.StopPointWorkflowPinCodeInvalidException;
import ch.sbb.workflow.module.sepodi.hearing.mail.StopPointWorkflowNotificationService;
import ch.sbb.workflow.module.sepodi.hearing.repository.StopPointWorkflowRepository;
import ch.sbb.workflow.otp.entity.Otp;
import ch.sbb.workflow.otp.repository.OtpRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@IntegrationTest
@ExtendWith(MockitoExtension.class)
class StopPointWorkflowOtpServiceTest {

  private static final String MAIL_ADDRESS = "marek@hamsik.com";

  @Autowired
  private StopPointWorkflowOtpService stopPointWorkflowOtpService;

  @Autowired
  private StopPointWorkflowRepository workflowRepository;

  @Autowired
  private OtpRepository otpRepository;

  @Autowired
  JdbcTemplate jdbcTemplate;

  @MockitoBean
  private StopPointWorkflowNotificationService notificationService;

  @Captor
  private ArgumentCaptor<String> pincodeCaptor;

  private StopPointWorkflow workflowInHearing;

  @AfterEach
  void tearDown() {
    otpRepository.deleteAll();
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
  void shouldNotObtainOtpIfNotInHearing() {
    workflowInHearing.setStatus(WorkflowStatus.REJECTED);
    workflowInHearing = workflowRepository.save(workflowInHearing);

    assertThatExceptionOfType(StopPointWorkflowNotInHearingException.class).isThrownBy(
        () -> stopPointWorkflowOtpService.obtainOtp(workflowInHearing, MAIL_ADDRESS));
  }

  @Test
  void shouldNotObtainOtpIfMailWasIncorrect() {
    assertThatExceptionOfType(StopPointWorkflowExaminantNotFoundException.class).isThrownBy(
        () -> stopPointWorkflowOtpService.obtainOtp(workflowInHearing, "invalid@mail.ch"));
  }

  @Test
  void shouldObtainOtpCorrectly() {
    stopPointWorkflowOtpService.obtainOtp(workflowInHearing, MAIL_ADDRESS);

    verify(notificationService).sendPinCodeMail(any(), anyString(), pincodeCaptor.capture());
    assertThat(pincodeCaptor.getValue()).isNotNull();
  }

  @Test
  void shouldReplaceObtainedOtpOnSecondRequest() {
    // first pin request
    stopPointWorkflowOtpService.obtainOtp(workflowInHearing, MAIL_ADDRESS);

    Otp otp = otpRepository.findByPersonId(workflowInHearing.getExaminants().iterator().next().getId());
    assertThat(otp.getCode()).isNotNull();
    String firstHashedPin = otp.getCode();

    // second pin request
    stopPointWorkflowOtpService.obtainOtp(workflowInHearing, MAIL_ADDRESS);

    verify(notificationService, times(2)).sendPinCodeMail(any(), anyString(), anyString());

    otp = otpRepository.findByPersonId(workflowInHearing.getExaminants().iterator().next().getId());
    assertThat(otp.getCode()).isNotNull();
    assertThat(firstHashedPin).isNotEqualTo(otp.getCode());
  }

  @Test
  void shouldValidatePinSuccessfully() {
    stopPointWorkflowOtpService.obtainOtp(workflowInHearing, MAIL_ADDRESS);
    verify(notificationService).sendPinCodeMail(any(), anyString(), pincodeCaptor.capture());

    Person examinant = stopPointWorkflowOtpService.getExaminantByMail(workflowInHearing.getId(),
        MAIL_ADDRESS);
    assertThatNoException().isThrownBy(() -> stopPointWorkflowOtpService.validatePinCode(examinant, pincodeCaptor.getValue()));
  }

  @Test
  void shouldValidatePinUnsuccessfully() {
    stopPointWorkflowOtpService.obtainOtp(workflowInHearing, MAIL_ADDRESS);
    verify(notificationService).sendPinCodeMail(any(), anyString(), pincodeCaptor.capture());

    Person examinant = stopPointWorkflowOtpService.getExaminantByMail(workflowInHearing.getId(),
        MAIL_ADDRESS);
    assertThatExceptionOfType(StopPointWorkflowPinCodeInvalidException.class).isThrownBy(
        () -> stopPointWorkflowOtpService.validatePinCode(examinant, "101010"));
  }

  @Test
  void shouldValidatePinUnsuccessfullyIfTimedOut() {
    //given
    stopPointWorkflowOtpService.obtainOtp(workflowInHearing, MAIL_ADDRESS);
    verify(notificationService).sendPinCodeMail(any(), anyString(), pincodeCaptor.capture());

    Person examinant = stopPointWorkflowOtpService.getExaminantByMail(workflowInHearing.getId(),
        MAIL_ADDRESS);

    Otp otp = otpRepository.findByPersonId(examinant.getId());
    LocalDateTime past = LocalDateTime.now().minusHours(1);

    jdbcTemplate.update(
        "update otp set creation_time = ? where id = ?",
        past,
        otp.getId()
    );

    Otp reloaded = otpRepository.findByPersonId(examinant.getId());
    assertThat(reloaded.getCreationTime()).isBefore(LocalDateTime.now().minusHours(1));

    // when & then
    assertThatExceptionOfType(StopPointWorkflowPinCodeInvalidException.class).isThrownBy(
        () -> stopPointWorkflowOtpService.validatePinCode(examinant, pincodeCaptor.getValue()));
  }

  @Test
  void shouldUpdateOtpCreationTimeForNewOne() {
    //given creation time an hour ago
    stopPointWorkflowOtpService.obtainOtp(workflowInHearing, MAIL_ADDRESS);
    Person examinant = stopPointWorkflowOtpService.getExaminantByMail(workflowInHearing.getId(),
        MAIL_ADDRESS);

    Otp otp = otpRepository.findByPersonId(examinant.getId());

    jdbcTemplate.update(
        "update otp set creation_time = ? where id = ?",
        LocalDateTime.now().minusHours(1),
        otp.getId()
    );

    // obtaining second otp
    stopPointWorkflowOtpService.obtainOtp(workflowInHearing, MAIL_ADDRESS);
    verify(notificationService, times(2)).sendPinCodeMail(any(), anyString(), pincodeCaptor.capture());

    // when & then
    assertThatNoException().isThrownBy(
        () -> stopPointWorkflowOtpService.validatePinCode(examinant, pincodeCaptor.getValue()));

    Otp renewedOtp = otpRepository.findByPersonId(examinant.getId());
    assertThat(renewedOtp.getCreationTime()).isAfter(LocalDateTime.now().minusMinutes(5));
  }
}
