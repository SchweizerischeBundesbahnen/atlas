package ch.sbb.exportservice.processor;

import ch.sbb.atlas.api.prm.model.referencepoint.ReadReferencePointVersionModel;
import ch.sbb.exportservice.entity.ReferencePointVersion;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;

@Slf4j
public class ReferencePointVersionJsonProcessor extends BaseServicePointProcessor implements ItemProcessor<ReferencePointVersion,
    ReadReferencePointVersionModel> {

  @Override
  public ReadReferencePointVersionModel process(ReferencePointVersion version) {
    return ReadReferencePointVersionModel.builder()
        .id(version.getId())
        .sloid(version.getSloid())
        .number(version.getParentServicePointNumber())
        .parentServicePointSloid(version.getParentServicePointSloid())
        .designation(version.getDesignation())
        .additionalInformation(version.getAdditionalInformation())
        .mainReferencePoint(version.isMainReferencePoint())
        .referencePointType(version.getReferencePointType())
        .validFrom(version.getValidFrom())
        .validTo(version.getValidTo())
        .creationDate(version.getCreationDate())
        .creator(version.getCreator())
        .editionDate(version.getEditionDate())
        .editor(version.getEditor())
        .etagVersion(version.getVersion())
        .build();
  }

}
