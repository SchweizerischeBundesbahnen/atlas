package ch.sbb.line.directory.mapper;

import ch.sbb.atlas.api.line.LineVersionSnapshotModel;
import ch.sbb.line.directory.converter.CmykColorConverter;
import ch.sbb.line.directory.converter.RgbColorConverter;
import ch.sbb.line.directory.entity.LineVersionSnapshot;
import lombok.experimental.UtilityClass;

@UtilityClass
public class LineVersionSnapshotMapper {

  public static LineVersionSnapshotModel toModel(LineVersionSnapshot lineVersionSnapshot) {
    return LineVersionSnapshotModel.builder()
        .id(lineVersionSnapshot.getId())
        .workflowId(lineVersionSnapshot.getWorkflowId())
        .workflowStatus(lineVersionSnapshot.getWorkflowStatus())
        .parentObjectId(lineVersionSnapshot.getParentObjectId())
        .slnid(lineVersionSnapshot.getSlnid())
        .status(lineVersionSnapshot.getStatus())
        .lineType(lineVersionSnapshot.getLineType())
        .paymentType(lineVersionSnapshot.getPaymentType())
        .number(lineVersionSnapshot.getNumber())
        .alternativeName(lineVersionSnapshot.getAlternativeName())
        .combinationName(lineVersionSnapshot.getCombinationName())
        .longName(lineVersionSnapshot.getLongName())
        .colorFontRgb(RgbColorConverter.toHex(lineVersionSnapshot.getColorFontRgb()))
        .colorBackRgb(RgbColorConverter.toHex(lineVersionSnapshot.getColorBackRgb()))
        .colorFontCmyk(CmykColorConverter.toCmykString(lineVersionSnapshot.getColorFontCmyk()))
        .colorBackCmyk(CmykColorConverter.toCmykString(lineVersionSnapshot.getColorBackCmyk()))
        .icon(lineVersionSnapshot.getIcon())
        .description(lineVersionSnapshot.getDescription())
        .validFrom(lineVersionSnapshot.getValidFrom())
        .validTo(lineVersionSnapshot.getValidTo())
        .businessOrganisation(lineVersionSnapshot.getBusinessOrganisation())
        .comment(lineVersionSnapshot.getComment())
        .creationDate(lineVersionSnapshot.getCreationDate())
        .editionDate(lineVersionSnapshot.getEditionDate())
        .creator(lineVersionSnapshot.getCreator())
        .editor(lineVersionSnapshot.getEditor())
        .etagVersion(lineVersionSnapshot.getVersion())
        .build();
  }


}
