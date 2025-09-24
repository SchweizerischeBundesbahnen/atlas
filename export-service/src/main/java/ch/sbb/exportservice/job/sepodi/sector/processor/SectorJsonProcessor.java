package ch.sbb.exportservice.job.sepodi.sector.processor;

import ch.sbb.atlas.api.servicepoint.sector.ReadSectorVersionModel;
import ch.sbb.atlas.api.servicepoint.sector.SectorVersionModel;
import ch.sbb.exportservice.job.sepodi.BaseSepodiProcessor;
import ch.sbb.exportservice.job.sepodi.sector.entity.SectorVersion;
import org.springframework.batch.item.ItemProcessor;

public class SectorJsonProcessor extends BaseSepodiProcessor implements ItemProcessor<SectorVersion, SectorVersionModel> {

  @Override
  public ReadSectorVersionModel process(SectorVersion version) throws Exception {
    return ReadSectorVersionModel.builder()
        .id(version.getId())
        .designation(version.getDesignation())
        .sectorGeolocation(toModel(version))
        .edgeHeight(version.getEdgeHeight())
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
