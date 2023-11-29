package ch.sbb.prm.directory.api;

import ch.sbb.atlas.api.prm.model.platform.CreatePlatformVersionModel;
import ch.sbb.atlas.api.prm.model.platform.ReadPlatformVersionModel;
import ch.sbb.atlas.imports.ItemImportResult;
import ch.sbb.atlas.imports.prm.stoppoint.PlatformImportRequestModel;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

@Tag(name = "Person with Reduced Mobility")
@RequestMapping("v1/platforms")
public interface PlatformApiV1 {

  @GetMapping
  List<ReadPlatformVersionModel> getPlatforms();

  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping
  ReadPlatformVersionModel createPlatform(@RequestBody @Valid CreatePlatformVersionModel model);

  @ResponseStatus(HttpStatus.OK)
  @PutMapping(path = "{id}")
  List<ReadPlatformVersionModel> updatePlatform(@PathVariable Long id,
      @RequestBody @Valid CreatePlatformVersionModel createPlatformVersionModel);

  @PostMapping("import")
  List<ItemImportResult> importPlatforms(@RequestBody @Valid PlatformImportRequestModel importRequestModel);
}
