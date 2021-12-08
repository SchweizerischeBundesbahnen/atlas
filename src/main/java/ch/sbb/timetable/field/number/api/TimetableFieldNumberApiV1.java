package ch.sbb.timetable.field.number.api;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDate;
import java.util.List;
import javax.validation.Valid;
import org.springdoc.core.converters.models.PageableAsQueryParam;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

@Tag(name = "Timetable field numbers")
@RequestMapping("v1/field-numbers")
public interface TimetableFieldNumberApiV1 {

  @GetMapping
  @PageableAsQueryParam
  TimetableFieldNumberContainer getOverview(@Parameter(hidden = true) Pageable pageable,
      @Parameter @RequestParam(required = false) List<String> searchCriteria,
      @Parameter @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate validOn);

  @GetMapping("/{id}")
  VersionModel getVersion(@PathVariable Long id);

  @GetMapping("versions/{ttfnId}")
  List<VersionModel> getAllVersionsVersioned(@PathVariable String ttfnId);

  @PutMapping({"versions/{id}"})
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200"),
      @ApiResponse(responseCode = "409", description = "Number or SwissTimeTableFieldNumber are already taken", content = @Content)
  })
  List<VersionModel> updateVersionWithVersioning(@PathVariable Long id, @RequestBody @Valid VersionModel newVersion);

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201"),
      @ApiResponse(responseCode = "409", description = "Number or SwissTimeTableFieldNumber are already taken", content = @Content)
  })
  VersionModel createVersion(@RequestBody @Valid VersionModel newVersion);

  @PutMapping({"/{id}"})
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200"),
      @ApiResponse(responseCode = "409", description = "Number or SwissTimeTableFieldNumber are already taken", content = @Content)
  })
  VersionModel updateVersion(@PathVariable Long id, @RequestBody @Valid VersionModel newVersion);

  @DeleteMapping({"/{ttfnid}"})
  void deleteVersions(@PathVariable String ttfnid);
}
