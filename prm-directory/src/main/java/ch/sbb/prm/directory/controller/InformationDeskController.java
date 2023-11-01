package ch.sbb.prm.directory.controller;

import ch.sbb.atlas.api.prm.model.informationdesk.CreateInformationDeskVersionModel;
import ch.sbb.atlas.api.prm.model.informationdesk.ReadInformationDeskVersionModel;
import ch.sbb.atlas.model.exception.NotFoundException.IdNotFoundException;
import ch.sbb.atlas.servicepoint.SharedServicePointVersionModel;
import ch.sbb.prm.directory.api.InformationDeskApiV1;
import ch.sbb.prm.directory.entity.InformationDeskVersion;
import ch.sbb.prm.directory.mapper.InformationDeskVersionMapper;
import ch.sbb.prm.directory.service.InformationDeskService;
import ch.sbb.prm.directory.service.SharedServicePointService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
public class InformationDeskController implements InformationDeskApiV1 {

  private final InformationDeskService informationDeskService;

  private final SharedServicePointService sharedServicePointService;

  @Override
  public List<ReadInformationDeskVersionModel> getInformationDesks() {
    return informationDeskService.getAllInformationDesks().stream().map(InformationDeskVersionMapper::toModel).toList();
  }

  @Override
  public ReadInformationDeskVersionModel createInformationDesk(CreateInformationDeskVersionModel model) {
    // here I need to get all servicePontVersions, or more precisely sharedServicePoints and to pass this one to service.createInformationDesk
    // out of sharedServicePoints I need to extract only for each version from String servicePoint, which looks like this {"servicePointSloid":"ch:1:sloid:6","sboids":["ch:1:sboid:100313"],"trafficPointSloids":[]}
    // I need per each version only sboids ch:1:sboid:101698 and then to put it in ServicePointVersion, or some smaller Entity which implements CountryAndBusinessOrganisationAssociated and then I can pass it to CountryAndBusinessOrganisationBasedUserAdministrationService
    // then there I can adjust check only for bo and without validFrom, validTo
    // maybe I can also implement different interface CountryAndBusinessOrganisationAssociated and write different CountryAndBusinessOrganisationBasedUserAdministrationService, but I don't think it is necessary
    SharedServicePointVersionModel sharedServicePointVersionModel = sharedServicePointService.findServicePoint(model.getParentServicePointSloid()).orElseThrow();
    InformationDeskVersion informationDeskVersion = informationDeskService.createInformationDesk(
        InformationDeskVersionMapper.toEntity(model), sharedServicePointVersionModel);
    return InformationDeskVersionMapper.toModel(informationDeskVersion);
  }

  @Override
  public List<ReadInformationDeskVersionModel> updateInformationDesk(Long id, CreateInformationDeskVersionModel model) {
    InformationDeskVersion informationDeskVersion =
        informationDeskService.getInformationDeskVersionById(id).orElseThrow(() -> new IdNotFoundException(id));

    InformationDeskVersion editedVersion = InformationDeskVersionMapper.toEntity(model);
    SharedServicePointVersionModel sharedServicePointVersionModel = sharedServicePointService.findServicePoint(model.getParentServicePointSloid()).orElseThrow();
    informationDeskService.updateInformationDeskVersion(informationDeskVersion, editedVersion, sharedServicePointVersionModel);

    return informationDeskService.findAllByNumberOrderByValidFrom(informationDeskVersion.getNumber()).stream()
        .map(InformationDeskVersionMapper::toModel).toList();
  }

}

