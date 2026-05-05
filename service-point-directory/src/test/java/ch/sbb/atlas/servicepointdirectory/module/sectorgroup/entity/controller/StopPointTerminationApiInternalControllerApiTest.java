package ch.sbb.atlas.servicepointdirectory.module.sectorgroup.entity.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ch.sbb.atlas.api.servicepoint.ReadServicePointVersionModel;
import ch.sbb.atlas.api.servicepoint.ServicePointConstants;
import ch.sbb.atlas.api.servicepoint.StopPointWorkflowTerminationModel;
import ch.sbb.atlas.api.servicepoint.UpdateTerminationServicePointModel;
import ch.sbb.atlas.business.organisation.service.SharedBusinessOrganisationService;
import ch.sbb.atlas.kafka.model.user.admin.ApplicationRole;
import ch.sbb.atlas.kafka.model.user.admin.ApplicationType;
import ch.sbb.atlas.kafka.model.user.admin.PermissionRestrictionType;
import ch.sbb.atlas.location.LocationService;
import ch.sbb.atlas.model.Status;
import ch.sbb.atlas.model.controller.BaseControllerApiTest;
import ch.sbb.atlas.model.controller.WithMockJwtAuthentication;
import ch.sbb.atlas.model.controller.WithMockJwtAuthentication.MockRole;
import ch.sbb.atlas.servicepoint.Country;
import ch.sbb.atlas.servicepoint.ServicePointNumber;
import ch.sbb.atlas.servicepoint.enumeration.OperatingPointTrafficPointType;
import ch.sbb.atlas.servicepointdirectory.module.geodata.service.ServicePointGeoDataService;
import ch.sbb.atlas.servicepointdirectory.module.servicepoint.ServicePointTestData;
import ch.sbb.atlas.servicepointdirectory.module.servicepoint.controller.ServicePointApiV1Controller;
import ch.sbb.atlas.servicepointdirectory.module.servicepoint.entity.ServicePointVersion;
import ch.sbb.atlas.servicepointdirectory.module.servicepoint.repository.ServicePointVersionRepository;
import ch.sbb.atlas.user.administration.security.entity.Permission;
import ch.sbb.atlas.user.administration.security.entity.PermissionRestriction;
import ch.sbb.atlas.user.administration.security.repository.PermissionRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.ResultActions;

class StopPointTerminationApiInternalControllerApiTest extends BaseControllerApiTest {

  @MockitoBean
  private SharedBusinessOrganisationService sharedBusinessOrganisationService;

  @MockitoBean
  private LocationService locationService;

  @MockitoBean
  private ServicePointGeoDataService servicePointGeoDataService;

  @Autowired
  private ServicePointVersionRepository repository;

  @Autowired
  private ServicePointApiV1Controller servicePointController;

  @Autowired
  private PermissionRepository permissionRepository;

  @BeforeEach
  void createDefaultVersion() {
    repository.save(ServicePointTestData.getBernWyleregg());
    when(locationService.generateSloid(any(), any(Country.class))).thenReturn("ch:1:sloid:1");
    when(servicePointGeoDataService.getGeoReferenceInformation(any())).thenReturn(
        ServicePointTestData.getServicePointGeolocationBernMittelland());
  }

  @AfterEach
  void cleanUpDb() {
    repository.deleteAll();
    permissionRepository.deleteAll();
  }

  @Nested
  @DisplayName("POST internal/service-points/termination/start/{sloid}/{id}")
  class StartServicePointTermination {

    @Test
    void shouldStartServicePointTermination() throws Exception {
      startTermination().andExpect(status().isOk()).andExpect(jsonPath("$.terminationInProgress", is(true)));
    }

    private ResultActions startTermination() throws Exception {
      ServicePointVersion servicePointVersion =
          ServicePointTestData.createStopPointServicePointWithUnknownMeanOfTransportVersion();
      servicePointVersion.setStatus(Status.VALIDATED);
      ServicePointVersion version = repository.save(servicePointVersion);
      Long id = version.getId();
      String sloid = version.getSloid();

      UpdateTerminationServicePointModel updateTerminationServicePointModel = UpdateTerminationServicePointModel.builder()
          .terminationInProgress(true).terminationDate(version.getValidTo().minusDays(1)).build();

      return mvc.perform(post("/internal/service-points/termination/start/" + sloid + "/" + id).contentType(contentType)
          .content(mapper.writeValueAsString(updateTerminationServicePointModel)));
    }

    @Test
    @WithMockJwtAuthentication(role = MockRole.UNAUTHORIZED)
    void shouldNotStartServicePointTerminationWhenUnauthorized() throws Exception {
      startTermination().andExpect(status().isForbidden());
    }

