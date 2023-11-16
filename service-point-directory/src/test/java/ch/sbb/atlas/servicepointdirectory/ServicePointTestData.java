package ch.sbb.atlas.servicepointdirectory;

import ch.sbb.atlas.api.servicepoint.CreateServicePointVersionModel;
import ch.sbb.atlas.imports.servicepoint.enumeration.SpatialReference;
import ch.sbb.atlas.kafka.model.SwissCanton;
import ch.sbb.atlas.model.Status;
import ch.sbb.atlas.servicepoint.Country;
import ch.sbb.atlas.servicepoint.ServicePointNumber;
import ch.sbb.atlas.servicepoint.enumeration.Category;
import ch.sbb.atlas.servicepoint.enumeration.MeanOfTransport;
import ch.sbb.atlas.servicepoint.enumeration.OperatingPointTechnicalTimetableType;
import ch.sbb.atlas.servicepoint.enumeration.StopPointType;
import ch.sbb.atlas.servicepointdirectory.entity.ServicePointVersion;
import ch.sbb.atlas.servicepointdirectory.entity.geolocation.ServicePointGeolocation;
import ch.sbb.atlas.servicepointdirectory.mapper.ServicePointGeolocationMapper;
import ch.sbb.atlas.servicepointdirectory.model.ServicePointStatus;
import lombok.experimental.UtilityClass;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@UtilityClass
public class ServicePointTestData {

  public static ServicePointVersion getBernWyleregg() {
    ServicePointGeolocation geolocation = getServicePointGeolocationBernMittelland();

    ServicePointVersion servicePoint = ServicePointVersion
        .builder()
        .servicePointGeolocation(geolocation)
        .number(ServicePointNumber.ofNumberWithoutCheckDigit(8589008))
        .sloid("ch:1:sloid:89008")
        .numberShort(89008)
        .country(Country.SWITZERLAND)
        .designationLong(null)
        .designationOfficial("Bern, Wyleregg")
        .abbreviation(null)
        .businessOrganisation("ch:1:sboid:100626")
        .status(Status.VALIDATED)
        .validFrom(LocalDate.of(2014, 12, 14))
        .validTo(LocalDate.of(2021, 3, 31))
        .categories(new HashSet<>())
        .meansOfTransport(Set.of(MeanOfTransport.BUS))
        .operatingPoint(true)
        .operatingPointWithTimetable(true)
        .operatingPointRouteNetwork(true)
        .creationDate(LocalDateTime.of(LocalDate.of(2021, 3, 22), LocalTime.of(9, 26, 29)))
        .creator("fs45117")
        .editionDate(LocalDateTime.of(LocalDate.of(2022, 2, 23), LocalTime.of(17, 10, 10)))
        .editor("fs45117")
        .build();

    geolocation.setServicePointVersion(servicePoint);
    return servicePoint;
  }

  public static ServicePointVersion getBern() {
    ServicePointGeolocation geolocation = ServicePointGeolocation
        .builder()
        .spatialReference(SpatialReference.LV95)
        .east(2600037.945)
        .north(1199749.812)
        .height(540.2)
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
        .number(ServicePointNumber.ofNumberWithoutCheckDigit(8507000))
        .sloid("ch:1:sloid:7000")
        .numberShort(7000)
        .country(Country.SWITZERLAND)
        .designationLong(null)
        .designationOfficial("Bern")
        .abbreviation("BN")
        .businessOrganisation("ch:1:sboid:100001")
        .status(Status.VALIDATED)
        .validFrom(LocalDate.of(2021, 4, 1))
        .validTo(LocalDate.of(2099, 12, 31))
        .categories(new HashSet<>())
        .meansOfTransport(Set.of(MeanOfTransport.TRAIN))
        .operatingPoint(true)
        .operatingPointWithTimetable(true)
        .freightServicePoint(true)
        .creationDate(LocalDateTime.of(LocalDate.of(2021, 3, 22), LocalTime.of(9, 26, 29)))
        .creator("fs45117")
        .editionDate(LocalDateTime.of(LocalDate.of(2022, 2, 23), LocalTime.of(17, 10, 10)))
        .editor("fs45117")
        .build();

    geolocation.setServicePointVersion(servicePoint);
    return servicePoint;
  }

