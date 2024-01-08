package ch.sbb.prm.directory.api;

import ch.sbb.atlas.api.model.Container;
import ch.sbb.atlas.api.prm.model.platform.CreatePlatformVersionModel;
import ch.sbb.atlas.api.prm.model.platform.PlatformOverviewModel;
import ch.sbb.atlas.api.prm.model.platform.ReadPlatformVersionModel;
import ch.sbb.atlas.configuration.Role;
import ch.sbb.atlas.imports.ItemImportResult;
import ch.sbb.atlas.imports.prm.platform.PlatformImportRequestModel;
import ch.sbb.prm.directory.entity.BasePrmEntityVersion;
import ch.sbb.prm.directory.model.PlatformRequestParams;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springdoc.core.annotations.ParameterObject;
import org.springdoc.core.converters.models.PageableAsQueryParam;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
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
  @PageableAsQueryParam
  Container<ReadPlatformVersionModel> getPlatforms(
      @Parameter(hidden = true) @PageableDefault(sort = {BasePrmEntityVersion.Fields.number,
          BasePrmEntityVersion.Fields.validFrom}) Pageable pageable,
      @Valid @ParameterObject PlatformRequestParams platformRequestParams);

  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping
  ReadPlatformVersionModel createPlatform(@RequestBody @Valid CreatePlatformVersionModel model);

  @ResponseStatus(HttpStatus.OK)
  @PutMapping(path = "{id}")
  List<ReadPlatformVersionModel> updatePlatform(@PathVariable Long id,
      @RequestBody @Valid CreatePlatformVersionModel createPlatformVersionModel);

  @Secured(Role.SECURED_FOR_ATLAS_ADMIN)
  @PostMapping("import")
  List<ItemImportResult> importPlatforms(@RequestBody @Valid PlatformImportRequestModel importRequestModel);

  @PageableAsQueryParam
  @GetMapping("/overview/{parentSloid}")
  List<PlatformOverviewModel> getPlatformOverview(@PathVariable String parentSloid);

  @GetMapping("{sloid}")
  List<ReadPlatformVersionModel> getPlatformVersions(@PathVariable String sloid);
}
