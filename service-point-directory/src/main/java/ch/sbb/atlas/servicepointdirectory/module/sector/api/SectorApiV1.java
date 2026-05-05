package ch.sbb.atlas.servicepointdirectory.module.sector.api;

import ch.sbb.atlas.annotation.AuthorizedOnly;
import ch.sbb.atlas.annotation.UnauthorizedAllowed;
import ch.sbb.atlas.annotation.UnauthorizedAllowed.FurtherLimitations;
import ch.sbb.atlas.api.servicepoint.sector.CreateSectorVersionModel;
import ch.sbb.atlas.api.servicepoint.sector.ReadSectorVersionModel;
import ch.sbb.atlas.validation.CreateIdCheck;
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

@Tag(name = "Sectors - Beta")
@RequestMapping("v1/sectors")
@Validated
public interface SectorApiV1 {

  @UnauthorizedAllowed(limitations = FurtherLimitations.NONE)
  @GetMapping("{sloid}")
  List<ReadSectorVersionModel> getSector(@PathVariable String sloid);

  @UnauthorizedAllowed(limitations = FurtherLimitations.NONE)
  @GetMapping("versions/{id}")
  ReadSectorVersionModel getSectorVersion(@PathVariable Long id);

  @AuthorizedOnly
  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping
  ReadSectorVersionModel createSectorVersion(@Valid @RequestBody @CreateIdCheck CreateSectorVersionModel sectorVersion);

  @AuthorizedOnly
  @ResponseStatus(HttpStatus.OK)
  @PutMapping(path = "{id}")
  List<ReadSectorVersionModel> updateSectorVersion(
      @PathVariable Long id,
      @Valid @RequestBody CreateSectorVersionModel updateSectorVersionModel
  );
}
