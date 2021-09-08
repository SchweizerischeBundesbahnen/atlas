package ch.sbb.timetable.field.number.api;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
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

@Tag(name = "timetable-field-numbers")
public interface VersionApi {

  @GetMapping
  @PageableAsQueryParam
  VersionsContainer getVersions(@Parameter(hidden = true) Pageable pageable);

  @GetMapping("/{id}")
  VersionModel getVersion(@PathVariable Long id);

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  VersionModel createVersion(@RequestBody VersionModel newVersion);

  @PutMapping({"/{id}"})
  VersionModel updateVersion(@PathVariable Long id, @RequestBody VersionModel newVersion);

  @DeleteMapping({"/{id}"})
  void deleteVersion(@PathVariable Long id);
}
