package ch.sbb.exportservice.job.sepodi.sector.processor;

import ch.sbb.atlas.api.servicepoint.sector.SectorVersionModel;
import ch.sbb.exportservice.job.sepodi.sector.entity.SectorVersion;
import org.springframework.batch.item.ItemProcessor;

public class SectorJsonProcessor implements ItemProcessor<SectorVersion, SectorVersionModel> {

  @Override
  public SectorVersionModel process(SectorVersion version) throws Exception {
    return SectorVersionModel.builder()
        .id(version.getId())
        .designation(version.getDesignation())
        .east(version.getEast())
        .north(version.getNorth())
        .status(version.getStatus())
        .edgeHeight(version.getEdgeHeight())
        .height(version.getHeight())
        .length(version.getLength())
        .spatialReference(version.getSpatialReference())
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
