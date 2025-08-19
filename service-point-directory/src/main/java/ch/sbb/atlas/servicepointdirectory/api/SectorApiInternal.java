package ch.sbb.atlas.servicepointdirectory.api;

import ch.sbb.atlas.api.AtlasApiConstants;
import ch.sbb.atlas.api.servicepoint.sector.SectorVersionModel;
import ch.sbb.atlas.validation.CreateIdCheck;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

@Tag(name = AtlasApiConstants.INTERNAL_API_TAG_PREFIX + "Sectors")
@RequestMapping("internal/sectors")
@Validated
@Hidden
public interface SectorApiInternal {

  @GetMapping
  List<SectorVersionModel> getSectors();

  @GetMapping("{sloid}")
  List<SectorVersionModel> getSector(@PathVariable String sloid);

  @GetMapping("versions/{id}")
  SectorVersionModel getSectorVersion(@PathVariable Long id);

  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping
  SectorVersionModel createSectorVersion(@Valid @RequestBody @CreateIdCheck SectorVersionModel sectorVersion);

  @ResponseStatus(HttpStatus.OK)
  @PutMapping(path = "{id}")
  List<SectorVersionModel> updateSectorVersion(
      @PathVariable Long id,
      @Valid @RequestBody SectorVersionModel updateSectorVersionModel
  );
}
