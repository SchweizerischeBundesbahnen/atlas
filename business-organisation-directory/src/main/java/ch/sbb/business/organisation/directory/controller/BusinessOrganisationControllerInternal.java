package ch.sbb.business.organisation.directory.controller;

import ch.sbb.atlas.api.bodi.BusinessOrganisationVersionModel;
import ch.sbb.atlas.model.Status;
import ch.sbb.atlas.model.exception.SboidNotFoundException;
import ch.sbb.business.organisation.directory.entity.BusinessOrganisationVersion;
import ch.sbb.business.organisation.directory.mapper.BusinessOrganisationVersionMapper;
import ch.sbb.business.organisation.directory.service.BusinessOrganisationService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class BusinessOrganisationControllerInternal implements BusinessOrganisationApiInternal {

  private final BusinessOrganisationService service;

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
        service.create(businessOrganisationVersion);
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
  public void syncBusinessOrganisations() {
    service.syncAllBusinessOrganisations();
  }
}
