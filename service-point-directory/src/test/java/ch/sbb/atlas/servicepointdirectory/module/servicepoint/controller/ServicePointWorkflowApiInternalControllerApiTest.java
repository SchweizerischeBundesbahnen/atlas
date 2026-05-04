package ch.sbb.atlas.servicepointdirectory.module.servicepoint.controller;

import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ch.sbb.atlas.api.servicepoint.CreateServicePointVersionModel;
import ch.sbb.atlas.api.servicepoint.ReadServicePointVersionModel;
import ch.sbb.atlas.api.servicepoint.ServicePointGeolocationCreateModel;
import ch.sbb.atlas.api.servicepoint.UpdateDesignationOfficialServicePointModel;
import ch.sbb.atlas.api.servicepoint.UpdateServicePointVersionModel;
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
import ch.sbb.atlas.servicepointdirectory.module.geodata.entity.ServicePointGeolocation;
import ch.sbb.atlas.servicepointdirectory.module.geodata.service.ServicePointGeoDataService;
import ch.sbb.atlas.servicepointdirectory.module.servicepoint.ServicePointTestData;
import ch.sbb.atlas.servicepointdirectory.module.servicepoint.entity.ServicePointVersion;
import ch.sbb.atlas.servicepointdirectory.module.servicepoint.mapper.ServicePointGeolocationMapper;
import ch.sbb.atlas.servicepointdirectory.module.servicepoint.repository.ServicePointVersionRepository;
import ch.sbb.atlas.user.administration.security.entity.Permission;
import ch.sbb.atlas.user.administration.security.entity.PermissionRestriction;
import ch.sbb.atlas.user.administration.security.repository.PermissionRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.ResultActions;

class ServicePointWorkflowApiInternalControllerApiTest extends BaseControllerApiTest {

  @MockitoBean
  private SharedBusinessOrganisationService sharedBusinessOrganisationService;

  @MockitoBean
  private LocationService locationService;

  @MockitoBean
  private ServicePointGeoDataService servicePointGeoDataService;

  @Autowired
  private ServicePointVersionRepository repository;

  @Autowired
  private ServicePointApiV1Controller servicePointApiV1Controller;

  @Autowired
  private ServicePointWorkflowApiInternalController servicePointWorkflowApiInternalController;

  @Autowired
  private PermissionRepository permissionRepository;

  private ServicePointVersion servicePointVersion;

  @BeforeEach
  void createDefaultVersion() {
    servicePointVersion = repository.save(ServicePointTestData.getBernWyleregg());
    when(locationService.generateSloid(any(), any(Country.class))).thenReturn("ch:1:sloid:1");
    when(servicePointGeoDataService.getGeoReferenceInformation(any(ServicePointGeolocation.class))).thenReturn(
        ServicePointTestData.getServicePointGeolocationBernMittelland());
  }

  @AfterEach
  void cleanUpDb() {
    repository.deleteAll();
    permissionRepository.deleteAll();
  }

  @Nested
  @DisplayName("POST internal/service-points/versions/{id}/skip-workflow")
  class ValidateServicePoint {

    @Test
    void shouldSetStatusToValidatedForServicePoint() throws Exception {
      setStatusToValidatedForServicePoint()
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.status", is(Status.VALIDATED.toString())));
    }

    private ResultActions setStatusToValidatedForServicePoint() throws Exception {
      servicePointVersion.setStatus(Status.DRAFT);
      repository.saveAndFlush(servicePointVersion);

      return mvc.perform(post("/internal/service-points/versions/" + servicePointVersion.getId() + "/skip-workflow"));
    }

    @Test
    @WithMockJwtAuthentication(role = MockRole.UNAUTHORIZED)
    void shouldNotSetStatusToValidatedForServicePointAsUnauthorized() throws Exception {
      setStatusToValidatedForServicePoint().andExpect(status().isForbidden());
    }

    @Test
    @WithMockJwtAuthentication(role = MockRole.STANDARD)
    void shouldNotSetStatusToValidatedForServicePointAsUserWithoutPermissions() throws Exception {
      setStatusToValidatedForServicePoint().andExpect(status().isForbidden());
    }

