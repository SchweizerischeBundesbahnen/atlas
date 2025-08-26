package ch.sbb.atlas.servicepointdirectory.module.servicepoint.controller;

import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ch.sbb.atlas.api.location.SloidType;
import ch.sbb.atlas.api.servicepoint.CreateServicePointVersionModel;
import ch.sbb.atlas.api.servicepoint.ReadServicePointVersionModel;
import ch.sbb.atlas.api.servicepoint.UpdateServicePointVersionModel;
import ch.sbb.atlas.business.organisation.service.SharedBusinessOrganisationService;
import ch.sbb.atlas.journey.poi.model.CountryCode;
import ch.sbb.atlas.location.LocationService;
import ch.sbb.atlas.model.Status;
import ch.sbb.atlas.model.controller.BaseControllerApiTest;
import ch.sbb.atlas.servicepoint.Country;
import ch.sbb.atlas.servicepoint.enumeration.MeanOfTransport;
import ch.sbb.atlas.servicepointdirectory.config.JourneyPoiConfig;
import ch.sbb.atlas.servicepointdirectory.config.OAuthFeignConfig;
import ch.sbb.atlas.servicepointdirectory.module.geodata.client.journepoy.JourneyPoiClientBase;
import ch.sbb.atlas.servicepointdirectory.module.servicepoint.ServicePointTestData;
import ch.sbb.atlas.servicepointdirectory.module.servicepoint.repository.ServicePointVersionRepository;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

class ServicePointApiInternalControllerApiTest extends BaseControllerApiTest {

  @MockitoBean
  private JourneyPoiConfig journeyPoiConfig;

  @MockitoBean
  private OAuthFeignConfig oAuthFeignConfig;

  @MockitoBean
  private JourneyPoiClientBase journeyPoiClient;

  @MockitoBean
  private SharedBusinessOrganisationService sharedBusinessOrganisationService;

  @MockitoBean
  private LocationService locationService;

  @Autowired
  private ServicePointVersionRepository repository;
  @Autowired
  private ServicePointApiV1Controller servicePointApiV1Controller;
  @Autowired
  private ServicePointWorkflowApiInternalController servicePointWorkflowApiInternalController;

  @BeforeEach
  void createDefaultVersion() {
    repository.save(ServicePointTestData.getBernWyleregg());

    ResponseEntity<ch.sbb.atlas.journey.poi.model.Country> poiResponse =
        ResponseEntity.ofNullable(
            new ch.sbb.atlas.journey.poi.model.Country().countryCode(new CountryCode().isoCountryCode("RO")));
    when(journeyPoiClient.closestCountry(any(), any())).thenReturn(poiResponse);
    when(locationService.generateSloid(any(), any(Country.class))).thenReturn("ch:1:sloid:1");
  }

  @AfterEach
  void cleanUpDb() {
    repository.deleteAll();
  }

