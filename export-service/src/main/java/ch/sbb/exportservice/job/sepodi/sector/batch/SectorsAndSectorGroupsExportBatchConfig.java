package ch.sbb.exportservice.job.sepodi.sector.batch;

import static ch.sbb.exportservice.util.JobDescriptionConstant.EXPORT_SECTORS_AND_SECTOR_GROUPS_CSV_JOB_NAME;

import ch.sbb.atlas.amazon.service.FileService;
import ch.sbb.exportservice.job.sepodi.sector.entity.SectorAndSectorGroup;
import ch.sbb.exportservice.job.sepodi.sector.model.SectorAndSectorGroupCsvModel;
import ch.sbb.exportservice.job.sepodi.sector.processor.SectorsAndSectorGroupsCsvProcessor;
import ch.sbb.exportservice.job.sepodi.sector.sql.SectorsAndSectorGroupsSqlQueryUtil;
import ch.sbb.exportservice.job.sepodi.sector.sql.SectorsAndSectorGroupsRowMapper;
import ch.sbb.exportservice.job.sepodi.sector.writer.CsvSectorsAndSectorGroupsVersionWriter;
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
public class SectorsAndSectorGroupsExportBatchConfig {

  private final JobRepository jobRepository;
  private final PlatformTransactionManager transactionManager;
  private final JobCompletionListener jobCompletionListener;
  private final StepTracerListener stepTracerListener;
  private final CsvSectorsAndSectorGroupsVersionWriter csvSectorsAndSectorGroupsVersionWriter;

  private final FileService fileService;

  @Bean
  @StepScope
  public JdbcCursorItemReader<SectorAndSectorGroup> sectorsAndSectorGroupsReader(
      @Autowired @Qualifier("servicePointDataSource") DataSource dataSource,
      @Value("#{jobParameters[exportTypeV2]}") ExportTypeV2 exportTypeV2
  ) {
    JdbcCursorItemReader<SectorAndSectorGroup> itemReader = new JdbcCursorItemReader<>();
    itemReader.setDataSource(dataSource);
    itemReader.setSql(SectorsAndSectorGroupsSqlQueryUtil.getSqlQuery(exportTypeV2));
    itemReader.setFetchSize(StepUtil.FETCH_SIZE);
    itemReader.setRowMapper(new SectorsAndSectorGroupsRowMapper());
    return itemReader;
  }

  @Bean
  @Qualifier(EXPORT_SECTORS_AND_SECTOR_GROUPS_CSV_JOB_NAME)
  public Job exportSectorsAndSectorGroupsCsvJob(ItemReader<SectorAndSectorGroup> itemReader) {
    return new JobBuilder(EXPORT_SECTORS_AND_SECTOR_GROUPS_CSV_JOB_NAME, jobRepository)
        .listener(jobCompletionListener)
        .incrementer(new RunIdIncrementer())
        .flow(exportSectorsAndSectorGroupsCsvStep(itemReader))
        .next(uploadSectorsAndSectorGroupsCsvFileStep())
        .next(deleteSectorsAndSectorGroupsCsvFileStepV2())
        .end()
        .build();
  }

  @Bean
  public Step exportSectorsAndSectorGroupsCsvStep(ItemReader<SectorAndSectorGroup> itemReader) {
    String stepName = "exportSectorsAndSectorGroupsCsvStep";
    return new StepBuilder(stepName, jobRepository)
        .<SectorAndSectorGroup, SectorAndSectorGroupCsvModel>chunk(StepUtil.CHUNK_SIZE, transactionManager)
        .reader(itemReader)
        .processor(csvProcessor())
        .writer(csvSectorsAndSectorGroupsWriter(null))
        .faultTolerant()
        .backOffPolicy(StepUtil.getBackOffPolicy(stepName))
        .retryPolicy(StepUtil.getRetryPolicy(stepName))
        .listener(stepTracerListener)
        .build();
  }

  @Bean
  public SectorsAndSectorGroupsCsvProcessor csvProcessor() {
    return new SectorsAndSectorGroupsCsvProcessor();
  }

  @Bean
  @StepScope
  public FlatFileItemWriter<SectorAndSectorGroupCsvModel> csvSectorsAndSectorGroupsWriter(
      @Value("#{jobParameters[exportTypeV2]}") ExportTypeV2 exportTypeV2) {
    return csvSectorsAndSectorGroupsVersionWriter.csvWriter(ExportObjectV2.SECTORS_AND_SECTORGROUPS, exportTypeV2);
  }

  @Bean
  public Step uploadSectorsAndSectorGroupsCsvFileStep() {
    return new StepBuilder("uploadSectorsAndSectorGroupsCsvFileStep", jobRepository)
        .tasklet(uploadSectorsAndSectorGroupsCsvFileTasklet(null), transactionManager)
        .listener(stepTracerListener)
        .build();
  }

  @Bean
  @StepScope
  public UploadCsvFileTaskletV2 uploadSectorsAndSectorGroupsCsvFileTasklet(
      @Value("#{jobParameters[exportTypeV2]}") ExportTypeV2 exportTypeV2) {
    final ExportFilePathV2 filePathV2 = ExportFilePathV2.getV2Builder(ExportObjectV2.SECTORS_AND_SECTORGROUPS, exportTypeV2)
        .extension(ExportExtensionFileType.CSV_EXTENSION.getExtension())
        .systemDir(fileService.getDir())
        .build();
    return new UploadCsvFileTaskletV2(filePathV2);
  }

  @Bean
  public Step deleteSectorsAndSectorGroupsCsvFileStepV2() {
    return new StepBuilder("deleteSectorsAndSectorGroupsCsvFileStepV2", jobRepository)
        .tasklet(deleteSectorsAndSectorGroupsCsvTaskletV2(null), transactionManager)
        .listener(stepTracerListener)
        .build();
  }

  @Bean
  @StepScope
  public FileDeletingTaskletV2 deleteSectorsAndSectorGroupsCsvTaskletV2(
      @Value("#{jobExecutionContext[filePathV2]}") ExportFilePathV2 filePathV2) {
    return new FileDeletingTaskletV2(filePathV2);
  }

}
