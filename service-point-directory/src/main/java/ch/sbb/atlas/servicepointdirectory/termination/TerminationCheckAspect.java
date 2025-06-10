package ch.sbb.atlas.servicepointdirectory.termination;

import ch.sbb.atlas.reflection.ParameterInfoReflectionResolver;
import ch.sbb.atlas.servicepointdirectory.entity.ServicePointVersion;
import ch.sbb.atlas.servicepointdirectory.service.servicepoint.ServicePointValidationService;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class TerminationCheckAspect {

  private final ServicePointValidationService validationService;

  @Around("@annotation(ch.sbb.atlas.servicepointdirectory.termination.TerminationCheck)")
  public Object checkTermination(ProceedingJoinPoint joinPoint) throws Throwable {

    ServicePointVersion servicePointVersion = getServicePointVersionParameter(joinPoint);
    validationService.checkIfServicePointIsTerminationInProgress(servicePointVersion);
    return joinPoint.proceed();
  }

  ServicePointVersion getServicePointVersionParameter(ProceedingJoinPoint joinPoint) {
    Optional<Object> parameterValue = ParameterInfoReflectionResolver.valueByAnnotation(joinPoint,
        TerminationCheckParameter.class);
    if (parameterValue.isPresent()) {
      if (parameterValue.get() instanceof ServicePointVersion servicePointVersion) {
        return servicePointVersion;
      }
      throw new IllegalStateException("Parameter marked with @TerminationCheckParameter must be type of ServicePointVersion!");
    }
    throw new IllegalStateException(
        "You have to mark @TerminationCheckParameter the userName parameter for the method annotated with "
            + "@TerminationCheck annotation.!");
  }

}
