package ch.sbb.exportservice.config;

import java.io.ObjectInputFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.repository.ExecutionContextSerializer;
import org.springframework.batch.core.repository.dao.DefaultExecutionContextSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Extends the Spring Batch deserialization allow-list by the export-service packages.
 *
 * <p>Since Spring Batch 6.0.5 (fix for CVE-2026-47878) {@link DefaultExecutionContextSerializer} rejects every type that is not
 * covered by {@link DefaultExecutionContextSerializer#DEFAULT_FILTER_PATTERN}. Reading an ExecutionContext that contains an ATLAS
 * type therefore fails with {@code InvalidClassException: filter status: REJECTED}, wrapped into
 * {@code Failed to deserialize object}.
 *
 * <p>Note that only the read path is filtered: writing such a type still succeeds, so a plain data cleanup would not solve the
 * problem permanently.
 *
 * <p>The allow-list is deliberately limited to {@value #ALLOWED_TYPES} instead of all {@code ch.sbb} packages. Only
 * {@link ch.sbb.exportservice.model.ExportFilePathV2}, written by
 * {@link ch.sbb.exportservice.tasklet.upload.FileUploadTaskletV2}, is currently stored in an ExecutionContext. Storing a type of
 * another module requires extending this pattern, otherwise reading it fails after the next restart.
 */
@Slf4j
@Configuration
public class BatchExecutionContextSerializerConfig {

  private static final String ALLOWED_TYPES = "ch.sbb.exportservice.**";

  @Bean
  public ExecutionContextSerializer executionContextSerializer() {
    String filterPattern = filterPattern();
    log.info("Spring Batch ExecutionContext deserialization filter: {}", filterPattern);

    DefaultExecutionContextSerializer serializer = new DefaultExecutionContextSerializer();
    serializer.setObjectInputFilter(ObjectInputFilter.Config.createFilter(filterPattern));
    return serializer;
  }

  static String filterPattern() {
    return ALLOWED_TYPES + ";" + DefaultExecutionContextSerializer.DEFAULT_FILTER_PATTERN;
  }

}
