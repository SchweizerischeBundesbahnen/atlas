package ch.sbb.exportservice.utils;

import feign.FeignException;
import feign.RetryableException;
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

  public static final int CHUNK_SIZE = 200;
  public static final int FETCH_SIZE = 10_000;

  private static final int BACK_OFF_PERIOD = 100_000;
  private static final int MAX_ATTEMPTS = 4;

  public static FixedBackOffPolicy getBackOffPolicy(String stepName) {
    FixedBackOffPolicy backOffPolicy = new FixedBackOffPolicy();
    int backOffPeriod = BACK_OFF_PERIOD;
    backOffPolicy.setBackOffPeriod(backOffPeriod);
    log.info("Set Back Off Period for step [{}] to [{}ms]", stepName, backOffPeriod);
    return backOffPolicy;
  }

  public static SimpleRetryPolicy getRetryPolicy(String stepName) {
    int maxAttempts = MAX_ATTEMPTS;
    Map<Class<? extends Throwable>, Boolean> exceptionsToRetry = new HashMap<>();
    exceptionsToRetry.put(FeignException.InternalServerError.class, true);
    exceptionsToRetry.put(ConnectTimeoutException.class, true);
    exceptionsToRetry.put(RetryableException.class, true);
    log.info("Configuring Retry policy for step [{}] ", stepName);
    log.info("Set max attemps to retry: {}", maxAttempts);
    log.info("Set exceptions to retry: {}", exceptionsToRetry);
    return new SimpleRetryPolicy(maxAttempts, exceptionsToRetry);
  }

}
