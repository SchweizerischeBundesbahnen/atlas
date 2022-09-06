package ch.sbb.atlas.user.administration.security;

import ch.sbb.atlas.model.service.UserService;
import ch.sbb.atlas.user.administration.security.model.ApplicationType;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class UserAdministrationAspect {

  @Autowired
  private UserAdministrationService userAdministrationService;

  @Around("execution(public * ch.sbb.atlas.user.administration.security.UserAdministrationAwareService.create(..)) && args(businessObject)")
  public Object measureMethodExecutionTime(ProceedingJoinPoint joinPoint,
      BusinessOrganisationAssociated businessObject) throws Throwable {
    log.info("Creating {}", businessObject);

    log.info("target = {}", joinPoint.getTarget().getClass().getSimpleName());

    ApplicationType administratedApplicationType = ((UserAdministrationAwareService<?>) joinPoint.getTarget()).getAdministratedApplicationType();

    boolean permissions = userAdministrationService.hasUserPermissionsToCreate(businessObject,
        administratedApplicationType);

    log.info("permissions = {}", permissions);
    return joinPoint.proceed();
  }

}
