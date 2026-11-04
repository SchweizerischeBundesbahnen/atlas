package ch.sbb.line.directory.module.line.mapper;

import ch.sbb.atlas.api.lidi.CreateLineVersionModelV3;
import ch.sbb.atlas.api.lidi.LineVersionModelV3;
import ch.sbb.atlas.api.lidi.ReferencedLineModelV3;
import ch.sbb.atlas.api.lidi.UpdateLineVersionModelV3;
import ch.sbb.atlas.api.lidi.enumaration.v3.LineObjectType;
import ch.sbb.line.directory.module.line.entity.LineVersionV3;
import lombok.experimental.UtilityClass;

@UtilityClass
public class LineVersionV3Mapper {

  public static LineVersionModelV3 toModel(LineVersionV3 version, LineVersionV3 parent) {
    return LineVersionModelV3.builder()
        .id(version.getId())
        .slnid(version.getSlnid())
        .type(resolveType(version))
        .number(version.getNumber())
        .description(version.getDescription())
        .longName(version.getLongName())
        .swissLineNumber(version.getSwissLineNumber())
        .linienId(version.getLinienId())
        .lineType(version.getLineType())
        .businessOrganisation(version.getBusinessOrganisation())
        .meansOfTransport(version.getMeansOfTransport())
        .concessionType(version.getConcessionType())
        .offerCategory(version.getOfferCategory())
        .financing(version.getFinancing())
        .cablewayNumber(version.getCablewayNumber())
        .cablewayType(version.getCablewayType())
        .cablewayName(version.getCablewayName())
        .comment(version.getComment())
        .fotComment(version.getFotComment())
        .referencedLine(toReferencedLine(parent))
        .validFrom(version.getValidFrom())
        .validTo(version.getValidTo())
        .status(version.getStatus())
        .creationDate(version.getCreationDate())
        .creator(version.getCreator())
        .editionDate(version.getEditionDate())
        .editor(version.getEditor())
        .etagVersion(version.getVersion())
        .build();
  }

  public static LineVersionV3 toEntity(CreateLineVersionModelV3 model) {
    return LineVersionV3.builder()
        .referencedLineSlnid(model.getReferencedLineSlnid())
        .lineType(model.getLineType())
        .concessionType(model.getConcessionType())
        .number(model.getNumber())
        .offerCategory(model.getOfferCategory())
        .description(model.getDescription())
        .longName(model.getLongName())
        .swissLineNumber(model.getSwissLineNumber())
        .linienId(model.getLinienId())
        .businessOrganisation(model.getBusinessOrganisation())
        .meansOfTransport(model.getMeansOfTransport())
        .financing(model.getFinancing())
        .cablewayNumber(model.getCablewayNumber())
        .cablewayType(model.getCablewayType())
        .cablewayName(model.getCablewayName())
        .comment(model.getComment())
        .fotComment(model.getFotComment())
        .validFrom(model.getValidFrom())
        .validTo(model.getValidTo())
        .build();
  }

  public static LineVersionV3 toEntityFromUpdate(UpdateLineVersionModelV3 model, LineVersionV3 current) {
    return LineVersionV3.builder()
        .id(current.getId())
        .slnid(current.getSlnid())
        .referencedLineSlnid(current.getReferencedLineSlnid())
        .lineType(current.getLineType())
        .concessionType(model.getConcessionType())
        .number(model.getNumber())
        .offerCategory(model.getOfferCategory())
        .description(model.getDescription())
        .longName(model.getLongName())
        .swissLineNumber(model.getSwissLineNumber())
        .linienId(model.getLinienId())
        .businessOrganisation(model.getBusinessOrganisation())
        .meansOfTransport(model.getMeansOfTransport())
        .financing(model.getFinancing())
        .cablewayNumber(model.getCablewayNumber())
        .cablewayType(model.getCablewayType())
        .cablewayName(model.getCablewayName())
        .comment(model.getComment())
        .fotComment(model.getFotComment())
        .validFrom(model.getValidFrom())
        .validTo(model.getValidTo())
        .version(model.getEtagVersion())
        .build();
  }

  public static LineObjectType resolveType(LineVersionV3 version) {
    return version.getReferencedLineSlnid() == null ? LineObjectType.LINE : LineObjectType.SUBLINE;
  }

  /**
   * Members never carry number, swissLineNumber, offerCategory and concessionType themselves; those values are read from the
   * referenced line and exposed here.
   */
  private static ReferencedLineModelV3 toReferencedLine(LineVersionV3 parent) {
    if (parent == null) {
      return null;
    }
    return ReferencedLineModelV3.builder()
        .number(parent.getNumber())
        .swissLineNumber(parent.getSwissLineNumber())
        .offerCategory(parent.getOfferCategory())
        .concessionType(parent.getConcessionType())
        .lineSlnid(parent.getSlnid())
        .build();
  }

}
