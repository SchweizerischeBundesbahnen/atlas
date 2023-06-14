package ch.sbb.exportservice.config;

import static ch.sbb.exportservice.model.ServicePointVersionCsvModel.Fields.numberShort;
import static ch.sbb.exportservice.utils.JobDescriptionConstants.EXPORT_SERVICE_POINT_CSV_JOB_NAME;
import static ch.sbb.exportservice.utils.JobDescriptionConstants.EXPORT_SERVICE_POINT_JSON_JOB_NAME;

import ch.sbb.atlas.amazon.service.FileService;
import ch.sbb.atlas.api.AtlasApiConstants;
:import ch.sbb.atlas.api.servicepoint.ServicePointVersionModel;
import ch.sbb.exportservice.entity.ServicePointVersion;
import ch.sbb.exportservice.listener.JobCompletionListener;
import ch.sbb.exportservice.listener.StepTracerListener;
import ch.sbb.exportservice.model.ServicePointExportType;
import ch.sbb.exportservice.model.ServicePointVersionCsvModel;
import ch.sbb.exportservice.model.ServicePointVersionCsvModel.Fields;
import ch.sbb.exportservice.processor.ServicePointVersionCsvProcessor;
import ch.sbb.exportservice.processor.ServicePointVersionJsonProcessor;
import ch.sbb.exportservice.reader.BaseServicePointVersionReader.ServicePointVersionRowMapper;
import ch.sbb.exportservice.reader.SqlQueryUtil;
import ch.sbb.exportservice.tasklet.FileDeletingTasklet;
import ch.sbb.exportservice.tasklet.FileUploadTasklet;
import ch.sbb.exportservice.utils.StepUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ThreadPoolExecutor;
import javax.sql.DataSource;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.batch.item.json.JacksonJsonObjectMarshaller;
import org.springframework.batch.item.json.JsonFileItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.WritableResource;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@AllArgsConstructor
@Slf4j
public class SpringBatchConfig {

  static final String[] CSV_HEADER = new String[]{numberShort, Fields.uicCountryCode,
      Fields.sloid, Fields.number, Fields.checkDigit, Fields.validFrom, Fields.validTo, Fields.designationOfficial,
      Fields.designationLong, Fields.abbreviation, Fields.operatingPoint, Fields.operatingPointWithTimetable, Fields.stopPoint,
      Fields.stopPointTypeCode, Fields.freightServicePoint, Fields.trafficPoint,
      Fields.borderPoint, Fields.hasGeolocation, Fields.isoCoutryCode, Fields.cantonAbbreviation,
      Fields.districtName, Fields.districtFsoName, Fields.municipalityName, Fields.fsoNumber,
      Fields.localityName, Fields.operatingPointTypeCode, Fields.operatingPointTechnicalTimetableTypeCode,
      Fields.meansOfTransportCode, Fields.categoriesCode, Fields.operatingPointTrafficPointTypeCode,
      Fields.operatingPointRouteNetwork, Fields.operatingPointKilometer, Fields.operatingPointKilometerMasterNumber,
      Fields.sortCodeOfDestinationStation, Fields.sboid, Fields.businessOrganisationOrganisationNumber,
      Fields.businessOrganisationAbbreviationDe, Fields.businessOrganisationAbbreviationFr,
      Fields.businessOrganisationAbbreviationIt, Fields.businessOrganisationAbbreviationEn,
      Fields.businessOrganisationDescriptionDe, Fields.businessOrganisationDescriptionFr,
      Fields.businessOrganisationDescriptionIt, Fields.businessOrganisationDescriptionEn, Fields.fotComment, Fields.lv95East,
      Fields.lv95North, Fields.wgs84East, Fields.wgs84North, Fields.wgs84WebEast, Fields.wgs84WebNorth,
      Fields.height, Fields.creationDate, Fields.editionDate, Fields.statusDidok3
  };
  private static final int CHUNK_SIZE = 200;
  private static final int THREAD_EXECUTION_SIZE = 64;
  private final JobRepository jobRepository;

  private final PlatformTransactionManager transactionManager;

  private final JobCompletionListener jobCompletionListener;
  private final StepTracerListener stepTracerListener;

  private final FileService fileService;

  @Bean
  @StepScope
  public JdbcCursorItemReader<ServicePointVersion> reader(@Autowired @Qualifier("servicePointDataSource") DataSource dataSource
      , @Value("#{jobParameters[exportType]}") ServicePointExportType exportType) {
    log.info("cazzo:{}", exportType);
    JdbcCursorItemReader<ServicePointVersion> itemReader = new JdbcCursorItemReader<>();
    itemReader.setDataSource(dataSource);
    itemReader.setSql(SqlQueryUtil.getSqlQuery(exportType));
    itemReader.setFetchSize(10000);
    itemReader.setRowMapper(new ServicePointVersionRowMapper());
    return itemReader;
  }

