package ch.sbb.exportservice.processor;

import ch.sbb.atlas.api.servicepoint.ReadLoadingPointVersionModel;
import ch.sbb.exportservice.entity.LoadingPointVersion;
import org.springframework.batch.item.ItemProcessor;

public class LoadingPointVersionJsonProcessor implements ItemProcessor<LoadingPointVersion, ReadLoadingPointVersionModel> {

  @Override
  public ReadLoadingPointVersionModel process(LoadingPointVersion version) {
    return ReadLoadingPointVersionModel.builder()
        .id(version.getId())
        .number(version.getNumber())
        .designation(version.getDesignation())
        .designationLong(version.getDesignationLong())
        .connectionPoint(version.isConnectionPoint())
        .validFrom(version.getValidFrom())
        .validTo(version.getValidTo())
        .etagVersion(version.getVersion())
        .creationDate(version.getCreationDate())
        .creator(version.getCreator())
        .editionDate(version.getEditionDate())
        .editor(version.getEditor())
        .servicePointNumber(version.getServicePointNumber())
        .servicePointSloid(version.getParentSloidServicePoint())
        .build();
  }

}
