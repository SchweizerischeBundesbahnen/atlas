package ch.sbb.business.organisation.directory.controller;

import static java.util.stream.Collectors.toList;

import ch.sbb.atlas.model.Status;
import ch.sbb.business.organisation.directory.api.BusinessOrganisationApiV1;
import ch.sbb.business.organisation.directory.api.BusinessOrganisationVersionModel;
import ch.sbb.business.organisation.directory.entity.BusinessOrganisationVersion;
import ch.sbb.business.organisation.directory.service.BusinessOrganisationVersionService;
import java.util.List;
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
    BusinessOrganisationVersion businessOrganisationVersion = BusinessOrganisationVersionModel.toEntity(
        newVersion);
    businessOrganisationVersion.setStatus(Status.ACTIVE);
    BusinessOrganisationVersion organisationVersionSaved = service.save(businessOrganisationVersion);
    return BusinessOrganisationVersionModel.toModel(organisationVersionSaved);
  }


}
