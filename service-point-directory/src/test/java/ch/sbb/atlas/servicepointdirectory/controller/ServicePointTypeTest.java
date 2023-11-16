package ch.sbb.atlas.servicepointdirectory.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import ch.sbb.atlas.api.servicepoint.CreateServicePointVersionModel;
import ch.sbb.atlas.api.servicepoint.ReadServicePointVersionModel;
import ch.sbb.atlas.business.organisation.service.SharedBusinessOrganisationService;
import ch.sbb.atlas.model.controller.BaseControllerApiTest;
import ch.sbb.atlas.servicepoint.Country;
import ch.sbb.atlas.servicepoint.enumeration.MeanOfTransport;
import ch.sbb.atlas.servicepoint.enumeration.OperatingPointTechnicalTimetableType;
import ch.sbb.atlas.servicepoint.enumeration.OperatingPointTrafficPointType;
import ch.sbb.atlas.servicepoint.enumeration.OperatingPointType;
import ch.sbb.atlas.servicepointdirectory.repository.ServicePointVersionRepository;
import ch.sbb.atlas.servicepointdirectory.service.servicepoint.ServicePointNumberService;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

class ServicePointTypeTest extends BaseControllerApiTest {

  @MockBean
  private SharedBusinessOrganisationService sharedBusinessOrganisationService;

  @MockBean
  private ServicePointNumberService servicePointNumberService;

  private final ServicePointVersionRepository repository;
  private final ServicePointController servicePointController;

  @Autowired
  ServicePointTypeTest(ServicePointVersionRepository repository, ServicePointController servicePointController) {
    this.repository = repository;
    this.servicePointController = servicePointController;
  }

  @BeforeEach
  void setUp() {
    when(servicePointNumberService.getNextAvailableServicePointId(Country.SWITZERLAND)).thenReturn(7000);
  }

  @AfterEach
  void cleanUpDb() {
    repository.deleteAll();
  }

  @Test
  void shouldCreatePlainServicePoint() {
    CreateServicePointVersionModel servicePoint = CreateServicePointVersionModel.builder()
        .numberShort(7000)
        .country(Country.SWITZERLAND)
        .designationOfficial("Bern")
        .businessOrganisation("ch:1:sboid:5846489645")
        .validFrom(LocalDate.of(2022, 1, 1))
        .validTo(LocalDate.of(2022, 12, 31))
        .build();

    ReadServicePointVersionModel result = servicePointController.createServicePoint(servicePoint);

    assertThat(result.getId()).isNotNull();
    assertThat(result.isOperatingPoint()).isFalse();
    assertThat(result.isOperatingPointWithTimetable()).isFalse();
  }

  @Test
  void shouldCreateOperatingPointServicePointAsInventoryPoint() {
    CreateServicePointVersionModel servicePoint = CreateServicePointVersionModel.builder()
        .numberShort(7000)
        .country(Country.SWITZERLAND)
        .designationOfficial("Bern")
        .businessOrganisation("ch:1:sboid:5846489645")
        .operatingPointType(OperatingPointType.INVENTORY_POINT)
        .validFrom(LocalDate.of(2022, 1, 1))
        .validTo(LocalDate.of(2022, 12, 31))
        .build();

    ReadServicePointVersionModel result = servicePointController.createServicePoint(servicePoint);

    assertThat(result.getId()).isNotNull();
    assertThat(result.isOperatingPoint()).isTrue();
    assertThat(result.isOperatingPointWithTimetable()).isFalse();
  }

  @Test
  void shouldCreateOperatingPointServicePointAsOperatingPointBus() {
    CreateServicePointVersionModel servicePoint = CreateServicePointVersionModel.builder()
        .numberShort(7000)
        .country(Country.SWITZERLAND)
        .designationOfficial("Bern")
        .businessOrganisation("ch:1:sboid:5846489645")
        .operatingPointTechnicalTimetableType(OperatingPointTechnicalTimetableType.OPERATING_POINT_BUS)
        .validFrom(LocalDate.of(2022, 1, 1))
        .validTo(LocalDate.of(2022, 12, 31))
        .build();

    ReadServicePointVersionModel result = servicePointController.createServicePoint(servicePoint);

    assertThat(result.getId()).isNotNull();
    assertThat(result.isOperatingPoint()).isTrue();
    assertThat(result.isOperatingPointWithTimetable()).isTrue();
  }

  @Test
  void shouldCreateStopPointServicePointAsStopPoint() {
    CreateServicePointVersionModel servicePoint = CreateServicePointVersionModel.builder()
        .numberShort(7000)
        .country(Country.SWITZERLAND)
        .designationOfficial("Bern")
        .businessOrganisation("ch:1:sboid:5846489645")
        .meansOfTransport(List.of(MeanOfTransport.BUS))
        .validFrom(LocalDate.of(2022, 1, 1))
        .validTo(LocalDate.of(2022, 12, 31))
        .build();

    ReadServicePointVersionModel result = servicePointController.createServicePoint(servicePoint);

    assertThat(result.getId()).isNotNull();
    assertThat(result.isOperatingPoint()).isTrue();
    assertThat(result.isOperatingPointWithTimetable()).isTrue();
  }

  @Test
  void shouldCreateServicePointAsFreightServicePoint() {
    CreateServicePointVersionModel servicePoint = CreateServicePointVersionModel.builder()
        .country(Country.SWITZERLAND)
        .numberShort(18771)
        .designationOfficial("Bern")
        .businessOrganisation("ch:1:sboid:5846489645")
        .freightServicePoint(true)
        .validFrom(LocalDate.of(2022, 1, 1))
        .validTo(LocalDate.of(2022, 12, 31))
        .build();

    ReadServicePointVersionModel result = servicePointController.createServicePoint(servicePoint);

    assertThat(result.getId()).isNotNull();
    assertThat(result.isOperatingPoint()).isTrue();
    assertThat(result.isOperatingPointWithTimetable()).isTrue();
  }

  @Test
  void shouldCreateServicePointAsTariffPoint() {
    CreateServicePointVersionModel servicePoint = CreateServicePointVersionModel.builder()
        .numberShort(7000)
        .country(Country.SWITZERLAND)
        .designationOfficial("Bern")
        .businessOrganisation("ch:1:sboid:5846489645")
        .operatingPointTrafficPointType(OperatingPointTrafficPointType.TARIFF_POINT)
        .validFrom(LocalDate.of(2022, 1, 1))
        .validTo(LocalDate.of(2022, 12, 31))
        .build();

    ReadServicePointVersionModel result = servicePointController.createServicePoint(servicePoint);

    assertThat(result.getId()).isNotNull();
    assertThat(result.isOperatingPoint()).isTrue();
    assertThat(result.isOperatingPointWithTimetable()).isTrue();
  }
}
