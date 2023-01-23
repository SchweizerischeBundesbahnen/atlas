package ch.sbb.importservice.listener;

import org.springframework.batch.core.step.skip.SkipLimitExceededException;
import org.springframework.batch.core.step.skip.SkipPolicy;
import org.springframework.beans.BeanInstantiationException;

public class ExceptionSkipPolicy implements SkipPolicy {

  @Override
  public boolean shouldSkip(Throwable throwable, int i) throws SkipLimitExceededException {
    return throwable instanceof BeanInstantiationException;
  }
}
