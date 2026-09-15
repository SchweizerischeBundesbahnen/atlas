package ch.sbb.workflow.module.sepodi.hearing.service;

import ch.sbb.workflow.entity.Person;
import ch.sbb.workflow.module.sepodi.hearing.enity.StopPointWorkflow;
import ch.sbb.workflow.module.sepodi.hearing.exception.StopPointWorkflowExaminantNotFoundException;
import ch.sbb.workflow.module.sepodi.hearing.exception.StopPointWorkflowPinCodeInvalidException;
import ch.sbb.workflow.module.sepodi.hearing.mail.StopPointWorkflowNotificationService;
import ch.sbb.workflow.module.sepodi.hearing.model.sepodi.OtpVerificationModel;
import ch.sbb.workflow.otp.entity.Otp;
import ch.sbb.workflow.otp.helper.OtpHelper;
import ch.sbb.workflow.otp.repository.OtpRepository;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Duration;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class StopPointWorkflowOtpService {

  private static final Duration OTP_LIFESPAN = Duration.ofMinutes(10);

  private final OtpRepository otpRepository;
  private final StopPointWorkflowService workflowService;
  private final StopPointWorkflowNotificationService notificationService;

  public void obtainOtp(StopPointWorkflow stopPointWorkflow, String examinantMail) {
    workflowService.validateIsStopPointInHearing(stopPointWorkflow);

    Person examinant = getExaminantByMail(stopPointWorkflow.getId(), examinantMail);

    String pinCode = OtpHelper.generatePinCode();
    savePinCode(examinant, pinCode);

    notificationService.sendPinCodeMail(stopPointWorkflow, examinantMail, pinCode);
  }

  public Person verifyExaminantPinCode(Long id, OtpVerificationModel verificationModel) {
    Person examinant = getExaminantByMail(id, verificationModel.getExaminantMail());
    validatePinCode(examinant, verificationModel.getPinCode());
    return examinant;
  }

  public void validatePinCode(Person person, String pinCode) {
    if (!isPinCodeValid(person, pinCode)) {
      throw new StopPointWorkflowPinCodeInvalidException();
    }
  }

  public Person getExaminantByMail(Long workflowId, String examinantMail) {
    return workflowService.findStopPointWorkflow(workflowId)
        .getExaminants().stream()
        .filter(i -> i.getMail().equalsIgnoreCase(examinantMail))
        .findFirst().orElseThrow(StopPointWorkflowExaminantNotFoundException::new);
  }

  private boolean isPinCodeValid(Person person, String pinCode) {
    Otp otp = otpRepository.findByPersonId(person.getId());
    if (otp == null) {
      log.info("Otp not found for workflow {}.", person.getStopPointWorkflow().getId());
      return false;
    }
    boolean stillValid = LocalDateTime.now().isBefore(otp.getCreationTime().plus(OTP_LIFESPAN));
    boolean codeMatches = MessageDigest.isEqual(
        otp.getCode().getBytes(StandardCharsets.UTF_8),
        OtpHelper.hashPinCode(pinCode).getBytes(StandardCharsets.UTF_8));
    log.info("Validating otp for workflow {}. Still valid: {}. Code matches: {}",
        person.getStopPointWorkflow().getId(), stillValid, codeMatches);
    return stillValid && codeMatches;
  }

  private void savePinCode(Person examinant, String pinCode) {
    Otp existingOtp = otpRepository.findByPersonId(examinant.getId());
    if (existingOtp == null) {
      otpRepository.save(Otp.builder()
          .person(examinant)
          .code(OtpHelper.hashPinCode(pinCode))
          .creationTime(LocalDateTime.now())
          .build());
    } else {
      existingOtp.setCode(OtpHelper.hashPinCode(pinCode));
      existingOtp.setCreationTime(LocalDateTime.now());
    }
  }

}
