package ch.sbb.prm.directory.referencepoint.controller;

import ch.sbb.atlas.api.prm.model.referencepoint.ReadReferencePointVersionModel;
import ch.sbb.atlas.model.Status;
import ch.sbb.prm.directory.referencepoint.service.ReferencePointService;
import ch.sbb.prm.directory.referencepoint.entity.ReferencePointVersion;
import ch.sbb.prm.directory.referencepoint.api.ReferencePointApiInternal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
public class ReferencePointApiInternalController implements ReferencePointApiInternal {

  private final ReferencePointService referencePointService;

  @Override
  public List<ReadReferencePointVersionModel> getReferencePointsOverview(String parentServicePointSloid) {
    List<ReferencePointVersion> referencePointVersions = referencePointService.findByParentServicePointSloid(
        parentServicePointSloid);
    List<ReferencePointVersion> referencePointsNotRevoked = referencePointVersions.stream()
        .filter(referencePointVersion -> referencePointVersion.getStatus() != Status.REVOKED).toList();
    return referencePointService.buildOverview(referencePointsNotRevoked);
  }

}
