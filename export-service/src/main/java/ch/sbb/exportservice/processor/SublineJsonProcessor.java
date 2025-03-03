package ch.sbb.exportservice.processor;

import ch.sbb.atlas.api.lidi.ReadSublineVersionModelV2;
import ch.sbb.exportservice.entity.lidi.Subline;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;

@Slf4j
public class SublineJsonProcessor implements ItemProcessor<Subline, ReadSublineVersionModelV2> {

  @Override
  public ReadSublineVersionModelV2 process(Subline subline) {
    return ReadSublineVersionModelV2.builder()
        .id(subline.getId())
        .swissSublineNumber(subline.getSwissSublineNumber())
        .mainlineSlnid(subline.getMainlineSlnid())
        .sublineConcessionType(subline.getConcessionType())
        .status(subline.getStatus())
        .sublineType(subline.getSublineType())
        .slnid(subline.getSlnid())
        .description(subline.getDescription())
        .longName(subline.getLongName())
        .validFrom(subline.getValidFrom())
        .validTo(subline.getValidTo())
        .businessOrganisation(subline.getBusinessOrganisation())
        .etagVersion(subline.getVersion())
        .creator(subline.getCreator())
        .creationDate(subline.getCreationDate())
        .editor(subline.getEditor())
        .editionDate(subline.getEditionDate())
        // From Line
        .mainSwissLineNumber(subline.getSwissLineNumber())
        .mainLineNumber(subline.getNumber())
        .mainShortNumber(subline.getShortNumber())
        .mainLineOfferCategory(subline.getOfferCategory())
        .build();
  }

}
