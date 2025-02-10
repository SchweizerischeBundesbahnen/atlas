package ch.sbb.atlas.servicepointdirectory.service.servicepoint.bulk;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.api.servicepoint.CreateServicePointVersionModel;
import ch.sbb.atlas.imports.bulk.BulkImportUpdateContainer;
import ch.sbb.atlas.imports.model.create.ServicePointCreateCsvModel;
import ch.sbb.atlas.servicepoint.enumeration.MeanOfTransport;
import java.time.LocalDate;
import java.util.Set;
import org.junit.jupiter.api.Test;

class ServicePointBulkImportCreateTest {

  @Test
  void shouldMapServicePointCreateWithFreightServicePointNull() {
    BulkImportUpdateContainer<ServicePointCreateCsvModel> container =
        BulkImportUpdateContainer.<ServicePointCreateCsvModel>builder()
            .object(ServicePointCreateCsvModel.builder()
                .numberShort(7000)
                .validFrom(LocalDate.of(2014, 12, 14))
                .validTo(LocalDate.of(2021, 3, 31))
                .meansOfTransport(Set.of(MeanOfTransport.BUS))
                .freightServicePoint(null)
                .build())
            .build();

    CreateServicePointVersionModel result = ServicePointBulkImportCreate.apply(container);

    assertThat(result.getMeansOfTransport()).containsExactly(MeanOfTransport.BUS);
    assertThat(result.getServicePointGeolocation()).isNull();
    assertThat(result.getCategories()).isEmpty();
    assertThat(result.getDesignationLong()).isNull();
    assertThat(result.isFreightServicePoint()).isFalse();
  }

  @Test
  void shouldMapServicePointCreateWithFreightServicePointTrue() {
    BulkImportUpdateContainer<ServicePointCreateCsvModel> container =
        BulkImportUpdateContainer.<ServicePointCreateCsvModel>builder()
            .object(ServicePointCreateCsvModel.builder()
                .numberShort(7000)
                .validFrom(LocalDate.of(2014, 12, 14))
                .validTo(LocalDate.of(2021, 3, 31))
                .meansOfTransport(Set.of(MeanOfTransport.BUS))
                .freightServicePoint(true)
                .build())
            .build();

    CreateServicePointVersionModel result = ServicePointBulkImportCreate.apply(container);

    assertThat(result.getMeansOfTransport()).containsExactly(MeanOfTransport.BUS);
    assertThat(result.getServicePointGeolocation()).isNull();
    assertThat(result.getCategories()).isEmpty();
    assertThat(result.getDesignationLong()).isNull();
    assertThat(result.isFreightServicePoint()).isTrue();
  }

}