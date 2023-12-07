package ch.sbb.prm.directory.controller;

import ch.sbb.atlas.api.prm.model.toilet.CreateToiletVersionModel;
import ch.sbb.atlas.api.prm.model.toilet.ReadToiletVersionModel;
import ch.sbb.atlas.model.exception.NotFoundException.IdNotFoundException;
import ch.sbb.prm.directory.api.ToiletApiV1;
import ch.sbb.prm.directory.entity.ToiletVersion;
import ch.sbb.prm.directory.mapper.ToiletVersionMapper;
import ch.sbb.prm.directory.service.ToiletService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
public class ToiletController implements ToiletApiV1 {

  private final ToiletService toiletService;

  @Override
  public List<ReadToiletVersionModel> getToilets() {
    return toiletService.getAllToilets().stream().map(ToiletVersionMapper::toModel).toList();
  }

  @Override
  public ReadToiletVersionModel createToiletVersion(CreateToiletVersionModel model) {
    ToiletVersion toiletVersion = toiletService.createToilet(ToiletVersionMapper.toEntity(model));
    return ToiletVersionMapper.toModel(toiletVersion);
  }

  @Override
  public List<ReadToiletVersionModel> updateToiletVersion(Long id, CreateToiletVersionModel model) {
    ToiletVersion toiletVersion =
        toiletService.getToiletVersionById(id).orElseThrow(() -> new IdNotFoundException(id));
    ToiletVersion editedVersion = ToiletVersionMapper.toEntity(model);
    toiletService.updateToiletVersion(toiletVersion, editedVersion);

    return toiletService.getAllVersions(toiletVersion.getSloid()).stream()
        .map(ToiletVersionMapper::toModel).toList();
  }

}
