package ch.sbb.business.organisation.directory.controller;

import ch.sbb.atlas.api.bodi.TransportCompanyApiInternal;
import ch.sbb.atlas.api.bodi.TransportCompanyModel;
import ch.sbb.business.organisation.directory.mapper.TransportCompanyMapper;
import ch.sbb.business.organisation.directory.service.TransportCompanyService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class TransportCompanyControllerInternal implements TransportCompanyApiInternal {

  private final TransportCompanyService transportCompanyService;

  @Override
  public void loadTransportCompaniesFromBav() {
    transportCompanyService.saveTransportCompaniesFromBav();
  }

  @Override
  public List<TransportCompanyModel> getTransportCompaniesBySboid(String sboid) {
    return transportCompanyService.findBySboid(sboid).stream().map(TransportCompanyMapper::fromEntity).toList();
  }

}
