package ch.sbb.exportservice.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import ch.sbb.atlas.model.DateRange;
import ch.sbb.exportservice.model.ExportFilePathV2;
import ch.sbb.exportservice.model.ExportObjectV2;
import ch.sbb.exportservice.model.ExportTypeV2;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InvalidClassException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.repository.ExecutionContextSerializer;
import org.springframework.batch.core.repository.dao.DefaultExecutionContextSerializer;

class BatchExecutionContextSerializerConfigTest {

  private static final String FILE_PATH_KEY = "filePathV2";

  private final ExecutionContextSerializer serializer =
      new BatchExecutionContextSerializerConfig().executionContextSerializer();

  @Test
  void shouldRejectExportFilePathWithSpringBatchDefaultSerializer() throws IOException {
    // Given
    final byte[] executionContext = serialize(Map.of(FILE_PATH_KEY, exportFilePath()));

    // When
    final Throwable result = catchDeserialization(new DefaultExecutionContextSerializer(), executionContext);

    // Then
    assertThat(result).hasMessage("Failed to deserialize object")
        .cause().isInstanceOf(InvalidClassException.class)
        .hasMessageContaining("filter status: REJECTED");
  }

  @Test
  void shouldDeserializeExportFilePathWithAtlasSerializer() throws IOException {
    // Given
    final byte[] executionContext = serialize(Map.of(FILE_PATH_KEY, exportFilePath()));

    // When
    final Map<String, Object> result = serializer.deserialize(new ByteArrayInputStream(executionContext));

    // Then
    assertThat(result).containsOnlyKeys(FILE_PATH_KEY);
    assertThat(result.get(FILE_PATH_KEY)).isInstanceOf(ExportFilePathV2.class);
    assertThat(((ExportFilePathV2) result.get(FILE_PATH_KEY)).fileName())
        .isEqualTo("actual-date-service-point-2025-01-01");
  }

  @Test
  void shouldStillRejectTypesOutsideAllowListWithAtlasSerializer() throws IOException {
    // Given a serializable type that is neither allow-listed by Spring Batch nor an ATLAS type
    final byte[] executionContext = serialize(Map.of("attack", new File("/etc/passwd")));

    // When
    final Throwable result = catchDeserialization(serializer, executionContext);

    // Then
    assertThat(result).hasMessage("Failed to deserialize object")
        .cause().isInstanceOf(InvalidClassException.class)
        .hasMessageContaining("filter status: REJECTED");
  }

  @Test
  void shouldStillDeserializeTypesAllowedBySpringBatchDefault() throws IOException {
    // Given
    final byte[] executionContext = serialize(Map.of("traceId", "abc", "versions", 42));

    // When
    final Map<String, Object> result = serializer.deserialize(new ByteArrayInputStream(executionContext));

    // Then
    assertThat(result).containsEntry("traceId", "abc").containsEntry("versions", 42);
  }

  @Test
  void shouldRejectAtlasTypesOutsideExportServiceWithAtlasSerializer() throws IOException {
    // Given a ch.sbb type of another module, whose fields would all be allowed by the Spring Batch default pattern
    final byte[] executionContext = serialize(Map.of("dateRange", new DateRange(LocalDate.MIN, LocalDate.MAX)));

    // When
    final Throwable result = catchDeserialization(serializer, executionContext);

    // Then only the narrowed allow-list rejects it
    assertThat(result).hasMessage("Failed to deserialize object")
        .cause().isInstanceOf(InvalidClassException.class)
        .hasMessageContaining("filter status: REJECTED");
  }

  @Test
  void shouldNotWeakenSpringBatchDefaultFilterPattern() {
    // When
    final String result = BatchExecutionContextSerializerConfig.filterPattern();

    // Then the default pattern is kept verbatim, the additional type only takes precedence over it
    assertThat(result).startsWith("ch.sbb.exportservice.**;")
        .endsWith(DefaultExecutionContextSerializer.DEFAULT_FILTER_PATTERN);
  }

  @Test
  void shouldSupportEmptyExecutionContext() throws IOException {
    // Given
    final byte[] executionContext = serialize(Map.of());

    // When / Then
    assertThatCode(() -> serializer.deserialize(new ByteArrayInputStream(executionContext))).doesNotThrowAnyException();
  }

  private ExportFilePathV2 exportFilePath() {
    return ExportFilePathV2.getV2Builder(ExportObjectV2.SERVICE_POINT, ExportTypeV2.ACTUAL)
        .actualDate(LocalDate.of(2025, 1, 1))
        .build();
  }

  private byte[] serialize(Map<String, Object> executionContext) throws IOException {
    final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    // HashMap mirrors what Spring Batch persists for an ExecutionContext
    new DefaultExecutionContextSerializer().serialize(new HashMap<>(executionContext), outputStream);
    return outputStream.toByteArray();
  }

  private Throwable catchDeserialization(ExecutionContextSerializer usedSerializer, byte[] executionContext) {
    return assertThatExceptionOfType(IllegalArgumentException.class)
        .isThrownBy(() -> usedSerializer.deserialize(new ByteArrayInputStream(executionContext)))
        .actual();
  }

}