    @Test
    @WithMockJwtAuthentication(role = MockRole.STANDARD)
    void shouldStartServicePointTerminationAsWriter() throws Exception {
      // given write permission on sboid and country match service point
      Permission permission = Permission.builder()
          .identifier(WithMockJwtAuthentication.MOCKUSER_SBB_UID)
          .application(ApplicationType.SEPODI)
          .role(ApplicationRole.WRITER)
          .build();
      permission.setPermissionRestrictions(Set.of(PermissionRestriction.builder()
              .permission(permission)
              .type(PermissionRestrictionType.BUSINESS_ORGANISATION)
              .restriction("ch:1:sboid:100626").build(),
          PermissionRestriction.builder()
              .permission(permission)
              .type(PermissionRestrictionType.COUNTRY)
              .restriction(Country.SWITZERLAND.name()).build()));
      permissionRepository.saveAndFlush(permission);

      // when & then
      startTermination().andExpect(status().isOk());
    }

    @Test
    void shouldNotStartServicePointTerminationWhenIdNotFound() throws Exception {
      ReadServicePointVersionModel servicePointVersionModel = servicePointController.createServicePoint(
          ServicePointTestData.getAargauServicePointVersionModel());
      long id = 456L;
      String sloid = servicePointVersionModel.getSloid();

      UpdateTerminationServicePointModel updateTerminationServicePointModel = UpdateTerminationServicePointModel.builder()
          .terminationInProgress(true)
          .terminationDate(servicePointVersionModel.getValidTo().minusDays(1))
          .build();
      mvc.perform(post("/internal/service-points/termination/start/" + sloid + "/" + id)
              .contentType(contentType)
              .content(mapper.writeValueAsString(updateTerminationServicePointModel)))
          .andExpect(status().isNotFound());
    }

    @Test
    void shouldNotStartServicePointTerminationWhenSloidDoesNotExists() throws Exception {
      long id = 123L;
      String sloid = "ch:1:sloid:753126";

      UpdateTerminationServicePointModel updateTerminationServicePointModel = UpdateTerminationServicePointModel.builder()
          .terminationInProgress(true)
          .terminationDate(LocalDate.now())
          .build();

      mvc.perform(post("/internal/service-points/termination/start/" + sloid + "/" + id)
              .contentType(contentType)
              .content(mapper.writeValueAsString(updateTerminationServicePointModel)))
          .andExpect(status().isNotFound());
    }
  }

  @Nested
  @DisplayName("POST internal/service-points/termination/stop/{sloid}/{id}")
  class StopServicePointTermination {

    @Test
    void shouldStopServicePointTermination() throws Exception {
      stopServicePointTermination()
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.terminationInProgress", is(false)));
    }

    private ResultActions stopServicePointTermination() throws Exception {
      ServicePointVersion servicePointVersion =
          ServicePointTestData.createStopPointServicePointWithUnknownMeanOfTransportVersion();
      servicePointVersion.setStatus(Status.VALIDATED);
      ServicePointVersion version = repository.save(servicePointVersion);
      Long id = version.getId();
      String sloid = version.getSloid();

      return mvc.perform(post("/internal/service-points/termination/stop/" + sloid + "/" + id));
    }

    @Test
    @WithMockJwtAuthentication(role = MockRole.STANDARD)
    void shouldNotStopServicePointTerminationAsStandardUser() throws Exception {
      stopServicePointTermination().andExpect(status().isForbidden());
    }

    @Test
    @WithMockJwtAuthentication(role = MockRole.UNAUTHORIZED)
    void shouldNotStopServicePointTerminationAsUnauthorized() throws Exception {
      stopServicePointTermination().andExpect(status().isForbidden());
    }

    @Test
    void shouldNotStopServicePointTerminationWhenIdNotFound() throws Exception {
      ReadServicePointVersionModel servicePointVersionModel = servicePointController.createServicePoint(
          ServicePointTestData.getAargauServicePointVersionModel());
      long id = 456L;
      String sloid = servicePointVersionModel.getSloid();

      mvc.perform(post("/internal/service-points/termination/stop/" + sloid + "/" + id))
          .andExpect(status().isNotFound());
    }

    @Test
    void shouldNotStopServicePointTerminationWhenSloidDoesNotExists() throws Exception {
      long id = 123L;
      String sloid = "ch:1:sloid:753126";

      mvc.perform(post("/internal/service-points/termination/stop/" + sloid + "/" + id))
          .andExpect(status().isNotFound());
    }
  }

  @Nested
  @DisplayName("POST internal/service-points/termination/terminate")
  class TerminateStopPointByWorkflow {