    @Test
    @WithMockJwtAuthentication(role = MockRole.STANDARD)
    void shouldSetStatusToValidatedForServicePointAsSupervisor() throws Exception {
      Permission permission = Permission.builder()
          .identifier(WithMockJwtAuthentication.MOCKUSER_SBB_UID)
          .application(ApplicationType.SEPODI)
          .role(ApplicationRole.SUPERVISOR)
          .build();
      permissionRepository.saveAndFlush(permission);

      setStatusToValidatedForServicePoint().andExpect(status().isOk());
    }

    @Test
    void shouldNotAllowSetStatusToValidatedForServicePointWithValidatedStatus() throws Exception {
      CreateServicePointVersionModel aargauServicePointVersionModel = ServicePointTestData.getAargauServicePointVersionModel();
      ReadServicePointVersionModel servicePointVersionModel = servicePointApiV1Controller.createServicePoint(
          aargauServicePointVersionModel);
      Long id = servicePointVersionModel.getId();
      Optional<ServicePointVersion> servicePointVersion1 = repository.findById(id);
      servicePointVersion1.ifPresent(pointVersion -> pointVersion.setStatus(Status.VALIDATED));
      servicePointVersion1.ifPresent(repository::save);

      mvc.perform(post("/internal/service-points/versions/" + id + "/skip-workflow")
              .contentType(contentType)
              .content(mapper.writeValueAsString(aargauServicePointVersionModel)))
          .andExpect(status().isPreconditionFailed())
          .andExpect(jsonPath("$.message", is(
              "ServicePoint Status cannot be changed for Status REVOKED and can be updated only from DRAFT to VALIDATED!")))
          .andExpect(jsonPath("$.error", endsWith(
              "Trying to update status for ServicePointNumber 8500001 and current status: VALIDATED")));
    }

    @Test
    void shouldNotAllowSetStatusToValidatedForServicePointWithRevokedStatus() throws Exception {
      CreateServicePointVersionModel aargauServicePointVersionModel = ServicePointTestData.getAargauServicePointVersionModel();
      ReadServicePointVersionModel servicePointVersionModel = servicePointApiV1Controller.createServicePoint(
          aargauServicePointVersionModel);
      Long id = servicePointVersionModel.getId();
      Optional<ServicePointVersion> servicePointVersion1 = repository.findById(id);
      servicePointVersion1.ifPresent(pointVersion -> pointVersion.setStatus(Status.REVOKED));
      servicePointVersion1.ifPresent(repository::save);

      mvc.perform(post("/internal/service-points/versions/" + id + "/skip-workflow")
              .contentType(contentType)
              .content(mapper.writeValueAsString(aargauServicePointVersionModel)))
          .andExpect(status().isPreconditionFailed())
          .andExpect(jsonPath("$.message", is(
              "ServicePoint Status cannot be changed for Status REVOKED and can be updated only from DRAFT to VALIDATED!")))
          .andExpect(jsonPath("$.error", endsWith(
              "Trying to update status for ServicePointNumber 8500001 and current status: REVOKED")));
    }

    @Test
    void shouldNotAllowSetStatusToValidatedForServicePointWithInReviewStatus() throws Exception {
      CreateServicePointVersionModel aargauServicePointVersionModel = ServicePointTestData.getAargauServicePointVersionModel();
      ReadServicePointVersionModel servicePointVersionModel = servicePointApiV1Controller.createServicePoint(
          aargauServicePointVersionModel);
      Long id = servicePointVersionModel.getId();
      Optional<ServicePointVersion> servicePointVersion1 = repository.findById(id);
      servicePointVersion1.ifPresent(pointVersion -> pointVersion.setStatus(Status.IN_REVIEW));
      servicePointVersion1.ifPresent(repository::save);

      mvc.perform(post("/internal/service-points/versions/" + id + "/skip-workflow")
              .contentType(contentType)
              .content(mapper.writeValueAsString(aargauServicePointVersionModel)))
          .andExpect(status().isPreconditionFailed())
          .andExpect(jsonPath("$.message", is(
              "ServicePoint Status cannot be changed for Status REVOKED and can be updated only from DRAFT to VALIDATED!")))
          .andExpect(jsonPath("$.error", endsWith(
              "Trying to update status for ServicePointNumber 8500001 and current status: IN_REVIEW")));
    }

