package ch.sbb.exportservice.config;

import static ch.sbb.exportservice.utils.JobDescriptionConstants.EXPORT_SERVICE_POINT_CSV_JOB_NAME;

import ch.sbb.exportservice.aggregator.ServicePointFooterCallBack;
import ch.sbb.exportservice.aggregator.ServicePointJsonItemAggregator;
import ch.sbb.exportservice.entity.ServicePointVersion;
import ch.sbb.exportservice.listener.JobCompletionListener;
import ch.sbb.exportservice.listener.StepTracerListener;
import ch.sbb.exportservice.processor.ServicePointVersionProcessor;
import ch.sbb.exportservice.repository.PointRepository;
import ch.sbb.exportservice.utils.StepUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.batch.item.support.CompositeItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.WritableResource;
import org.springframework.core.task.TaskExecutor;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@AllArgsConstructor
@Slf4j
public class SpringBatchConfig {

  private static final int CHUNK_SIZE = 20;
  private static final int THREAD_EXECUTION_SIZE = 64;

  private final JobRepository jobRepository;

  private final PlatformTransactionManager transactionManager;

  private final JobCompletionListener jobCompletionListener;
  private final StepTracerListener stepTracerListener;

  private final PointRepository pointRepository;

  @StepScope
  @Bean
  public RepositoryItemReader<ServicePointVersion> servicePointItemReader() {
    RepositoryItemReader<ServicePointVersion> repositoryItemReader = new RepositoryItemReader<>();
    repositoryItemReader.setRepository(pointRepository);
    repositoryItemReader.setMethodName("findAll");
    final HashMap<String, Direction> sorts = new HashMap<>();
    sorts.put("id", Sort.Direction.ASC);
    repositoryItemReader.setSort(sorts);
    return repositoryItemReader;
  }

  @Bean
  public CompositeItemWriter<ServicePointVersion> compositeItemWriter() {
    List<ItemWriter> writers = new ArrayList<>();
    writers.add(csvWriter());
    writers.add(jsonWriter());

    CompositeItemWriter itemWriter = new CompositeItemWriter();

    itemWriter.setDelegates(writers);

    return itemWriter;
  }

  @Bean
  public ItemWriter<ServicePointVersion> jsonWriter() {
    FlatFileItemWriter<ServicePointVersion> writer = new FlatFileItemWriter<>();
    writer.setLineSeparator(System.getProperty("line.separator"));

    //Setting header and footer.
    ServicePointFooterCallBack headerFooterCallback = new ServicePointFooterCallBack();
    writer.setHeaderCallback(headerFooterCallback);
    writer.setFooterCallback(headerFooterCallback);

    writer.setLineAggregator(new ServicePointJsonItemAggregator<>());

    writer.setResource(new FileSystemResource(".export/outputData.json"));
    //    writer.setEncoding(utf8);
    writer.setShouldDeleteIfExists(true);

    return writer;
  }

  @Bean
  public FlatFileItemWriter<ServicePointVersion> csvWriter() {
    WritableResource outputResource = new FileSystemResource(".export/outputData.csv");
    String[] headers = new String[]{"id"};
    FlatFileItemWriter<ServicePointVersion> writer = new FlatFileItemWriter<>();
    writer.setResource(outputResource);
    writer.setAppendAllowed(true);
    writer.setLineAggregator(new DelimitedLineAggregator<>() {
      {
        setDelimiter(";");
        setFieldExtractor(new BeanWrapperFieldExtractor<>() {
          {
            setNames(new String[]{"id"});
          }
        });
      }
    });
    writer.setHeaderCallback(writer1 -> {
      for (int i = 0; i < headers.length; i++) {
        if (i != headers.length - 1) {
          writer1.append(headers[i] + ";");
        } else {
          writer1.append(headers[i]);
        }
      }
    });

    return writer;
  }

  @Bean
  public ServicePointVersionProcessor servicePointVersionProcessor() {
    return new ServicePointVersionProcessor();
  }

  @Bean
  public Step parseServicePointCsvStep() {
    String stepName = "parseServicePointCsvStep";
    return new StepBuilder(stepName, jobRepository)
        .<ServicePointVersion, ServicePointVersion>chunk(CHUNK_SIZE, transactionManager)
        .reader(servicePointItemReader())
        .processor(servicePointVersionProcessor())
        .writer(compositeItemWriter())
        .faultTolerant()
        .backOffPolicy(StepUtils.getBackOffPolicy(stepName))
        .retryPolicy(StepUtils.getRetryPolicy(stepName))
        .listener(stepTracerListener)
        //        .taskExecutor(asyncTaskExecutor())
        .build();
  }

  @Bean
  public Job importServicePointCsvJob() {
    return new JobBuilder(EXPORT_SERVICE_POINT_CSV_JOB_NAME, jobRepository)
        .listener(jobCompletionListener)
        .incrementer(new RunIdIncrementer())
        .flow(parseServicePointCsvStep())
        .end()
        .build();
  }

  @Bean
  public TaskExecutor asyncTaskExecutor() {
    ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
    taskExecutor.setCorePoolSize(THREAD_EXECUTION_SIZE);
    taskExecutor.setMaxPoolSize(THREAD_EXECUTION_SIZE);
    taskExecutor.setQueueCapacity(THREAD_EXECUTION_SIZE);
    taskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
    taskExecutor.setThreadNamePrefix("Thread-");
    return taskExecutor;
  }

}
