package ch.sbb.exportservice.job.prm.toilet.processor;

import ch.sbb.atlas.api.prm.model.toilet.ReadToiletVersionModel;
import ch.sbb.exportservice.job.prm.toilet.entity.ToiletVersion;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;

@Slf4j
public class ToiletVersionJsonProcessor implements ItemProcessor<ToiletVersion, ReadToiletVersionModel> {

  @Override
  public ReadToiletVersionModel process(ToiletVersion version) {
    return ReadToiletVersionModel.builder()
        .id(version.getId())
        .sloid(version.getSloid())
        .number(version.getParentServicePointNumber())
        .parentServicePointSloid(version.getParentServicePointSloid())
        .designation(version.getDesignation())
        .additionalInformation(version.getAdditionalInformation())
        .wheelchairToilet(version.getWheelchairToilet())
        .validFrom(version.getValidFrom())
        .validTo(version.getValidTo())
        .creationDate(version.getCreationDate())
        .creator(version.getCreator())
        .editionDate(version.getEditionDate())
        .editor(version.getEditor())
        .etagVersion(version.getVersion())
        .status(version.getStatus())
        .build();
  }

}
