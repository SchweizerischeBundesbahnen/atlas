package ch.sbb.prm.directory.api;

import ch.sbb.prm.directory.controller.model.platform.CreatePlatformVersionModel;
import ch.sbb.prm.directory.controller.model.platform.ReadPlatformVersionModel;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

@Tag(name = "PRM - Person with Reduced Mobility")
@RequestMapping("v1/platforms")
public interface PlatformApiV1 {

  @GetMapping
  List<ReadPlatformVersionModel> getPlatforms();

  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping
  ReadPlatformVersionModel createStopPlace(@RequestBody @Valid CreatePlatformVersionModel model);
}
