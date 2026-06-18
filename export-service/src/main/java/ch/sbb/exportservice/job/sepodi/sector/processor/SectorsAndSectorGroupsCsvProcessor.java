package ch.sbb.exportservice.job.sepodi.sector.processor;

import static ch.sbb.atlas.helper.DateHelper.DATE_FORMATTER_BASE;
import static ch.sbb.exportservice.util.MapperUtil.LOCAL_DATE_TIME_FORMATTER;

import ch.sbb.atlas.api.servicepoint.GeolocationBaseReadModel;
import ch.sbb.exportservice.job.sepodi.BaseSepodiProcessor;
import ch.sbb.exportservice.job.sepodi.sector.entity.SectorAndSectorGroup;
import ch.sbb.exportservice.job.sepodi.sector.model.SectorAndSectorGroupCsvModel;
import ch.sbb.exportservice.job.sepodi.sector.model.SectorAndSectorGroupCsvModel.SectorAndSectorGroupCsvModelBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.infrastructure.item.ItemProcessor;

@Slf4j
public class SectorsAndSectorGroupsCsvProcessor extends BaseSepodiProcessor implements
    ItemProcessor<SectorAndSectorGroup, SectorAndSectorGroupCsvModel> {

  @Override
  public SectorAndSectorGroupCsvModel process(SectorAndSectorGroup version) {
    SectorAndSectorGroupCsvModelBuilder builder = SectorAndSectorGroupCsvModel.builder()
        .sloid(version.getSloid())
        .type(version.getType())
        .trafficPointSloid(version.getTrafficPointSloid())
        .validFrom(DATE_FORMATTER_BASE.format(version.getValidFrom()))
        .validTo(DATE_FORMATTER_BASE.format(version.getValidTo()))
        .designation(version.getDesignation())
        .length(version.getLength())
        .edgeHeight(version.getEdgeHeight())
        .relatedGroups(version.getRelatedGroups())
        .relatedSectors(version.getRelatedSectors())
        .status(version.getStatus())
        .creationDate(LOCAL_DATE_TIME_FORMATTER.format(version.getCreationDate()))
        .editionDate(LOCAL_DATE_TIME_FORMATTER.format(version.getEditionDate()));
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
