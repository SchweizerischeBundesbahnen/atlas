package ch.sbb.prm.directory.domain.contactpoint.controller;

import ch.sbb.atlas.api.prm.model.contactpoint.ContactPointOverviewModel;
import ch.sbb.prm.directory.domain.contactpoint.api.ContactPointApiInternal;
import ch.sbb.prm.directory.domain.contactpoint.service.ContactPointService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
public class ContactPointApiInternalController implements ContactPointApiInternal {

  private final ContactPointService contactPointService;

  @Override
  public List<ContactPointOverviewModel> getContactPointOverview(String parentServicePointSloid) {
    return contactPointService.buildOverview(contactPointService.findByParentServicePointSloid(parentServicePointSloid));
  }

}
