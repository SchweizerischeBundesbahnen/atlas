package ch.sbb.prm.directory.mapper;

import ch.sbb.atlas.api.prm.model.toilet.ReadToiletVersionModel;
import ch.sbb.atlas.api.prm.model.toilet.ToiletVersionModel;
import ch.sbb.prm.directory.entity.ToiletVersion;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ToiletVersionMapper {

  public static ReadToiletVersionModel toModel(ToiletVersion version){
    return ReadToiletVersionModel.builder()
        .id(version.getId())
        .sloid(version.getSloid())
        .parentServicePointSloid(version.getParentServicePointSloid())
        .number(version.getNumber())
        .validFrom(version.getValidFrom())
        .validTo(version.getValidTo())
        .designation(version.getDesignation())
        .additionalInformation(version.getAdditionalInformation())
        .wheelchairToilet(version.getWheelchairToilet())
        .creator(version.getCreator())
        .creationDate(version.getCreationDate())
        .editor(version.getEditor())
        .editionDate(version.getEditionDate())
        .etagVersion(version.getVersion())
        .build();
  }

  public static ToiletVersion toEntity(ToiletVersionModel model){
    return ToiletVersion.builder()
        .id(model.getId())
        .sloid(model.getSloid())
        .parentServicePointSloid(model.getParentServicePointSloid())
        .number(new Sloid(model.getParentServicePointSloid()).getServicePointNumber())
        .validFrom(model.getValidFrom())
        .validTo(model.getValidTo())
        .designation(model.getDesignation())
        .additionalInformation(model.getAdditionalInformation())
        .wheelchairToilet(model.getWheelchairToilet())
        .creator(model.getCreator())
        .creationDate(model.getCreationDate())
        .editor(model.getEditor())
        .editionDate(model.getEditionDate())
        .version(model.getEtagVersion())
        .build();
  }

}
