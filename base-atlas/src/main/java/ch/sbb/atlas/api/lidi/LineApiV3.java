package ch.sbb.atlas.api.lidi;

import static ch.sbb.atlas.model.ResponseCodeDescription.ENTITY_ALREADY_UPDATED;
import static ch.sbb.atlas.model.ResponseCodeDescription.NO_ENTITIES_WERE_MODIFIED;
import static ch.sbb.atlas.model.ResponseCodeDescription.VERSIONING_NOT_IMPLEMENTED;

import ch.sbb.atlas.annotation.AuthorizedOnly;
import ch.sbb.atlas.annotation.UnauthorizedAllowed;
import ch.sbb.atlas.annotation.UnauthorizedAllowed.FurtherLimitations;
import ch.sbb.atlas.api.model.Container;
import ch.sbb.atlas.api.model.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springdoc.core.annotations.ParameterObject;
import org.springdoc.core.converters.models.PageableAsQueryParam;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

@Tag(name = "Lines V3")
@RequestMapping("v3/lines")
@Validated
public interface LineApiV3 {

  @UnauthorizedAllowed(limitations = FurtherLimitations.NONE)
  @GetMapping("versions")
  @PageableAsQueryParam
  @Operation(operationId = "getLineVersionsV3Paged")
  Container<LineVersionModelV3> getLineVersions(@Parameter(hidden = true) Pageable pageable,
      @Valid @ParameterObject LineVersionV3RequestParams params);

  @UnauthorizedAllowed(limitations = FurtherLimitations.NONE)
  @GetMapping("versions/{slnid}")
  List<LineVersionModelV3> getLineVersionsV3(@PathVariable String slnid);

  @AuthorizedOnly
  @PostMapping("versions")
  @ResponseStatus(HttpStatus.CREATED)
  @Operation(operationId = "createLineVersionV3")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201"),
      @ApiResponse(responseCode = "400", description = "Validation error, e.g. line type not allowed, member type not "
          + "allowed for the referenced line type or concession type / swiss line number rules violated", content =
      @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "403", description = "No permission to create this line", content = @Content(schema =
      @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "404", description = "Referenced line not found", content = @Content(schema =
      @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "409", description = "Swiss line number overlap, member validity lies outside of the "
          + "referenced line validity, referenced line is REVOKED or referenced line is itself a member", content =
      @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "422", description = "Validity duration rule violated: TEMPORARY lines max. 14 days, "
          + "ORDERLY lines min. 15 days", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  LineVersionModelV3 createLineVersionV3(@RequestBody @Valid CreateLineVersionModelV3 newVersion);

  @AuthorizedOnly
  @PutMapping("versions/{id}")
  @Operation(operationId = "updateLineVersionV3")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200"),
      @ApiResponse(responseCode = "400", description = "Validation error, immutable field sent or concession type / swiss "
          + "line number rules violated", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "403", description = "No permission to update this line or the line is REVOKED", content =
      @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "404", description = "Line version not found", content = @Content(schema =
      @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "409", description = "Swiss line number overlap, field not updatable for the line type, "
          + "member validity lies outside of the referenced line validity or the line would have members outside of the "
          + "new line validity", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "412", description = ENTITY_ALREADY_UPDATED, content =
      @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "422", description = "Validity duration rule violated: TEMPORARY lines max. 14 days, "
          + "ORDERLY lines min. 15 days", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "501", description = VERSIONING_NOT_IMPLEMENTED, content =
      @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "520", description = NO_ENTITIES_WERE_MODIFIED, content =
      @Content(schema = @Schema(implementation = ErrorResponse.class))),
  })
  List<LineVersionModelV3> updateLineVersionV3(@PathVariable Long id, @RequestBody @Valid UpdateLineVersionModelV3 newVersion);

}
