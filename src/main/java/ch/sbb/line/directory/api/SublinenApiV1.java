package ch.sbb.line.directory.api;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import javax.validation.Valid;
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
import org.springframework.web.bind.annotation.ResponseStatus;

@Tag(name = "Sublines")
@RequestMapping("v1/sublines")
public interface SublinenApiV1 {

  @GetMapping
  @PageableAsQueryParam
  Container<SublineModel> getSublines(@Parameter(hidden = true) Pageable pageable);

  @GetMapping("/{slnid}")
  List<SublineVersionModel> getSubline(@PathVariable String slnid);

  @GetMapping("version/{id}")
  SublineVersionModel getSublineVersion(@PathVariable Long id);

  @PostMapping("version")
  @ResponseStatus(HttpStatus.CREATED)
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201"),
      @ApiResponse(responseCode = "409", description = "Swiss number is not unique in time", content = @Content)
  })
  SublineVersionModel createSublineVersion(@RequestBody SublineVersionModel newSublineVersion);

  @PutMapping({"versions/{id}"})
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200"),
      @ApiResponse(responseCode = "409", description = "Swiss number is not unique in time", content = @Content)
  })
  List<SublineVersionModel> updateWithVersioning(@PathVariable Long id,
      @RequestBody @Valid SublineVersionModel newVersion);

  @DeleteMapping("version/{id}")
  void deleteSublineVersion(@PathVariable Long id);
}
