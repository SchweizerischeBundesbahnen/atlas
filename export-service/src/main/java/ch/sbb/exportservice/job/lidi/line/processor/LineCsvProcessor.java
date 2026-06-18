package ch.sbb.exportservice.job.lidi.line.processor;

import static ch.sbb.atlas.helper.DateHelper.DATE_FORMATTER_BASE;
import static ch.sbb.exportservice.util.MapperUtil.LOCAL_DATE_TIME_FORMATTER;

import ch.sbb.exportservice.job.lidi.line.entity.Line;
import ch.sbb.exportservice.job.lidi.line.model.LineCsvModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.infrastructure.item.ItemProcessor;

@Slf4j
public class LineCsvProcessor implements ItemProcessor<Line, LineCsvModel> {

  @Override
  public LineCsvModel process(Line line) {
    return LineCsvModel.builder()
        .slnid(line.getSlnid())
        .linienId(line.getLinienId())
        .validFrom(DATE_FORMATTER_BASE.format(line.getValidFrom()))
        .validTo(DATE_FORMATTER_BASE.format(line.getValidTo()))
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
        .creationTime(LOCAL_DATE_TIME_FORMATTER.format(line.getCreationDate()))
        .editionTime(LOCAL_DATE_TIME_FORMATTER.format(line.getEditionDate()))
        .build();
  }

}