    @Test
    void shouldNotAllowSetStatusToValidatedForServicePointWithWithdrawnStatus() throws Exception {
      CreateServicePointVersionModel aargauServicePointVersionModel = ServicePointTestData.getAargauServicePointVersionModel();
      ReadServicePointVersionModel servicePointVersionModel = servicePointApiV1Controller.createServicePoint(
          aargauServicePointVersionModel);
      Long id = servicePointVersionModel.getId();
      Optional<ServicePointVersion> servicePointVersion1 = repository.findById(id);
      servicePointVersion1.ifPresent(pointVersion -> pointVersion.setStatus(Status.WITHDRAWN));
      servicePointVersion1.ifPresent(repository::save);

      mvc.perform(post("/internal/service-points/versions/" + id + "/skip-workflow")
              .contentType(contentType)
              .content(mapper.writeValueAsString(aargauServicePointVersionModel)))
          .andExpect(status().isPreconditionFailed())
          .andExpect(jsonPath("$.message", is(
              "ServicePoint Status cannot be changed for Status REVOKED and can be updated only from DRAFT to VALIDATED!")))
          .andExpect(jsonPath("$.error", endsWith(
              "Trying to update status for ServicePointNumber 8500001 and current status: WITHDRAWN")));
    }

    @Test
    void shouldNotValidateServicePointIfTerminationInProgress() throws Exception {
      //given
      CreateServicePointVersionModel aargauServicePointVersionModel = ServicePointTestData.getAargauServicePointVersionModel();
      ReadServicePointVersionModel servicePointVersionModel = servicePointApiV1Controller.createServicePoint(
          aargauServicePointVersionModel);
      Long id = servicePointVersionModel.getId();
      repository.findById(id).ifPresent(servicePoint -> {
        servicePoint.setTerminationInProgress(true);
        repository.save(servicePoint);
      });
      //when & then
      mvc.perform(post("/internal/service-points/versions/" + id + "/skip-workflow"))
          .andExpect(status().isPreconditionFailed());
    }
  }

  @Nested
  @DisplayName("PUT internal/service-points/update-designation-official/{id}")
  class UpdateDesignationOfficial {

    @Test
    void shouldUpdateServicePointDesignationOfficial() throws Exception {
      updateServicePointDesignationOfficial()
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.designationOfficial", is("test")));
    }

    private ResultActions updateServicePointDesignationOfficial() throws Exception {
      UpdateDesignationOfficialServicePointModel updateDesignationOfficialServicePointModel =
          UpdateDesignationOfficialServicePointModel.builder()
              .designationOfficial("test")
              .build();

      return mvc.perform(put("/internal/service-points/update-designation-official/" + servicePointVersion.getId())
          .contentType(contentType)
          .content(mapper.writeValueAsString(updateDesignationOfficialServicePointModel)));
    }

    @Test
    @WithMockJwtAuthentication(role = MockRole.UNAUTHORIZED)
    void shouldNotSetStatusToValidatedForServicePointAsUnauthorized() throws Exception {
      updateServicePointDesignationOfficial().andExpect(status().isForbidden());
    }

    @Test
    @WithMockJwtAuthentication(role = MockRole.STANDARD)
    void shouldNotSetStatusToValidatedForServicePointAsUserWithoutPermissions() throws Exception {
      updateServicePointDesignationOfficial().andExpect(status().isForbidden());
    }

    @Test
    @WithMockJwtAuthentication(role = MockRole.STANDARD)
    void shouldSetStatusToValidatedForServicePointAsSupervisor() throws Exception {
      Permission permission = Permission.builder()
          .identifier(WithMockJwtAuthentication.MOCKUSER_SBB_UID)
          .application(ApplicationType.SEPODI)
          .role(ApplicationRole.SUPERVISOR)
          .build();
      permissionRepository.saveAndFlush(permission);

      updateServicePointDesignationOfficial().andExpect(status().isOk());
    }

