package ch.sbb.workflow.service.sepodi;

import ch.sbb.atlas.workflow.model.WorkflowStatus;
import ch.sbb.workflow.entity.Otp;
import ch.sbb.workflow.entity.Person;
import ch.sbb.workflow.entity.StopPointWorkflow;
import ch.sbb.workflow.exception.StopPointWorkflowExaminantNotFoundException;
import ch.sbb.workflow.exception.StopPointWorkflowNotInHearingException;
import ch.sbb.workflow.exception.StopPointWorkflowPinCodeInvalidException;
import ch.sbb.workflow.helper.OtpHelper;
import ch.sbb.workflow.kafka.StopPointWorkflowNotificationService;
import ch.sbb.workflow.repository.OtpRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional
public class StopPointWorkflowOtpService {

  private final OtpRepository otpRepository;
  private final StopPointWorkflowService workflowService;
  private final StopPointWorkflowNotificationService notificationService;

  public void obtainOtp(StopPointWorkflow stopPointWorkflow, String examinantMail) {
    if (stopPointWorkflow.getStatus() != WorkflowStatus.HEARING) {
      throw new StopPointWorkflowNotInHearingException();
    }

    Person examinant = getExaminantByMail(stopPointWorkflow.getId(), examinantMail);

    String pinCode = OtpHelper.generatePinCode();
    otpRepository.save(Otp.builder()
        .person(examinant)
        .code(OtpHelper.hashPinCode(pinCode))
        .build());

    notificationService.sendPinCodeMail(stopPointWorkflow, examinantMail, pinCode);
  }

  public void validatePinCode(Person person, String pinCode) {
    if (!isPinCodeValid(person, pinCode)) {
      throw new StopPointWorkflowPinCodeInvalidException();
    }
  }

  private boolean isPinCodeValid(Person person, String pinCode) {
    Otp otp = otpRepository.findByPersonId(person.getId());
    return otp.getCode().equals(OtpHelper.hashPinCode(pinCode));
  }

  public Person getExaminantByMail(Long workflowId, String examinantMail) {
    return workflowService.findStopPointWorkflow(workflowId)
        .getExaminants().stream()
        .filter(i -> i.getMail().equals(examinantMail))
        .findFirst().orElseThrow(StopPointWorkflowExaminantNotFoundException::new);
  }

}
