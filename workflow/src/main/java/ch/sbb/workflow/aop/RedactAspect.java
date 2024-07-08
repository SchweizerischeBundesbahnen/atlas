package ch.sbb.workflow.aop;

import ch.sbb.atlas.kafka.model.user.admin.ApplicationType;
import ch.sbb.atlas.service.UserService;
import ch.sbb.atlas.user.administration.security.service.BusinessOrganisationBasedUserAdministrationService;
import ch.sbb.workflow.entity.Decision;
import ch.sbb.workflow.entity.Person;
import ch.sbb.workflow.entity.StopPointWorkflow;
import ch.sbb.workflow.helper.StringHelper;
import ch.sbb.workflow.repository.StopPointWorkflowRepository;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class RedactAspect {

  private static final String ERROR_MSG = "You are redacting a wrong method!!";

  private final BusinessOrganisationBasedUserAdministrationService businessOrganisationBasedUserAdministrationService;
  private final StopPointWorkflowRepository stopPointWorkflowRepository;

  private static final Set<Class<?>> SUPPORTED_CLASSES = Set.of(StopPointWorkflow.class, Decision.class);

  @Around("@annotation(ch.sbb.workflow.aop.Redacted)")
  public Object redactSensitiveData(final ProceedingJoinPoint joinPoint) throws Throwable {
    validateIsRedactedMethodSupported(joinPoint);
    return redact(joinPoint);
  }

  Object redact(ProceedingJoinPoint joinPoint) throws Throwable {
    Object proceed = joinPoint.proceed();
    if (proceed instanceof StopPointWorkflow workflow) {
      return redactStopPointWorkflowSensitiveData(workflow);
    }
    if (proceed instanceof PageImpl page) {
      return redactStopPointPageSensitiveData(page);
    }
    if (proceed instanceof Decision decision) {
      return redactDecisionSensitiveData(decision);
    }
    throw new IllegalStateException(ERROR_MSG);
  }

  private static void validateIsRedactedMethodSupported(ProceedingJoinPoint joinPoint) {
    Class<?> redactedClassType = ((MethodSignature) joinPoint.getSignature()).getMethod().getAnnotation(Redacted.class)
        .redactedClassType();
    if (!SUPPORTED_CLASSES.contains(redactedClassType)) {
      throw new IllegalStateException("Redaction for Class " + redactedClassType.getName() + " not supported!");
    }
  }

  private StopPointWorkflow redactStopPointWorkflowSensitiveData(StopPointWorkflow stopPointWorkflow) {
    if (redactData(stopPointWorkflow.getSboid())) {
      return redactData(stopPointWorkflow);
    }
    return stopPointWorkflow;
  }

  private Page<StopPointWorkflow> redactStopPointPageSensitiveData(Page<StopPointWorkflow> page) {
    List<StopPointWorkflow> redactedPage = page.getContent().stream().map(this::redactStopPointWorkflowSensitiveData).toList();
    return new PageImpl<>(redactedPage, page.getPageable(), page.getTotalElements());
  }

  private StopPointWorkflow redactData(StopPointWorkflow stopPointWorkflow) {
    List<String> mails = stopPointWorkflow.getCcEmails().stream().map(StringHelper::redactString).toList();
    Set<Person> examinantsRedacted = new HashSet<>();
    stopPointWorkflow.getExaminants().forEach(person -> examinantsRedacted.add(redactPerson(person)));
    return stopPointWorkflow.toBuilder()
        .ccEmails(mails)
        .examinants(examinantsRedacted)
        .applicantMail(StringHelper.redactString(stopPointWorkflow.getApplicantMail()))
        .build();
  }

  private Person redactPerson(Person person) {
    if (person == null) {
      return null;
    }
    return person.toBuilder()
        .mail(StringHelper.redactString(person.getMail()))
        .firstName(StringHelper.redactString(person.getFirstName()))
        .lastName(StringHelper.redactString(person.getLastName()))
        .build();
  }

  private Decision redactDecisionSensitiveData(Decision decision) {
    StopPointWorkflow workflow = stopPointWorkflowRepository.findByDecisionId(decision.getId());
    if (redactData(workflow.getSboid())) {
      return decision.toBuilder()
          .examinant(redactPerson(decision.getExaminant()))
          .fotOverrider(redactPerson(decision.getFotOverrider()))
          .build();
    }
    return decision;
  }

  private boolean redactData(String sboid) {
    boolean hasPermission = businessOrganisationBasedUserAdministrationService.hasUserPermissionsForBusinessOrganisation(sboid,
        ApplicationType.SEPODI);
    boolean isUnauthorized = UserService.hasUnauthorizedRole();
    return !hasPermission || isUnauthorized;
  }

}
