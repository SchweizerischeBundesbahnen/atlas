package ch.sbb.business.organisation.directory.controller;

import ch.sbb.atlas.api.bodi.TransportCompanyApiV1;
import ch.sbb.atlas.api.bodi.TransportCompanyModel;
import ch.sbb.atlas.api.model.Container;
import ch.sbb.atlas.model.exception.NotFoundException.IdNotFoundException;
import ch.sbb.business.organisation.directory.entity.TransportCompany;
import ch.sbb.business.organisation.directory.mapper.TransportCompanyMapper;
import ch.sbb.business.organisation.directory.service.TransportCompanyService;
import ch.sbb.atlas.api.bodi.enumeration.TransportCompanyStatus;
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
      List<String> searchCriteria, List<TransportCompanyStatus> statusChoices) {
    Page<TransportCompany> transportCompanies = transportCompanyService.getTransportCompanies(
        TransportCompanySearchRestrictions.builder()
                                          .pageable(pageable)
                                          .searchCriterias(searchCriteria)
                                          .statusRestrictions(statusChoices)
                                          .build());
    List<TransportCompanyModel> transportCompanyModels = transportCompanies.stream()
                                                                           .map(
                                                                               TransportCompanyMapper::fromEntity)
                                                                           .collect(
                                                                               Collectors.toList());
    return Container.<TransportCompanyModel>builder()
                    .objects(transportCompanyModels)
                    .totalCount(transportCompanies.getTotalElements())
                    .build();
  }

  @Override
  public TransportCompanyModel getTransportCompany(Long id) {
    return transportCompanyService.findById(id)
                                  .map(TransportCompanyMapper::fromEntity)
                                  .orElseThrow(() -> new IdNotFoundException(id));
  }

}
