package ch.sbb.exportservice.job.sepodi.servicepoint.processor;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.api.servicepoint.ReadServicePointVersionModel;
import ch.sbb.atlas.servicepoint.ServicePointNumber;
import ch.sbb.exportservice.job.sepodi.SharedBusinessOrganisation;
import ch.sbb.exportservice.job.sepodi.servicepoint.entity.ServicePointVersion;
import org.junit.jupiter.api.Test;

class ServicePointVersionJsonProcessorTest {

  private final ServicePointVersionJsonProcessor processor = new ServicePointVersionJsonProcessor();

  @Test
  void shouldMapGlobalIdToJsonModel() {
    // Given
    ServicePointVersion version = ServicePointVersion.builder()
        .number(ServicePointNumber.ofNumberWithoutCheckDigit(8500000))
        .sharedBusinessOrganisation(SharedBusinessOrganisation.builder().businessOrganisation("ch:1:sboid:1").build())
        .globalId("de:05770:1282")
        .build();

    // When
    ReadServicePointVersionModel result = processor.process(version);

    // Then
    assertThat(result.getGlobalId()).isEqualTo("de:05770:1282");
  }

  @Test
  void shouldMapNullGlobalIdToJsonModel() {
    // Given
    ServicePointVersion version = ServicePointVersion.builder()
        .number(ServicePointNumber.ofNumberWithoutCheckDigit(8500000))
        .sharedBusinessOrganisation(SharedBusinessOrganisation.builder().businessOrganisation("ch:1:sboid:1").build())
        .globalId(null)
        .build();

    // When
    ReadServicePointVersionModel result = processor.process(version);

    // Then
    assertThat(result.getGlobalId()).isNull();
  }

}
