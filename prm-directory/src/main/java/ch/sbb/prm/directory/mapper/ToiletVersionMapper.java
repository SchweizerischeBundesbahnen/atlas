package ch.sbb.prm.directory.mapper;

import ch.sbb.prm.directory.controller.model.ToiletVersionModel;
import ch.sbb.prm.directory.entity.ToiletVersion;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ToiletVersionMapper {

  public static ToiletVersionModel toModel(ToiletVersion version){
    return ToiletVersionModel.builder()
        .id(version.getId())
        .sloid(version.getSloid())
        .parentServicePointSloid(version.getParentServicePointSloid())
        .number(version.getNumber())
        .validFrom(version.getValidFrom())
        .validTo(version.getValidTo())
        .designation(version.getDesignation())
        .info(version.getInfo())
        .wheelchairToilet(version.getWheelchairToilet())
        .creator(version.getCreator())
        .creationDate(version.getCreationDate())
        .editor(version.getEditor())
        .editionDate(version.getEditionDate())
        .etagVersion(version.getVersion())
        .build();
  }

}
