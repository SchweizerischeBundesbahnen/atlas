package ch.sbb.exportservice.config;

import ch.sbb.exportservice.model.SePoDiBatchExportFileName;
import ch.sbb.exportservice.model.SePoDiExportType;
import jakarta.validation.constraints.NotNull;
import java.util.concurrent.Callable;
import java.util.concurrent.ThreadPoolExecutor.AbortPolicy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.aop.interceptor.SimpleAsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.async.CallableProcessingInterceptor;
import org.springframework.web.context.request.async.TimeoutCallableProcessingInterceptor;
import org.springframework.web.servlet.config.annotation.AsyncSupportConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Slf4j
@Configuration
@EnableAsync
public class AsyncConfig implements AsyncConfigurer {

  private static final int THREAD_EXECUTION_SIZE = 64;

  private static final int DEFAULT_TIMEOUT = 7200000;

  /**
   * When using {@link org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody}
   * {@link ch.sbb.exportservice.controller.ExportServicePointBatchControllerApiV1#streamExportJsonFile(SePoDiBatchExportFileName,
   * SePoDiExportType)},
   * it is highly recommended to configure TaskExecutor used in Spring MVC for executing asynchronous requests.
   *
   * @return taskExecutor
   */
  @Override
  @Bean(name = "taskExecutor")
  public TaskExecutor getAsyncExecutor() {
    log.debug("Creating Async Task Executor");
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(5);
    executor.setMaxPoolSize(10);
    executor.setQueueCapacity(25);
    executor.setRejectedExecutionHandler(new AbortPolicy());
    executor.setThreadNamePrefix("asyncExecutor-");
    executor.setRejectedExecutionHandler((r, executor1) -> {
      log.info("rejectedExecution");
      try {
        executor1.getQueue().put(r);
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
    });
    executor.initialize();
    return executor;
  }

  @Override
  public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
    return new SimpleAsyncUncaughtExceptionHandler();
  }

  /** Configure async support for Spring MVC. */
  @Bean
  public WebMvcConfigurer webMvcConfigurerConfigurer(AsyncTaskExecutor taskExecutor,
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
}
