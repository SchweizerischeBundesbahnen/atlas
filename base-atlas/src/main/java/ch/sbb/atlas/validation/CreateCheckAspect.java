package ch.sbb.atlas.validation;

import ch.sbb.atlas.exception.IdProvidedOnCreateException;
import ch.sbb.atlas.model.Identifiable;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class CreateCheckAspect {

  @Around("@annotation(ch.sbb.atlas.validation.CreateCheck)")
  public Object createCheck(ProceedingJoinPoint joinPoint) throws Throwable {
    Object[] args = joinPoint.getArgs();
    for (Object arg : args) {
      if (arg instanceof Identifiable identifiable && identifiable.getId() != null) {
        throw new IdProvidedOnCreateException();
      }
    }
    return joinPoint.proceed();
  }
}
