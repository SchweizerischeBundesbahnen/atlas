package ch.sbb.exportservice.job.prm.recording.obligation.batch;

import static ch.sbb.exportservice.util.JobDescriptionConstant.EXPORT_RECORDING_OBLIGATION_CSV_JOB_NAME;

import ch.sbb.atlas.amazon.service.FileService;
import ch.sbb.exportservice.job.prm.recording.obligation.entity.RecordingObligation;
import ch.sbb.exportservice.job.prm.recording.obligation.model.RecordingObligationCsvModel;
import ch.sbb.exportservice.job.prm.recording.obligation.processor.RecordingObligationCsvProcessor;
import ch.sbb.exportservice.job.prm.recording.obligation.sql.RecordingObligationRowMapper;
import ch.sbb.exportservice.job.prm.recording.obligation.sql.RecordingObligationSqlQueryUtil;
import ch.sbb.exportservice.job.prm.recording.obligation.writer.CsvRecordingObligationCsvModelWriter;
import ch.sbb.exportservice.listener.JobCompletionListener;
import ch.sbb.exportservice.listener.StepTracerListener;
import ch.sbb.exportservice.model.ExportExtensionFileType;
import ch.sbb.exportservice.model.ExportFilePathV2;
import ch.sbb.exportservice.model.ExportObjectV2;
import ch.sbb.exportservice.model.ExportTypeV2;
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
public class RecordingObligationExportBatchConfig {

  private final JobRepository jobRepository;
  private final PlatformTransactionManager transactionManager;
  private final JobCompletionListener jobCompletionListener;
  private final StepTracerListener stepTracerListener;
  private final CsvRecordingObligationCsvModelWriter csvRecordingObligationCsvModelWriter;

  private final FileService fileService;

  @Bean
  @StepScope
  public JdbcCursorItemReader<RecordingObligation> recordingObligationReader(
      @Autowired @Qualifier("prmDataSource") DataSource dataSource
  ) {
    JdbcCursorItemReader<RecordingObligation> itemReader = new JdbcCursorItemReader<>();
    itemReader.setDataSource(dataSource);
    itemReader.setSql(RecordingObligationSqlQueryUtil.getSqlQuery());
    itemReader.setFetchSize(StepUtil.FETCH_SIZE);
    itemReader.setRowMapper(new RecordingObligationRowMapper());
    return itemReader;
  }

  // --- CSV ---
  @Bean
  @Qualifier(EXPORT_RECORDING_OBLIGATION_CSV_JOB_NAME)
  public Job recordingObligationCsvJob(ItemReader<RecordingObligation> itemReader) {
    return new JobBuilder(EXPORT_RECORDING_OBLIGATION_CSV_JOB_NAME, jobRepository)
        .listener(jobCompletionListener)
        .incrementer(new RunIdIncrementer())
        .flow(exportRecordingObligationCsvStep(itemReader))
        .next(uploadRecordingObligationCsvFileStepV2())
        .end()
        .build();
  }

  @Bean
  public Step exportRecordingObligationCsvStep(ItemReader<RecordingObligation> itemReader) {
    final String stepName = "exportRecordingObligationCsvStep";
    return new StepBuilder(stepName, jobRepository)
        .<RecordingObligation, RecordingObligationCsvModel>chunk(StepUtil.CHUNK_SIZE, transactionManager)
        .reader(itemReader)
        .processor(recordingObligationCsvProcessor())
        .writer(recordingObligationCsvWriter(null))
        .faultTolerant()
        .backOffPolicy(StepUtil.getBackOffPolicy(stepName))
        .retryPolicy(StepUtil.getRetryPolicy(stepName))
        .listener(stepTracerListener)
        .build();
  }

  @Bean
  public RecordingObligationCsvProcessor recordingObligationCsvProcessor() {
    return new RecordingObligationCsvProcessor();
  }

  @Bean
  @StepScope
  public FlatFileItemWriter<RecordingObligationCsvModel> recordingObligationCsvWriter(
      @Value("#{jobParameters[exportTypeV2]}") ExportTypeV2 exportTypeV2
  ) {
    return csvRecordingObligationCsvModelWriter.csvWriter(ExportObjectV2.RECORDING_OBLIGATION, exportTypeV2);
  }

  // BEGIN: Upload Csv V2
  @Bean
  public Step uploadRecordingObligationCsvFileStepV2() {
    return new StepBuilder("uploadCsvFileV2", jobRepository)
        .tasklet(uploadRecordingObligationCsvFileTaskletV2(null), transactionManager)
        .listener(stepTracerListener)
        .build();
  }

  @Bean
  @StepScope
  public UploadCsvFileTaskletV2 uploadRecordingObligationCsvFileTaskletV2(
      @Value("#{jobParameters[exportTypeV2]}") ExportTypeV2 exportTypeV2) {
    final ExportFilePathV2 filePath = ExportFilePathV2.getV2Builder(ExportObjectV2.RECORDING_OBLIGATION, exportTypeV2)
        .extension(ExportExtensionFileType.CSV_EXTENSION.getExtension())
        .systemDir(fileService.getDir())
        .build();
    return new UploadCsvFileTaskletV2(filePath);
  }
  // END: Upload Csv V2

}
