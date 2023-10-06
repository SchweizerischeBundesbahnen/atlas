package ch.sbb.prm.directory.controller;

import ch.sbb.prm.directory.api.ToiletApiV1;
import ch.sbb.prm.directory.controller.model.toilet.CreateToiletVersionModel;
import ch.sbb.prm.directory.controller.model.toilet.ReadToiletVersionModel;
import ch.sbb.prm.directory.entity.ToiletVersion;
import ch.sbb.prm.directory.mapper.ToiletVersionMapper;
import ch.sbb.prm.directory.service.ToiletService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
public class ToiletController implements ToiletApiV1 {

  private final ToiletService toiletService;

  @Override
  public List<ReadToiletVersionModel> getToilets() {
    return toiletService.getAllToilets().stream().map(ToiletVersionMapper::toModel).sorted().toList();
  }

  @Override
  public ReadToiletVersionModel createToiletVersion(CreateToiletVersionModel toiletVersionModel) {
    ToiletVersion toiletVersion = toiletService.createToilet(ToiletVersionMapper.toEntity(toiletVersionModel));
    return ToiletVersionMapper.toModel(toiletVersion);
  }

}
