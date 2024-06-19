package ch.sbb.workflow.service.sepodi;

import ch.sbb.atlas.workflow.model.WorkflowStatus;
import ch.sbb.workflow.entity.Otp;
import ch.sbb.workflow.entity.Person;
import ch.sbb.workflow.entity.StopPointWorkflow;
import ch.sbb.workflow.exception.StopPointWorkflowPinCodeInvalidException;
import ch.sbb.workflow.helper.OtpHelper;
import ch.sbb.workflow.model.sepodi.OtpVerificationModel;
import ch.sbb.workflow.repository.OtpRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional
public class StopPointWorkflowOtpService {

  private final OtpRepository otpRepository;
  private final StopPointWorkflowService workflowService;

  public void obtainOtp(StopPointWorkflow stopPointWorkflow, String examinantMail) {
    if (stopPointWorkflow.getStatus() == WorkflowStatus.HEARING) {
      Optional<Person> person = stopPointWorkflow.getExaminants().stream()
          .filter(p -> p.getMail().equals(examinantMail))
          .findFirst();
      if (person.isPresent()) {
        String pinCode = OtpHelper.generatePinCode();
        Otp otp = Otp.builder()
            .person(person.get())
            .code(OtpHelper.hashPinCode(pinCode))
            .build();
        otpRepository.save(otp);
        // Send mail with pin
      }
    }
  }

  public boolean isPinCodeValid(Long workflowId, OtpVerificationModel otpVerification) {
    Optional<Person> person = workflowService.findStopPointWorkflow(workflowId)
        .getExaminants().stream()
        .filter(i -> i.getMail().equals(otpVerification.getExaminantMail()))
        .findFirst();
    if (person.isPresent()) {
      Otp otp = otpRepository.findByPersonId(person.get().getId());
      return otp.getCode().equals(OtpHelper.hashPinCode(otpVerification.getPinCode()));
    }
    return false;
  }

  public void validatePinCode(Long workflowId, OtpVerificationModel otpVerification) {
    if (!isPinCodeValid(workflowId, otpVerification)) {
      throw new StopPointWorkflowPinCodeInvalidException();
    }
  }

}
