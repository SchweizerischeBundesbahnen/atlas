package ch.sbb.atlas.servicepointdirectory.module.servicepoint;

import ch.sbb.atlas.api.AtlasApiConstants;
import ch.sbb.atlas.api.model.ErrorResponse;
import ch.sbb.atlas.api.servicepoint.ReadServicePointVersionModel;
import ch.sbb.atlas.servicepointdirectory.module.servicepoint.globalid.GlobalIdUpdateModel;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = AtlasApiConstants.INTERNAL_API_TAG_PREFIX + "Service Points")
@RequestMapping("internal/service-points")
@Validated
public interface ServicePointGlobalIdApiInternal {

  @ApiResponses(value = {
      @ApiResponse(responseCode = "400", description = "Global-ID is invalid for the stop point", content =
      @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "409", description = "Global-ID is already used by another stop point", content =
      @Content(schema = @Schema(implementation = ErrorResponse.class))),
  })
  @PreAuthorize("@businessOrganisationBasedUserAdministrationService.isAtLeastSupervisor(T(ch.sbb.atlas.kafka.model.user.admin"
      + ".ApplicationType).SEPODI)")
  @PutMapping("{servicePointNumber}/global-id")
  List<ReadServicePointVersionModel> updateGlobalId(@PathVariable Integer servicePointNumber,
      @Valid @RequestBody GlobalIdUpdateModel globalId);

}