  @Test
  void shouldSetStatusToRevokeForAllServicePoints() throws Exception {
    CreateServicePointVersionModel aargauServicePointVersionModel = ServicePointTestData.getAargauServicePointVersionModel();
    UpdateServicePointVersionModel createServicePointVersionModel1 = ServicePointTestData.getAargauServicePointVersionModel();
    createServicePointVersionModel1.setMeansOfTransport(List.of(MeanOfTransport.BUS));
    createServicePointVersionModel1.setValidFrom(LocalDate.of(2019, 8, 11));
    createServicePointVersionModel1.setValidTo(LocalDate.of(2020, 8, 10));
    ReadServicePointVersionModel servicePointVersionModel = servicePointApiV1Controller.createServicePoint(
        aargauServicePointVersionModel);
    Long id = servicePointVersionModel.getId();
    createServicePointVersionModel1.setEtagVersion(servicePointVersionModel.getEtagVersion());
    List<ReadServicePointVersionModel> servicePointVersionModels = servicePointApiV1Controller.updateServicePoint(id,
        createServicePointVersionModel1);
    servicePointVersionModels.forEach(v -> v.setStatus(Status.IN_REVIEW));
    Integer number = servicePointVersionModel.getNumber().getNumber();

    mvc.perform(post("/internal/service-points/" + number + "/revoke"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].status", is(Status.REVOKED.toString())))
        .andExpect(jsonPath("$[1].status", is(Status.REVOKED.toString())));

    verify(locationService, times(1)).generateSloid(SloidType.SERVICE_POINT, Country.SWITZERLAND);
  }

  @Test
  void shouldVerifyDesignationOfficialDesignationLongCanBeReusedAfterStatusRevoked() throws Exception {
    repository.deleteAll();
    CreateServicePointVersionModel aargauServicePointVersionModel = ServicePointTestData.getAargauServicePointVersionModel();
    aargauServicePointVersionModel.setCountry(Country.GERMANY);
    aargauServicePointVersionModel.setNumberShort(12345);
    CreateServicePointVersionModel createServicePointVersionModel1 = ServicePointTestData.getAargauServicePointVersionModel();
    createServicePointVersionModel1.setCountry(Country.GERMANY);
    createServicePointVersionModel1.setNumberShort(12345);
    createServicePointVersionModel1.setMeansOfTransport(List.of(MeanOfTransport.BUS));
    createServicePointVersionModel1.setValidFrom(LocalDate.of(2019, 8, 11));
    createServicePointVersionModel1.setValidTo(LocalDate.of(2020, 8, 10));
    ReadServicePointVersionModel servicePointVersionModel = servicePointApiV1Controller.createServicePoint(
        aargauServicePointVersionModel);
    Long id = servicePointVersionModel.getId();
    createServicePointVersionModel1.setEtagVersion(servicePointVersionModel.getEtagVersion());
    List<ReadServicePointVersionModel> servicePointVersionModels = servicePointApiV1Controller.updateServicePoint(id,
        createServicePointVersionModel1);
    servicePointVersionModels.forEach(v -> v.setStatus(Status.IN_REVIEW));
    Integer number = servicePointVersionModel.getNumber().getNumber();

    mvc.perform(post("/internal/service-points/" + number + "/revoke"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].status", is(Status.REVOKED.toString())))
        .andExpect(jsonPath("$[1].status", is(Status.REVOKED.toString())));

    CreateServicePointVersionModel buchsiServicePoint = ServicePointTestData.getBuchsiServicePoint();
    buchsiServicePoint.setCountry(Country.GERMANY);
    buchsiServicePoint.setNumberShort(55555);
    buchsiServicePoint.setValidFrom(LocalDate.of(2019, 8, 11));
    buchsiServicePoint.setValidTo(LocalDate.of(2020, 8, 10));
    buchsiServicePoint.setDesignationLong("designation long 1");
    buchsiServicePoint.setDesignationOfficial("Aargau Strasse");
    buchsiServicePoint.setAbbreviation("NEWABC");
    buchsiServicePoint.setBusinessOrganisation("ch:1:sboid:100879");
    mvc.perform(post("/v1/service-points")
            .contentType(contentType)
            .content(mapper.writeValueAsString(buchsiServicePoint)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.designationOfficial", is("Aargau Strasse")))
        .andExpect(jsonPath("$.designationLong", is("designation long 1")))
        .andExpect(jsonPath("$.abbreviation", is("NEWABC")));
    mvc.perform(get("/v1/service-points"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.totalCount", is(3)))
        .andExpect(jsonPath("$.objects[0].abbreviation", is("ABC")))
        .andExpect(jsonPath("$.objects[0].designationLong", is("designation long 1")))
        .andExpect(jsonPath("$.objects[0].designationOfficial", is("Aargau Strasse")))
        .andExpect(jsonPath("$.objects[0].number.number", is(8012345)))
        .andExpect(jsonPath("$.objects[1].abbreviation", is("ABC")))
        .andExpect(jsonPath("$.objects[1].designationLong", is("designation long 1")))
        .andExpect(jsonPath("$.objects[1].designationOfficial", is("Aargau Strasse")))
        .andExpect(jsonPath("$.objects[1].number.number", is(8012345)))
        .andExpect(jsonPath("$.objects[2].abbreviation", is("NEWABC")))
        .andExpect(jsonPath("$.objects[2].designationLong", is("designation long 1")))
        .andExpect(jsonPath("$.objects[2].designationOfficial", is("Aargau Strasse")))
        .andExpect(jsonPath("$.objects[2].number.number", is(8055555)));

    verify(locationService, times(0)).generateSloid(any(), any(Country.class));
  }

  @Test
  void shouldNotAllowAbbreviationReuseAfterStatusRevoked() throws Exception {
    repository.deleteAll();
    CreateServicePointVersionModel aargauServicePointVersionModel = ServicePointTestData.getAargauServicePointVersionModel();
    aargauServicePointVersionModel.setCountry(Country.GERMANY);
    aargauServicePointVersionModel.setNumberShort(12345);
    CreateServicePointVersionModel createServicePointVersionModel1 = ServicePointTestData.getAargauServicePointVersionModel();
    createServicePointVersionModel1.setCountry(Country.GERMANY);
    createServicePointVersionModel1.setNumberShort(12345);
    createServicePointVersionModel1.setMeansOfTransport(List.of(MeanOfTransport.BUS));
    createServicePointVersionModel1.setValidFrom(LocalDate.of(2019, 8, 11));
    createServicePointVersionModel1.setValidTo(LocalDate.of(2020, 8, 10));

    ReadServicePointVersionModel servicePointVersionModel = servicePointApiV1Controller.createServicePoint(
        aargauServicePointVersionModel);
    createServicePointVersionModel1.setEtagVersion(servicePointVersionModel.getEtagVersion());
    Long id = servicePointVersionModel.getId();
    List<ReadServicePointVersionModel> servicePointVersionModels = servicePointApiV1Controller.updateServicePoint(id,
        createServicePointVersionModel1);
    servicePointVersionModels.forEach(v -> v.setStatus(Status.IN_REVIEW));
    Integer number = servicePointVersionModel.getNumber().getNumber();

    mvc.perform(post("/internal/service-points/" + number + "/revoke"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].status", is(Status.REVOKED.toString())))
        .andExpect(jsonPath("$[1].status", is(Status.REVOKED.toString())));

    CreateServicePointVersionModel buchsiServicePoint = ServicePointTestData.getBuchsiServicePoint();
    buchsiServicePoint.setCountry(Country.GERMANY);
    buchsiServicePoint.setNumberShort(55555);
    buchsiServicePoint.setValidFrom(LocalDate.of(2019, 8, 11));
    buchsiServicePoint.setValidTo(LocalDate.of(2020, 8, 10));
    buchsiServicePoint.setDesignationLong("designation long 1");
    buchsiServicePoint.setDesignationOfficial("Aargau Strasse");
    buchsiServicePoint.setAbbreviation("ABC");
    buchsiServicePoint.setBusinessOrganisation("ch:1:sboid:100879");
    mvc.perform(post("/v1/service-points")
            .contentType(contentType)
            .content(mapper.writeValueAsString(buchsiServicePoint)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message",
            is("The abbreviation must be unique and the chosen servicepoint version should be the most recent version.")))
        .andExpect(jsonPath("$.details.[0].message", endsWith(
            "The abbreviation must be unique and the chosen servicepoint version should be the most recent version.")));
    verify(locationService, times(0)).generateSloid(any(), any(Country.class));
  }

  @Test
  void shouldThrowExceptionOnRevokeNonExistingServicePoint() throws Exception {
    int number = 1234567;

    mvc.perform(post("/internal/service-points/" + number + "/revoke"))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.message", is("Entity not found")))
        .andExpect(jsonPath("$.details.[0].message", endsWith(
            "Object with servicePointNumber 1234567 not found")));
  }

  @Test
  void shouldNotRevokeWhenOneOrMoreVersionsAreInReview() throws Exception {
    CreateServicePointVersionModel aargauServicePointVersionModel = ServicePointTestData.getAargauServicePointVersionModel();
    UpdateServicePointVersionModel createServicePointVersionModel1 = ServicePointTestData.getAargauServicePointVersionModel();
    createServicePointVersionModel1.setMeansOfTransport(List.of(MeanOfTransport.BUS));
    createServicePointVersionModel1.setValidFrom(LocalDate.of(2019, 8, 11));
    createServicePointVersionModel1.setValidTo(LocalDate.of(2020, 8, 10));

    ReadServicePointVersionModel servicePointVersionModel = servicePointApiV1Controller.createServicePoint(
        aargauServicePointVersionModel);

    Long id = servicePointVersionModel.getId();
    createServicePointVersionModel1.setEtagVersion(servicePointVersionModel.getEtagVersion());
    servicePointApiV1Controller.updateServicePoint(id, createServicePointVersionModel1);
    servicePointWorkflowApiInternalController.updateServicePointStatus(servicePointVersionModel.getSloid(),
        servicePointVersionModel.getId(),
        Status.IN_REVIEW);

    Integer number = servicePointVersionModel.getNumber().getNumber();

    mvc.perform(post("/internal/service-points/" + number + "/revoke"))
        .andExpect(status().isForbidden())
        .andExpect(
            jsonPath("$.error",
                is("Termination not allowed")));
  }
}
