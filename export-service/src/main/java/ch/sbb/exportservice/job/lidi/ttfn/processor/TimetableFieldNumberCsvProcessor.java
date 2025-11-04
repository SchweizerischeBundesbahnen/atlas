package ch.sbb.exportservice.job.lidi.ttfn.processor;

import static ch.sbb.exportservice.util.MapperUtil.DATE_FORMATTER;
import static ch.sbb.exportservice.util.MapperUtil.LOCAL_DATE_FORMATTER;

import ch.sbb.exportservice.job.lidi.ttfn.entity.TimetableFieldNumber;
import ch.sbb.exportservice.job.lidi.ttfn.model.TimetableFieldNumberCsvModel;
import ch.sbb.exportservice.util.RowMapperUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;

@Slf4j
public class TimetableFieldNumberCsvProcessor implements ItemProcessor<TimetableFieldNumber, TimetableFieldNumberCsvModel> {

  @Override
  public TimetableFieldNumberCsvModel process(TimetableFieldNumber timetableFieldNumber) {
    return TimetableFieldNumberCsvModel.builder()
        .ttfnid(timetableFieldNumber.getTtfnid())
        .validFrom(DATE_FORMATTER.format(timetableFieldNumber.getValidFrom()))
        .validTo(DATE_FORMATTER.format(timetableFieldNumber.getValidTo()))
        .status(timetableFieldNumber.getStatus())
        .swissTimetableFieldNumber(timetableFieldNumber.getSwissTimetableFieldNumber())
        .number(timetableFieldNumber.getNumber())
        .businessOrganisation(timetableFieldNumber.getBusinessOrganisation())
        .descriptionOutwardLine1(timetableFieldNumber.getDescriptionOutwardLine1())
        .descriptionOutwardLine2(timetableFieldNumber.getDescriptionOutwardLine2())
        .descriptionOutwardLine3(timetableFieldNumber.getDescriptionOutwardLine3())
        .descriptionReturnLine1(timetableFieldNumber.getDescriptionReturnLine1())
        .descriptionReturnLine2(timetableFieldNumber.getDescriptionReturnLine2())
        .descriptionReturnLine3(timetableFieldNumber.getDescriptionReturnLine3())
        .meanOfTransport(timetableFieldNumber.getMeanOfTransport())
        .lineRelations(RowMapperUtil.stringsToPipedString(timetableFieldNumber.getLineRelations()))
        .creationTime(LOCAL_DATE_FORMATTER.format(timetableFieldNumber.getCreationDate()))
        .editionTime(LOCAL_DATE_FORMATTER.format(timetableFieldNumber.getEditionDate()))
        .build();
  }

}
