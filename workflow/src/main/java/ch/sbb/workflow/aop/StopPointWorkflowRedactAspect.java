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
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class StopPointWorkflowRedactAspect {

  private final BusinessOrganisationBasedUserAdministrationService businessOrganisationBasedUserAdministrationService;

  @Around("execution(* ch.sbb.workflow.repository.StopPointWorkflowRepository.find*(..))")
  public Object redactSensitiveData(final ProceedingJoinPoint joinPoint) throws Throwable {
    return doRedactSensitiveData(joinPoint);
  }

  private Object doRedactSensitiveData(ProceedingJoinPoint joinPoint) throws Throwable {
    Object proceed = joinPoint.proceed();

    if(proceed instanceof PageImpl<?>){
      final List<StopPointWorkflow> list = new ArrayList<>();
      try (Stream<StopPointWorkflow> stopPointWorkflowStream = ((PageImpl<StopPointWorkflow>) proceed).stream()) {
        stopPointWorkflowStream.forEach( stopPointWorkflow -> {
          if(!showConfidentialData(stopPointWorkflow.getSboid())){
            list.add(redact(stopPointWorkflow));
          }else {
            list.add(stopPointWorkflow);
          }
        });
      }
      return new PageImpl<>(list,((PageImpl<?>) proceed).getPageable(), ((PageImpl) proceed).getTotalElements());
    }
    if(proceed instanceof Optional<?>){
      StopPointWorkflow stopPointWorkflow = ((Optional<StopPointWorkflow>) proceed).get();
      if(!showConfidentialData(stopPointWorkflow.getSboid())){
        return Optional.of(redact(stopPointWorkflow));
      }
    }
    return proceed;
  }

  private StopPointWorkflow redact(StopPointWorkflow stopPointWorkflow){
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