    @Test
    void shouldNotUpdateServicePointOnEmptyDesignationOfficial() throws Exception {
      ReadServicePointVersionModel servicePointVersionModel = servicePointApiV1Controller.createServicePoint(
          ServicePointTestData.getAargauServicePointVersionModel());
      Long id = servicePointVersionModel.getId();

      UpdateDesignationOfficialServicePointModel updateDesignationOfficialServicePointModel =
          UpdateDesignationOfficialServicePointModel.builder()
              .designationOfficial("")
              .build();

      mvc.perform(put("/internal/service-points/update-designation-official/" + id)
              .contentType(contentType)
              .content(mapper.writeValueAsString(updateDesignationOfficialServicePointModel)))
          .andExpect(status().isBadRequest());
    }

    @Test
    void shouldNotMergeServicePointVersionOnUpdateDesignationOfficial() throws Exception {
      ReadServicePointVersionModel servicePointVersionModel = servicePointApiV1Controller.createServicePoint(
          ServicePointTestData.getAargauServicePointVersionModel());

      UpdateServicePointVersionModel updateServicePointVersionModel = ServicePointTestData.getAargauServicePointVersionModel();
      updateServicePointVersionModel.setDesignationOfficial("Aargau Strasse 1");
      updateServicePointVersionModel.setValidFrom(LocalDate.of(2019, 8, 11));
      updateServicePointVersionModel.setValidTo(LocalDate.of(2020, 8, 11));
      updateServicePointVersionModel.setEtagVersion(0);

      List<ReadServicePointVersionModel> list = servicePointApiV1Controller.updateServicePoint(servicePointVersionModel.getId(),
          updateServicePointVersionModel);

      Long id = list.getLast().getId();
      String sloid = list.getLast().getSloid();

      servicePointWorkflowApiInternalController.updateServicePointStatus(sloid, id, Status.IN_REVIEW);

      UpdateDesignationOfficialServicePointModel updateDesignationOfficialServicePointModel =
          UpdateDesignationOfficialServicePointModel.builder()
              .designationOfficial("Aargau Strasse")
              .build();

      mvc.perform(put("/internal/service-points/update-designation-official/" + id)
              .contentType(contentType)
              .content(mapper.writeValueAsString(updateDesignationOfficialServicePointModel)))
          .andExpect(status().isConflict());
    }

    @Test
    void shouldNotUpdateServicePointOnLongDesignationOfficial() throws Exception {
      ReadServicePointVersionModel servicePointVersionModel = servicePointApiV1Controller.createServicePoint(
          ServicePointTestData.getAargauServicePointVersionModel());
      Long id = servicePointVersionModel.getId();

      UpdateDesignationOfficialServicePointModel updateDesignationOfficialServicePointModel =
          UpdateDesignationOfficialServicePointModel.builder()
              .designationOfficial("DASISTEINEVIELZULANGEDESIGNATIONOFFICIAL")
              .build();

      mvc.perform(put("/internal/service-points/update-designation-official/" + id)
              .contentType(contentType)
              .content(mapper.writeValueAsString(updateDesignationOfficialServicePointModel)))
          .andExpect(status().isBadRequest());
    }
  }

  @Nested
  @DisplayName("PUT internal/service-points/status/{sloid}/{id}")
  class UpdateServicePointStatus {

    @Test
    void shouldUpdateServicePointStatusFromDraftToInReviewOnStartingWorkflow() throws Exception {
      updateServicePointStatusFromDraftToInReviewOnStartingWorkflow().andExpect(status().isOk());
    }

    private ResultActions updateServicePointStatusFromDraftToInReviewOnStartingWorkflow() throws Exception {
      //given
      servicePointVersion.setStatus(Status.DRAFT);
      repository.save(servicePointVersion);

      //when
      return mvc.perform(
          put("/internal/service-points/status/" + servicePointVersion.getSloid() + "/" + servicePointVersion.getId())
              .contentType(contentType)
              .content(mapper.writeValueAsString(Status.IN_REVIEW)));
    }

