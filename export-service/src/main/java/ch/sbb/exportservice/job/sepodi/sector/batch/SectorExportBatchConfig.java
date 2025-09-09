package ch.sbb.exportservice.job.sepodi.sector.batch;

import static ch.sbb.exportservice.util.JobDescriptionConstant.EXPORT_SECTOR_JSON_JOB_NAME;

import ch.sbb.atlas.amazon.service.FileService;
import ch.sbb.atlas.api.servicepoint.sector.SectorVersionModel;
import ch.sbb.exportservice.job.sepodi.sector.entity.SectorVersion;
import ch.sbb.exportservice.job.sepodi.sector.processor.SectorJsonProcessor;
import ch.sbb.exportservice.job.sepodi.sector.sql.SectorSqlQueryUtil;
import ch.sbb.exportservice.job.sepodi.sector.sql.SectorVersionRowMapper;
import ch.sbb.exportservice.job.sepodi.sector.writer.JsonSectorVersionWriter;
import ch.sbb.exportservice.listener.JobCompletionListener;
import ch.sbb.exportservice.listener.StepTracerListener;
import ch.sbb.exportservice.model.ExportExtensionFileType;
import ch.sbb.exportservice.model.ExportFilePathV2;
import ch.sbb.exportservice.model.ExportObjectV2;
import ch.sbb.exportservice.model.ExportTypeV2;
import ch.sbb.exportservice.tasklet.delete.FileDeletingTaskletV2;
import ch.sbb.exportservice.tasklet.upload.UploadJsonFileTaskletV2;
import ch.sbb.exportservice.util.StepUtil;
import javax.sql.DataSource;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.json.JsonFileItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class SectorExportBatchConfig {

  private final JobRepository jobRepository;
  private final PlatformTransactionManager transactionManager;
  private final JobCompletionListener jobCompletionListener;
  private final StepTracerListener stepTracerListener;
  private final JsonSectorVersionWriter jsonSectorVersionWriter;

  private final FileService fileService;

  @Bean
  @StepScope
  public JdbcCursorItemReader<SectorVersion> sectorReader(
      @Autowired @Qualifier("servicePointDataSource") DataSource dataSource,
      @Value("#{jobParameters[exportTypeV2]}") ExportTypeV2 exportTypeV2
  ) {
    JdbcCursorItemReader<SectorVersion> itemReader = new JdbcCursorItemReader<>();
    itemReader.setDataSource(dataSource);
    itemReader.setSql(SectorSqlQueryUtil.getSqlQuery(exportTypeV2));
    itemReader.setFetchSize(StepUtil.FETCH_SIZE);
    itemReader.setRowMapper(new SectorVersionRowMapper());
    return itemReader;
  }

  @Bean
  @Qualifier(EXPORT_SECTOR_JSON_JOB_NAME)
  public Job exportSectorJsonJob(ItemReader<SectorVersion> itemReader) {
    return new JobBuilder(EXPORT_SECTOR_JSON_JOB_NAME, jobRepository)
        .listener(jobCompletionListener)
        .incrementer(new RunIdIncrementer())
        .flow(exportSectorJsonStep(itemReader))
        .next(uploadLoadingPointJsonFileStep())
        .next(deleteSectorJsonFileStepV2())
        .end()
        .build();
  }

  @Bean
  public Step exportSectorJsonStep(ItemReader<SectorVersion> itemReader) {
    String stepName = "exportSectorJsonStep";
    return new StepBuilder(stepName, jobRepository)
        .<SectorVersion, SectorVersionModel>chunk(StepUtil.CHUNK_SIZE, transactionManager)
        .reader(itemReader)
        .processor(sectorJsonProcessor())
        .writer(sectorJsonFileItemWriter(null))
        .faultTolerant()
        .backOffPolicy(StepUtil.getBackOffPolicy(stepName))
        .retryPolicy(StepUtil.getRetryPolicy(stepName))
        .listener(stepTracerListener)
        .build();
  }

  @Bean
  public SectorJsonProcessor sectorJsonProcessor() {
    return new SectorJsonProcessor();
  }

  @Bean
  @StepScope
  public JsonFileItemWriter<SectorVersionModel> sectorJsonFileItemWriter(
      @Value("#{jobParameters[exportTypeV2]}") ExportTypeV2 exportTypeV2) {
    return jsonSectorVersionWriter.getWriter(ExportObjectV2.SECTOR, exportTypeV2);
  }

  @Bean
  public Step uploadLoadingPointJsonFileStep() {
    return new StepBuilder("uploadSectorJsonFile", jobRepository)
        .tasklet(uploadSectorJsonFileTasklet(null), transactionManager)
        .listener(stepTracerListener)
        .build();
  }

  @Bean
  @StepScope
  public UploadJsonFileTaskletV2 uploadSectorJsonFileTasklet(
      @Value("#{jobParameters[exportTypeV2]}") ExportTypeV2 exportTypeV2) {
    final ExportFilePathV2 filePathV2 = ExportFilePathV2.getV2Builder(ExportObjectV2.SECTOR, exportTypeV2)
        .extension(ExportExtensionFileType.JSON_EXTENSION.getExtension())
        .systemDir(fileService.getDir())
        .build();
    return new UploadJsonFileTaskletV2(filePathV2);
  }

  @Bean
  public Step deleteSectorJsonFileStepV2() {
    return new StepBuilder("deleteJsonFileV2", jobRepository)
        .tasklet(deleteSectorJsonTaskletV2(null), transactionManager)
        .listener(stepTracerListener)
        .build();
  }

  @Bean
  @StepScope
  public FileDeletingTaskletV2 deleteSectorJsonTaskletV2(
      @Value("#{jobExecutionContext[filePathV2]}") ExportFilePathV2 filePathV2) {
    return new FileDeletingTaskletV2(filePathV2);
  }

}
