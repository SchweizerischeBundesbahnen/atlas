package ch.sbb.business.organisation.directory.controller;

import static ch.sbb.business.organisation.directory.api.BusinessOrganisationVersionVersionModel.toEntity;

import ch.sbb.atlas.base.service.model.Status;
import ch.sbb.atlas.base.service.model.api.Container;
import ch.sbb.business.organisation.directory.api.BusinessOrganisationApiV1;
import ch.sbb.business.organisation.directory.api.BusinessOrganisationModel;
import ch.sbb.business.organisation.directory.api.BusinessOrganisationVersionVersionModel;
import ch.sbb.business.organisation.directory.entity.BusinessOrganisation;
import ch.sbb.business.organisation.directory.entity.BusinessOrganisationVersion;
import ch.sbb.business.organisation.directory.exception.SboidNotFoundException;
import ch.sbb.business.organisation.directory.service.BusinessOrganisationService;
import ch.sbb.business.organisation.directory.service.export.BusinessOrganisationVersionExportService;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class BusinessOrganisationController implements BusinessOrganisationApiV1 {

  private final BusinessOrganisationService service;

  private final BusinessOrganisationVersionExportService exportService;

  @Override
  public Container<BusinessOrganisationModel> getAllBusinessOrganisations(Pageable pageable,
      List<String> searchCriteria, List<String> inSboids, Optional<LocalDate> validOn, List<Status> statusChoices) {
    log.info(
        "Load BusinessOrganisations using pageable={}, searchCriteriaSpecification={}, inSboids={} validOn={} and "
            + "statusChoices={}",
        pageable, searchCriteria, inSboids, validOn, statusChoices);
    Page<BusinessOrganisation> timetableFieldNumberPage = service.getBusinessOrganisations(
        BusinessOrganisationSearchRestrictions.builder()
            .pageable(pageable)
            .searchCriterias(searchCriteria)
            .inSboids(inSboids)
            .statusRestrictions(statusChoices)
            .validOn(validOn)
            .build());
    List<BusinessOrganisationModel> versions = timetableFieldNumberPage.stream()
        .map(
            BusinessOrganisationModel::toModel)
        .toList();
    return Container.<BusinessOrganisationModel>builder()
        .objects(versions)
        .totalCount(timetableFieldNumberPage.getTotalElements())
        .build();
  }

  @Override
  public List<BusinessOrganisationVersionVersionModel> getBusinessOrganisationVersions(String sboid) {
    List<BusinessOrganisationVersionVersionModel> organisationVersionModels =
        service.findBusinessOrganisationVersions(sboid).stream()
            .map(BusinessOrganisationVersionVersionModel::toModel)
            .toList();
    if (organisationVersionModels.isEmpty()) {
      throw new SboidNotFoundException(sboid);
    }
    return organisationVersionModels;
  }

  @Override
  public List<BusinessOrganisationVersionVersionModel> revokeBusinessOrganisation(String sboid) {
    List<BusinessOrganisationVersionVersionModel> businessOrganisationVersionModels =
        service.revokeBusinessOrganisation(sboid).stream()
            .map(BusinessOrganisationVersionVersionModel::toModel)
            .toList();
    if (businessOrganisationVersionModels.isEmpty()) {
      throw new SboidNotFoundException(sboid);
    }
    return businessOrganisationVersionModels;
  }

  @Override
  public BusinessOrganisationVersionVersionModel createBusinessOrganisationVersion(
      BusinessOrganisationVersionVersionModel newVersion) {
    BusinessOrganisationVersion businessOrganisationVersion = toEntity(newVersion);
    businessOrganisationVersion.setStatus(Status.VALIDATED);
    BusinessOrganisationVersion organisationVersionSaved =
        service.save(businessOrganisationVersion);
    return BusinessOrganisationVersionVersionModel.toModel(organisationVersionSaved);
  }

  @Override
  public List<BusinessOrganisationVersionVersionModel> updateBusinessOrganisationVersion(Long id,
      BusinessOrganisationVersionVersionModel newVersion) {
    BusinessOrganisationVersion versionToUpdate = service.findById(id);
    service.updateBusinessOrganisationVersion(versionToUpdate, toEntity(newVersion));
    return service.findBusinessOrganisationVersions(versionToUpdate.getSboid())
        .stream()
        .map(BusinessOrganisationVersionVersionModel::toModel)
        .toList();
  }

  @Override
  public void deleteBusinessOrganisation(String sboid) {
    List<BusinessOrganisationVersion> versions = service.findBusinessOrganisationVersions(sboid);
    if (versions.isEmpty()) {
      throw new SboidNotFoundException(sboid);
    }
    service.deleteAll(versions);
  }

  @Override
  public List<URL> exportFullBusinessOrganisationVersions() {
    return exportService.exportFullVersions();
  }

  @Override
  public List<URL> exportActualBusinessOrganisationVersions() {
    return exportService.exportActualVersions();
  }

  @Override
  public List<URL> exportFutureTimetableBusinessOrganisationVersions() {
    return exportService.exportFutureTimetableVersions();
  }

}