    @Test
    @WithMockJwtAuthentication(role = MockRole.STANDARD)
    void shouldUpdateServicePointStatusFromDraftToInReviewOnStartingWorkflowAsWriter() throws Exception {
      // given write permission on sboid and country match service point
      Permission permission = Permission.builder()
          .identifier(WithMockJwtAuthentication.MOCKUSER_SBB_UID)
          .application(ApplicationType.SEPODI)
          .role(ApplicationRole.WRITER)
          .build();
      permission.setPermissionRestrictions(Set.of(PermissionRestriction.builder()
              .permission(permission)
              .type(PermissionRestrictionType.BUSINESS_ORGANISATION)
              .restriction(servicePointVersion.getBusinessOrganisation())
              .build(),
          PermissionRestriction.builder()
              .permission(permission)
              .type(PermissionRestrictionType.COUNTRY)
              .restriction(servicePointVersion.getCountry().name())
              .build()));
      permissionRepository.saveAndFlush(permission);

      updateServicePointStatusFromDraftToInReviewOnStartingWorkflow().andExpect(status().isOk());
    }

    @Test
    @WithMockJwtAuthentication(role = MockRole.UNAUTHORIZED)
    void shouldNotUpdateServicePointStatusFromDraftToInReviewOnStartingWorkflowAsUnauthorized() throws Exception {
      updateServicePointStatusFromDraftToInReviewOnStartingWorkflow().andExpect(status().isForbidden());
    }

    @Test
    void shouldNotUpdateServicePointStatusWhenAlreadyInAdded() throws Exception {
      //given
      servicePointVersion.setStatus(Status.IN_REVIEW);
      repository.save(servicePointVersion);

      //when & then
      mvc.perform(put("/internal/service-points/status/" + servicePointVersion.getSloid() + "/" + servicePointVersion.getId())
              .contentType(contentType)
              .content(mapper.writeValueAsString(Status.IN_REVIEW)))
          .andExpect(status().isPreconditionFailed());
    }

    @Test
    void shouldNotUpdateServicePointStatusIfTerminationInProgress() throws Exception {
      //given
      CreateServicePointVersionModel aargauServicePointVersionModel = ServicePointTestData.getAargauServicePointVersionModel();
      ReadServicePointVersionModel servicePointVersionModel = servicePointApiV1Controller.createServicePoint(
          aargauServicePointVersionModel);
      Long id = servicePointVersionModel.getId();
      repository.findById(id).ifPresent(servicePoint -> {
        servicePoint.setTerminationInProgress(true);
        repository.save(servicePoint);
      });

      //when & then
      mvc.perform(put("/internal/service-points/status/" + servicePointVersion.getSloid() + "/" + servicePointVersion.getId())
              .contentType(contentType)
              .content(mapper.writeValueAsString(Status.IN_REVIEW)))
          .andExpect(status().isPreconditionFailed());
    }
  }

  @Nested
  @DisplayName("PUT v1/service-points//{id}")
  class UpdateServicePoint {

    @Test
    void shouldNotUpdateWhenOneVersionInReviewIsGoingToBeMergedLeft() throws Exception {
      //given
      repository.deleteAll();
      //Create 1st Version
      ServicePointGeolocationCreateModel servicePointGeolocationCreateModel =
          ServicePointGeolocationMapper.toCreateModel(ServicePointTestData.getAargauServicePointGeolocation());
      CreateServicePointVersionModel stopPoint1 = ServicePointTestData.getAargauServicePointVersionModel();
      stopPoint1.setValidFrom(LocalDate.of(2000, 1, 1));
      stopPoint1.setValidTo(LocalDate.of(2001, 12, 31));
      stopPoint1.setDesignationOfficial("Bern");
      stopPoint1.setServicePointGeolocation(servicePointGeolocationCreateModel);
      ReadServicePointVersionModel servicePointVersionModel =
          servicePointApiV1Controller.createServicePoint(stopPoint1);
      Long id = servicePointVersionModel.getId();

      //Create 2nd version
      UpdateServicePointVersionModel stopPoint2 = ServicePointTestData.getAargauServicePointVersionModel();
      stopPoint2.setValidFrom(LocalDate.of(2001, 1, 1));
      stopPoint2.setValidTo(LocalDate.of(2002, 12, 31));
      stopPoint2.setDesignationOfficial("Bern1");
      stopPoint2.setServicePointGeolocation(servicePointGeolocationCreateModel);
      stopPoint2.setEtagVersion(servicePointApiV1Controller.getServicePointVersion(id).getEtagVersion());

      List<ReadServicePointVersionModel> servicePointVersionModel2 = servicePointApiV1Controller.updateServicePoint(id,
          stopPoint2);
      Long id2 = servicePointVersionModel2.get(1).getId();

      //1st version is now in hearing => status = IN_REVIEW
      servicePointWorkflowApiInternalController.updateServicePointStatus(servicePointVersionModel.getSloid(),
          servicePointVersionModel.getId(),
          Status.IN_REVIEW);

      //when
      //Try to update 2nd version with designationOfficial Bern == to 1st version. This should results in a merge
      UpdateServicePointVersionModel stopPoint3 = ServicePointTestData.getAargauServicePointVersionModel();
      stopPoint3.setValidFrom(LocalDate.of(2001, 1, 1));
      stopPoint3.setValidTo(LocalDate.of(2002, 12, 31));
      stopPoint3.setDesignationOfficial("Bern");
      stopPoint3.setServicePointGeolocation(servicePointGeolocationCreateModel);
      stopPoint3.setEtagVersion(servicePointApiV1Controller.getServicePointVersion(id2).getEtagVersion());

      mvc.perform(put("/v1/service-points/" + id2)
              .contentType(contentType)
              .content(mapper.writeValueAsString(stopPoint3)))
          .andExpect(status().isConflict())
          .andExpect(
              jsonPath("$.error",
                  is("Update affects one or more versions that have status: IN_REVIEW.")));
    }

