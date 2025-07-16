package ch.sbb.atlas.servicepointdirectory.api;

import ch.sbb.atlas.api.servicepoint.sector.CreateSectorGroupVersionModel;
import ch.sbb.atlas.api.servicepoint.sector.ReadSectorGroupVersionModel;
import ch.sbb.atlas.api.servicepoint.sector.SectorGroupVersionModel;
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

@Tag(name = "Sector Groups")
@RequestMapping("v1/sector-groups")
@Validated
public interface SectorGroupApiV1 {

  @GetMapping
  List<SectorGroupVersionModel> getSectorGroups();

  @GetMapping("{sloid}")
  List<SectorGroupVersionModel> getSectorGroup(@PathVariable String sloid);

  @GetMapping("versions/{id}")
  ReadSectorGroupVersionModel getSectorGroupVersion(@PathVariable Long id);

  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping
  ReadSectorGroupVersionModel createSectorGroupVersion(
      @Valid @RequestBody CreateSectorGroupVersionModel createSectorGroupVersionModel);

  @ResponseStatus(HttpStatus.OK)
  @PutMapping(path = "{id}")
  List<SectorGroupVersionModel> updateSectorGroupVersion(
      @PathVariable Long id,
      @Valid @RequestBody SectorGroupVersionModel updateSectorGroupVersionModel
  );
}
