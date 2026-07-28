package ch.sbb.exportservice.job.sepodi.servicepoint.processor;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.servicepoint.Country;
import ch.sbb.atlas.servicepoint.ServicePointNumber;
import ch.sbb.exportservice.job.sepodi.SharedBusinessOrganisation;
import ch.sbb.exportservice.job.sepodi.servicepoint.entity.ServicePointVersion;
import ch.sbb.exportservice.job.sepodi.servicepoint.entity.ServicePointVersion.ServicePointVersionBuilder;
import ch.sbb.exportservice.job.sepodi.servicepoint.model.ServicePointVersionCsvModel;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import org.junit.jupiter.api.Test;

class ServicePointVersionCsvProcessorTest {

  private final ServicePointVersionCsvProcessor processor = new ServicePointVersionCsvProcessor();

  private ServicePointVersionBuilder<?, ?> baseBuilder() {
    return ServicePointVersion.builder()
        .number(ServicePointNumber.ofNumberWithoutCheckDigit(8500000))
        .country(Country.GERMANY_BUS)
        .validFrom(LocalDate.of(2024, Month.JANUARY, 1))
        .validTo(LocalDate.of(2024, Month.DECEMBER, 31))
        .creationDate(LocalDateTime.of(2024, Month.JANUARY, 1, 10, 0))
        .editionDate(LocalDateTime.of(2024, Month.JANUARY, 2, 10, 0))
        .sharedBusinessOrganisation(SharedBusinessOrganisation.builder().businessOrganisation("ch:1:sboid:1").build());
  }

  @Test
  void shouldMapGlobalIdToCsvModel() {
    // Given
    ServicePointVersion version = baseBuilder().globalId("de:05770:1282").build();

    // When
    ServicePointVersionCsvModel result = processor.process(version);

    // Then
    assertThat(result.getGlobalId()).isEqualTo("de:05770:1282");
  }

  @Test
  void shouldMapNullGlobalIdToCsvModel() {
    // Given
    ServicePointVersion version = baseBuilder().globalId(null).build();

    // When
    ServicePointVersionCsvModel result = processor.process(version);

    // Then
    assertThat(result.getGlobalId()).isNull();
  }

}
