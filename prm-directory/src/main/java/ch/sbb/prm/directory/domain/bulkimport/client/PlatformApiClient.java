package ch.sbb.prm.directory.domain.bulkimport.client;

import ch.sbb.atlas.api.prm.model.platform.PlatformVersionModel;
import ch.sbb.prm.directory.domain.platform.api.PlatformApiV1;
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
