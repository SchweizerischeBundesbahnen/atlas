package ch.sbb.prm.directory.service.bulk;

import ch.sbb.atlas.api.prm.model.platform.PlatformVersionModel;
import ch.sbb.prm.directory.api.PlatformApiV1;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class PlatformApiClient {

  private final PlatformApiV1 platformApiV1;

  public void updatePlatform(Long currentVersionId, PlatformVersionModel platformVersionModel) {
    platformApiV1.updatePlatform(currentVersionId, platformVersionModel);
  }

}
