package ch.sbb.atlas.servicepointdirectory.module.sector.api;

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

@Tag(name = "Sectors")
@RequestMapping("v1/sectors")
@Validated
@Hidden // ATLAS-3130 To Remove once public, add link to sector restdoc in SePoDi
public interface SectorApiV1 {

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
