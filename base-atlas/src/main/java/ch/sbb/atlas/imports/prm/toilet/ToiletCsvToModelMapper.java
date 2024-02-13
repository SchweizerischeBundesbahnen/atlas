package ch.sbb.atlas.imports.prm.toilet;

import ch.sbb.atlas.api.prm.model.toilet.ToiletVersionModel;
import ch.sbb.atlas.imports.prm.ImportMapperUtil;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ToiletCsvToModelMapper {

  public static ToiletVersionModel toModel(ToiletCsvModel csvModel) {
    return ToiletVersionModel.builder()
        .parentServicePointSloid(csvModel.getDsSloid())
        .sloid(csvModel.getSloid())
        .additionalInformation(csvModel.getInfo())
        .wheelchairToilet(ImportMapperUtil.mapStandardAttributeType(csvModel.getWheelchairToilet()))
        .designation(csvModel.getDescription())
        .validFrom(csvModel.getValidFrom())
        .validTo(csvModel.getValidTo())
        .creationDate(csvModel.getCreatedAt())
        .creator(csvModel.getAddedBy())
        .editor(csvModel.getModifiedBy())
        .editionDate(csvModel.getModifiedAt())
        .build();
  }

}
