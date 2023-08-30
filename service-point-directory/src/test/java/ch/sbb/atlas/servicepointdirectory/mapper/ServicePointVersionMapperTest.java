package ch.sbb.atlas.servicepointdirectory.mapper;

import ch.sbb.atlas.api.servicepoint.ReadServicePointVersionModel;
import ch.sbb.atlas.model.Status;
import ch.sbb.atlas.servicepoint.Country;
import ch.sbb.atlas.servicepoint.ServicePointNumber;
import ch.sbb.atlas.servicepoint.enumeration.Category;
import ch.sbb.atlas.servicepoint.enumeration.OperatingPointTechnicalTimetableType;
import ch.sbb.atlas.servicepoint.enumeration.OperatingPointType;
import ch.sbb.atlas.servicepointdirectory.entity.ServicePointVersion;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class ServicePointVersionMapperTest {

  @Test
  void shouldMapToModelCorrectlyInventaryPointNotSpecific() {
    // Given
    ServicePointVersion servicePointVersion = ServicePointVersion
        .builder()
        .number(ServicePointNumber.ofNumberWithoutCheckDigit(8500030))
        .sloid("ch:1:sloid:30")
        .numberShort(30)
        .country(Country.SWITZERLAND)
        .designationLong(null)
        .designationOfficial("Basel EuroAirport P")
        .abbreviation("BSEU")
        .meansOfTransport(Collections.emptySet())
        .businessOrganisation("ch:1:sboid:100001")
        .status(Status.VALIDATED)
        .operatingPoint(true)
        .operatingPointWithTimetable(true)
        .validFrom(LocalDate.of(2000, 2, 7))
        .validTo(LocalDate.of(2007, 2, 26))
        .categories(Set.of(Category.MAINTENANCE_POINT, Category.HOSTNAME, Category.MIGRATION_DIVERSE))
        .operatingPointType(OperatingPointType.INVENTORY_POINT)
        .operatingPointTechnicalTimetableType(OperatingPointTechnicalTimetableType.UNKNOWN)
        .creationDate(LocalDateTime.of(LocalDate.of(2018, 2, 19), LocalTime.of(13, 44, 2)))
        .creator("fs45117")
        .editionDate(LocalDateTime.of(LocalDate.of(2018, 2, 19), LocalTime.of(13, 44, 2)))
        .editor("fs45117")
        .build();

    // When
    ReadServicePointVersionModel servicePointVersionModel = ServicePointVersionMapper.toModel(servicePointVersion);

    ReadServicePointVersionModel expected = ReadServicePointVersionModel
        .builder()
        .number(ServicePointNumber.ofNumberWithoutCheckDigit(8500030))
        .sloid("ch:1:sloid:30")
        .designationLong(null)
        .designationOfficial("Basel EuroAirport P")
        .abbreviation("BSEU")
        .meansOfTransport(Collections.emptyList())
        .businessOrganisation("ch:1:sboid:100001")
        .status(Status.VALIDATED)
        .operatingPoint(true)
        .operatingPointWithTimetable(true)
        .validFrom(LocalDate.of(2000, 2, 7))
        .validTo(LocalDate.of(2007, 2, 26))
        .categories(List.of(Category.MAINTENANCE_POINT, Category.HOSTNAME, Category.MIGRATION_DIVERSE))
        .operatingPointType(OperatingPointType.INVENTORY_POINT)
        .operatingPointTechnicalTimetableType(OperatingPointTechnicalTimetableType.UNKNOWN)
        .creationDate(LocalDateTime.of(LocalDate.of(2018, 2, 19), LocalTime.of(13, 44, 2)))
        .creator("fs45117")
        .editionDate(LocalDateTime.of(LocalDate.of(2018, 2, 19), LocalTime.of(13, 44, 2)))
        .editor("fs45117")
        .build();

    assertThat(servicePointVersionModel).usingRecursiveComparison().isEqualTo(expected);
  }
}