    @Test
    void shouldNotUpdateWhenOneVersionInReviewIsGoingToBeMergedRight() throws Exception {
      //given
      repository.deleteAll();
      //Create 1st Version
      ServicePointGeolocationCreateModel servicePointGeolocationCreateModel =
          ServicePointGeolocationMapper.toCreateModel(ServicePointTestData.getAargauServicePointGeolocation());
      CreateServicePointVersionModel stopPoint1 = ServicePointTestData.getAargauServicePointVersionModel();
      stopPoint1.setValidFrom(LocalDate.of(2000, 1, 1));
      stopPoint1.setValidTo(LocalDate.of(2001, 12, 31));
      stopPoint1.setDesignationOfficial("Bern");
      stopPoint1.setServicePointGeolocation(servicePointGeolocationCreateModel);
      ReadServicePointVersionModel servicePointVersionModel =
          servicePointApiV1Controller.createServicePoint(stopPoint1);
      Long id = servicePointVersionModel.getId();

      //Create 2nd version
      UpdateServicePointVersionModel stopPoint2 = ServicePointTestData.getAargauServicePointVersionModel();
      stopPoint2.setValidFrom(LocalDate.of(2001, 1, 1));
      stopPoint2.setValidTo(LocalDate.of(2002, 12, 31));
      stopPoint2.setDesignationOfficial("Bern1");
      stopPoint2.setServicePointGeolocation(servicePointGeolocationCreateModel);
      stopPoint2.setEtagVersion(servicePointApiV1Controller.getServicePointVersion(id).getEtagVersion());

      List<ReadServicePointVersionModel> servicePointVersionModel2 = servicePointApiV1Controller.updateServicePoint(id,
          stopPoint2);

      //2nd version is now in hearing => status = IN_REVIEW
      servicePointWorkflowApiInternalController.updateServicePointStatus(servicePointVersionModel2.get(1).getSloid(),
          servicePointVersionModel2.get(1).getId(),
          Status.IN_REVIEW);

      //when
      //Try to update 2nd version with designationOfficial Bern == to 1st version. This should results in a merge
      UpdateServicePointVersionModel stopPoint3 = ServicePointTestData.getAargauServicePointVersionModel();
      stopPoint3.setValidFrom(LocalDate.of(2000, 1, 1));
      stopPoint3.setValidTo(LocalDate.of(2000, 12, 31));
      stopPoint3.setDesignationOfficial("Bern1");
      stopPoint3.setServicePointGeolocation(servicePointGeolocationCreateModel);
      stopPoint3.setEtagVersion(servicePointApiV1Controller.getServicePointVersion(id).getEtagVersion());

      //when && then
      mvc.perform(put("/v1/service-points/" + id)
              .contentType(contentType)
              .content(mapper.writeValueAsString(stopPoint3)))
          .andExpect(status().isConflict())
          .andExpect(
              jsonPath("$.error",
                  is("Update affects one or more versions that have status: IN_REVIEW.")));
    }

