package ch.sbb.importservice.utils;

import feign.FeignException;
import java.util.HashMap;
import java.util.Map;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.conn.ConnectTimeoutException;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;

@Slf4j
@UtilityClass
public class StepUtils {

  public static FixedBackOffPolicy getBackOffPolicy(String stepName) {
    FixedBackOffPolicy backOffPolicy = new FixedBackOffPolicy();
    int backOffPeriod = 10_000;
    backOffPolicy.setBackOffPeriod(backOffPeriod);
    log.info("Set Back Off Period for step [{}] to [{}ms]", stepName, backOffPeriod);
    return backOffPolicy;
  }

  public static SimpleRetryPolicy getRetryPolicy(String stepName) {
    int maxAttempts = 4;
    Map<Class<? extends Throwable>, Boolean> exceptionsToRetry = new HashMap<>();
    exceptionsToRetry.put(FeignException.InternalServerError.class, true);
    exceptionsToRetry.put(ConnectTimeoutException.class, true);
    log.info("Configuring Retry policy for step [{}] ", stepName);
    log.info("Set max attemps to retry: {}", maxAttempts);
    log.info("Set exceptions to retry: {}", exceptionsToRetry);
    return new SimpleRetryPolicy(maxAttempts, exceptionsToRetry);
  }

}
