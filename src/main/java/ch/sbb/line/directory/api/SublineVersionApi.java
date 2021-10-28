package ch.sbb.line.directory.api;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Optional;
import org.springdoc.core.converters.models.PageableAsQueryParam;
import org.springframework.data.domain.Pageable;
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

@Tag(name = "sublines")
@RequestMapping("sublines")
public interface SublineVersionApi {

  @GetMapping("/{id}")
  SublineVersionModel getSublineVersion(@PathVariable Long id);

  @GetMapping
  @PageableAsQueryParam
  VersionsContainer<SublineVersionModel> getSublineVersions(@Parameter(hidden = true) Pageable pageable);

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201"),
      @ApiResponse(responseCode = "409", description = "Swiss number is not unique in time", content = @Content)
  })
  SublineVersionModel createSublineVersion(@RequestBody SublineVersionModel newSublineVersion);

  @PutMapping({"/{id}"})
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200"),
      @ApiResponse(responseCode = "409", description = "Swiss number is not unique in time", content = @Content)
  })
  SublineVersionModel updateSublineVersion(@PathVariable Long id, @RequestBody SublineVersionModel newVersion);

  @DeleteMapping({"/{id}"})
  void deleteSublineVersion(@PathVariable Long id);
}
