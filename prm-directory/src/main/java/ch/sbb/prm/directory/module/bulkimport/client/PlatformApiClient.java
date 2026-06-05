package ch.sbb.prm.directory.module.bulkimport.client;

import ch.sbb.atlas.api.prm.model.platform.PlatformVersionModel;
import ch.sbb.prm.directory.module.platform.api.PlatformApiV1;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class PlatformApiClient {

  private final PlatformApiV1 platformApiV1;

  public void updatePlatform(Long currentVersionId, PlatformVersionModel platformVersionModel) {
    platformApiV1.updatePlatform(currentVersionId, platformVersionModel);
  }

  public void terminatePlatform(String sloid, LocalDate validTo) {
    platformApiV1.terminatePlatform(sloid, validTo);
  }

}
