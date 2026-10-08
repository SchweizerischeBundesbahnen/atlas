package ch.sbb.atlas.api.mail;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;

@Configuration(proxyBeanMethods = false)
public class MailTaskExecutorConfig {

  public static final String MAIL_TASK_EXECUTOR = "mailTaskExecutor";
  private static final long TASK_TERMINATION_TIMEOUT_MILLIS = 10_000;

  @Bean(name = MAIL_TASK_EXECUTOR)
  public SimpleAsyncTaskExecutor mailTaskExecutor() {
    SimpleAsyncTaskExecutor taskExecutor = new SimpleAsyncTaskExecutor("mail-client-");
    taskExecutor.setVirtualThreads(true);
    taskExecutor.setTaskTerminationTimeout(TASK_TERMINATION_TIMEOUT_MILLIS);
    return taskExecutor;
  }

}
