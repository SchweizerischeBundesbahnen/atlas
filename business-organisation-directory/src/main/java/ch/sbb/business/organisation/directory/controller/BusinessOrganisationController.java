package ch.sbb.business.organisation.directory.controller;

import ch.sbb.business.organisation.directory.api.BusinessOrganisationApiV1;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BusinessOrganisationController implements BusinessOrganisationApiV1 {

  @Override
  public String getHelloWorld() {
    return "Forza Napoli!";
  }
}
