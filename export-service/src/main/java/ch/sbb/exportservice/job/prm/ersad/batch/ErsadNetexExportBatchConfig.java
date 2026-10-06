package ch.sbb.exportservice.job.prm.ersad.batch;

import static ch.sbb.exportservice.util.JobDescriptionConstant.EXPORT_ERSAD_NETEX_XML_JOB_NAME;

import ch.sbb.atlas.s3.service.FileService;
import ch.sbb.exportservice.job.prm.ersad.service.ErsadNetexGenerationTasklet;
import ch.sbb.exportservice.listener.JobCompletionListener;
import ch.sbb.exportservice.listener.StepTracerListener;
import ch.sbb.exportservice.model.ExportExtensionFileType;
import ch.sbb.exportservice.model.ExportFilePathV2;
import ch.sbb.exportservice.model.ExportObjectV2;
import ch.sbb.exportservice.model.ExportTypeV2;
import ch.sbb.exportservice.tasklet.delete.FileDeletingTaskletV2;
import ch.sbb.exportservice.tasklet.upload.UploadXmlFileTaskletV2;
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
public class ErsadNetexExportBatchConfig {

  private final JobRepository jobRepository;
  private final PlatformTransactionManager transactionManager;
  private final JobCompletionListener jobCompletionListener;
  private final StepTracerListener stepTracerListener;
  private final ErsadNetexGenerationTasklet ersadNetexGenerationTasklet;
  private final FileService fileService;

  @Bean
  @Qualifier(EXPORT_ERSAD_NETEX_XML_JOB_NAME)
  public Job exportErsadNetexXmlJob(Step exportErsadNetexXmlStep) {
    return new JobBuilder(EXPORT_ERSAD_NETEX_XML_JOB_NAME, jobRepository)
        .listener(jobCompletionListener)
        .flow(exportErsadNetexXmlStep)
        .next(uploadErsadNetexXmlFileStep())
        .next(deleteErsadNetexXmlFileStep())
        .end()
        .build();
  }

  @Bean
  public Step exportErsadNetexXmlStep() {
    final String stepName = "exportErsadNetexXmlStep";
    return new StepBuilder(stepName, jobRepository)
        .tasklet(ersadNetexGenerationTasklet)
        .listener(stepTracerListener)
        .build();
  }

  @Bean
  public Step uploadErsadNetexXmlFileStep() {
    return new StepBuilder("uploadXmlFile", jobRepository)
        .tasklet(uploadErsadNetexXmlFileTasklet(), transactionManager)
        .listener(stepTracerListener)
        .build();
  }

  @Bean
  @StepScope
  public UploadXmlFileTaskletV2 uploadErsadNetexXmlFileTasklet() {
    final ExportFilePathV2 filePath = ExportFilePathV2.getV2Builder(ExportObjectV2.ERSAD_NETEX, ExportTypeV2.ACTUAL)
        .extension(ExportExtensionFileType.XML_EXTENSION.getExtension())
        .systemDir(fileService.getDir())
        .build();
    return new UploadXmlFileTaskletV2(filePath);
  }

  @Bean
  public Step deleteErsadNetexXmlFileStep() {
    return new StepBuilder("deleteXmlFile", jobRepository)
        .tasklet(deleteErsadNetexXmlFileTasklet(null), transactionManager)
        .listener(stepTracerListener)
        .build();
  }

  @Bean
  @StepScope
  public FileDeletingTaskletV2 deleteErsadNetexXmlFileTasklet(
      @Value("#{jobExecutionContext[filePathV2]}") ExportFilePathV2 filePathV2
  ) {
    return new FileDeletingTaskletV2(filePathV2);
  }

}
