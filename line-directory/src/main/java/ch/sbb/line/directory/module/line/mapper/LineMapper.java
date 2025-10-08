package ch.sbb.line.directory.module.line.mapper;

import ch.sbb.atlas.api.lidi.LineModel;
import ch.sbb.atlas.api.lidi.UpdateLineVersionModelV2;
import ch.sbb.line.directory.module.line.entity.Line;
import ch.sbb.line.directory.module.line.entity.LineVersion;
import lombok.experimental.UtilityClass;

@UtilityClass
public class LineMapper {

  public static LineVersion toEntityFromUpdate(UpdateLineVersionModelV2 lineVersionModel, LineVersion versionToUpdate) {
    return LineVersion.builder()
        .id(lineVersionModel.getId())
        .slnid(lineVersionModel.getSlnid())
        .lineType(versionToUpdate.getLineType())
        .number(lineVersionModel.getNumber())
        .longName(lineVersionModel.getLongName())
        .concessionType(lineVersionModel.getLineConcessionType())
        .shortNumber(lineVersionModel.getShortNumber())
        .offerCategory(lineVersionModel.getOfferCategory())
        .description(lineVersionModel.getDescription())
        .validFrom(lineVersionModel.getValidFrom())
        .validTo(lineVersionModel.getValidTo())
        .businessOrganisation(lineVersionModel.getBusinessOrganisation())
        .comment(lineVersionModel.getComment())
        .swissLineNumber(lineVersionModel.getSwissLineNumber())
        .creationDate(lineVersionModel.getCreationDate())
        .creator(lineVersionModel.getCreator())
        .editionDate(lineVersionModel.getEditionDate())
        .editor(lineVersionModel.getEditor())
        .version(lineVersionModel.getEtagVersion())
        .build();
  }

  public static LineModel toModel(Line line) {
    return LineModel.builder()
        .status(line.getStatus())
        .lidiElementType(line.getLidiElementType())
        .elementType(line.getElementType())
        .slnid(line.getSlnid())
        .number(line.getNumber())
        .description(line.getDescription())
        .validFrom(line.getValidFrom())
        .validTo(line.getValidTo())
        .businessOrganisation(line.getBusinessOrganisation())
        .swissLineNumber(line.getSwissLineNumber())
        .build();
  }

}
