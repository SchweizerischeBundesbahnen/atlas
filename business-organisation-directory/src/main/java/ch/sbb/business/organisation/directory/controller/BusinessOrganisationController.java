package ch.sbb.business.organisation.directory.controller;

import static ch.sbb.business.organisation.directory.api.BusinessOrganisationVersionModel.toEntity;
import static java.util.stream.Collectors.toList;

import ch.sbb.atlas.model.Status;
import ch.sbb.business.organisation.directory.api.BusinessOrganisationApiV1;
import ch.sbb.business.organisation.directory.api.BusinessOrganisationVersionModel;
import ch.sbb.business.organisation.directory.entity.BusinessOrganisationVersion;
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
  public List<BusinessOrganisationVersionModel> getBusinessOrganisations() {
    return service.getBusinessOrganisations()
                  .stream()
                  .map(BusinessOrganisationVersionModel::toModel)
                  .collect(toList());
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
    BusinessOrganisationVersion versionToUpdate = service.findById(id)
                                                         .orElseThrow(
                                                             () -> new IllegalStateException(
                                                                 "Replace me wit IdNotFoundException"));
    service.updateBusinessOrganisationVersion(versionToUpdate, toEntity(newVersion));
    return service.findBusinessOrganisationVersions(versionToUpdate.getSboid())
                  .stream()
                  .map(BusinessOrganisationVersionModel::toModel)
                  .collect(Collectors.toList());
  }

}
