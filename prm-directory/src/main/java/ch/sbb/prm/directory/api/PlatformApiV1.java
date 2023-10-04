package ch.sbb.prm.directory.api;

import ch.sbb.prm.directory.controller.model.PlatformVersionModel;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "Platform")
@RequestMapping("v1/platforms")
public interface PlatformApiV1 {

  @GetMapping
  List<PlatformVersionModel> getPlatforms();

}
