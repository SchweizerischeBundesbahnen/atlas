package ch.sbb.workflow.aop;

import ch.sbb.workflow.model.sepodi.StopPointAddWorkflowModel;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.MDC;
import org.slf4j.MDC.MDCCloseable;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

  public static final String WORKFLOW_TYPE_VOTE_WORKFLOW = "VOTE_WORKFLOW";
  public static final String CANCEL_WORKFLOW = "CANCEL_WORKFLOW";
  public static final String REJECT_WORKFLOW = "REJECT_WORKFLOW";
  public static final String RESTART_WORKFLOW = "RESTART_WORKFLOW";
  public static final String ADD_WORKFLOW = "ADD_WORKFLOW";
  public static final String ERROR_MARKER = "CRITICAL_WORKFLOW_ERROR"; // this value should not be changed, or if so, splunk
  // alert should be adjusted as well

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

    try (MDCCloseable ignored1 = MDC.putCloseable("className", className);
        MDCCloseable ignored = MDC.putCloseable("methodName", methodName);
        MDCCloseable ignored2 = MDC.putCloseable("workflowType", workflowType)) {
      return joinPoint.proceed();
    } catch (Exception e) {
      Map<String, Object> errorDetails = buildErrorDetails(className, methodName, workflowType, joinPoint.getArgs(), e);
      String jsonErrorDetails = objectMapper.writeValueAsString(errorDetails);
      log.error("{}: {}", ERROR_MARKER, jsonErrorDetails, e);
      throw e;
    }
  }

  private Map<String, Object> buildErrorDetails(String className, String methodName, String workflowType,
      Object[] args, Exception e) {
    Map<String, Object> details = new HashMap<>();
    details.put("className", className);
    details.put("methodName", methodName);
    details.put("workflowType", workflowType);
    details.put("errorMessage", e.getMessage());

    for (Object arg : args) {
      switch (arg) {
        case StopPointAddWorkflowModel model -> {
          details.put("workflowId", model.getId());
          details.put("servicePointVersionId", model.getVersionId());
          details.put("sloid", model.getSloid());
        }
        case Long id -> details.put("workflowId", id);
        default -> log.warn("Unexpected value: {}", arg);
      }
    }
    return details;
  }

}
