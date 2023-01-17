package ch.sbb.importservice.config;

import ch.sbb.atlas.base.service.imports.ServicePointCsvModel;
import ch.sbb.importservice.batch.ServicePointApiWriter;
import ch.sbb.importservice.batch.ServicePointProcessor;
import ch.sbb.importservice.listener.JobCompletitionListener;
import ch.sbb.importservice.listener.StepSkipListener;
import ch.sbb.importservice.service.CsvService;
import java.io.File;
import java.io.IOException;
import java.util.List;
import lombok.AllArgsConstructor;
import org.apache.http.conn.ConnectTimeoutException;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.SkipListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.step.skip.SkipPolicy;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.dao.DeadlockLoserDataAccessException;

@Configuration
@EnableBatchProcessing
@AllArgsConstructor
public class SpringBatchConfig {

  private final JobBuilderFactory jobBuilderFactory;

  private final StepBuilderFactory stepBuilderFactory;

  private final ServicePointApiWriter servicePointApiWriter;

  private final CsvService csvService;

  private final JobCompletitionListener jobCompletitionListener;

  @Bean
  public ServicePointProcessor processor() {
    return new ServicePointProcessor();
  }

  @StepScope
  @Bean
  public ListItemReader<ServicePointCsvModel> listItemReader(@Value("#{jobParameters[fullPathFileName]}") String pathToFIle)
      throws IOException {
    List<ServicePointCsvModel> actualServicePotinCsvModelsFromS3;
    if (pathToFIle != null) {
      File file = new File(pathToFIle);
      actualServicePotinCsvModelsFromS3 = csvService.getActualServicePotinCsvModelsFromS3(file);
    } else {
      actualServicePotinCsvModelsFromS3 = csvService.getActualServicePotinCsvModelsFromS3();
    }
    return new ListItemReader<>(actualServicePotinCsvModelsFromS3);
  }

  @Bean
  public Step parseCsvStep(ListItemReader<ServicePointCsvModel> listItemReader) {
    return stepBuilderFactory.get("parseCsvStep").<ServicePointCsvModel, ServicePointCsvModel>chunk(100)
        .reader(listItemReader)
        .processor(processor())
        .writer(servicePointApiWriter)
        .faultTolerant()
        .retryLimit(3)
        .retry(ConnectTimeoutException.class)
        .retry(DeadlockLoserDataAccessException.class)
        .taskExecutor(getAsyncExecutor())
        .build();
  }

  @Bean
  public Job importServicePointCsvJob(ListItemReader<ServicePointCsvModel> listItemReader) {
    return jobBuilderFactory.get("importServicePointCsvJob")
        .listener(jobCompletitionListener)
        .incrementer(new RunIdIncrementer())
        .flow(parseCsvStep(listItemReader))
        .end()
        .build();
  }

  @Bean
  public Job importLoadingPointCsvJob(ListItemReader<ServicePointCsvModel> listItemReader) {
    return jobBuilderFactory.get("importLoadingPointCsvJob")
        .listener(jobCompletitionListener)
        .incrementer(new RunIdIncrementer())
        .flow(parseCsvStep(listItemReader))
        .end()
        .build();
  }

  @Bean
  public SkipPolicy skipPolicy() {
    return new ExceptionSkipPolicy();
  }

  @Bean
  public SkipListener skipListener() {
    return new StepSkipListener();
  }

  @Bean
  public TaskExecutor getAsyncExecutor() {
    SimpleAsyncTaskExecutor taskExecutor = new SimpleAsyncTaskExecutor();
    taskExecutor.setConcurrencyLimit(2);
    return taskExecutor;
  }

}
