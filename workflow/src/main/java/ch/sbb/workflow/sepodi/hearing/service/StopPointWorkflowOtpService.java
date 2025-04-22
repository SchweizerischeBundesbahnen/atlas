package ch.sbb.workflow.sepodi.hearing.service;

import ch.sbb.workflow.entity.Person;
import ch.sbb.workflow.exception.StopPointWorkflowExaminantNotFoundException;
import ch.sbb.workflow.exception.StopPointWorkflowPinCodeInvalidException;
import ch.sbb.workflow.otp.entity.Otp;
import ch.sbb.workflow.otp.helper.OtpHelper;
import ch.sbb.workflow.otp.repository.OtpRepository;
import ch.sbb.workflow.sepodi.hearing.enity.StopPointWorkflow;
import ch.sbb.workflow.sepodi.hearing.mail.StopPointWorkflowNotificationService;
import ch.sbb.workflow.sepodi.hearing.model.sepodi.OtpVerificationModel;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional
public class StopPointWorkflowOtpService {

  private static final int OTP_LIFESPAN_IN_MINUTES = 30;

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
    boolean stillValid = ChronoUnit.MINUTES.between(otp.getCreationTime(), LocalDateTime.now()) <= OTP_LIFESPAN_IN_MINUTES;
    boolean codeMatches = otp.getCode().equals(OtpHelper.hashPinCode(pinCode));
    return stillValid && codeMatches;
  }

  private void savePinCode(Person examinant, String pinCode) {
    Otp existingOtp = otpRepository.findByPersonId(examinant.getId());
    if (existingOtp == null) {
      otpRepository.save(Otp.builder()
          .person(examinant)
          .code(OtpHelper.hashPinCode(pinCode))
          .build());
    } else {
      existingOtp.setCode(OtpHelper.hashPinCode(pinCode));
      existingOtp.setCreationTime(LocalDateTime.now());
    }
  }

}
