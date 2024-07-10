package ch.sbb.workflow.aop;

import ch.sbb.workflow.model.sepodi.StopPointAddWorkflowModel;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.Map;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.slf4j.MDC.MDCCloseable;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {

  private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);
  public static final String workflowTypeVoteWorkflow = "VOTE_WORKFLOW";
  public static final String workflowTypeOverrideVoteWorkflow = "OVERRIDE_VOTE_WORKFLOW";
  private static final String ERROR_MARKER = "CRITICAL_WORKFLOW_ERROR";

  private final ObjectMapper objectMapper;

  public LoggingAspect(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  @Around("@annotation(methodLogged)")
  public Object logMethod(ProceedingJoinPoint joinPoint, MethodLogged methodLogged) throws Throwable {
    MethodSignature signature = (MethodSignature) joinPoint.getSignature();
    String className = signature.getDeclaringType().getSimpleName();
    String methodName = joinPoint.getSignature().getName();
    String workflowType = methodLogged.workflowType();
    boolean isCritical = methodLogged.critical();

    try (MDCCloseable ignored1 = MDC.putCloseable("className", className);
        MDCCloseable ignored = MDC.putCloseable("methodName", methodName);
        MDCCloseable ignored2 = MDC.putCloseable("workflowType", workflowType);
        MDCCloseable ignored3 = MDC.putCloseable("isCritical", String.valueOf(isCritical))) {
      return joinPoint.proceed();
    } catch (Exception e) {
      Map<String, Object> errorDetails = buildErrorDetails(className, methodName, workflowType, isCritical, joinPoint.getArgs(), e);
      String jsonErrorDetails = objectMapper.writeValueAsString(errorDetails);
      logger.error("{}: {}", ERROR_MARKER, jsonErrorDetails, e);
      throw e;
    }
  }

  private Map<String, Object> buildErrorDetails(String className, String methodName, String workflowType,
      boolean isCritical, Object[] args, Exception e) {
    Map<String, Object> details = new HashMap<>();
    details.put("className", className);
    details.put("methodName", methodName);
    details.put("workflowType", workflowType);
    details.put("isCritical", isCritical);
    details.put("errorMessage", e.getMessage());

    for (Object arg : args) {
      switch (arg) {
        case StopPointAddWorkflowModel model -> {
          details.put("workflowId", model.getId());
          details.put("servicePointVersionId", model.getVersionId());
          details.put("sloid", model.getSloid());
        }
        case Long id -> {
          if (!workflowType.equals(workflowTypeVoteWorkflow) && !workflowType.equals(workflowTypeOverrideVoteWorkflow)) {
            details.put("workflowId", id);
          }
        }
        default -> {}
      }
    }

    return details;
  }

}
