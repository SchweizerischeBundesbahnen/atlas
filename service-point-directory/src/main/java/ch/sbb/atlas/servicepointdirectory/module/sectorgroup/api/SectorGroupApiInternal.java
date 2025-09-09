package ch.sbb.atlas.servicepointdirectory.module.sectorgroup.api;

import ch.sbb.atlas.api.AtlasApiConstants;
import ch.sbb.atlas.api.model.Container;
import ch.sbb.atlas.api.servicepoint.sector.SectorGroupVersionModel;
import ch.sbb.atlas.api.servicepoint.sector.SectorVersionModel;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springdoc.core.converters.models.PageableAsQueryParam;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = AtlasApiConstants.INTERNAL_API_TAG_PREFIX + "Sector Groups")
@RequestMapping("internal/sector-groups")
@Validated
@Hidden // ATLAS-3130 To Remove once public, add link to sector restdoc in SePoDi
public interface SectorGroupApiInternal {

  @GetMapping("{trafficPointSloid}/overview")
  @PageableAsQueryParam
  Container<SectorGroupVersionModel> getSectorGroupsOfTrafficPoint(@PathVariable String trafficPointSloid,
      @Parameter(hidden = true) Pageable pageable);

  @GetMapping("{sloid}/sectors")
  List<SectorVersionModel> getSectorsBySectorGroupSloid(@PathVariable String sloid);
}
