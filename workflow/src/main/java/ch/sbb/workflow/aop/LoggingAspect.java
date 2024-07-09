package ch.sbb.workflow.aop;

import ch.sbb.workflow.model.sepodi.StopPointAddWorkflowModel;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.Map;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {

  private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);
  private static final String ERROR_MARKER = "CRITICAL_WORKFLOW_ERROR";

  private final ObjectMapper objectMapper;

  public LoggingAspect(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  @Around("@annotation(methodLogged)")
  public Object logMethod(ProceedingJoinPoint joinPoint, MethodLogged methodLogged) throws Throwable {
    var methodName = joinPoint.getSignature().getName();
    var workflowType = methodLogged.workflowType();
    var isCritical = methodLogged.critical();

    try (var ignored = MDC.putCloseable("methodName", methodName);
        var ignored2 = MDC.putCloseable("workflowType", workflowType);
        var ignored3 = MDC.putCloseable("isCritical", String.valueOf(isCritical))) {

      return joinPoint.proceed();
    } catch (Exception e) {
      var errorDetails = buildErrorDetails(methodName, workflowType, isCritical, joinPoint.getArgs(), e);
      var jsonErrorDetails = objectMapper.writeValueAsString(errorDetails);
      logger.error("{}: {}", ERROR_MARKER, jsonErrorDetails, e);
      throw e;
    }
  }

  private Map<String, Object> buildErrorDetails(String methodName, String workflowType, boolean isCritical, Object[] args, Exception e) {
    var details = new HashMap<String, Object>();
    details.put("methodName", methodName);
    details.put("workflowType", workflowType);
    details.put("isCritical", isCritical);
    details.put("errorMessage", e.getMessage());

    for (var arg : args) {
      switch (arg) {
        case StopPointAddWorkflowModel model -> {
          details.put("workflowId", model.getId());
          details.put("servicePointVersionId", model.getVersionId());
          details.put("sloid", model.getSloid());
        }
        case Long id -> details.put("workflowId", id);
        default -> {}
      }
    }

    return details;
  }

}
