package ch.sbb.prm.directory.controller;

import ch.sbb.prm.directory.api.PlatformApiV1;
import ch.sbb.prm.directory.controller.model.PlatformVersionModel;
import ch.sbb.prm.directory.mapper.PlatformVersionMapper;
import ch.sbb.prm.directory.service.PlatformService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
public class PlatformController implements PlatformApiV1 {

  private final PlatformService platformService;

  @Override
  public List<PlatformVersionModel> getPlatforms() {
    return platformService.getAllPlatforms().stream().map(PlatformVersionMapper::toModel).sorted().toList();
  }
}
