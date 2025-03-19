package ch.sbb.exportservice.job.lidi.line.processor;

import static ch.sbb.exportservice.util.MapperUtil.DATE_FORMATTER;
import static ch.sbb.exportservice.util.MapperUtil.LOCAL_DATE_FORMATTER;

import ch.sbb.exportservice.job.lidi.line.entity.Line;
import ch.sbb.exportservice.job.lidi.line.model.LineCsvModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;

@Slf4j
public class LineCsvProcessor implements ItemProcessor<Line, LineCsvModel> {

  @Override
  public LineCsvModel process(Line line) {
    return LineCsvModel.builder()
        .slnid(line.getSlnid())
        .validFrom(DATE_FORMATTER.format(line.getValidFrom()))
        .validTo(DATE_FORMATTER.format(line.getValidTo()))
        .status(line.getStatus())
        .lineType(line.getLineType())
        .concessionType(line.getConcessionType())
        .swissLineNumber(line.getSwissLineNumber())
        .description(line.getDescription())
        .longName(line.getLongName())
        .number(line.getNumber())
        .shortNumber(line.getShortNumber())
        .offerCategory(line.getOfferCategory())
        .businessOrganisation(line.getBusinessOrganisation())
        .comment(line.getComment())
        .creationTime(LOCAL_DATE_FORMATTER.format(line.getCreationDate()))
        .editionTime(LOCAL_DATE_FORMATTER.format(line.getEditionDate()))
        .build();
  }

}
