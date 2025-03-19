package ch.sbb.exportservice.job.lidi.subline;

import ch.sbb.atlas.api.lidi.ReadSublineVersionModelV2;
import java.util.Objects;
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
        .mainLineNumber(Objects.requireNonNull(subline.getNumber(), "Line must have a number!"))
        .mainSwissLineNumber(subline.getSwissLineNumber())
        .mainShortNumber(subline.getShortNumber())
        .mainLineOfferCategory(subline.getOfferCategory())
        .build();
  }

}
