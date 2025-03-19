package ch.sbb.exportservice.job.lidi.line;

import ch.sbb.atlas.api.lidi.LineVersionModelV2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;

@Slf4j
public class LineJsonProcessor implements ItemProcessor<Line, LineVersionModelV2> {

  @Override
  public LineVersionModelV2 process(Line line) {
    return LineVersionModelV2.builder()
        .id(line.getId())
        .status(line.getStatus())
        .lineType(line.getLineType())
        .lineConcessionType(line.getConcessionType())
        .swissLineNumber(line.getSwissLineNumber())
        .slnid(line.getSlnid())
        .number(line.getNumber())
        .longName(line.getLongName())
        .shortNumber(line.getShortNumber())
        .offerCategory(line.getOfferCategory())
        .description(line.getDescription())
        .validFrom(line.getValidFrom())
        .validTo(line.getValidTo())
        .businessOrganisation(line.getBusinessOrganisation())
        .comment(line.getComment())
        .etagVersion(line.getVersion())
        .creator(line.getCreator())
        .creationDate(line.getCreationDate())
        .editor(line.getEditor())
        .editionDate(line.getEditionDate())
        .build();
  }

}
