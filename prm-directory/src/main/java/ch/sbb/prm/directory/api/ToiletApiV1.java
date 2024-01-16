package ch.sbb.prm.directory.api;

import ch.sbb.atlas.api.prm.model.toilet.ReadToiletVersionModel;
import ch.sbb.atlas.api.prm.model.toilet.ToiletVersionModel;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

@Tag(name = "Person with Reduced Mobility")
@RequestMapping("v1/toilets")
public interface ToiletApiV1 {

  @GetMapping
  List<ReadToiletVersionModel> getToilets();

  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping
  ReadToiletVersionModel createToiletVersion(@RequestBody @Valid ToiletVersionModel toiletVersionModel);

  @ResponseStatus(HttpStatus.OK)
  @PutMapping(path = "{id}")
  List<ReadToiletVersionModel> updateToiletVersion(@PathVariable Long id,
      @RequestBody @Valid ToiletVersionModel model);

}
