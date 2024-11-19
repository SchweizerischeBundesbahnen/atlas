package ch.sbb.workflow.aop;

import ch.sbb.atlas.kafka.model.user.admin.ApplicationType;
import ch.sbb.atlas.service.UserService;
import ch.sbb.atlas.user.administration.security.aspect.AopUtils;
import ch.sbb.atlas.user.administration.security.aspect.AopUtils.ParameterInfo;
import ch.sbb.atlas.user.administration.security.service.BusinessOrganisationBasedUserAdministrationService;
import ch.sbb.workflow.entity.Decision;
import ch.sbb.workflow.entity.Person;
import ch.sbb.workflow.entity.StopPointWorkflow;
import ch.sbb.workflow.helper.StringHelper;
import ch.sbb.workflow.repository.StopPointWorkflowRepository;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

@Aspect
@Component
@RequiredArgsConstructor
public class RedactAspect {

  private static final String ERROR_MSG = "You are redacting a wrong method!!";

  private final BusinessOrganisationBasedUserAdministrationService businessOrganisationBasedUserAdministrationService;
  private final StopPointWorkflowRepository stopPointWorkflowRepository;

  @Around("@annotation(ch.sbb.workflow.aop.Redacted)")
  public Object redactSensitiveData(final ProceedingJoinPoint joinPoint) throws Throwable {
    return redact(joinPoint);
  }

  Object redact(ProceedingJoinPoint joinPoint) throws Throwable {
    Object proceed = joinPoint.proceed();

    boolean redact = shouldRedact(joinPoint);

    if (!redact) {
      return proceed;
    }

    if (proceed instanceof Page<?> page) {
      page.forEach(this::redactObject);
      return page;
    } else {
      return this.redactObject(proceed);
    }
  }

  private boolean shouldRedact(ProceedingJoinPoint joinPoint) {
    Optional<ParameterInfo<RedactBySboid>> redactBySboidParameterInfo = AopUtils.resolveParameterInfoByAnnotation(joinPoint,
        RedactBySboid.class);
    if (redactBySboidParameterInfo.isPresent()) {
      ApplicationType application = redactBySboidParameterInfo.get().getAnnotation().application();
      String sboid = redactBySboidParameterInfo.get().getValueAsString();

      return redactData(sboid, application);
    } else {
      return UserService.hasUnauthorizedRole();
    }
  }

  Object redactObject(Object object) {
    if (object == null) {
      return null;
    }
    if (object.getClass().isAnnotationPresent(Redacted.class)) {

      object = getObjectCopy(object);

      for (Field field : object.getClass().getDeclaredFields()) {
        if (field.isAnnotationPresent(Redacted.class) && field.getType().isAnnotationPresent(Redacted.class)) {
          Object fieldObject = ReflectionUtils.getField(field, object);
          redactObject(fieldObject);
        } else if (field.isAnnotationPresent(Redacted.class)) {
          redactField(field, object);
        }
      }
      return object;
    } else {
      throw new IllegalStateException("Class not configured for redact");
    }
  }

  private static Object getObjectCopy(Object object) {
    try {
      Method toBuilder = object.getClass().getDeclaredMethod("toBuilder");
      toBuilder.setAccessible(true);
      Object builder = toBuilder.invoke(object);
      Method build = builder.getClass().getMethod("build");
      build.setAccessible(true);
      return build.invoke(builder);
    } catch (Exception e) {
      throw new IllegalStateException(
          "Could not invoke .toBuilder().build() for Object copy on " + object.getClass().getSimpleName(), e);
    }
  }

  void redactField(Field field,Object object){
    ReflectionUtils.makeAccessible(field);
    Object currentFieldValue = ReflectionUtils.getField(field, object);
    Redacted redacted = field.getAnnotation(Redacted.class);

    if (currentFieldValue instanceof String stringValue) {
      ReflectionUtils.setField(field, object, StringHelper.redactString(stringValue, redacted.showFirstChar()));
    }
    if (currentFieldValue instanceof List<?> list) {
      List<String> redactedList = list.stream().map(i -> StringHelper.redactString((String) i, redacted.showFirstChar())).toList();
      ReflectionUtils.setField(field, object, redactedList);
    }
  }



  private StopPointWorkflow redactStopPointWorkflowSensitiveData(StopPointWorkflow stopPointWorkflow) {
    if (redactData(stopPointWorkflow.getSboid(), ApplicationType.SEPODI)) {
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
    if (redactData(workflow.getSboid(), ApplicationType.SEPODI)) {
      return decision.toBuilder()
          .examinant(redactPerson(decision.getExaminant()))
          .fotOverrider(redactPerson(decision.getFotOverrider()))
          .build();
    }
    return decision;
  }

  private boolean redactData(String sboid, ApplicationType application) {
    boolean hasPermission = businessOrganisationBasedUserAdministrationService.hasUserPermissionsForBusinessOrganisation(sboid,application);
    boolean isUnauthorized = UserService.hasUnauthorizedRole();
    return !hasPermission || isUnauthorized;
  }

}