    @Test
    void shouldTerminateServicePointAndStopTermination() throws Exception {
      terminateServicePointAndStopTermination()
          .andExpect(status().isOk());

      List<ServicePointVersion> result = repository.findAllByNumberOrderByValidFrom(
          ServicePointNumber.ofNumberWithoutCheckDigit(8501234));
      assertThat(result).hasSize(1);
      assertThat(result.getFirst().getValidTo()).isEqualTo(LocalDate.of(2030, 12, 31));
      assertThat(result.getFirst().isTerminationInProgress()).isFalse();
    }

    private ResultActions terminateServicePointAndStopTermination() throws Exception {
      ServicePointVersion servicePointVersion =
          ServicePointTestData.createStopPointServicePointWithUnknownMeanOfTransportVersion();
      servicePointVersion.setDesignationOfficial("Bern, Salem");
      servicePointVersion.setValidTo(LocalDate.of(2099, 12, 31));
      servicePointVersion.setStatus(Status.VALIDATED);
      servicePointVersion.setTerminationInProgress(true);

      ServicePointVersion version = repository.save(servicePointVersion);

      StopPointWorkflowTerminationModel terminationModel = StopPointWorkflowTerminationModel.builder()
          .sloid(version.getSloid())
          .versionId(version.getId())
          .terminationDate(LocalDate.of(2030, 12, 31))
          .build();
      return mvc.perform(post("/internal/service-points/termination/terminate")
          .contentType(contentType)
          .content(mapper.writeValueAsString(terminationModel)));
    }

    @Test
    @WithMockJwtAuthentication(role = MockRole.STANDARD)
    void shouldNotTerminateAsStandardUser() throws Exception {
      terminateServicePointAndStopTermination().andExpect(status().isForbidden());
    }

    @Test
    @WithMockJwtAuthentication(role = MockRole.UNAUTHORIZED)
    void shouldNotTerminateAsUnauthorized() throws Exception {
      terminateServicePointAndStopTermination().andExpect(status().isForbidden());
    }
  }

  @Nested
  @DisplayName("POST internal/service-points/termination/change-to-tariff-stop")
  class ChangeToTariffStop {

    @Test
    void shouldChangeToTariffStop() throws Exception {
      changeToTariffStop()
          .andExpect(status().isOk());

      List<ServicePointVersion> result = repository.findAllByNumberOrderByValidFrom(
          ServicePointNumber.ofNumberWithoutCheckDigit(8501234));
      assertThat(result).hasSize(2);
      assertThat(result.getFirst().getValidTo()).isEqualTo(LocalDate.of(2030, 12, 30));
      assertThat(result.getFirst().isStopPoint()).isTrue();

      assertThat(result.getLast().getOperatingPointTrafficPointType()).isEqualTo(OperatingPointTrafficPointType.TARIFF_POINT);
      assertThat(result.getLast().isStopPoint()).isFalse();
      assertThat(result.getLast().getStopPointType()).isNull();
      assertThat(result.getLast().hasGeolocation()).isFalse();
      assertThat(result.getLast().getBusinessOrganisation()).isEqualTo(ServicePointConstants.ALLIANCE_SWISS_PASS_SBOID);
      assertThat(result.getLast().getValidFrom()).isEqualTo(LocalDate.of(2030, 12, 31));
      assertThat(result.getLast().getValidTo()).isEqualTo(LocalDate.of(2099, 12, 31));
    }

    private ResultActions changeToTariffStop() throws Exception {
      ServicePointVersion servicePointVersion =
          ServicePointTestData.createStopPointServicePointWithUnknownMeanOfTransportVersion();
      servicePointVersion.setDesignationOfficial("Bern, Salem");
      servicePointVersion.setValidTo(LocalDate.of(2099, 12, 31));
      servicePointVersion.setStatus(Status.VALIDATED);
      servicePointVersion.setTerminationInProgress(true);
      assertThat(servicePointVersion.isStopPoint()).isTrue();
      assertThat(servicePointVersion.getOperatingPointTrafficPointType()).isNull();

      ServicePointVersion version = repository.save(servicePointVersion);
      Long id = version.getId();

      StopPointWorkflowTerminationModel terminationModel = StopPointWorkflowTerminationModel.builder()
          .sloid(version.getSloid())
          .versionId(id)
          .terminationDate(LocalDate.of(2030, 12, 31))
          .build();
      return mvc.perform(post("/internal/service-points/termination/change-to-tariff-stop")
          .contentType(contentType)
          .content(mapper.writeValueAsString(terminationModel)));
    }

    @Test
    @WithMockJwtAuthentication(role = MockRole.STANDARD)
    void shouldNotChangeToTariffStopAsStandardUser() throws Exception {
      changeToTariffStop().andExpect(status().isForbidden());
    }

    @Test
    @WithMockJwtAuthentication(role = MockRole.UNAUTHORIZED)
    void shouldNotChangeToTariffStopAsUnauthorized() throws Exception {
      changeToTariffStop().andExpect(status().isForbidden());
    }
  }

}