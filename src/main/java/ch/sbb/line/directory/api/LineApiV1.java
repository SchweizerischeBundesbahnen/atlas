package ch.sbb.line.directory.api;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.Optional;
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

@Tag(name = "Lines")
@RequestMapping("v1/lines")
public interface LineApiV1 {

  @GetMapping
  @PageableAsQueryParam
  Container<LineModel> getLines(@Parameter(hidden = true) Pageable pageable, @Parameter Optional<String> swissLineNumber);

  @GetMapping("/{slnid}")
  List<LineVersionModel> getLine(@PathVariable String slnid);

  @DeleteMapping("{slnid}")
  void deleteLines(@PathVariable String slnid);

  @PostMapping("versions")
  @ResponseStatus(HttpStatus.CREATED)
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201"),
      @ApiResponse(responseCode = "409", description = "Swiss number is not unique in time", content = @Content)
  })
  LineVersionModel createLineVersion(@RequestBody @Valid LineVersionModel newVersion);

  @GetMapping("versions/{id}")
  LineVersionModel getLineVersion(@PathVariable Long id);


  @PutMapping({"versions/{id}"})
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200"),
      @ApiResponse(responseCode = "409", description = "Swiss number is not unique in time", content = @Content)
  })
  List<LineVersionModel> updateLineVersion(@PathVariable Long id,
      @RequestBody @Valid LineVersionModel newVersion);

}
