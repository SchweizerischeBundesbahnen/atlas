package ch.sbb.atlas.servicepointdirectory.service.trafficpoint.bulk;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.api.servicepoint.CreateTrafficPointElementVersionModel;
import ch.sbb.atlas.api.servicepoint.GeolocationBaseCreateModel;
import ch.sbb.atlas.api.servicepoint.SpatialReference;
import ch.sbb.atlas.imports.bulk.BulkImportUpdateContainer;
import ch.sbb.atlas.imports.model.create.TrafficPointCreateCsvModel;
import ch.sbb.atlas.servicepoint.enumeration.TrafficPointElementType;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class TrafficPointElementBulkImportCreateTest {

  @Test
  void shouldMapFromCsvToCreateModelWithStopPointSloid() {
    BulkImportUpdateContainer<TrafficPointCreateCsvModel> container =
        BulkImportUpdateContainer.<TrafficPointCreateCsvModel>builder()
            .object(TrafficPointCreateCsvModel.builder()
                .sloid("ch:1:sloid:7000:1:2")
                .validFrom(LocalDate.of(2021, 4, 1))
                .validTo(LocalDate.of(2099, 12, 31))
                .trafficPointElementType(TrafficPointElementType.BOARDING_PLATFORM)
                .parentSloid("ch:1:sloid:7000:1")
                .stopPointSloid("ch:1:sloid:7000")
                .designation("Perron 3")
                .designationOperational("CAMPSTR2")
                .length(12.0)
                .boardingAreaHeight(16.0)
                .compassDirection(278.0)
                .east(2600037.945)
                .north(1199749.812)
                .spatialReference(SpatialReference.LV95)
                .height(540.2)
                .build())
            .build();

    CreateTrafficPointElementVersionModel expected = CreateTrafficPointElementVersionModel.builder()
        .sloid("ch:1:sloid:7000:1:2")
        .validFrom(LocalDate.of(2021, 4, 1))
        .validTo(LocalDate.of(2099, 12, 31))
        .trafficPointElementType(TrafficPointElementType.BOARDING_PLATFORM)
        .numberWithoutCheckDigit(8507000)
        .parentSloid("ch:1:sloid:7000:1")
        .designation("Perron 3")
        .designationOperational("CAMPSTR2")
        .length(12.0)
        .boardingAreaHeight(16.0)
        .compassDirection(278.0)
        .trafficPointElementGeolocation(GeolocationBaseCreateModel.builder()
            .east(2600037.945)
            .north(1199749.812)
            .spatialReference(SpatialReference.LV95)
            .height(540.2)
            .build())
        .build();

    CreateTrafficPointElementVersionModel result = TrafficPointElementBulkImportCreate.apply(container);
    assertThat(result).usingRecursiveComparison().isEqualTo(expected);
  }

  @Test
  void shouldMapFromCsvToCreateModelWithStopPointNumber() {
    BulkImportUpdateContainer<TrafficPointCreateCsvModel> container =
        BulkImportUpdateContainer.<TrafficPointCreateCsvModel>builder()
            .object(TrafficPointCreateCsvModel.builder()
                .sloid("ch:1:sloid:7000:1:2")
                .validFrom(LocalDate.of(2021, 4, 1))
                .validTo(LocalDate.of(2099, 12, 31))
                .trafficPointElementType(TrafficPointElementType.BOARDING_PLATFORM)
                .parentSloid("ch:1:sloid:7000:1")
                .number(8507000)
                .designation("Perron 3")
                .designationOperational("CAMPSTR2")
                .length(12.0)
                .boardingAreaHeight(16.0)
                .compassDirection(278.0)
                .east(2600037.945)
                .north(1199749.812)
                .spatialReference(SpatialReference.LV95)
                .height(540.2)
                .build())
            .build();

    CreateTrafficPointElementVersionModel expected = CreateTrafficPointElementVersionModel.builder()
        .sloid("ch:1:sloid:7000:1:2")
        .validFrom(LocalDate.of(2021, 4, 1))
        .validTo(LocalDate.of(2099, 12, 31))
        .trafficPointElementType(TrafficPointElementType.BOARDING_PLATFORM)
        .numberWithoutCheckDigit(8507000)
        .parentSloid("ch:1:sloid:7000:1")
        .designation("Perron 3")
        .designationOperational("CAMPSTR2")
        .length(12.0)
        .boardingAreaHeight(16.0)
        .compassDirection(278.0)
        .trafficPointElementGeolocation(GeolocationBaseCreateModel.builder()
            .east(2600037.945)
            .north(1199749.812)
            .spatialReference(SpatialReference.LV95)
            .height(540.2)
            .build())
        .build();

    CreateTrafficPointElementVersionModel result = TrafficPointElementBulkImportCreate.apply(container);
    assertThat(result).usingRecursiveComparison().isEqualTo(expected);
  }
}