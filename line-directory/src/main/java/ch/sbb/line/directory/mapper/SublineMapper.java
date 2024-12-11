package ch.sbb.line.directory.mapper;

import ch.sbb.atlas.api.lidi.CreateSublineVersionModelV2;
import ch.sbb.atlas.api.lidi.ReadSublineVersionModelV2;
import ch.sbb.atlas.api.lidi.SublineVersionModelV2;
import ch.sbb.line.directory.entity.LineVersion;
import ch.sbb.line.directory.entity.SublineVersion;
import lombok.experimental.UtilityClass;

@UtilityClass
public class SublineMapper {

  public static ReadSublineVersionModelV2 toModel(SublineVersion sublineVersion, LineVersion lineVersion) {
    return ReadSublineVersionModelV2.builder()
        .id(sublineVersion.getId())
        .swissSublineNumber(sublineVersion.getSwissSublineNumber())
        .mainlineSlnid(sublineVersion.getMainlineSlnid())
        .sublineConcessionType(sublineVersion.getConcessionType())
        .status(sublineVersion.getStatus())
        .sublineType(sublineVersion.getSublineType())
        .slnid(sublineVersion.getSlnid())
        .description(sublineVersion.getDescription())
        .longName(sublineVersion.getLongName())
        .validFrom(sublineVersion.getValidFrom())
        .validTo(sublineVersion.getValidTo())
        .businessOrganisation(sublineVersion.getBusinessOrganisation())
        .etagVersion(sublineVersion.getVersion())
        .creator(sublineVersion.getCreator())
        .creationDate(sublineVersion.getCreationDate())
        .editor(sublineVersion.getEditor())
        .editionDate(sublineVersion.getEditionDate())
        .mainSwissLineNumber(lineVersion.getSwissLineNumber())
        .mainLineNumber(lineVersion.getNumber())
        .mainShortNumber(lineVersion.getShortNumber())
        .mainLineOfferCategory(lineVersion.getOfferCategory())
        .build();
  }

  public static SublineVersion toEntity(SublineVersionModelV2 model) {
    return SublineVersion.builder()
        .id(model.getId())
        .swissSublineNumber(model.getSwissSublineNumber())
        .mainlineSlnid(model.getMainlineSlnid())
        .concessionType(model.getSublineConcessionType())
        .status(model.getStatus())
        .slnid(model.getSlnid())
        .description(model.getDescription())
        .longName(model.getLongName())
        .validFrom(model.getValidFrom())
        .validTo(model.getValidTo())
        .businessOrganisation(model.getBusinessOrganisation())
        .version(model.getEtagVersion())
        .creator(model.getCreator())
        .creationDate(model.getCreationDate())
        .editor(model.getEditor())
        .editionDate(model.getEditionDate())
        .build();
  }

  public static SublineVersion toEntity(CreateSublineVersionModelV2 model) {
    return SublineVersion.builder()
        .id(model.getId())
        .swissSublineNumber(model.getSwissSublineNumber())
        .mainlineSlnid(model.getMainlineSlnid())
        .concessionType(model.getSublineConcessionType())
        .status(model.getStatus())
        .sublineType(model.getSublineType())
        .slnid(model.getSlnid())
        .description(model.getDescription())
        .longName(model.getLongName())
        .validFrom(model.getValidFrom())
        .validTo(model.getValidTo())
        .businessOrganisation(model.getBusinessOrganisation())
        .version(model.getEtagVersion())
        .creator(model.getCreator())
        .creationDate(model.getCreationDate())
        .editor(model.getEditor())
        .editionDate(model.getEditionDate())
        .build();
  }
}
