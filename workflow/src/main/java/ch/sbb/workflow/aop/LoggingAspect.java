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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {

  private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);
  private static final String ERROR_MARKER = "CRITICAL_WORKFLOW_ERROR";

  @Autowired
  private ObjectMapper objectMapper;

  @Around("@annotation(methodLogged)")
  public Object logMethod(ProceedingJoinPoint joinPoint, MethodLogged methodLogged) throws Throwable {
    String methodName = joinPoint.getSignature().getName();
    String workflowType = methodLogged.workflowType();
    boolean isCritical = methodLogged.critical();
    Object[] args = joinPoint.getArgs();

    MDC.put("methodName", methodName);
    MDC.put("workflowType", workflowType);
    MDC.put("isCritical", String.valueOf(isCritical));

    try {
      Object result = joinPoint.proceed();
      return result;
    } catch (Exception e) {
      Map<String, Object> errorDetails = buildErrorDetails(methodName, workflowType, isCritical, args, e);
      String jsonErrorDetails = objectMapper.writeValueAsString(errorDetails);
      logger.error("{}: {}", ERROR_MARKER, jsonErrorDetails, e);
      throw e;
    } finally {
      MDC.remove("methodName");
      MDC.remove("workflowType");
      MDC.remove("isCritical");
    }
  }

  private Map<String, Object> buildErrorDetails(String methodName, String workflowType, boolean isCritical, Object[] args, Exception e) {
    Map<String, Object> details = new HashMap<>();
    details.put("methodName", methodName);
    details.put("workflowType", workflowType);
    details.put("isCritical", isCritical);
    details.put("errorMessage", e.getMessage());

    for (Object arg : args) {
      if (arg instanceof StopPointAddWorkflowModel) {
        StopPointAddWorkflowModel model = (StopPointAddWorkflowModel) arg;
        details.put("workflowId", model.getId());
        details.put("servicePointVersionId", model.getVersionId());
        details.put("sloid", model.getSloid());
      } else if (arg instanceof Long) {
        Long id = (Long) arg;
        details.put("workflowId", id);
      }
    }

    return details;
  }

}
