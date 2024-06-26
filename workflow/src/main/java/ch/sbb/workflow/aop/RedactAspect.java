package ch.sbb.workflow.aop;

import ch.sbb.atlas.kafka.model.user.admin.ApplicationType;
import ch.sbb.atlas.service.UserService;
import ch.sbb.atlas.user.administration.security.service.BusinessOrganisationBasedUserAdministrationService;
import ch.sbb.workflow.entity.Person;
import ch.sbb.workflow.entity.StopPointWorkflow;
import ch.sbb.workflow.helper.StringHelper;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class RedactAspect {

  private static final String ERROR_MSG = "You are redacting a wrong method!!";

  private final BusinessOrganisationBasedUserAdministrationService businessOrganisationBasedUserAdministrationService;

  @Around("@annotation(ch.sbb.workflow.aop.Redacted)")
  public Object redactSensitiveData(final ProceedingJoinPoint joinPoint) throws Throwable {
    validateIsRedactedMethodSupported(joinPoint);
    return redact(joinPoint);
  }

  Object redact(ProceedingJoinPoint joinPoint) throws Throwable {
    Object proceed = joinPoint.proceed();
    if (proceed instanceof StopPointWorkflow) {
      return redactStopPointSensitiveData(joinPoint);
    }
    if (proceed instanceof PageImpl) {
      return redactStopPointPageSensitiveData(joinPoint);
    }
    throw new IllegalStateException(ERROR_MSG);
  }

  private static void validateIsRedactedMethodSupported(ProceedingJoinPoint joinPoint) {
    Class<?> redactedClassType = ((MethodSignature) joinPoint.getSignature()).getMethod().getAnnotation(Redacted.class)
        .redactedClassType();
    if (!StopPointWorkflow.class.equals(redactedClassType)) {
      throw new IllegalStateException("Redaction for Class " + redactedClassType.getName() + " not supported!");
    }
  }

  StopPointWorkflow redactStopPointSensitiveData(final ProceedingJoinPoint joinPoint) throws Throwable {
    Object proceed = joinPoint.proceed();
    if (proceed instanceof StopPointWorkflow stopPointWorkflow) {
      if (!showConfidentialData(stopPointWorkflow.getSboid())) {
        return redactData(stopPointWorkflow);
      }
      return stopPointWorkflow;
    }
    throw new IllegalStateException(ERROR_MSG);
  }

  PageImpl<StopPointWorkflow> redactStopPointPageSensitiveData(final ProceedingJoinPoint joinPoint) throws Throwable {
    Object proceed = joinPoint.proceed();
    if (proceed instanceof PageImpl) {
      final List<StopPointWorkflow> list = new ArrayList<>();
      try (Stream<StopPointWorkflow> stopPointWorkflowStream = ((PageImpl<StopPointWorkflow>) proceed).stream()) {
        stopPointWorkflowStream.forEach(stopPointWorkflow -> {
          if (!showConfidentialData(stopPointWorkflow.getSboid())) {
            list.add(redactData(stopPointWorkflow));
          } else {
            list.add(stopPointWorkflow);
          }
        });
      }
      return new PageImpl<>(list, ((PageImpl<?>) proceed).getPageable(), ((PageImpl) proceed).getTotalElements());
    }
    throw new IllegalStateException(ERROR_MSG);
  }

  private StopPointWorkflow redactData(StopPointWorkflow stopPointWorkflow) {
    List<String> mails = stopPointWorkflow.getCcEmails().stream().map(StringHelper::redactString).toList();
    Set<Person> examinantsRedacted = new HashSet<>();
    stopPointWorkflow.getExaminants().forEach(person -> examinantsRedacted.add(
        person.toBuilder()
            .mail(StringHelper.redactString(person.getMail()))
            .firstName(StringHelper.redactString(person.getFirstName()))
            .lastName(StringHelper.redactString(person.getLastName()))
            .build()));
    return stopPointWorkflow.toBuilder()
        .ccEmails(mails)
        .examinants(examinantsRedacted)
        .applicantMail(StringHelper.redactString(stopPointWorkflow.getApplicantMail()))
        .build();
  }

  boolean showConfidentialData(String sboid) {
    boolean hasPermission = businessOrganisationBasedUserAdministrationService
        .hasUserPermissionsForBusinessOrganisation(sboid, ApplicationType.SEPODI);
    boolean isUnauthorized = UserService.hasUnauthorizedRole();
    return hasPermission && !isUnauthorized;
  }

}
