package ch.sbb.atlas.api.lidi;

import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "Sublines")
@RequestMapping("v2/sublines")
public interface SublineApiV2 {

  @GetMapping("versions/{slnid}")
  List<SublineVersionModelV2> getSublineVersionV2(@PathVariable String slnid);

//  @PostMapping("versions")
//  @ResponseStatus(HttpStatus.CREATED)
//  @ApiResponses(value = {
//      @ApiResponse(responseCode = "201"),
//      @ApiResponse(responseCode = "409", description = "Swiss number is not unique in time", content = @Content(schema =
//      @Schema(implementation = ErrorResponse.class)))
//  })
//  SublineVersionModelV2 createSublineVersionV2(@RequestBody @Valid CreateLineVersionModelV2 newVersion);
//
//  @PostMapping({"versions/{id}"})
//  @ApiResponses(value = {
//      @ApiResponse(responseCode = "200"),
//      @ApiResponse(responseCode = "409", description = "Swiss number is not unique in time", content = @Content(schema =
//      @Schema(implementation = ErrorResponse.class))),
//      @ApiResponse(responseCode = "412", description = ENTITY_ALREADY_UPDATED, content =
//      @Content(schema = @Schema(implementation = ErrorResponse.class))),
//      @ApiResponse(responseCode = "501", description = VERSIONING_NOT_IMPLEMENTED, content =
//      @Content(schema = @Schema(implementation = ErrorResponse.class))),
//      @ApiResponse(responseCode = "520", description = NO_ENTITIES_WERE_MODIFIED, content =
//      @Content(schema = @Schema(implementation = ErrorResponse.class))),
//  })
//  List<SublineVersionModelV2> updateSublineVersion(@PathVariable Long id, @RequestBody @Valid UpdateLineVersionModelV2 newVersion);

}
