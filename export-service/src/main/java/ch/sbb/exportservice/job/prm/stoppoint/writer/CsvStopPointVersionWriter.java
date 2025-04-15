package ch.sbb.exportservice.job.prm.stoppoint.writer;

import ch.sbb.atlas.amazon.service.FileService;
import ch.sbb.exportservice.job.BaseCsvWriter;
import ch.sbb.exportservice.job.prm.BasePrmCsvModel;
import ch.sbb.exportservice.job.prm.stoppoint.model.StopPointVersionCsvModel;
import ch.sbb.exportservice.job.prm.stoppoint.model.StopPointVersionCsvModel.Fields;
import org.springframework.stereotype.Component;

@Component
public class CsvStopPointVersionWriter extends BaseCsvWriter<StopPointVersionCsvModel> {

  CsvStopPointVersionWriter(FileService fileService) {
    super(fileService);
  }

  @Override
  protected String[] getCsvHeader() {
    return new String[]{
        Fields.sloid, Fields.number, Fields.meansOfTransport, Fields.address, Fields.zipCode, Fields.city,
        Fields.alternativeTransport, Fields.shuttleService, Fields.assistanceAvailability, Fields.alternativeTransportCondition,
        Fields.assistanceCondition, Fields.assistanceService, Fields.audioTicketMachine, Fields.additionalInformation,
        Fields.dynamicAudioSystem, Fields.dynamicOpticSystem, Fields.freeText, Fields.infoTicketMachine, Fields.interoperable,
        Fields.assistanceRequestFulfilled, Fields.ticketMachine, Fields.url, Fields.visualInfo, Fields.wheelchairTicketMachine,
        Fields.recordingObligation,
        BasePrmCsvModel.Fields.validFrom, BasePrmCsvModel.Fields.validTo, BasePrmCsvModel.Fields.creationDate,
        BasePrmCsvModel.Fields.editionDate, BasePrmCsvModel.Fields.status
    };
  }

}
