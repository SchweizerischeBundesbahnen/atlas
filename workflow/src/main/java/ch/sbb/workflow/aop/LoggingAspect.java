package ch.sbb.workflow.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {

  private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

  @Around("@annotation(ch.sbb.workflow.aop.MethodLogged)")
  public Object logMethod(ProceedingJoinPoint joinPoint) throws Throwable {
    String methodName = joinPoint.getSignature().getName();
    logger.info("Method {} started", methodName);
    try {
      Object result = joinPoint.proceed();
      logger.info("Method {} completed successfully", methodName);
      return result;
    } catch (Exception e) {
      logger.error("Error in method {}: {}", methodName, e.getMessage(), e);
      throw e;
    }
  }

}