    @Test
    void shouldNotUpdateWhenTwoVersionInReviewIsGoingToBeMerged() throws Exception {
      //given
      repository.deleteAll();
      //Create 1st Version
      ServicePointGeolocationCreateModel servicePointGeolocationCreateModel =
          ServicePointGeolocationMapper.toCreateModel(ServicePointTestData.getAargauServicePointGeolocation());
      CreateServicePointVersionModel stopPoint1 = ServicePointTestData.getAargauServicePointVersionModel();
      stopPoint1.setValidFrom(LocalDate.of(2000, 1, 1));
      stopPoint1.setValidTo(LocalDate.of(2001, 12, 31));
      stopPoint1.setDesignationOfficial("Bern");
      stopPoint1.setServicePointGeolocation(servicePointGeolocationCreateModel);
      ReadServicePointVersionModel servicePointVersionModel =
          servicePointApiV1Controller.createServicePoint(stopPoint1);
      Long id = servicePointVersionModel.getId();

      //Create 2nd version
      UpdateServicePointVersionModel stopPoint2 = ServicePointTestData.getAargauServicePointVersionModel();
      stopPoint2.setValidFrom(LocalDate.of(2001, 1, 1));
      stopPoint2.setValidTo(LocalDate.of(2001, 12, 31));
      stopPoint2.setDesignationOfficial("Bern1");
      stopPoint2.setServicePointGeolocation(servicePointGeolocationCreateModel);
      stopPoint2.setEtagVersion(servicePointApiV1Controller.getServicePointVersion(id).getEtagVersion());

      List<ReadServicePointVersionModel> servicePointVersionModel2 = servicePointApiV1Controller.updateServicePoint(id,
          stopPoint2);
      Long id2 = servicePointVersionModel2.get(1).getId();

      //Create 3rd version
      UpdateServicePointVersionModel stopPoint3 = ServicePointTestData.getAargauServicePointVersionModel();
      stopPoint3.setValidFrom(LocalDate.of(2002, 1, 1));
      stopPoint3.setValidTo(LocalDate.of(2002, 12, 31));
      stopPoint3.setDesignationOfficial("Bern");
      stopPoint3.setServicePointGeolocation(servicePointGeolocationCreateModel);
      stopPoint3.setEtagVersion(servicePointApiV1Controller.getServicePointVersion(id2).getEtagVersion());

      List<ReadServicePointVersionModel> servicePointVersionModel3 = servicePointApiV1Controller.updateServicePoint(id,
          stopPoint3);

      //1st version is now in hearing => status = IN_REVIEW
      servicePointWorkflowApiInternalController.updateServicePointStatus(servicePointVersionModel.getSloid(),
          servicePointVersionModel.getId(),
          Status.IN_REVIEW);
      //3rd version is now in hearing => status = IN_REVIEW
      servicePointWorkflowApiInternalController.updateServicePointStatus(servicePointVersionModel3.get(2).getSloid(),
          servicePointVersionModel3.get(2).getId(), Status.IN_REVIEW);

      //when
      //Try to update 2nd version with designationOfficial Bern == to 1st version. This should results in a merge
      UpdateServicePointVersionModel stopPoint4 = ServicePointTestData.getAargauServicePointVersionModel();
      stopPoint4.setValidFrom(LocalDate.of(2001, 1, 1));
      stopPoint4.setValidTo(LocalDate.of(2001, 12, 31));
      stopPoint4.setDesignationOfficial("Bern");
      stopPoint4.setServicePointGeolocation(servicePointGeolocationCreateModel);
      stopPoint4.setEtagVersion(servicePointApiV1Controller.getServicePointVersion(id2).getEtagVersion());

      //when && then
      mvc.perform(put("/v1/service-points/" + id2)
              .contentType(contentType)
              .content(mapper.writeValueAsString(stopPoint4)))
          .andExpect(status().isConflict())
          .andExpect(
              jsonPath("$.error",
                  is("Update affects one or more versions that have status: IN_REVIEW.")));

    }
  }
}