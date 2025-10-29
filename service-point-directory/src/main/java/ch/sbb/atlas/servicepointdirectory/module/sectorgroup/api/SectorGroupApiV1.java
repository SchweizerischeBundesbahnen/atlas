package ch.sbb.atlas.servicepointdirectory.module.sectorgroup.api;

import ch.sbb.atlas.api.servicepoint.sector.CreateSectorGroupVersionModel;
import ch.sbb.atlas.api.servicepoint.sector.ReadSectorGroupVersionModel;
import ch.sbb.atlas.api.servicepoint.sector.UpdateSectorGroupVersionModel;
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

@Tag(name = "Sector Groups - Beta")
@RequestMapping("v1/sector-groups")
@Validated
public interface SectorGroupApiV1 {

  @GetMapping("{sloid}")
  List<ReadSectorGroupVersionModel> getSectorGroup(@PathVariable String sloid);

  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping
  ReadSectorGroupVersionModel createSectorGroupVersion(
      @Valid @RequestBody @CreateIdCheck CreateSectorGroupVersionModel createSectorGroupVersionModel);

  @ResponseStatus(HttpStatus.OK)
  @PutMapping(path = "{id}")
  List<ReadSectorGroupVersionModel> updateSectorGroupVersion(
      @PathVariable Long id,
      @Valid @RequestBody UpdateSectorGroupVersionModel updateSectorGroupVersionModel
  );
}
