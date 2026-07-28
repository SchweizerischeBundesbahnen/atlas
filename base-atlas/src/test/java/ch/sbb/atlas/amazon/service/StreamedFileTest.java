package ch.sbb.atlas.amazon.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.ByteArrayInputStream;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.InputStreamResource;

class StreamedFileTest {

  @Test
  void shouldCreateStreamedFile() {
    // Given
    InputStreamResource resource = new InputStreamResource(new ByteArrayInputStream(new byte[0]));
    ContentLength contentLength = ContentLength.of(42L);

    // When
    StreamedFile streamedFile = new StreamedFile(resource, contentLength);

    // Then
    assertThat(streamedFile.resource()).isSameAs(resource);
    assertThat(streamedFile.contentLength()).isEqualTo(contentLength);
  }

  @Test
  void shouldThrowExceptionWhenResourceIsNull() {
    ContentLength contentLength = ContentLength.of(42L);
    // When / Then
    assertThatThrownBy(() -> new StreamedFile(null, contentLength))
        .isInstanceOf(NullPointerException.class)
        .hasMessageContaining("resource");
  }

  @Test
  void shouldThrowExceptionWhenContentLengthIsNull() {
    // Given
    InputStreamResource resource = new InputStreamResource(new ByteArrayInputStream(new byte[0]));

    // When / Then
    assertThatThrownBy(() -> new StreamedFile(resource, null))
        .isInstanceOf(NullPointerException.class)
        .hasMessageContaining("contentLength");
  }

}
