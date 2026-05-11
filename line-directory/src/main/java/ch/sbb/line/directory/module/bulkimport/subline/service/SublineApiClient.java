package ch.sbb.line.directory.module.bulkimport.subline.service;

import ch.sbb.atlas.api.lidi.SublineApiV2;
import ch.sbb.atlas.api.lidi.SublineVersionModelV2;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class SublineApiClient {

  private final SublineApiV2 sublineApi;

  public void updateSubline(Long currentVersionId, SublineVersionModelV2 sublineVersionModel) {
    sublineApi.updateSublineVersionV2(currentVersionId, sublineVersionModel);
  }

}

