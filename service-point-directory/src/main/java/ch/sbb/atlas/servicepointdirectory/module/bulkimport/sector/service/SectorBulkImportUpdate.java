package ch.sbb.atlas.servicepointdirectory.module.bulkimport.sector.service;

import static ch.sbb.atlas.servicepointdirectory.module.bulkimport.servicepoint.mapper.GeolocationBulkImportUpdateDataMapper.roundToSpatialReferencePrecision;

import ch.sbb.atlas.api.servicepoint.GeolocationBaseCreateModel;
import ch.sbb.atlas.api.servicepoint.SpatialReference;
import ch.sbb.atlas.api.servicepoint.sector.CreateSectorVersionModel;
import ch.sbb.atlas.imports.bulk.BulkImportUpdateContainer;
import ch.sbb.atlas.imports.bulk.BulkImportUpdateDataMapper;
import ch.sbb.atlas.imports.model.SectorUpdateCsvModel;
import ch.sbb.atlas.servicepointdirectory.module.sector.entity.SectorVersion;
import java.util.Optional;

public class SectorBulkImportUpdate extends
    BulkImportUpdateDataMapper<SectorUpdateCsvModel, SectorVersion, CreateSectorVersionModel> {

  public static CreateSectorVersionModel apply(
      BulkImportUpdateContainer<SectorUpdateCsvModel> bulkImportContainer, SectorVersion currentVersion) {
    return new SectorBulkImportUpdate().applyUpdate(bulkImportContainer, currentVersion,
        new CreateSectorVersionModel());
  }

  @Override
  protected void applySpecificUpdate(SectorUpdateCsvModel update, SectorVersion currentVersion,
      CreateSectorVersionModel updateModel) {
    updateModel.setSectorGeolocation(applyGeolocationUpdate(currentVersion, update));
  }

  private static GeolocationBaseCreateModel applyGeolocationUpdate(SectorVersion currentVersion,
      SectorUpdateCsvModel update) {
    SpatialReference spatialReference = Optional.ofNullable(update.getSpatialReference())
        .orElse(currentVersion.getSpatialReference());

    return GeolocationBaseCreateModel.builder()
        .spatialReference(spatialReference)
        .north(Optional.ofNullable(roundToSpatialReferencePrecision(update.getNorth(), spatialReference))
            .orElse(currentVersion.getNorth()))
        .east(Optional.ofNullable(roundToSpatialReferencePrecision(update.getEast(), spatialReference))
            .orElse(currentVersion.getEast()))
        .height(Optional.ofNullable(update.getHeight()).orElse(currentVersion.getHeight()))
        .build();
  }

}
