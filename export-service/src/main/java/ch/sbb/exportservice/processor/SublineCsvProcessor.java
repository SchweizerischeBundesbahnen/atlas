package ch.sbb.exportservice.processor;

import static ch.sbb.exportservice.processor.BaseServicePointProcessor.DATE_FORMATTER;
import static ch.sbb.exportservice.processor.BaseServicePointProcessor.LOCAL_DATE_FORMATTER;

import ch.sbb.exportservice.entity.lidi.Subline;
import ch.sbb.exportservice.model.SublineCsvModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;

@Slf4j
public class SublineCsvProcessor implements ItemProcessor<Subline, SublineCsvModel> {

  @Override
  public SublineCsvModel process(Subline subline) {
    return SublineCsvModel.builder()
        .slnid(subline.getSlnid())
        .mainlineSlnid(subline.getMainlineSlnid())
        .validFrom(DATE_FORMATTER.format(subline.getValidFrom()))
        .validTo(DATE_FORMATTER.format(subline.getValidTo()))
        .status(subline.getStatus())
        .sublineType(subline.getSublineType())
        .concessionType(subline.getConcessionType())
        .swissSublineNumber(subline.getSwissSublineNumber())
        .number(subline.getNumber())
        .shortNumber(subline.getShortNumber())
        .offerCategory(subline.getOfferCategory())
        .description(subline.getDescription())
        .longName(subline.getLongName())
        .businessOrganisation(subline.getBusinessOrganisation())
        .creationTime(LOCAL_DATE_FORMATTER.format(subline.getCreationDate()))
        .editionTime(LOCAL_DATE_FORMATTER.format(subline.getEditionDate()))
        .build();
  }

}
