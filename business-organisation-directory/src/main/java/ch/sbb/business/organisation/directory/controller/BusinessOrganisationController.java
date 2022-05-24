package ch.sbb.business.organisation.directory.controller;

import static ch.sbb.business.organisation.directory.api.BusinessOrganisationVersionModel.toEntity;
import static java.util.stream.Collectors.toList;

import ch.sbb.atlas.model.Status;
import ch.sbb.business.organisation.directory.api.BusinessOrganisationApiV1;
import ch.sbb.business.organisation.directory.api.BusinessOrganisationVersionModel;
import ch.sbb.business.organisation.directory.entity.BusinessOrganisationVersion;
import ch.sbb.business.organisation.directory.exception.SboidNotFoundException;
import ch.sbb.business.organisation.directory.service.BusinessOrganisationVersionService;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class BusinessOrganisationController implements BusinessOrganisationApiV1 {

  private final BusinessOrganisationVersionService service;

  @Override
  public List<BusinessOrganisationVersionModel> getAllBusinessOrganisations() {
    return service.getBusinessOrganisations()
                  .stream()
                  .map(BusinessOrganisationVersionModel::toModel)
                  .collect(toList());
  }

  @Override
  public List<BusinessOrganisationVersionModel> getBusinessOrganisationVersions(String sboid) {
    List<BusinessOrganisationVersionModel> organisationVersionModels =
        service.findBusinessOrganisationVersions(sboid).stream()
               .map(BusinessOrganisationVersionModel::toModel)
               .collect(toList());
    if(organisationVersionModels.isEmpty()){
      throw new SboidNotFoundException(sboid);
    }
    return organisationVersionModels;
  }

  @Override
  public BusinessOrganisationVersionModel createBusinessOrganisationVersion(
      BusinessOrganisationVersionModel newVersion) {
    BusinessOrganisationVersion businessOrganisationVersion = toEntity(newVersion);
    businessOrganisationVersion.setStatus(Status.ACTIVE);
    BusinessOrganisationVersion organisationVersionSaved =
        service.save(businessOrganisationVersion);
    return BusinessOrganisationVersionModel.toModel(organisationVersionSaved);
  }

  @Override
  public List<BusinessOrganisationVersionModel> updateBusinessOrganisationVersion(Long id,
      BusinessOrganisationVersionModel newVersion) {
    BusinessOrganisationVersion versionToUpdate = service.findById(id);
    service.updateBusinessOrganisationVersion(versionToUpdate, toEntity(newVersion));
    return service.findBusinessOrganisationVersions(versionToUpdate.getSboid())
                  .stream()
                  .map(BusinessOrganisationVersionModel::toModel)
                  .collect(Collectors.toList());
  }

}
