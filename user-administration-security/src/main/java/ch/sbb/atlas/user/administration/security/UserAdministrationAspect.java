package ch.sbb.atlas.user.administration.security;

import ch.sbb.atlas.model.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class UserAdministrationAspect {

  @Around("execution(public * ch.sbb.atlas.user.administration.security.UserAdministrationAwareService.create(..)) && args(businessObject)")
  public Object measureMethodExecutionTime(ProceedingJoinPoint joinPoint,
      BusinessOrganisationAssociated businessObject) throws Throwable {
    log.info("Creating {}", businessObject);

    return joinPoint.proceed();
  }

  private String getCurrentUserSbbUid() {
    return UserService.getSbbUid();
  }
}
