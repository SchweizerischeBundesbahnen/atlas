package ch.sbb.line.directory.mapper;

import ch.sbb.atlas.api.lidi.LineVersionSnapshotModel;
import ch.sbb.line.directory.entity.LineVersionSnapshot;
import lombok.experimental.UtilityClass;

@UtilityClass
public class LineVersionSnapshotMapper {

  public static LineVersionSnapshotModel toModel(LineVersionSnapshot lineVersionSnapshot) {
    return LineVersionSnapshotModel.builder()
        .id(lineVersionSnapshot.getId())
        .lineConcessionType(lineVersionSnapshot.getConcessionType())
        .shortNumber(lineVersionSnapshot.getShortNumber())
        .offerCategory(lineVersionSnapshot.getOfferCategory())
        .workflowId(lineVersionSnapshot.getWorkflowId())
        .workflowStatus(lineVersionSnapshot.getWorkflowStatus())
        .parentObjectId(lineVersionSnapshot.getParentObjectId())
        .slnid(lineVersionSnapshot.getSlnid())
        .status(lineVersionSnapshot.getStatus())
        .lineType(lineVersionSnapshot.getLineType())
        .paymentType(lineVersionSnapshot.getPaymentType())
        .number(lineVersionSnapshot.getNumber())
        .longName(lineVersionSnapshot.getLongName())
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
