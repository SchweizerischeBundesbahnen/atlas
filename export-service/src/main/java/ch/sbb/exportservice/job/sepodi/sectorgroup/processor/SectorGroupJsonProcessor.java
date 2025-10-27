package ch.sbb.exportservice.job.sepodi.sectorgroup.processor;

import ch.sbb.atlas.api.servicepoint.sector.ReadSectorGroupVersionModel;
import ch.sbb.exportservice.job.sepodi.BaseSepodiProcessor;
import ch.sbb.exportservice.job.sepodi.sectorgroup.entity.SectorGroupVersion;
import org.springframework.batch.item.ItemProcessor;

public class SectorGroupJsonProcessor extends BaseSepodiProcessor implements
    ItemProcessor<SectorGroupVersion, ReadSectorGroupVersionModel> {

  @Override
  public ReadSectorGroupVersionModel process(SectorGroupVersion version) throws Exception {
    return ReadSectorGroupVersionModel.builder()
        .id(version.getId())
        .designation(version.getDesignation())
        .length(version.getLength())
        .sloid(version.getSloid())
        .trafficPointSloid(version.getTrafficPointSloid())
        .validFrom(version.getValidFrom())
        .validTo(version.getValidTo())
        .etagVersion(version.getVersion())
        .creationDate(version.getCreationDate())
        .creator(version.getCreator())
        .editionDate(version.getEditionDate())
        .editor(version.getEditor())
        .build();
  }
}
