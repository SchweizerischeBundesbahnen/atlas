package ch.sbb.atlas.servicepointdirectory.api;

import ch.sbb.atlas.api.servicepoint.ReadSectorVersionModel;
import ch.sbb.atlas.api.servicepoint.SectorVersionModel;
import ch.sbb.atlas.api.servicepoint.UpdateSectorVersionModel;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "Sectors")
@RequestMapping("v1/sectors")
@Validated
public interface SectorApiV1 {

  @GetMapping
  List<ReadSectorVersionModel> getSectorVersions();

  @PostMapping
  ReadSectorVersionModel createSectorVersion(@Valid @RequestBody SectorVersionModel sectorVersion);

  @PutMapping(path = "{id}")
  List<ReadSectorVersionModel> updateSectorVersion(
      @PathVariable Long id,
      @Valid @RequestBody UpdateSectorVersionModel updateSectorVersionModel
  );
}
