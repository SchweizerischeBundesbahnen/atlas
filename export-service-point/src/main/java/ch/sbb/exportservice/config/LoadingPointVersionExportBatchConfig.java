package ch.sbb.exportservice.config;

import static ch.sbb.exportservice.utils.JobDescriptionConstants.EXPORT_LOADING_POINT_CSV_JOB_NAME;
import static ch.sbb.exportservice.utils.JobDescriptionConstants.EXPORT_LOADING_POINT_JSON_JOB_NAME;

import ch.sbb.exportservice.entity.LoadingPointVersion;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemReader;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LoadingPointVersionExportBatchConfig {

  @Bean
  @Qualifier(EXPORT_LOADING_POINT_CSV_JOB_NAME)
  public Job exportLoadingPointCsvJob(ItemReader<LoadingPointVersion> itemReader) {
    return new JobBuilder(EXPORT_LOADING_POINT_CSV_JOB_NAME, jobRepository)
        .listener(jobCompletionListener)
        .incrementer(new RunIdIncrementer())
        .flow(exportTrafficPointElementCsvStep(itemReader))
        .next(uploadTrafficPointElementCsvFileStep())
        .next(deleteTrafficPointElementCsvFileStep())
        .end()
        .build();
  }

  @Bean
  @Qualifier(EXPORT_LOADING_POINT_JSON_JOB_NAME)
  public Job exportLoadingPointJsonJob(ItemReader<LoadingPointVersion> itemReader) {
    return new JobBuilder(EXPORT_LOADING_POINT_JSON_JOB_NAME, jobRepository)
        .listener(jobCompletionListener)
        .incrementer(new RunIdIncrementer())
        .flow(exportTrafficPointElementJsonStep(itemReader))
        .next(uploadTrafficPointElementJsonFileStep())
        .next(deleteTrafficPointElementJsonFileStep())
        .end()
        .build();
  }

}
