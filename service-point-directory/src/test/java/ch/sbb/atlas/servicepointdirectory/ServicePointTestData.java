package ch.sbb.atlas.servicepointdirectory;

import ch.sbb.atlas.imports.servicepoint.enumeration.SpatialReference;
import ch.sbb.atlas.kafka.model.SwissCanton;
import ch.sbb.atlas.model.Status;
import ch.sbb.atlas.servicepointdirectory.api.model.CodeAndDesignation;
import ch.sbb.atlas.servicepointdirectory.api.model.CreateServicePointVersionModel;
import ch.sbb.atlas.servicepointdirectory.entity.ServicePointVersion;
import ch.sbb.atlas.servicepointdirectory.entity.geolocation.ServicePointGeolocation;
import ch.sbb.atlas.servicepointdirectory.enumeration.Category;
import ch.sbb.atlas.servicepointdirectory.enumeration.Country;
import ch.sbb.atlas.servicepointdirectory.enumeration.MeanOfTransport;
import ch.sbb.atlas.servicepointdirectory.enumeration.OperatingPointTechnicalTimetableType;
import ch.sbb.atlas.servicepointdirectory.enumeration.OperatingPointType;
import ch.sbb.atlas.servicepointdirectory.enumeration.ServicePointStatus;
import ch.sbb.atlas.servicepointdirectory.enumeration.StopPointType;
import ch.sbb.atlas.servicepointdirectory.mapper.ServicePointGeolocationMapper;
import ch.sbb.atlas.servicepointdirectory.model.ServicePointNumber;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ServicePointTestData {

  public static ServicePointVersion getBernWyleregg() {
    ServicePointGeolocation geolocation = getServicePointGeolocationBernMittelland();

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

  public static ServicePointVersion getVersionWithCategoriesAndMeansOfTransport(int servicePointId) {
    ServicePointGeolocation geolocation = getServicePointGeolocationBernMittelland();

    ServicePointVersion servicePoint = ServicePointVersion
        .builder()
        .servicePointGeolocation(geolocation)
        .number(ServicePointNumber.of(Country.SWITZERLAND, servicePointId))
        .sloid("ch:1:sloid:89008")
        .numberShort(servicePointId)
        .country(Country.SWITZERLAND)
        .designationLong(null)
        .designationOfficial("Bern, Wyleregg")
        .abbreviation(null)
        .statusDidok3(ServicePointStatus.IN_OPERATION)
        .businessOrganisation("ch:1:sboid:100626")
        .status(Status.VALIDATED)
        .validFrom(LocalDate.of(2014, 12, 14))
        .validTo(LocalDate.of(2021, 3, 31))
        .categories(Set.of(Category.MAINTENANCE_POINT, Category.HOSTNAME))
        .meansOfTransport(Set.of(MeanOfTransport.BUS, MeanOfTransport.TRAM))
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

  public static ServicePointGeolocation getServicePointGeolocationBernMittelland() {
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
    return geolocation;
  }

  public static ServicePointGeolocation getAargauServicePointGeolocation() {
    ServicePointGeolocation geolocation = ServicePointGeolocation
        .builder()
        .spatialReference(SpatialReference.LV95)
        .east(2671984.26107)
        .north(1485245.92913)
        .country(Country.SWITZERLAND)
        .swissCanton(SwissCanton.AARGAU)
        .swissDistrictName("Rheinfelden")
        .swissDistrictNumber(1909)
        .swissMunicipalityName("Hellikon")
        .swissLocalityName("Hellikon")
        .creationDate(LocalDateTime.of(LocalDate.of(2021, 3, 22), LocalTime.of(9, 26, 29)))
        .creator("fs45117")
        .editionDate(LocalDateTime.of(LocalDate.of(2022, 2, 23), LocalTime.of(17, 10, 10)))
        .editor("fs45117")
        .build();
    return geolocation;
  }

  public static ServicePointVersion createServicePointVersionWithCountryBorder() {
    ServicePointGeolocation servicePointGeolocation = getServicePointGeolocation();

    ServicePointVersion servicePointVersionWithCountryBorder = ServicePointVersion
        .builder()
        .servicePointGeolocation(servicePointGeolocation)
        .number(ServicePointNumber.of(85197616))
        .sloid("ch:1:sloid:19761")
        .numberShort(19761)
        .country(Country.SWITZERLAND)
        .designationLong(null)
        .designationOfficial("Flüh Grenze")
        .abbreviation(null)
        .meansOfTransport(Collections.emptySet())
        .statusDidok3(ServicePointStatus.IN_OPERATION)
        .businessOrganisation("ch:1:sboid:100019")
        .comment("(Tram)")
        .status(Status.VALIDATED)
        .validFrom(LocalDate.of(2017, 11, 2))
        .validTo(LocalDate.of(2099, 12, 31))
        .categories(new HashSet<>())
        .operatingPointTechnicalTimetableType(OperatingPointTechnicalTimetableType.COUNTRY_BORDER)
        .operatingPoint(true)
        .operatingPointWithTimetable(true)
        .creationDate(LocalDateTime.of(LocalDate.of(2017, 11, 9), LocalTime.of(11, 53, 5)))
        .creator("GSU_DIDOK")
        .editionDate(LocalDateTime.of(LocalDate.of(2019, 5, 20), LocalTime.of(15, 3, 58)))
        .editor("fs45117")
        .build();
    servicePointGeolocation.setServicePointVersion(servicePointVersionWithCountryBorder);

    return servicePointVersionWithCountryBorder;
  }

  public static ServicePointVersion createServicePointVersionWithMultipleMeanOfTransport() {
    ServicePointGeolocation servicePointGeolocation = getServicePointGeolocation();

    ServicePointVersion servicePointVersionWithCountryBorder = ServicePointVersion
        .builder()
        .servicePointGeolocation(servicePointGeolocation)
        .number(ServicePointNumber.of(85197616))
        .sloid("ch:1:sloid:19761")
        .numberShort(19761)
        .country(Country.SWITZERLAND)
        .designationLong(null)
        .designationOfficial("Flüh Grenze")
        .abbreviation(null)
        .meansOfTransport(Set.of(MeanOfTransport.BUS, MeanOfTransport.TRAM, MeanOfTransport.TRAIN))
        .statusDidok3(ServicePointStatus.IN_OPERATION)
        .businessOrganisation("ch:1:sboid:100019")
        .comment("(Tram)")
        .status(Status.VALIDATED)
        .validFrom(LocalDate.of(2017, 11, 2))
        .validTo(LocalDate.of(2099, 12, 31))
        .categories(new HashSet<>())
        .operatingPointTechnicalTimetableType(OperatingPointTechnicalTimetableType.COUNTRY_BORDER)
        .operatingPoint(true)
        .operatingPointWithTimetable(true)
        .creationDate(LocalDateTime.of(LocalDate.of(2017, 11, 9), LocalTime.of(11, 53, 5)))
        .creator("GSU_DIDOK")
        .editionDate(LocalDateTime.of(LocalDate.of(2019, 5, 20), LocalTime.of(15, 3, 58)))
        .editor("fs45117")
        .build();
    servicePointGeolocation.setServicePointVersion(servicePointVersionWithCountryBorder);

    return servicePointVersionWithCountryBorder;
  }

  private static ServicePointGeolocation getServicePointGeolocation() {
    ServicePointGeolocation servicePointGeolocation = ServicePointGeolocation
        .builder()
        .spatialReference(SpatialReference.LV95)
        .east(2604525D)
        .north(1259900D)
        .height(370D)
        .country(Country.SWITZERLAND)
        .creationDate(LocalDateTime.of(LocalDate.of(2017, 11, 9), LocalTime.of(11, 53, 5)))
        .creator("GSU_DIDOK")
        .editionDate(LocalDateTime.of(LocalDate.of(2019, 5, 20), LocalTime.of(15, 3, 58)))
        .editor("fs45117")
        .build();
    return servicePointGeolocation;
  }

  public static ServicePointVersion createServicePointVersionWithoutServicePointGeolocation() {

    ServicePointVersion servicePointVersionWithCountryBorder = ServicePointVersion
        .builder()
        .number(ServicePointNumber.of(85197616))
        .sloid("ch:1:sloid:19761")
        .numberShort(19761)
        .country(Country.SWITZERLAND)
        .designationLong(null)
        .designationOfficial("Flüh Grenze")
        .abbreviation(null)
        .meansOfTransport(Set.of(MeanOfTransport.BUS, MeanOfTransport.TRAM, MeanOfTransport.TRAIN))
        .statusDidok3(ServicePointStatus.IN_OPERATION)
        .businessOrganisation("ch:1:sboid:100019")
        .comment("(Tram)")
        .status(Status.VALIDATED)
        .validFrom(LocalDate.of(2017, 11, 2))
        .validTo(LocalDate.of(2099, 12, 31))
        .categories(new HashSet<>())
        .operatingPointTechnicalTimetableType(OperatingPointTechnicalTimetableType.COUNTRY_BORDER)
        .operatingPoint(true)
        .operatingPointWithTimetable(true)
        .creationDate(LocalDateTime.of(LocalDate.of(2017, 11, 9), LocalTime.of(11, 53, 5)))
        .creator("GSU_DIDOK")
        .editionDate(LocalDateTime.of(LocalDate.of(2019, 5, 20), LocalTime.of(15, 3, 58)))
        .editor("fs45117")
        .build();

    return servicePointVersionWithCountryBorder;
  }

  public static CreateServicePointVersionModel getAargauServicePointVersionModel() {
    return CreateServicePointVersionModel.builder()
        .numberWithoutCheckDigit(8034510)
        .sloid("ch:1:sloid:18771")
        .designationLong("designation long 1")
        .designationOfficial("Aargau Strasse")
        .abbreviation("3")
        .statusDidok3(ServicePointStatus.IN_OPERATION)
        .statusDidok3Information(CodeAndDesignation.builder()
            .code("3")
            .designationDe("In Betrieb")
            .designationFr("En fonctionnement")
            .designationIt("In funzione")
            .designationEn("In operation")
            .build())
        .operatingPoint(true)
        .operatingPointWithTimetable(true)
        .freightServicePoint(false)
        .sortCodeOfDestinationStation("39136")
        .businessOrganisation("ch:1:sboid:100871")
        .categories(List.of(Category.POINT_OF_SALE))
        .categoriesInformation(List.of(CodeAndDesignation.builder()
            .code("6")
            .designationDe("Verkaufsstelle")
            .designationFr("Point de vente")
            .designationIt("Punto vendita")
            .designationEn("Verkaufsstelle")
            .build()))
        .operatingPointType(OperatingPointType.INVENTORY_POINT)
        .operatingPointTechnicalTimetableType(OperatingPointTechnicalTimetableType.ASSIGNED_OPERATING_POINT)
        .operatingPointRouteNetwork(false)
        .operatingPointKilometerMasterNumber(8034511)
        .operatingPointKilometerMaster(null)
        .meansOfTransport(List.of(MeanOfTransport.TRAIN))
        .meansOfTransportInformation(List.of(CodeAndDesignation.builder()
            .code("U")
            .designationDe("Unbekannt")
            .designationFr("Inconnu")
            .designationIt("Sconosciute")
            .designationEn("Unknown")
            .build()))
        .stopPointType(StopPointType.ON_REQUEST)
        .servicePointGeolocation(ServicePointGeolocationMapper.toModel(ServicePointTestData.getServicePointGeolocationBernMittelland()))
        .fotComment("Bahnersatz")
        .status(Status.VALIDATED)
        .validFrom(LocalDate.of(2010, 12, 11))
        .validTo(LocalDate.of(2019, 8, 10))
        .build();
  }

}
