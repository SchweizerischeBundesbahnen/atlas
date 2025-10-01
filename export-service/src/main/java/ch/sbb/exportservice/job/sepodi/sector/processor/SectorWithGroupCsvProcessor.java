package ch.sbb.exportservice.job.sepodi.sector.processor;

import ch.sbb.atlas.api.servicepoint.GeolocationBaseReadModel;
import ch.sbb.exportservice.job.sepodi.BaseSepodiProcessor;
import ch.sbb.exportservice.job.sepodi.sector.entity.SectorWithGroupVersion;
import ch.sbb.exportservice.job.sepodi.sector.model.SectorWithGroupVersionCsvModel;
import ch.sbb.exportservice.job.sepodi.sector.model.SectorWithGroupVersionCsvModel.SectorWithGroupVersionCsvModelBuilder;
import ch.sbb.exportservice.util.MapperUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;

@Slf4j
public class SectorWithGroupCsvProcessor extends BaseSepodiProcessor implements
    ItemProcessor<SectorWithGroupVersion, SectorWithGroupVersionCsvModel> {

  @Override
  public SectorWithGroupVersionCsvModel process(SectorWithGroupVersion version) {
    SectorWithGroupVersionCsvModelBuilder builder = SectorWithGroupVersionCsvModel.builder()
        .sloid(version.getSloid())
        .type(version.getType())
        .trafficPointSloid(version.getTrafficPointSloid())
        .validFrom(MapperUtil.DATE_FORMATTER.format(version.getValidFrom()))
        .validTo(MapperUtil.DATE_FORMATTER.format(version.getValidTo()))
        .designation(version.getDesignation())
        .length(version.getLength())
        .edgeHeight(version.getEdgeHeight())
        .relatedGroups(version.getRelatedGroups())
        .relatedSectors(version.getRelatedSectors())
        .creationDate(MapperUtil.LOCAL_DATE_FORMATTER.format(version.getCreationDate()))
        .editionDate(MapperUtil.LOCAL_DATE_FORMATTER.format(version.getEditionDate()));
    if (version.getSpatialReference() != null) {
      GeolocationBaseReadModel geolocation = toModel(version);
      builder.height(geolocation.getHeight())
          .lv95East(geolocation.getLv95().getEast())
          .lv95North(geolocation.getLv95().getNorth())
          .wgs84East(geolocation.getWgs84().getEast())
          .wgs84North(geolocation.getWgs84().getNorth())
          .spatialReference(geolocation.getSpatialReference());
    }
    return builder.build();
  }

}