  public static ServicePointVersion getBernOst() {
    ServicePointGeolocation geolocation = ServicePointGeolocation
        .builder()
        .spatialReference(SpatialReference.LV95)
        .east(2600212D)
        .north(1200214D)
        .height(533D)
        .country(Country.SWITZERLAND)
        .swissMunicipalityNumber(351)
        .swissCanton(SwissCanton.BERN)
        .swissDistrictName("Bern-Mittelland")
        .swissDistrictNumber(246)
        .swissMunicipalityName("Bern")
        .swissLocalityName("Bern")
        .creationDate(LocalDateTime.of(LocalDate.of(2018, 1, 31), LocalTime.of(13, 2, 54)))
        .creator("u150522")
        .editionDate(LocalDateTime.of(LocalDate.of(2022, 2, 24), LocalTime.of(21, 48, 25)))
        .editor("fs45117")
        .build();

    ServicePointVersion bernOst = ServicePointVersion
        .builder()
        .servicePointGeolocation(geolocation)
        .number(ServicePointNumber.ofNumberWithoutCheckDigit(8500925))
        .sloid("ch:1:sloid:925")
        .numberShort(925)
        .country(Country.SWITZERLAND)
        .designationLong(null)
        .designationOfficial("Bern Ost (Spw)")
        .abbreviation("BNO")
        .statusDidok3(ServicePointStatus.IN_OPERATION)
        .businessOrganisation("ch:1:sboid:100001")
        .status(Status.VALIDATED)
        .validFrom(LocalDate.of(2018, 1, 31))
        .validTo(LocalDate.of(2099, 12, 31))
        .categories(new HashSet<>())
        .meansOfTransport(Collections.emptySet())
        .operatingPoint(true)
        .operatingPointRouteNetwork(true)
        .operatingPointWithTimetable(true)
        .operatingPointTechnicalTimetableType(OperatingPointTechnicalTimetableType.LANE_CHANGE)
        .creationDate(LocalDateTime.of(LocalDate.of(2018, 1, 31), LocalTime.of(13, 2, 54)))
        .creator("u150522")
        .editionDate(LocalDateTime.of(LocalDate.of(2022, 2, 24), LocalTime.of(21, 48, 25)))
        .editor("fs45117")
        .build();

    geolocation.setServicePointVersion(bernOst);
    return bernOst;
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
        .number(ServicePointNumber.ofNumberWithoutCheckDigit(8519761))
        .sloid("ch:1:sloid:19761")
        .numberShort(19761)
        .country(Country.SWITZERLAND)
        .designationLong(null)
        .designationOfficial("Flüh Grenze")
        .abbreviation(null)
        .meansOfTransport(Collections.emptySet())
        .businessOrganisation("ch:1:sboid:100019")
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
        .number(ServicePointNumber.ofNumberWithoutCheckDigit(8519761))
        .sloid("ch:1:sloid:19761")
        .numberShort(19761)
        .country(Country.SWITZERLAND)
        .designationLong(null)
        .designationOfficial("Flüh Grenze")
        .abbreviation(null)
        .meansOfTransport(Set.of(MeanOfTransport.BUS, MeanOfTransport.TRAM, MeanOfTransport.TRAIN))
        .businessOrganisation("ch:1:sboid:100019")
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
    return ServicePointVersion
        .builder()
        .number(ServicePointNumber.ofNumberWithoutCheckDigit(8519761))
        .sloid("ch:1:sloid:19761")
        .numberShort(19761)
        .country(Country.SWITZERLAND)
        .designationLong(null)
        .designationOfficial("Flüh Grenze")
        .abbreviation(null)
        .meansOfTransport(Set.of(MeanOfTransport.BUS, MeanOfTransport.TRAM, MeanOfTransport.TRAIN))
        .businessOrganisation("ch:1:sboid:100019")
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
  }

