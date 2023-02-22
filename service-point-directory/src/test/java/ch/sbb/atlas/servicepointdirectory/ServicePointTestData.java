package ch.sbb.atlas.servicepointdirectory;

import ch.sbb.atlas.imports.servicepoint.enumeration.SpatialReference;
import ch.sbb.atlas.model.Status;
import ch.sbb.atlas.servicepointdirectory.entity.ServicePointVersion;
import ch.sbb.atlas.servicepointdirectory.entity.geolocation.ServicePointGeolocation;
import ch.sbb.atlas.servicepointdirectory.enumeration.Country;
import ch.sbb.atlas.servicepointdirectory.enumeration.MeanOfTransport;
import ch.sbb.atlas.servicepointdirectory.enumeration.ServicePointStatus;
import ch.sbb.atlas.servicepointdirectory.enumeration.SwissCanton;
import ch.sbb.atlas.servicepointdirectory.model.ServicePointNumber;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ServicePointTestData {

  public static ServicePointVersion getBernWyleregg() {
    ServicePointGeolocation geolocation = ServicePointGeolocation
        .builder()
        .spatialReference(SpatialReference.LV95)
        .east(2600783D)
        .north(1201099D)
        .height(555D)
        .country(Country.SWITZERLAND)
        .swissMunicipalityNumber(351)
        .swissCanton(SwissCanton.BERN)
        .swissDistrictName("Bern-Mittelland")
        .swissDistrictNumber(246)
        .swissMunicipalityName("Bern")
        .swissLocalityName("Bern")
        .creationDate(LocalDateTime.of(LocalDate.of(2021, 3, 22), LocalTime.of(9, 26, 29)))
        .creator("fs45117")
        .editionDate(LocalDateTime.of(LocalDate.of(2022, 2, 23), LocalTime.of(17, 10, 10)))
        .editor("fs45117")
        .build();

    ServicePointVersion servicePoint = ServicePointVersion
        .builder()
        .servicePointGeolocation(geolocation)
        .number(ServicePointNumber.of(85890087))
        .sloid("ch:1:sloid:89008")
        .numberShort(89008)
        .country(Country.SWITZERLAND)
        .designationLong(null)
        .designationOfficial("Bern, Wyleregg")
        .abbreviation(null)
        .statusDidok3(ServicePointStatus.IN_OPERATION)
        .businessOrganisation("ch:1:sboid:100626")
        .status(Status.VALIDATED)
        .validFrom(LocalDate.of(2014, 12, 14))
        .validTo(LocalDate.of(2021, 3, 31))
        .categories(new HashSet<>())
        .meansOfTransport(Set.of(MeanOfTransport.BUS))
        .operatingPoint(true)
        .operatingPointWithTimetable(true)
        .creationDate(LocalDateTime.of(LocalDate.of(2021, 3, 22), LocalTime.of(9, 26, 29)))
        .creator("fs45117")
        .editionDate(LocalDateTime.of(LocalDate.of(2022, 2, 23), LocalTime.of(17, 10, 10)))
        .editor("fs45117")
        .build();

    geolocation.setServicePointVersion(servicePoint);
    return servicePoint;
  }

}
