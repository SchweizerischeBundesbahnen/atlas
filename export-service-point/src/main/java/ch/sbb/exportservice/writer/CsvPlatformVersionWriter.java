package ch.sbb.exportservice.writer;

import ch.sbb.atlas.export.model.prm.StopPointVersionCsvModel;

public class CsvPlatformVersionWriter extends BaseCsvWriter<StopPointVersionCsvModel>{

    @Override
    String[] getCsvHeader() {
        return new String[0];
    }
}
