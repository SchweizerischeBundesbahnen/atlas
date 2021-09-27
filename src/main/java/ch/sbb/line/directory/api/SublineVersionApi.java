package ch.sbb.line.directory.api;

import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Set;
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
  Set<SublineVersionModel> getSublineVersionsBySwissLineNumber(@RequestParam String swissLineNumber);

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  SublineVersionModel createSublineVersion(@RequestBody SublineVersionModel newSublineVersion);

  @PutMapping({"/{id}"})
  SublineVersionModel updateSublineVersion(@PathVariable Long id, @RequestBody SublineVersionModel newVersion);

  @DeleteMapping({"/{id}"})
  void deleteSublineVersion(@PathVariable Long id);
}
