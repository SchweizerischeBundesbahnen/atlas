package ch.sbb.exportservice.job.sepodi.sectorgroup.batch;

import static ch.sbb.exportservice.util.JobDescriptionConstant.EXPORT_SECTOR_GROUP_JSON_JOB_NAME;

import ch.sbb.atlas.amazon.service.FileService;
import ch.sbb.atlas.api.servicepoint.sector.SectorGroupVersionModel;
import ch.sbb.exportservice.job.sepodi.sectorgroup.entity.SectorGroupVersion;
import ch.sbb.exportservice.job.sepodi.sectorgroup.processor.SectorGroupJsonProcessor;
import ch.sbb.exportservice.job.sepodi.sectorgroup.sql.SectorGroupSqlQueryUtil;
import ch.sbb.exportservice.job.sepodi.sectorgroup.sql.SectorGroupVersionRowMapper;
import ch.sbb.exportservice.job.sepodi.sectorgroup.writer.JsonSectorGroupVersionWriter;
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
public class SectorGroupExportBatchConfig {

  private final JobRepository jobRepository;
  private final PlatformTransactionManager transactionManager;
  private final JobCompletionListener jobCompletionListener;
  private final StepTracerListener stepTracerListener;
  private final JsonSectorGroupVersionWriter jsonSectorGroupVersionWriter;

  private final FileService fileService;

  @Bean
  @StepScope
  public JdbcCursorItemReader<SectorGroupVersion> sectorGroupReader(
      @Autowired @Qualifier("servicePointDataSource") DataSource dataSource,
      @Value("#{jobParameters[exportTypeV2]}") ExportTypeV2 exportTypeV2
  ) {
    JdbcCursorItemReader<SectorGroupVersion> itemReader = new JdbcCursorItemReader<>();
    itemReader.setDataSource(dataSource);
    itemReader.setSql(SectorGroupSqlQueryUtil.getSqlQuery(exportTypeV2));
    itemReader.setFetchSize(StepUtil.FETCH_SIZE);
    itemReader.setRowMapper(new SectorGroupVersionRowMapper());
    return itemReader;
  }

  @Bean
  @Qualifier(EXPORT_SECTOR_GROUP_JSON_JOB_NAME)
  public Job exportSectorGroupJsonJob(ItemReader<SectorGroupVersion> itemReader) {
    return new JobBuilder(EXPORT_SECTOR_GROUP_JSON_JOB_NAME, jobRepository)
        .listener(jobCompletionListener)
        .incrementer(new RunIdIncrementer())
        .flow(exportSectorGroupJsonStep(itemReader))
        .next(uploadSectorGroupJsonFileStep())
        .next(deleteSectorGroupJsonFileStepV2())
        .end()
        .build();
  }

  @Bean
  public Step exportSectorGroupJsonStep(ItemReader<SectorGroupVersion> itemReader) {
    String stepName = "exportSectorGroupJsonStep";
    return new StepBuilder(stepName, jobRepository)
        .<SectorGroupVersion, SectorGroupVersionModel>chunk(StepUtil.CHUNK_SIZE, transactionManager)
        .reader(itemReader)
        .processor(sectorGroupJsonProcessor())
        .writer(sectorGroupJsonFileItemWriter(null))
        .faultTolerant()
        .backOffPolicy(StepUtil.getBackOffPolicy(stepName))
        .retryPolicy(StepUtil.getRetryPolicy(stepName))
        .listener(stepTracerListener)
        .build();
  }

  @Bean
  public SectorGroupJsonProcessor sectorGroupJsonProcessor() {
    return new SectorGroupJsonProcessor();
  }

  @Bean
  @StepScope
  public JsonFileItemWriter<SectorGroupVersionModel> sectorGroupJsonFileItemWriter(
      @Value("#{jobParameters[exportTypeV2]}") ExportTypeV2 exportTypeV2) {
    return jsonSectorGroupVersionWriter.getWriter(ExportObjectV2.SECTOR_GROUP, exportTypeV2);
  }

  @Bean
  public Step uploadSectorGroupJsonFileStep() {
    return new StepBuilder("uploadSectorGroupJsonFile", jobRepository)
        .tasklet(uploadSectorGroupJsonFileTasklet(null), transactionManager)
        .listener(stepTracerListener)
        .build();
  }

  @Bean
  @StepScope
  public UploadJsonFileTaskletV2 uploadSectorGroupJsonFileTasklet(
      @Value("#{jobParameters[exportTypeV2]}") ExportTypeV2 exportTypeV2) {
    final ExportFilePathV2 filePathV2 = ExportFilePathV2.getV2Builder(ExportObjectV2.SECTOR_GROUP, exportTypeV2)
        .extension(ExportExtensionFileType.JSON_EXTENSION.getExtension())
        .systemDir(fileService.getDir())
        .build();
    return new UploadJsonFileTaskletV2(filePathV2);
  }

  @Bean
  public Step deleteSectorGroupJsonFileStepV2() {
    return new StepBuilder("deleteJsonFileV2", jobRepository)
        .tasklet(deleteSectorGroupJsonTaskletV2(null), transactionManager)
        .listener(stepTracerListener)
        .build();
  }

  @Bean
  @StepScope
  public FileDeletingTaskletV2 deleteSectorGroupJsonTaskletV2(
      @Value("#{jobExecutionContext[filePathV2]}") ExportFilePathV2 filePathV2) {
    return new FileDeletingTaskletV2(filePathV2);
  }

}
