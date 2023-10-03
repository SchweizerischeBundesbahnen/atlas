package ch.sbb.prm.directory.controller;

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
