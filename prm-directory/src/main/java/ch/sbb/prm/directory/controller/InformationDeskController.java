package ch.sbb.prm.directory.controller;

import ch.sbb.atlas.api.prm.model.informationdesk.CreateInformationDeskVersionModel;
import ch.sbb.atlas.api.prm.model.informationdesk.ReadInformationDeskVersionModel;
import ch.sbb.atlas.model.exception.NotFoundException.IdNotFoundException;
import ch.sbb.prm.directory.api.InformationDeskApiV1;
import ch.sbb.prm.directory.entity.InformationDeskVersion;
import ch.sbb.prm.directory.mapper.InformationDeskVersionMapper;
import ch.sbb.prm.directory.service.InformationDeskService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
public class InformationDeskController implements InformationDeskApiV1 {

  private final InformationDeskService informationDeskService;

  @Override
  public List<ReadInformationDeskVersionModel> getInformationDesks() {
    return informationDeskService.getAllInformationDesks().stream().map(InformationDeskVersionMapper::toModel).toList();
  }

  @Override
  public ReadInformationDeskVersionModel createInformationDesk(CreateInformationDeskVersionModel model) {
    InformationDeskVersion informationDeskVersion = informationDeskService.createInformationDesk(
        InformationDeskVersionMapper.toEntity(model));
    return InformationDeskVersionMapper.toModel(informationDeskVersion);
  }

  @Override
  public List<ReadInformationDeskVersionModel> updateInformationDesk(Long id, CreateInformationDeskVersionModel model) {
    InformationDeskVersion informationDeskVersion =
        informationDeskService.getInformationDeskVersionById(id).orElseThrow(() -> new IdNotFoundException(id));

    InformationDeskVersion editedVersion = InformationDeskVersionMapper.toEntity(model);
    informationDeskService.updateInformationDeskVersion(informationDeskVersion, editedVersion);

    return informationDeskService.getAllVersions(informationDeskVersion.getSloid()).stream()
        .map(InformationDeskVersionMapper::toModel).toList();
  }

}

