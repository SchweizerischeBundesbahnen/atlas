package ch.sbb.atlas.servicepointdirectory.api;

import ch.sbb.atlas.api.servicepoint.CreateSectorGroupVersionModel;
import ch.sbb.atlas.api.servicepoint.ReadSectorGroupVersionModel;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "Sector Groups")
@RequestMapping("v1/sector-groups")
@Validated
public interface SectorGroupApiV1 {

  @GetMapping
  List<ReadSectorGroupVersionModel> getSectorVersions();

  @PostMapping
  ReadSectorGroupVersionModel createSectorVersion(
      @Valid @RequestBody CreateSectorGroupVersionModel createSectorGroupVersionModel);

  @PostMapping
  ReadSectorGroupVersionModel updateSectorVersion();
}
