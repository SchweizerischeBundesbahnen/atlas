package ch.sbb.business.organisation.directory.api;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "Business Organisations")
@RequestMapping("v1/business-organisations")
public interface BusinessOrganisationApiV1 {

  @GetMapping
  String getHelloWorld();

}
