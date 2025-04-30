package ch.sbb.line.directory.service.bulk;

import ch.sbb.atlas.api.lidi.LineApiV2;
import ch.sbb.atlas.api.lidi.UpdateLineVersionModelV2;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class LineApiClient {

  private final LineApiV2 lineApi;

  public void updateLine(Long currentVersionId, UpdateLineVersionModelV2 lineVersionModel) {
    lineApi.updateLineVersion(currentVersionId, lineVersionModel);
  }

}
