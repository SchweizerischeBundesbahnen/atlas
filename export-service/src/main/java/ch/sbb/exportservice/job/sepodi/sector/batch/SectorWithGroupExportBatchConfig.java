package ch.sbb.exportservice.job.sepodi.sector.batch;

import static ch.sbb.exportservice.util.JobDescriptionConstant.EXPORT_SECTOR_WITH_GROUP_CSV_JOB_NAME;

import ch.sbb.atlas.amazon.service.FileService;
import ch.sbb.exportservice.job.sepodi.sector.entity.SectorWithGroupVersion;
import ch.sbb.exportservice.job.sepodi.sector.model.SectorWithGroupVersionCsvModel;
import ch.sbb.exportservice.job.sepodi.sector.processor.SectorWithGroupCsvProcessor;
import ch.sbb.exportservice.job.sepodi.sector.sql.SectorWithGroupSqlQueryUtil;
import ch.sbb.exportservice.job.sepodi.sector.sql.SectorWithGroupVersionRowMapper;
import ch.sbb.exportservice.job.sepodi.sector.writer.CsvSectorWithGroupVersionWriter;
import ch.sbb.exportservice.listener.JobCompletionListener;
import ch.sbb.exportservice.listener.StepTracerListener;
import ch.sbb.exportservice.model.ExportExtensionFileType;
import ch.sbb.exportservice.model.ExportFilePathV2;
import ch.sbb.exportservice.model.ExportObjectV2;
import ch.sbb.exportservice.model.ExportTypeV2;
import ch.sbb.exportservice.tasklet.delete.FileDeletingTaskletV2;
import ch.sbb.exportservice.tasklet.upload.UploadCsvFileTaskletV2;
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
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class SectorWithGroupExportBatchConfig {

  private final JobRepository jobRepository;
  private final PlatformTransactionManager transactionManager;
  private final JobCompletionListener jobCompletionListener;
  private final StepTracerListener stepTracerListener;
  private final CsvSectorWithGroupVersionWriter csvSectorWithGroupVersionWriter;

  private final FileService fileService;

  @Bean
  @StepScope
  public JdbcCursorItemReader<SectorWithGroupVersion> sectorWithGroupReader(
      @Autowired @Qualifier("servicePointDataSource") DataSource dataSource,
      @Value("#{jobParameters[exportTypeV2]}") ExportTypeV2 exportTypeV2
  ) {
    JdbcCursorItemReader<SectorWithGroupVersion> itemReader = new JdbcCursorItemReader<>();
    itemReader.setDataSource(dataSource);
    itemReader.setSql(SectorWithGroupSqlQueryUtil.getSqlQuery(exportTypeV2));
    itemReader.setFetchSize(StepUtil.FETCH_SIZE);
    itemReader.setRowMapper(new SectorWithGroupVersionRowMapper());
    return itemReader;
  }

  @Bean
  @Qualifier(EXPORT_SECTOR_WITH_GROUP_CSV_JOB_NAME)
  public Job exportSectorCsvJob(ItemReader<SectorWithGroupVersion> itemReader) {
    return new JobBuilder(EXPORT_SECTOR_WITH_GROUP_CSV_JOB_NAME, jobRepository)
        .listener(jobCompletionListener)
        .incrementer(new RunIdIncrementer())
        .flow(exportSectorCsvStep(itemReader))
        .next(uploadSectorWithGroupCsvFileStep())
        .next(deleteSectorWithGroupCsvFileStepV2())
        .end()
        .build();
  }

  @Bean
  public Step exportSectorCsvStep(ItemReader<SectorWithGroupVersion> itemReader) {
    String stepName = "exportSectorCsvStep";
    return new StepBuilder(stepName, jobRepository)
        .<SectorWithGroupVersion, SectorWithGroupVersionCsvModel>chunk(StepUtil.CHUNK_SIZE, transactionManager)
        .reader(itemReader)
        .processor(csvProcessor())
        .writer(csvSectorWithGroupWriter(null))
        .faultTolerant()
        .backOffPolicy(StepUtil.getBackOffPolicy(stepName))
        .retryPolicy(StepUtil.getRetryPolicy(stepName))
        .listener(stepTracerListener)
        .build();
  }

  @Bean
  public SectorWithGroupCsvProcessor csvProcessor() {
    return new SectorWithGroupCsvProcessor();
  }

  @Bean
  @StepScope
  public FlatFileItemWriter<SectorWithGroupVersionCsvModel> csvSectorWithGroupWriter(
      @Value("#{jobParameters[exportTypeV2]}") ExportTypeV2 exportTypeV2) {
    return csvSectorWithGroupVersionWriter.csvWriter(ExportObjectV2.SECTOR, exportTypeV2);
  }

  @Bean
  public Step uploadSectorWithGroupCsvFileStep() {
    return new StepBuilder("uploadSectorWithGroupCsvFileStep", jobRepository)
        .tasklet(uploadSectorWithGroupCsvFileTasklet(null), transactionManager)
        .listener(stepTracerListener)
        .build();
  }

  @Bean
  @StepScope
  public UploadCsvFileTaskletV2 uploadSectorWithGroupCsvFileTasklet(
      @Value("#{jobParameters[exportTypeV2]}") ExportTypeV2 exportTypeV2) {
    final ExportFilePathV2 filePathV2 = ExportFilePathV2.getV2Builder(ExportObjectV2.SECTOR, exportTypeV2)
        .extension(ExportExtensionFileType.CSV_EXTENSION.getExtension())
        .systemDir(fileService.getDir())
        .build();
    return new UploadCsvFileTaskletV2(filePathV2);
  }

  @Bean
  public Step deleteSectorWithGroupCsvFileStepV2() {
    return new StepBuilder("deleteSectorWithGroupCsvFileStepV2", jobRepository)
        .tasklet(deleteSectorWithGroupCsvTaskletV2(null), transactionManager)
        .listener(stepTracerListener)
        .build();
  }

  @Bean
  @StepScope
  public FileDeletingTaskletV2 deleteSectorWithGroupCsvTaskletV2(
      @Value("#{jobExecutionContext[filePathV2]}") ExportFilePathV2 filePathV2) {
    return new FileDeletingTaskletV2(filePathV2);
  }

}
