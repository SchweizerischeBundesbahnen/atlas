package ch.sbb.business.organisation.directory.module.businessorganisation.controller;

import ch.sbb.atlas.api.bodi.BusinessOrganisationApiV1;
import ch.sbb.atlas.api.bodi.BusinessOrganisationModel;
import ch.sbb.atlas.api.bodi.BusinessOrganisationVersionModel;
import ch.sbb.atlas.api.bodi.BusinessOrganisationVersionRequestParams;
import ch.sbb.atlas.api.model.Container;
import ch.sbb.atlas.model.Status;
import ch.sbb.atlas.model.exception.SboidNotFoundException;
import ch.sbb.business.organisation.directory.module.businessorganisation.entity.BusinessOrganisation;
import ch.sbb.business.organisation.directory.module.businessorganisation.entity.BusinessOrganisationVersion;
import ch.sbb.business.organisation.directory.module.businessorganisation.mapper.BusinessOrganisationMapper;
import ch.sbb.business.organisation.directory.module.businessorganisation.mapper.BusinessOrganisationVersionMapper;
import ch.sbb.business.organisation.directory.module.businessorganisation.model.BusinessOrganisationSearchRestrictions;
import ch.sbb.business.organisation.directory.module.businessorganisation.model.BusinessOrganisationVersionSearchRestrictions;
import ch.sbb.business.organisation.directory.module.businessorganisation.service.BusinessOrganisationService;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class BusinessOrganisationControllerV1 implements BusinessOrganisationApiV1 {

  private final BusinessOrganisationService service;

  @Override
  public Container<BusinessOrganisationModel> getAllBusinessOrganisations(Pageable pageable,
      List<String> searchCriteria, List<String> inSboids, LocalDate validOn, List<Status> statusChoices) {
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
  public List<BusinessOrganisationVersionModel> getVersions(String sboid) {
    List<BusinessOrganisationVersionModel> organisationVersionModels =
        service.findBusinessOrganisationVersions(sboid).stream()
            .map(BusinessOrganisationVersionMapper::toModel)
            .toList();
    if (organisationVersionModels.isEmpty()) {
      throw new SboidNotFoundException(sboid);
    }
    return organisationVersionModels;
  }
}
