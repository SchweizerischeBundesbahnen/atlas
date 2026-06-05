package ch.sbb.exportservice.job.prm.wheelchairaccessibility.batch;

import static ch.sbb.exportservice.util.JobDescriptionConstant.EXPORT_WHEELCHAIR_ACCESSIBILITY_CSV_JOB_NAME;

import ch.sbb.atlas.amazon.service.FileService;
import ch.sbb.exportservice.job.prm.wheelchairaccessibility.service.WheelchairAccessibilityCalculationTasklet;
import ch.sbb.exportservice.listener.JobCompletionListener;
import ch.sbb.exportservice.listener.StepTracerListener;
import ch.sbb.exportservice.model.ExportExtensionFileType;
import ch.sbb.exportservice.model.ExportFilePathV2;
import ch.sbb.exportservice.model.ExportObjectV2;
import ch.sbb.exportservice.model.ExportTypeV2;
import ch.sbb.exportservice.tasklet.delete.FileDeletingTaskletV2;
import ch.sbb.exportservice.tasklet.upload.UploadCsvFileTaskletV2;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class WheelchariAccessibilityExportBatchConfig {

  private final JobRepository jobRepository;
  private final PlatformTransactionManager transactionManager;
  private final JobCompletionListener jobCompletionListener;
  private final StepTracerListener stepTracerListener;
  private final WheelchairAccessibilityCalculationTasklet wheelchairAccessibilityCalculationTasklet;
  private final FileService fileService;


  // --- CSV ---
  @Bean
  @Qualifier(EXPORT_WHEELCHAIR_ACCESSIBILITY_CSV_JOB_NAME)
  public Job exportWheelchairAccessibilityCsvJob(Step exportWheelchairAccessibilityCsvStep) {
    return new JobBuilder(EXPORT_WHEELCHAIR_ACCESSIBILITY_CSV_JOB_NAME, jobRepository)
        .listener(jobCompletionListener)
        .flow(exportWheelchairAccessibilityCsvStep)
        .next(uploadWheelchairAccessibilityCsvFileStep())
        .next(deleteWheelchairAccessibilityCsvFileStep())
        .end()
        .build();
  }

  @Bean
  public Step exportWheelchairAccessibilityCsvStep() {
    final String stepName = "exportWheelchairAccessibilityCsvStep";
    return new StepBuilder(stepName, jobRepository)
        .tasklet(wheelchairAccessibilityCalculationTasklet)
        .listener(stepTracerListener)
        .build();
  }

  // BEGIN: Upload Csv
  @Bean
  public Step uploadWheelchairAccessibilityCsvFileStep() {
    return new StepBuilder("uploadCsvFile", jobRepository)
        .tasklet(uploadWheelchairAccessibilityCsvFileTasklet(), transactionManager)
        .listener(stepTracerListener)
        .build();
  }

  @Bean
  @StepScope
  public UploadCsvFileTaskletV2 uploadWheelchairAccessibilityCsvFileTasklet() {
    final ExportFilePathV2 filePath = ExportFilePathV2.getV2Builder(ExportObjectV2.WHEELCHAIR_ACCESSIBILITY, ExportTypeV2.ACTUAL)
        .extension(ExportExtensionFileType.CSV_EXTENSION.getExtension())
        .systemDir(fileService.getDir())
        .build();
    return new UploadCsvFileTaskletV2(filePath);
  }
  // END: Upload Csv

  // BEGIN: Delete Csv
  @Bean
  public Step deleteWheelchairAccessibilityCsvFileStep() {
    return new StepBuilder("deleteCsvFile", jobRepository)
        .tasklet(deleteWheelchairAccessibilityCsvFileTasklet(null), transactionManager)
        .listener(stepTracerListener)
        .build();
  }

  @Bean
  @StepScope
  public FileDeletingTaskletV2 deleteWheelchairAccessibilityCsvFileTasklet(
      @Value("#{jobExecutionContext[filePathV2]}") ExportFilePathV2 filePathV2
  ) {
    return new FileDeletingTaskletV2(filePathV2);
  }
  // END: Delete Csv


}
