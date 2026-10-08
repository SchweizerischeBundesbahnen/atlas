package ch.sbb.line.directory.module.line.mapper;

import ch.sbb.atlas.api.lidi.LineVersionModelV3;
import ch.sbb.atlas.api.lidi.ReferencedLineModelV3;
import ch.sbb.atlas.api.lidi.enumaration.v3.ConcessionType;
import ch.sbb.atlas.api.lidi.enumaration.v3.LineObjectType;
import ch.sbb.atlas.api.lidi.enumaration.v3.LineType;
import ch.sbb.line.directory.module.line.entity.LineVersionV3;
import java.util.Set;
import lombok.experimental.UtilityClass;

@UtilityClass
public class LineVersionV3Mapper {

  private static final Set<LineType> OWN_CONCESSION_TYPES = Set.of(LineType.ORDERLY, LineType.ZONE);

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
        .concessionType(resolveConcessionType(version, parent))
        .offerCategory(version.getOfferCategory())
        .financing(version.getFinancing())
        .cablewayNumber(version.getCablewayNumber())
        .cablewayType(version.getCablewayType())
        .cablewayName(version.getCablewayName())
        .comment(version.getComment())
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

  public static LineObjectType resolveType(LineVersionV3 version) {
    return version.getReferencedLineSlnid() == null ? LineObjectType.LINE : LineObjectType.SUBLINE;
  }

  private static ConcessionType resolveConcessionType(LineVersionV3 version, LineVersionV3 parent) {
    if (parent == null || OWN_CONCESSION_TYPES.contains(version.getLineType())) {
      return version.getConcessionType();
    }
    return parent.getConcessionType();
  }

  private static ReferencedLineModelV3 toReferencedLine(LineVersionV3 parent) {
    if (parent == null) {
      return null;
    }
    return ReferencedLineModelV3.builder()
        .number(parent.getNumber())
        .lineNumber(parent.getNumber())
        .swissLineNumber(parent.getSwissLineNumber())
        .lineSlnid(parent.getSlnid())
        .build();
  }

}
