package ch.sbb.business.organisation.directory.controller;


import ch.sbb.atlas.api.bodi.BusinessOrganisationApiV1;
import ch.sbb.atlas.api.bodi.BusinessOrganisationModel;
import ch.sbb.atlas.api.bodi.BusinessOrganisationVersionModel;
import ch.sbb.atlas.api.bodi.BusinessOrganisationVersionRequestParams;
import ch.sbb.atlas.kafka.model.Status;
import ch.sbb.atlas.api.model.Container;
import ch.sbb.business.organisation.directory.entity.BusinessOrganisation;
import ch.sbb.business.organisation.directory.entity.BusinessOrganisationVersion;
import ch.sbb.atlas.model.exception.SboidNotFoundException;
import ch.sbb.business.organisation.directory.mapper.BusinessOrganisationMapper;
import ch.sbb.business.organisation.directory.mapper.BusinessOrganisationVersionMapper;
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
    Page<BusinessOrganisation> businessOrganisationPage = service.getBusinessOrganisations(
        BusinessOrganisationSearchRestrictions.builder()
            .pageable(pageable)
            .searchCriterias(searchCriteria)
            .inSboids(inSboids)
            .statusRestrictions(statusChoices)
            .validOn(validOn)
            .build());
    List<BusinessOrganisationModel> versions = businessOrganisationPage.stream()
        .map(
            BusinessOrganisationMapper::toModel)
        .toList();
    return Container.<BusinessOrganisationModel>builder()
        .objects(versions)
        .totalCount(businessOrganisationPage.getTotalElements())
        .build();
  }

  @Override
  public Container<BusinessOrganisationVersionModel> getBusinessOrganisationVersions(Pageable pageable,
      BusinessOrganisationVersionRequestParams businessOrganisationVersionRequestParams) {
    log.info("Load BusinessOrganisationVersions using pageable={}, params={}", pageable,
        businessOrganisationVersionRequestParams);
    Page<BusinessOrganisationVersion> businessOrganisationVersions = service.getBusinessOrganisationVersions(
        BusinessOrganisationVersionSearchRestrictions.builder()
            .pageable(pageable)
            .businessOrganisationVersionRequestParams(businessOrganisationVersionRequestParams)
            .build());
    return Container.<BusinessOrganisationVersionModel>builder()
        .objects(businessOrganisationVersions.getContent().stream().map(BusinessOrganisationVersionMapper::toModel).toList())
        .totalCount(businessOrganisationVersions.getTotalElements())
        .build();
  }

  @Override
  public List<BusinessOrganisationVersionModel> getBusinessOrganisationVersions(String sboid) {
    List<BusinessOrganisationVersionModel> organisationVersionModels =
        service.findBusinessOrganisationVersions(sboid).stream()
            .map(BusinessOrganisationVersionMapper::toModel)
            .toList();
    if (organisationVersionModels.isEmpty()) {
      throw new SboidNotFoundException(sboid);
    }
    return organisationVersionModels;
  }

  @Override
  public List<BusinessOrganisationVersionModel> revokeBusinessOrganisation(String sboid) {
    List<BusinessOrganisationVersionModel> businessOrganisationVersionModels =
        service.revokeBusinessOrganisation(sboid).stream()
            .map(BusinessOrganisationVersionMapper::toModel)
            .toList();
    if (businessOrganisationVersionModels.isEmpty()) {
      throw new SboidNotFoundException(sboid);
    }
    return businessOrganisationVersionModels;
  }

  @Override
  public BusinessOrganisationVersionModel createBusinessOrganisationVersion(
      BusinessOrganisationVersionModel newVersion) {
    BusinessOrganisationVersion businessOrganisationVersion = BusinessOrganisationVersionMapper.toEntity(newVersion);
    businessOrganisationVersion.setStatus(Status.VALIDATED);
    BusinessOrganisationVersion organisationVersionSaved =
        service.save(businessOrganisationVersion);
    return BusinessOrganisationVersionMapper.toModel(organisationVersionSaved);
  }

  @Override
  public List<BusinessOrganisationVersionModel> updateBusinessOrganisationVersion(Long id,
      BusinessOrganisationVersionModel newVersion) {
    BusinessOrganisationVersion versionToUpdate = service.findById(id);
    service.updateBusinessOrganisationVersion(versionToUpdate, BusinessOrganisationVersionMapper.toEntity(newVersion));
    return service.findBusinessOrganisationVersions(versionToUpdate.getSboid())
        .stream()
        .map(BusinessOrganisationVersionMapper::toModel)
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

  @Override
  public void syncBusinessOrganisations() {
    service.syncAllBusinessOrganisations();
  }

}
