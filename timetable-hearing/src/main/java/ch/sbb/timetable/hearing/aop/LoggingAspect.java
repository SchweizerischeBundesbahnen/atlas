package ch.sbb.timetable.hearing.aop;

import java.util.Arrays;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class LoggingAspect {

  @RequiredArgsConstructor
  public enum WorkflowType {
    STOP_POINT_WORKFLOW("CRITICAL_STOP_POINT_WORKFLOW_ERROR"),
    STOP_POINT_TERMINATION_WORKFLOW("CRITICAL_STOP_POINT_TERMINATION_WORKFLOW_ERROR"),
    TTH_DOSSIER_WORKFLOW("CRITICAL_TTH_DOSSIER_WORKFLOW_ERROR");

    public final String errorMarker;
  }

  private final ObjectMapper objectMapper;

  @Around("@annotation(methodLogged)")
  public Object logMethod(ProceedingJoinPoint joinPoint, MethodLogged methodLogged) throws Throwable {
    String className = joinPoint.getSignature().getDeclaringType().getSimpleName();
    String methodName = joinPoint.getSignature().getName();
    WorkflowType workflowType = methodLogged.workflowType();

    try {
      return joinPoint.proceed();
    } catch (Exception e) {
      Map<String, String> errorDetails = buildErrorDetails(className, methodName, workflowType, joinPoint.getArgs(), e);
      String jsonErrorDetails = objectMapper.writeValueAsString(errorDetails);
      log.error("{}: {}", workflowType.errorMarker, jsonErrorDetails, e);
      throw e;
    }
  }

  private Map<String, String> buildErrorDetails(
      String className, String methodName, WorkflowType workflowType, Object[] args, Exception e
  ) {
    return Map.ofEntries(
        Map.entry("className", className),
        Map.entry("methodName", methodName),
        Map.entry("workflowType", workflowType.name()),
        // Make sure that object abstractions implement toString() properly for all details to be logged.
        Map.entry("arguments", Arrays.toString(args)),
        Map.entry("errorMessage", String.valueOf(e.getMessage()))
    );
  }
}

