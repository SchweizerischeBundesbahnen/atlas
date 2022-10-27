package ch.sbb.atlas.base.service.aspect;

import static java.util.Collections.emptyList;
import static org.springframework.security.core.authority.AuthorityUtils.NO_AUTHORITIES;

import ch.sbb.atlas.base.service.aspect.annotation.RunAsUser;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class RunAsUserAspect {

  @Around("@annotation(ch.sbb.atlas.base.service.aspect.annotation.RunAsUser)")
  public Object executeRunAsUser(ProceedingJoinPoint joinPoint) throws Throwable {
    RunAsUser runAsUser = ((MethodSignature) joinPoint.getSignature()).getMethod().getAnnotation(RunAsUser.class);
    FakeUserType fakeUserType = runAsUser.fakeUserType();
    if (FakeUserType.KAFKA == fakeUserType) {
      return runAsKafkaUser(joinPoint, fakeUserType);
    }
    throw new IllegalStateException("Please provide an implementation for the given FakeUserType:" + fakeUserType);
  }

  private Object runAsKafkaUser(ProceedingJoinPoint joinPoint, FakeUserType fakeUserType) throws Throwable {
    log.info("Create fake authentication for {} with username: {}", fakeUserType.name(), fakeUserType.getUserName());
    Authentication fakeAuth = new UsernamePasswordAuthenticationToken(fakeUserType.getUserName(), emptyList(), NO_AUTHORITIES);
    SecurityContextHolder.getContext().setAuthentication(fakeAuth);
    final Object proceed = joinPoint.proceed();
    SecurityContextHolder.getContext().getAuthentication().setAuthenticated(false);
    log.info("Disable fake authentication.");
    return proceed;
  }

}
