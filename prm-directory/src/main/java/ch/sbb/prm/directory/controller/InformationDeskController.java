package ch.sbb.prm.directory.controller;

import ch.sbb.prm.directory.api.InformationDeskCounterApiV1;
import ch.sbb.prm.directory.controller.model.create.CreateInformationDeskVersionModel;
import ch.sbb.prm.directory.controller.model.read.ReadInformationDeskVersionModel;
import ch.sbb.prm.directory.entity.InformationDeskVersion;
import ch.sbb.prm.directory.mapper.InformationDeskVersionMapper;
import ch.sbb.prm.directory.service.InformationDeskService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
public class InformationDeskController implements InformationDeskCounterApiV1 {

  private final InformationDeskService informationDeskService;

  @Override
  public List<ReadInformationDeskVersionModel> getInformationDesks() {
    return informationDeskService.getAllInformationDesks().stream().map(InformationDeskVersionMapper::toModel).sorted().toList();
  }

  @Override
  public ReadInformationDeskVersionModel createStopPlace(CreateInformationDeskVersionModel model) {
    InformationDeskVersion informationDeskVersion = informationDeskService.createInformationDesk(
        InformationDeskVersionMapper.toEntity(model));
    return InformationDeskVersionMapper.toModel(informationDeskVersion);
  }

}