  public static ServicePointVersion createServicePointVersion() {
    return ServicePointVersion
        .builder()
        .number(ServicePointNumber.ofNumberWithoutCheckDigit(8519768))
        .sloid("ch:1:sloid:19765")
        .numberShort(19744)
        .country(Country.SWITZERLAND)
        .designationLong(null)
        .designationOfficial("Flüh Grenze")
        .abbreviation(null)
        .meansOfTransport(Set.of(MeanOfTransport.BUS))
        .businessOrganisation("ch:1:sboid:100015")
        .status(Status.VALIDATED)
        .validFrom(LocalDate.of(2014, 11, 2))
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
  }

  public static ServicePointVersion createAbroadServicePointVersion() {
    return ServicePointVersion
        .builder()
        .number(ServicePointNumber.ofNumberWithoutCheckDigit(5819768))
        .sloid("ch:1:sloid:19768")
        .numberShort(19768)
        .country(Country.ARMENIA)
        .designationLong(null)
        .designationOfficial("Flüh Grenze")
        .abbreviation(null)
        .meansOfTransport(Set.of(MeanOfTransport.BUS))
        .businessOrganisation("ch:1:sboid:100626")
        .status(Status.VALIDATED)
        .validFrom(LocalDate.of(2014, 11, 2))
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
  }

  public static CreateServicePointVersionModel getAargauServicePointVersionModel() {
    return CreateServicePointVersionModel.builder()
        .country(Country.SWITZERLAND)
        .designationLong("designation long 1")
        .designationOfficial("Aargau Strasse")
        .abbreviation("ABC")
        .freightServicePoint(false)
        .sortCodeOfDestinationStation("39136")
        .businessOrganisation("ch:1:sboid:100871")
        .categories(List.of(Category.POINT_OF_SALE))
        .operatingPointRouteNetwork(true)
        .meansOfTransport(List.of(MeanOfTransport.TRAIN))
        .stopPointType(StopPointType.ON_REQUEST)
        .servicePointGeolocation(
            ServicePointGeolocationMapper.toCreateModel(ServicePointTestData.getServicePointGeolocationBernMittelland()))
        .validFrom(LocalDate.of(2010, 12, 11))
        .validTo(LocalDate.of(2019, 8, 10))
        .build();
  }

  public static CreateServicePointVersionModel getBuchsiServicePoint() {
    return CreateServicePointVersionModel.builder()
        .numberShort(34511)
        .country(Country.GERMANY)
        .designationLong("designation long 1")
        .designationOfficial("Buchsi Hood")
        .abbreviation(null)
        .freightServicePoint(false)
        .sortCodeOfDestinationStation("39136")
        .businessOrganisation("ch:1:sboid:100016")
        .categories(List.of(Category.POINT_OF_SALE))
        .operatingPointRouteNetwork(true)
        .meansOfTransport(List.of(MeanOfTransport.TRAIN))
        .stopPointType(StopPointType.ON_REQUEST)
        .servicePointGeolocation(
            ServicePointGeolocationMapper.toCreateModel(ServicePointTestData.getServicePointGeolocationBernMittelland()))
        .validFrom(LocalDate.of(2010, 12, 11))
        .validTo(LocalDate.of(2099, 8, 10))
        .build();
  }

  public static CreateServicePointVersionModel getAargauServicePointVersionModelWithRouteNetworkFalse() {
    CreateServicePointVersionModel createServicePointVersionModel = getAargauServicePointVersionModel();
    createServicePointVersionModel.setOperatingPointRouteNetwork(false);
    return createServicePointVersionModel;
  }

}
