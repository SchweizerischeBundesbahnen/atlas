package ch.sbb.line.directory.module.line.mapper;

import ch.sbb.atlas.api.lidi.LineVersionModelV3;
import ch.sbb.atlas.api.lidi.ReferencedLineModelV3;
import ch.sbb.atlas.api.lidi.enumaration.LineObjectType;
import ch.sbb.line.directory.module.line.entity.LineVersionV3;
import lombok.experimental.UtilityClass;

@UtilityClass
public class LineVersionV3Mapper {

  /**
   * Maps a {@link LineVersionV3} row to its read model.
   *
   * <p>All top-level values are taken from the row itself. Values of the referenced line are exposed exclusively inside
   * {@link ReferencedLineModelV3}; {@code meansOfTransport} and {@code financing} are never inherited from the parent.
   *
   * @param version the row to map, never {@code null}
   * @param parent  the resolved direct parent version, or {@code null} when the row is a LINE or the reference is unresolvable
   */
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

  /**
   * Derives the {@link LineObjectType} discriminator from the presence of a referenced line.
   */
  public static LineObjectType resolveType(LineVersionV3 version) {
    return version.getReferencedLineSlnid() == null ? LineObjectType.LINE : LineObjectType.SUBLINE;
  }

  /**
   * Builds the nested reference. Nesting depth is structurally capped at 1: {@link ReferencedLineModelV3} carries no further
   * reference, so a parent that is itself a SUBLINE never contributes its own parent.
   */
  private static ReferencedLineModelV3 toReferencedLine(LineVersionV3 parent) {
    if (parent == null) {
      return null;
    }
    return ReferencedLineModelV3.builder()
        .number(parent.getNumber())
        .lineNumber(parent.getNumber())
        .swissLineNumber(parent.getSwissLineNumber())
        .lineSlnid(parent.getSlnid())
        .concessionType(parent.getConcessionType())
        .build();
  }

}
