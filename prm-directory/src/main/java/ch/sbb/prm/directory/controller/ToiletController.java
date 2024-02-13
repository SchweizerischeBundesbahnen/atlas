package ch.sbb.prm.directory.controller;

import ch.sbb.atlas.api.prm.model.toilet.ReadToiletVersionModel;
import ch.sbb.atlas.api.prm.model.toilet.ToiletVersionModel;
import ch.sbb.atlas.imports.ItemImportResult;
import ch.sbb.atlas.imports.prm.platform.ToiletImportRequestModel;
import ch.sbb.atlas.model.exception.NotFoundException.IdNotFoundException;
import ch.sbb.prm.directory.api.ToiletApiV1;
import ch.sbb.prm.directory.entity.ToiletVersion;
import ch.sbb.prm.directory.mapper.ToiletVersionMapper;
import ch.sbb.prm.directory.service.ToiletService;
import ch.sbb.prm.directory.service.dataimport.ToiletImportService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
public class ToiletController implements ToiletApiV1 {

  private final ToiletService toiletService;
  private final ToiletImportService toiletImportService;

  @Override
  public List<ReadToiletVersionModel> getToilets() {
    return toiletService.getAllToilets().stream().map(ToiletVersionMapper::toModel).toList();
  }

  @Override
  public ReadToiletVersionModel createToiletVersion(ToiletVersionModel model) {
    ToiletVersion toiletVersion = toiletService.createToilet(ToiletVersionMapper.toEntity(model));
    return ToiletVersionMapper.toModel(toiletVersion);
  }

  @Override
  public List<ReadToiletVersionModel> updateToiletVersion(Long id, ToiletVersionModel model) {
    ToiletVersion toiletVersion =
        toiletService.getToiletVersionById(id).orElseThrow(() -> new IdNotFoundException(id));
    ToiletVersion editedVersion = ToiletVersionMapper.toEntity(model);
    toiletService.updateToiletVersion(toiletVersion, editedVersion);

    return toiletService.getAllVersions(toiletVersion.getSloid()).stream()
        .map(ToiletVersionMapper::toModel).toList();
  }

  @Override
  public List<ItemImportResult> importToilets(ToiletImportRequestModel importRequestModel) {
    return toiletImportService.importToiletPoints(importRequestModel.getToiletCsvModelContainers());
  }

}
