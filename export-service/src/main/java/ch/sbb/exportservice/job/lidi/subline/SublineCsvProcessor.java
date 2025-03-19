package ch.sbb.exportservice.job.lidi.subline;

import static ch.sbb.exportservice.utile.MapperUtil.DATE_FORMATTER;
import static ch.sbb.exportservice.utile.MapperUtil.LOCAL_DATE_FORMATTER;

import java.util.Objects;
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
        .description(subline.getDescription())
        .longName(subline.getLongName())
        .businessOrganisation(subline.getBusinessOrganisation())
        .creationTime(LOCAL_DATE_FORMATTER.format(subline.getCreationDate()))
        .editionTime(LOCAL_DATE_FORMATTER.format(subline.getEditionDate()))
        // From Line
        .number(Objects.requireNonNull(subline.getNumber(), "Line must have a number!"))
        .swissLineNumber(subline.getSwissLineNumber())
        .shortNumber(subline.getShortNumber())
        .offerCategory(subline.getOfferCategory())
        .build();
  }

}
