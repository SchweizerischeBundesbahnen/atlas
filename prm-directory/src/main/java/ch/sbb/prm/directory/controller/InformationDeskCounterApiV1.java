package ch.sbb.prm.directory.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "InformationDesk")
@RequestMapping("v1/information-desks")
public interface InformationDeskCounterApiV1 {

  @GetMapping
  List<InformationDeskVersionModel> getInformationDesks();

}
