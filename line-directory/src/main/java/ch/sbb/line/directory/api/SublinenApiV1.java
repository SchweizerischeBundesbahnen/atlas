package ch.sbb.line.directory.api;

import ch.sbb.atlas.model.Status;
import ch.sbb.atlas.model.api.AtlasApiConstants;
import ch.sbb.atlas.model.api.Container;
import ch.sbb.atlas.model.api.ErrorResponse;
import ch.sbb.line.directory.enumaration.SublineType;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import javax.validation.Valid;
import org.springdoc.core.converters.models.PageableAsQueryParam;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

@Tag(name = "Sublines")
@RequestMapping("v1/sublines")
public interface SublinenApiV1 {

  @GetMapping
  @PageableAsQueryParam
  Container<SublineModel> getSublines(@Parameter(hidden = true) Pageable pageable,
      @RequestParam(required = false) List<String> searchCriteria,
      @RequestParam(required = false) List<Status> statusRestrictions,
      @RequestParam(required = false) List<SublineType> typeRestrictions,
      @RequestParam(required = false) @DateTimeFormat(pattern = AtlasApiConstants.DATE_FORMAT_PATTERN) Optional<LocalDate> validOn);

  @DeleteMapping("{slnid}")
  void deleteSublines(@PathVariable String slnid);

  @PostMapping("versions")
  @ResponseStatus(HttpStatus.CREATED)
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201"),
      @ApiResponse(responseCode = "409", description = "Swiss number is not unique in time", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  SublineVersionModel createSublineVersion(
      @RequestBody @Valid SublineVersionModel newSublineVersion);

  @GetMapping("versions/{slnid}")
  List<SublineVersionModel> getSublineVersion(@PathVariable String slnid);

  @PostMapping({"versions/{id}"})
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200"),
      @ApiResponse(responseCode = "409", description = "Swiss number is not unique in time", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "412", description = "Entity has already been updated (etagVersion out of date)", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  List<SublineVersionModel> updateSublineVersion(@PathVariable Long id,
      @RequestBody @Valid SublineVersionModel newVersion);

  @GetMapping("subline-coverage/{slnid}")
  CoverageModel getSublineCoverage(@PathVariable String slnid);
}
