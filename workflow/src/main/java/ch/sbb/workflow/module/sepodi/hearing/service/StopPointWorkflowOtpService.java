package ch.sbb.workflow.module.sepodi.hearing.service;

import ch.sbb.workflow.entity.Person;
import ch.sbb.workflow.module.sepodi.hearing.enity.StopPointWorkflow;
import ch.sbb.workflow.module.sepodi.hearing.exception.StopPointWorkflowExaminantNotFoundException;
import ch.sbb.workflow.module.sepodi.hearing.exception.StopPointWorkflowPinCodeInvalidException;
import ch.sbb.workflow.module.sepodi.hearing.mail.StopPointWorkflowNotificationService;
import ch.sbb.workflow.module.sepodi.hearing.model.sepodi.OtpResponseModel;
import ch.sbb.workflow.module.sepodi.hearing.model.sepodi.OtpVerificationModel;
import ch.sbb.workflow.otp.entity.Otp;
import ch.sbb.workflow.otp.helper.OtpHelper;
import ch.sbb.workflow.otp.repository.OtpRepository;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
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

  private final OtpRepository otpRepository;
  private final StopPointWorkflowService workflowService;
  private final StopPointWorkflowNotificationService notificationService;

  public OtpResponseModel obtainOtp(StopPointWorkflow stopPointWorkflow, String examinantMail) {
    workflowService.validateIsStopPointInHearing(stopPointWorkflow);

    Person examinant = getExaminantByMail(stopPointWorkflow.getId(), examinantMail);

    Otp existingOtp = otpRepository.findByPersonId(examinant.getId());
    if (existingOtp != null && existingOtp.isStillValid()) {
      log.info("Otp for workflow {} is still valid. No new pin code mail sent.", stopPointWorkflow.getId());
      return OtpResponseModel.builder()
          .mailSent(false)
          .expiresInSeconds(existingOtp.getExpiresInSeconds())
          .build();
    }

    String pinCode = OtpHelper.generatePinCode();
    Otp otp = savePinCode(examinant, existingOtp, pinCode);

    notificationService.sendPinCodeMail(stopPointWorkflow, examinantMail, pinCode);

    return OtpResponseModel.builder()
        .mailSent(true)
        .expiresInSeconds(otp.getExpiresInSeconds())
        .build();
  }

  public Person verifyExaminantPinCode(Long id, OtpVerificationModel verificationModel) {
    Person examinant = getExaminantByMail(id, verificationModel.getExaminantMail());
    validatePinCode(examinant, verificationModel.getPinCode());
    return examinant;
  }

  public Person verifyExaminantPinCode(Long workflowId, Long personId, OtpVerificationModel verificationModel) {
    Person examinant = getExaminantById(workflowId, personId);
    validateExaminantMail(examinant, verificationModel.getExaminantMail());
    validatePinCode(examinant, verificationModel.getPinCode());
    return examinant;
  }

  public Person getExaminantById(Long workflowId, Long personId) {
    return workflowService.findStopPointWorkflow(workflowId)
        .getExaminants().stream()
        .filter(examinant -> examinant.getId().equals(personId))
        .findFirst().orElseThrow(StopPointWorkflowExaminantNotFoundException::new);
  }

  private void validateExaminantMail(Person examinant, String examinantMail) {
    if (examinant.getMail() == null || !examinant.getMail().equalsIgnoreCase(examinantMail)) {
      log.info("Examinant mail does not match examinant {} of workflow {}.", examinant.getId(),
          examinant.getStopPointWorkflow().getId());
      throw new StopPointWorkflowPinCodeInvalidException();
    }
  }

  public void invalidateOtp(Person examinant) {
    Otp otp = otpRepository.findByPersonId(examinant.getId());
    if (otp != null) {
      otpRepository.delete(otp);
    }
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
    boolean stillValid = otp.isStillValid();
    boolean codeMatches = MessageDigest.isEqual(
        otp.getCode().getBytes(StandardCharsets.UTF_8),
        OtpHelper.hashPinCode(pinCode).getBytes(StandardCharsets.UTF_8));
    log.info("Validating otp for workflow {}. Still valid: {}. Code matches: {}",
        person.getStopPointWorkflow().getId(), stillValid, codeMatches);
    return stillValid && codeMatches;
  }

  private Otp savePinCode(Person examinant, Otp existingOtp, String pinCode) {
    if (existingOtp == null) {
      return otpRepository.save(Otp.builder()
          .person(examinant)
          .code(OtpHelper.hashPinCode(pinCode))
          .creationTime(LocalDateTime.now())
          .build());
    }
    existingOtp.setCode(OtpHelper.hashPinCode(pinCode));
    existingOtp.setCreationTime(LocalDateTime.now());
    return existingOtp;
  }

}
