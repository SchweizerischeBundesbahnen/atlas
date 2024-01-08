package ch.sbb.exportservice.config;

import ch.sbb.exportservice.controller.ExportServicePointBatchControllerApiV1;
import ch.sbb.exportservice.model.SePoDiBatchExportFileName;
import ch.sbb.exportservice.model.SePoDiExportType;
import jakarta.validation.constraints.NotNull;
import java.util.concurrent.Callable;
import java.util.concurrent.ThreadPoolExecutor.AbortPolicy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.aop.interceptor.SimpleAsyncUncaughtExceptionHandler;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.async.CallableProcessingInterceptor;
import org.springframework.web.context.request.async.TimeoutCallableProcessingInterceptor;
import org.springframework.web.servlet.config.annotation.AsyncSupportConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

@Slf4j
@Configuration
@EnableAsync
public class AsyncConfig implements AsyncConfigurer , DisposableBean {

  private static final int CORE_POOL_SIZE = 100;
  private static final int MAX_POOL_SIZE = 200;
  private static final int QUEUE_CAPACITY = 100;

  private static final int DEFAULT_TIMEOUT = 600_000;

  private ThreadPoolTaskExecutor executor;

  /**
   * When using {@link StreamingResponseBody}
   * {@link ExportServicePointBatchControllerApiV1#streamExportJsonFile(SePoDiBatchExportFileName,
   * SePoDiExportType)},
   * it is highly recommended to configure TaskExecutor used in Spring MVC for executing asynchronous requests.
   *
   * @return taskExecutor
   */
  @Override
  @Bean(name = "asyncExecutor")
  public AsyncTaskExecutor getAsyncExecutor() {
    log.debug("Creating Async Task Executor");
    executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(CORE_POOL_SIZE);
    executor.setMaxPoolSize(MAX_POOL_SIZE);
    executor.setQueueCapacity(QUEUE_CAPACITY);
    executor.setRejectedExecutionHandler(new AbortPolicy());
    executor.setThreadNamePrefix("async-executor-");
//    executor.setWaitForTasksToCompleteOnShutdown(true);
    executor.setRejectedExecutionHandler((rejected, exec) -> {
      log.warn("Execution rejected...");
      try {
        log.warn("Put rejected execution in queue...");
        //TODO: do nothing
        exec.getQueue().put(rejected);
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
    });
    executor.initialize();
    return executor;
  }

  @Override
  public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
    log.error("SimpleAsyncUncaughtExceptionHandler occurred!");
    return new SimpleAsyncUncaughtExceptionHandler();
  }

  /** Configure async support for Spring MVC. */
  @Bean
  public WebMvcConfigurer webMvcConfigurerConfigurer(
      @Qualifier("asyncExecutor") AsyncTaskExecutor taskExecutor,
      CallableProcessingInterceptor callableProcessingInterceptor) {
    return new WebMvcConfigurer() {
      @Override
      public void configureAsyncSupport(@NotNull AsyncSupportConfigurer configurer) {
        configurer.setDefaultTimeout(DEFAULT_TIMEOUT).setTaskExecutor(taskExecutor);
        configurer.registerCallableInterceptors(callableProcessingInterceptor);
        WebMvcConfigurer.super.configureAsyncSupport(configurer);
      }
    };
  }

  @Bean
  public CallableProcessingInterceptor callableProcessingInterceptor() {
    return new TimeoutCallableProcessingInterceptor() {
      @Override
      public <T> @NotNull Object handleTimeout(@NotNull NativeWebRequest request, @NotNull Callable<T> task) throws Exception {
        log.error("timeout!");
        return super.handleTimeout(request, task);
      }
    };
  }

  @Override
  public void destroy(){
    log.info("Shutdown executor...");
    executor.shutdown();
  }
}
