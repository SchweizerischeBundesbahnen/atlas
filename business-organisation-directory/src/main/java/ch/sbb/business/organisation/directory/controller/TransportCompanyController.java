package ch.sbb.business.organisation.directory.controller;

import ch.sbb.atlas.model.api.Container;
import ch.sbb.business.organisation.directory.api.TransportCompanyApiV1;
import ch.sbb.business.organisation.directory.api.TransportCompanyModel;
import ch.sbb.business.organisation.directory.entity.TransportCompany;
import ch.sbb.business.organisation.directory.service.TransportCompanyService;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class TransportCompanyController implements TransportCompanyApiV1 {

  private final TransportCompanyService transportCompanyService;

  @Override
  public void loadTransportCompaniesFromBav() {
    transportCompanyService.saveTransportCompaniesFromBav();
  }

  @Override
  public Container<TransportCompanyModel> getTransportCompanies(Pageable pageable,
      List<String> searchCriteria) {
    Page<TransportCompany> transportCompanies = transportCompanyService.findAll(
        TransportCompanySearchRestrictions.builder()
                                          .pageable(pageable)
                                          .searchCriterias(searchCriteria)
                                          .build());
    List<TransportCompanyModel> transportCompanyModels = transportCompanies.stream()
                                                                           .map(this::toModel)
                                                                           .collect(
                                                                               Collectors.toList());
    return Container.<TransportCompanyModel>builder()
                    .objects(transportCompanyModels)
                    .totalCount(transportCompanies.getTotalElements())
                    .build();
  }

  private TransportCompanyModel toModel(TransportCompany entity) {
    return TransportCompanyModel.builder()
                                .id(entity.getId())
                                .number(entity.getNumber())
                                .abbreviation(entity.getAbbreviation())
                                .description(entity.getDescription())
                                .businessRegisterName(entity.getBusinessRegisterName())
                                .transportCompanyStatus(entity.getTransportCompanyStatus())
                                .businessRegisterNumber(entity.getBusinessRegisterNumber())
                                .enterpriseId(entity.getEnterpriseId())
                                .ricsCode(entity.getRicsCode())
                                .businessOrganisationNumbers(
                                    entity.getBusinessOrganisationNumbers())
                                .comment(entity.getComment())
                                .build();
  }
}
