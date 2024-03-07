package ch.sbb.exportservice.writer;

import ch.sbb.atlas.export.model.prm.BasePrmCsvModel;
import ch.sbb.atlas.export.model.prm.StopPointVersionCsvModel;
import ch.sbb.atlas.export.model.prm.StopPointVersionCsvModel.Fields;
import org.springframework.stereotype.Component;

@Component
public class CsvStopPointVersionWriter extends BaseCsvWriter<StopPointVersionCsvModel> {

  @Override
  String[] getCsvHeader() {
    return new String[]{
        Fields.sloid, Fields.number, Fields.meansOfTransport, Fields.address, Fields.zipCode, Fields.city,
        Fields.alternativeTransport, Fields.assistanceAvailability, Fields.alternativeTransportCondition,
        Fields.assistanceCondition, Fields.assistanceService, Fields.audioTicketMachine, Fields.additionalInformation,
        Fields.dynamicAudioSystem, Fields.dynamicOpticSystem, Fields.freeText, Fields.infoTicketMachine, Fields.interoperable,
        Fields.assistanceRequestFulfilled, Fields.ticketMachine, Fields.url, Fields.visualInfo, Fields.wheelchairTicketMachine,
        BasePrmCsvModel.Fields.validFrom, BasePrmCsvModel.Fields.validTo, BasePrmCsvModel.Fields.creationDate,
        BasePrmCsvModel.Fields.editionDate, BasePrmCsvModel.Fields.status
    };
  }

}
