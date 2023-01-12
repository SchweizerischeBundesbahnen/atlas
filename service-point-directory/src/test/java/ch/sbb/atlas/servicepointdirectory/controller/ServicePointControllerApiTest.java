package ch.sbb.atlas.servicepointdirectory.controller;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ch.sbb.atlas.base.service.model.Status;
import ch.sbb.atlas.base.service.model.controller.BaseControllerApiTest;
import ch.sbb.atlas.servicepointdirectory.api.ServicePointVersionModel;
import ch.sbb.atlas.servicepointdirectory.entity.ServicePointVersion;
import ch.sbb.atlas.servicepointdirectory.entity.geolocation.ServicePointGeolocation;
import ch.sbb.atlas.servicepointdirectory.enumeration.Country;
import ch.sbb.atlas.servicepointdirectory.enumeration.MeanOfTransport;
import ch.sbb.atlas.servicepointdirectory.enumeration.ServicePointStatus;
import ch.sbb.atlas.servicepointdirectory.enumeration.SpatialReference;
import ch.sbb.atlas.servicepointdirectory.enumeration.SwissCanton;
import ch.sbb.atlas.servicepointdirectory.model.ServicePointNumber;
import ch.sbb.atlas.servicepointdirectory.repository.ServicePointVersionRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

public class ServicePointControllerApiTest extends BaseControllerApiTest {

  private final ServicePointVersionRepository repository;

  @Autowired
  public ServicePointControllerApiTest(ServicePointVersionRepository repository) {
    this.repository = repository;
  }

  @BeforeEach
  void createDefaultVersion() {
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
        .creationDate(LocalDateTime.of(LocalDate.of(2021, 3, 22), LocalTime.of(9, 26, 29)))
        .creator("fs45117")
        .editionDate(LocalDateTime.of(LocalDate.of(2022, 2, 23), LocalTime.of(17, 10, 10)))
        .editor("fs45117")
        .build();

    geolocation.setServicePointVersion(servicePoint);
    repository.save(servicePoint);
  }

  @AfterEach
  void cleanUpDb() {
    repository.deleteAll();
  }

  @Test
  void shouldGetServicePointVersions() throws Exception {
    mvc.perform(get("/v1/service-points/85890087")).andExpect(status().isOk())
        .andDo(MockMvcResultHandlers.print())
        .andExpect(jsonPath("$[0]." + ServicePointVersionModel.Fields.id, is(1000)))
        .andExpect(jsonPath("$[0].number.number", is(8589008)))
        .andExpect(jsonPath("$[0].designationOfficial", is("Bern, Wyleregg")))
        .andExpect(jsonPath("$[0].meansOfTransportInformation[0].code", is("B")))
        .andExpect(jsonPath("$[0].meansOfTransportInformation[0].designationDe", is("Bus")))
        .andExpect(jsonPath("$[0].creationDate", is("2021-03-22T09:26:29")))
        .andExpect(jsonPath("$[0].creator", is("fs45117")));
  }

  @Test
  void shouldFailOnInvalidServicePointNumber() throws Exception {
    mvc.perform(get("/v1/service-points/123"))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().isNotFound());
  }

}