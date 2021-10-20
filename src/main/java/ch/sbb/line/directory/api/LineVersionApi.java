package ch.sbb.line.directory.api;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
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
import org.springframework.web.bind.annotation.ResponseStatus;

@Tag(name = "lines")
public interface LineVersionApi {

  @GetMapping
  @PageableAsQueryParam
  VersionsContainer<LineVersionModel> getLineVersions(@Parameter(hidden = true) Pageable pageable);

  @GetMapping("/{id}")
  LineVersionModel getLineVersion(@PathVariable Long id);

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  LineVersionModel createLineVersion(@RequestBody @Valid LineVersionModel newVersion);

  @PutMapping({"/{id}"})
  LineVersionModel updateLineVersion(@PathVariable Long id,
      @RequestBody @Valid LineVersionModel newVersion);

  @DeleteMapping({"/{id}"})
  void deleteLineVersion(@PathVariable Long id);
}
