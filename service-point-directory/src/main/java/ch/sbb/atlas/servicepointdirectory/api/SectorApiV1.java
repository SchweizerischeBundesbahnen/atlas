package ch.sbb.atlas.servicepointdirectory.api;

import ch.sbb.atlas.api.servicepoint.CreateSectorVersionModel;
import ch.sbb.atlas.api.servicepoint.ReadSectorVersionModel;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "Sectors")
@RequestMapping("v1/sectors")
@Validated
public interface SectorApiV1 {

  @GetMapping
  List<ReadSectorVersionModel> getSectorVersions();

  @PostMapping
  ReadSectorVersionModel createSectorVersion(@Valid @RequestBody CreateSectorVersionModel sectorVersion);
}