  @Bean
  public JsonFileItemWriter<ServicePointVersionModel> jsonFileItemWriter() {
    JacksonJsonObjectMarshaller<ServicePointVersionModel> jacksonJsonObjectMarshaller = new JacksonJsonObjectMarshaller<>();
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
    objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    jacksonJsonObjectMarshaller.setObjectMapper(objectMapper);
    FileSystemResource fileSystemResource = new FileSystemResource(createFileNamePath("json"));
    JsonFileItemWriter<ServicePointVersionModel> writer = new JsonFileItemWriter<>(
        fileSystemResource,
        jacksonJsonObjectMarshaller);
    writer.setEncoding(StandardCharsets.ISO_8859_1.name());
    return writer;
  }

  private String createFileNamePath(String fileType) {
    String dir = fileService.getDir();
    String actualDate = LocalDate.now()
        .format(DateTimeFormatter.ofPattern(
            AtlasApiConstants.DATE_FORMAT_PATTERN));
    return dir + "service-point-" + actualDate + "." + fileType;
  }

  @Bean
  @StepScope
  public FlatFileItemWriter<ServicePointVersionCsvModel> csvWriter(@Value("#{jobParameters[exportType]}") String exportType) {
    log.warn("FlatFileItemWriter:{}", exportType);
    WritableResource outputResource = new FileSystemResource(createFileNamePath("csv"));
    FlatFileItemWriter<ServicePointVersionCsvModel> writer = new FlatFileItemWriter<>();
    writer.setResource(outputResource);
    writer.setAppendAllowed(true);
    writer.setLineAggregator(new DelimitedLineAggregator<>() {
      {
        setDelimiter(";");
        setFieldExtractor(new BeanWrapperFieldExtractor<>() {{
          setNames(CSV_HEADER);
        }});
      }
    });
    writer.setHeaderCallback(new CsvFlatFileHeaderCallback());
    writer.setEncoding(StandardCharsets.ISO_8859_1.name());
    return writer;
  }

  @Bean
  public ServicePointVersionCsvProcessor servicePointVersionCsvProcessor() {
    return new ServicePointVersionCsvProcessor();
  }

  @Bean
  public ServicePointVersionJsonProcessor servicePointVersionJsonProcessor() {
    return new ServicePointVersionJsonProcessor();
  }

  @Bean
  public Step exportServicePointCsvStep(ItemReader<ServicePointVersion> itemReader) {
    String stepName = "exportServicePointCsvStep";
    return new StepBuilder(stepName, jobRepository)
        .<ServicePointVersion, ServicePointVersionCsvModel>chunk(CHUNK_SIZE, transactionManager)
        .reader(itemReader)
        .processor(servicePointVersionCsvProcessor())
        .writer(csvWriter(null))
        .faultTolerant()
        .backOffPolicy(StepUtils.getBackOffPolicy(stepName))
        .retryPolicy(StepUtils.getRetryPolicy(stepName))
        .listener(stepTracerListener)
        .build();
  }

  @Bean
  public Step exportServicePointJsonStep(ItemReader<ServicePointVersion> itemReader) {
    String stepName = "exportServicePointJsonStep";
    return new StepBuilder(stepName, jobRepository)
        .<ServicePointVersion, ServicePointVersionModel>chunk(CHUNK_SIZE, transactionManager)
        .reader(itemReader)
        .processor(servicePointVersionJsonProcessor())
        .writer(jsonFileItemWriter())
        .faultTolerant()
        .backOffPolicy(StepUtils.getBackOffPolicy(stepName))
        .retryPolicy(StepUtils.getRetryPolicy(stepName))
        .listener(stepTracerListener)
        .build();
  }

  @Bean
  @Qualifier(EXPORT_SERVICE_POINT_CSV_JOB_NAME)
  public Job exportServicePointCsvJob(ItemReader<ServicePointVersion> itemReader) {
    return new JobBuilder(EXPORT_SERVICE_POINT_CSV_JOB_NAME, jobRepository)
        .listener(jobCompletionListener)
        .incrementer(new RunIdIncrementer())
        .flow(exportServicePointCsvStep(itemReader))
        .next(uploadFilesStep())
        .next(deleteFilesStep())
        .end()
        .build();
  }

  @Bean
  @Qualifier(EXPORT_SERVICE_POINT_JSON_JOB_NAME)
  @JobScope
  public Job exportServicePointJsonJob(ItemReader<ServicePointVersion> itemReader,
      @Value("#{jobParameters[exportType]}") String exportType) {
    log.warn("------------------>: {}", exportType);
    return new JobBuilder(EXPORT_SERVICE_POINT_JSON_JOB_NAME, jobRepository)
        .listener(jobCompletionListener)
        .incrementer(new RunIdIncrementer())
        .flow(exportServicePointJsonStep(itemReader))
        .next(uploadFilesStep())
        .next(deleteFilesStep())
        .end()
        .build();
  }

  @Bean
  public FileUploadTasklet fileUploadTasklet() {
    return new FileUploadTasklet();
  }

  @Bean
  public FileDeletingTasklet fileDeletingTasklet() {
    return new FileDeletingTasklet();
  }

  @Bean
  public Step uploadFilesStep() {
    return new StepBuilder("uploadFiles", jobRepository)
        .tasklet(fileUploadTasklet(), transactionManager)
        .build();
  }

  @Bean
  public Step deleteFilesStep() {
    return new StepBuilder("deleteFiles", jobRepository)
        .tasklet(fileDeletingTasklet(), transactionManager)
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
