package ch.sbb.exportservice.processor;

import ch.sbb.atlas.api.lidi.TimetableFieldNumberVersionModel;
import ch.sbb.exportservice.entity.lidi.TimetableFieldNumber;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;

@Slf4j
public class TimetableFieldNumberJsonProcessor implements ItemProcessor<TimetableFieldNumber, TimetableFieldNumberVersionModel> {

  @Override
  public TimetableFieldNumberVersionModel process(TimetableFieldNumber timetableFieldNumber) {
    return TimetableFieldNumberVersionModel.builder()
        .id(timetableFieldNumber.getId())
        .description(timetableFieldNumber.getDescription())
        .number(timetableFieldNumber.getNumber())
        .ttfnid(timetableFieldNumber.getTtfnid())
        .swissTimetableFieldNumber(timetableFieldNumber.getSwissTimetableFieldNumber())
        .status(timetableFieldNumber.getStatus())
        .validFrom(timetableFieldNumber.getValidFrom())
        .validTo(timetableFieldNumber.getValidTo())
        .businessOrganisation(timetableFieldNumber.getBusinessOrganisation())
        .comment(timetableFieldNumber.getComment())
        .creator(timetableFieldNumber.getCreator())
        .creationDate(timetableFieldNumber.getCreationDate())
        .editor(timetableFieldNumber.getEditor())
        .editionDate(timetableFieldNumber.getEditionDate())
        .etagVersion(timetableFieldNumber.getVersion())
        .build();
  }

}
