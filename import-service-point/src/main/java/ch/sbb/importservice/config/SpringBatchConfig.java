package ch.sbb.importservice.config;

import ch.sbb.importservice.batch.ServicePointApiWriter;
import ch.sbb.importservice.batch.ServicePointProcessor;
import ch.sbb.importservice.listener.StepSkipListener;
import ch.sbb.importservice.model.ServicePoint;
import java.io.File;
import java.util.concurrent.ThreadPoolExecutor;
import lombok.AllArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.SkipListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.step.skip.SkipPolicy;
import org.springframework.batch.integration.async.AsyncItemProcessor;
import org.springframework.batch.integration.async.AsyncItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableBatchProcessing
@AllArgsConstructor
public class SpringBatchConfig {

  private final JobBuilderFactory jobBuilderFactory;

  private final StepBuilderFactory stepBuilderFactory;

  private final ServicePointApiWriter servicePointApiWriter;

  @Bean
  @StepScope
  public FlatFileItemReader<ServicePoint> itemReader(@Value("#{jobParameters[fullPathFileName]}") String pathToFIle) {
    FlatFileItemReader<ServicePoint> flatFileItemReader = new FlatFileItemReader<>();
    flatFileItemReader.setResource(new FileSystemResource(new File(pathToFIle)));
    flatFileItemReader.setName("CSV-Reader");
    flatFileItemReader.setLinesToSkip(1);
    flatFileItemReader.setLineMapper(lineMapper());
    return flatFileItemReader;
  }

  private LineMapper<ServicePoint> lineMapper() {
    DefaultLineMapper<ServicePoint> lineMapper = new DefaultLineMapper<>();

    DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
    lineTokenizer.setDelimiter(";");
    lineTokenizer.setStrict(false);
    lineTokenizer.setNames("NUMMER", "LAENDERCODE");

    BeanWrapperFieldSetMapper<ServicePoint> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
    fieldSetMapper.setTargetType(ServicePoint.class);

    lineMapper.setLineTokenizer(lineTokenizer);
    lineMapper.setFieldSetMapper(fieldSetMapper);

    return lineMapper;
  }

  @Bean
  public ServicePointProcessor processor() {
    return new ServicePointProcessor();
  }

  @Bean
  public AsyncItemProcessor<ServicePoint, ServicePoint> asyncItemProcessor() {
    AsyncItemProcessor<ServicePoint, ServicePoint> asyncItemProcessor = new AsyncItemProcessor<>();
    asyncItemProcessor.setDelegate(processor());
    return asyncItemProcessor;
  }

  @Bean
  public AsyncItemWriter<ServicePoint> dataAsyncWriter() throws Exception {
    AsyncItemWriter<ServicePoint> asyncItemWriter = new AsyncItemWriter<>();
    asyncItemWriter.setDelegate(servicePointApiWriter);
    asyncItemWriter.afterPropertiesSet();
    return asyncItemWriter;
  }

  @Bean
  public Step parseCsvStep(FlatFileItemReader<ServicePoint> itemReader) throws Exception {
    return stepBuilderFactory.get("parseCsvStep").<ServicePoint, ServicePoint>chunk(100)
        .reader(itemReader)
        .processor(processor())
        .writer(servicePointApiWriter)
        .faultTolerant()
        .listener(skipListener())
        .skipPolicy(skipPolicy())
        //        .taskExecutor(getAsyncExecutor())
        .build();
  }

  @Bean
  public Job runJob(FlatFileItemReader<ServicePoint> itemReader) throws Exception {
    return jobBuilderFactory.get("importServicePointCsv")
        .flow(parseCsvStep(itemReader))
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

  @Bean(name = "asyncExecutor")
  public TaskExecutor getAsyncExecutor() {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(100);
    executor.setMaxPoolSize(100);
    executor.setQueueCapacity(100);
    executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
    executor.setThreadNamePrefix("batch-import-");
    return executor;
  }

}
