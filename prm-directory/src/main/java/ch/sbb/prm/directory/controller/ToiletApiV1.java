package ch.sbb.prm.directory.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "Toilet")
@RequestMapping("v1/toilets")
public interface ToiletApiV1 {

  @GetMapping
  List<ToiletVersionModel> getToilets();

}
