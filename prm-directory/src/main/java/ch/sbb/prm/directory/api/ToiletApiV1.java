package ch.sbb.prm.directory.api;

import ch.sbb.prm.directory.controller.model.create.CreateToiletVersionModel;
import ch.sbb.prm.directory.controller.model.read.ReadToiletVersionModel;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

@Tag(name = "Toilet")
@RequestMapping("v1/toilets")
public interface ToiletApiV1 {

  @GetMapping
  List<ReadToiletVersionModel> getToilets();

  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping
  ReadToiletVersionModel createToiletVersion(@RequestBody CreateToiletVersionModel toiletVersionModel);

}
