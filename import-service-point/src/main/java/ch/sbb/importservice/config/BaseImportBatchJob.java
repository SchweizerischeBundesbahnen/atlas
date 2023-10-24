package ch.sbb.importservice.config;

import ch.sbb.importservice.listener.JobCompletionListener;
import ch.sbb.importservice.listener.StepTracerListener;
import java.util.concurrent.ThreadPoolExecutor;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

public abstract class BaseImportBatchJob {

  private static final int THREAD_EXECUTION_SIZE = 64;

  protected final JobRepository jobRepository;
  protected final PlatformTransactionManager transactionManager;

  protected final JobCompletionListener jobCompletionListener;
  protected final StepTracerListener stepTracerListener;
  protected BaseImportBatchJob(JobRepository jobRepository, PlatformTransactionManager transactionManager,
      JobCompletionListener jobCompletionListener, StepTracerListener stepTracerListener) {
    this.jobRepository = jobRepository;
    this.transactionManager = transactionManager;
    this.jobCompletionListener = jobCompletionListener;
    this.stepTracerListener = stepTracerListener;
  }


  @StepScope
  @Bean
  protected TaskExecutor asyncTaskExecutor() {
    ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
    taskExecutor.setCorePoolSize(THREAD_EXECUTION_SIZE);
    taskExecutor.setMaxPoolSize(THREAD_EXECUTION_SIZE);
    taskExecutor.setQueueCapacity(THREAD_EXECUTION_SIZE);
    taskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
    taskExecutor.setThreadNamePrefix("Thread-");
    return taskExecutor;
  }

}
