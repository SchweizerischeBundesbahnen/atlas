package ch.sbb.atlas.servicepointdirectory.mapper;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import ch.sbb.atlas.model.Status;
import ch.sbb.atlas.servicepointdirectory.api.model.CodeAndDesignation;
import ch.sbb.atlas.servicepointdirectory.api.model.ReadServicePointVersionModel;
import ch.sbb.atlas.servicepointdirectory.entity.ServicePointVersion;
import ch.sbb.atlas.servicepointdirectory.enumeration.Category;
import ch.sbb.atlas.servicepointdirectory.enumeration.Country;
import ch.sbb.atlas.servicepointdirectory.enumeration.OperatingPointTechnicalTimetableType;
import ch.sbb.atlas.servicepointdirectory.enumeration.OperatingPointType;
import ch.sbb.atlas.servicepointdirectory.enumeration.ServicePointStatus;
import ch.sbb.atlas.servicepointdirectory.model.ServicePointNumber;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;

class ServicePointVersionMapperTest {

  @Test
  void shouldMapToModelCorrectlyInventaryPointNotSpecific() {
    // Given
    ServicePointVersion servicePointVersion = ServicePointVersion
        .builder()
        .number(ServicePointNumber.of(85000307))
        .sloid("ch:1:sloid:30")
        .numberShort(30)
        .country(Country.SWITZERLAND)
        .designationLong(null)
        .designationOfficial("Basel EuroAirport P")
        .abbreviation("BSEU")
        .meansOfTransport(Collections.emptySet())
        .statusDidok3(ServicePointStatus.IN_OPERATION)
        .businessOrganisation("ch:1:sboid:100001")
        .comment(null)
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
        .number(ServicePointNumber.of(85000307))
        .sloid("ch:1:sloid:30")
        .designationLong(null)
        .designationOfficial("Basel EuroAirport P")
        .abbreviation("BSEU")
        .meansOfTransport(Collections.emptyList())
        .meansOfTransportInformation(Collections.emptyList())
        .statusDidok3(ServicePointStatus.IN_OPERATION)
        .statusDidok3Information(CodeAndDesignation.fromEnum(ServicePointStatus.IN_OPERATION))
        .businessOrganisation("ch:1:sboid:100001")
        .fotComment(null)
        .status(Status.VALIDATED)
        .operatingPoint(true)
        .operatingPointWithTimetable(true)
        .validFrom(LocalDate.of(2000, 2, 7))
        .validTo(LocalDate.of(2007, 2, 26))
        .categories(List.of(Category.MAINTENANCE_POINT, Category.HOSTNAME, Category.MIGRATION_DIVERSE))
        .categoriesInformation(List.of(CodeAndDesignation.fromEnum(Category.MAINTENANCE_POINT),
            CodeAndDesignation.fromEnum(Category.HOSTNAME),
            CodeAndDesignation.fromEnum(Category.MIGRATION_DIVERSE)))
        .operatingPointType(OperatingPointType.INVENTORY_POINT)
        .operatingPointTypeInformation(CodeAndDesignation.fromEnum(OperatingPointType.INVENTORY_POINT))
        .operatingPointTechnicalTimetableType(OperatingPointTechnicalTimetableType.UNKNOWN)
        .operatingPointTechnicalTimetableTypeInformation(
            CodeAndDesignation.fromEnum(OperatingPointTechnicalTimetableType.UNKNOWN))
        .creationDate(LocalDateTime.of(LocalDate.of(2018, 2, 19), LocalTime.of(13, 44, 2)))
        .creator("fs45117")
        .editionDate(LocalDateTime.of(LocalDate.of(2018, 2, 19), LocalTime.of(13, 44, 2)))
        .editor("fs45117")
        .build();

    assertThat(servicePointVersionModel).usingRecursiveComparison().isEqualTo(expected);
  }
}