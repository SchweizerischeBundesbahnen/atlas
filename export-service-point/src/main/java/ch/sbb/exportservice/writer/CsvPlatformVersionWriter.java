package ch.sbb.exportservice.writer;

import ch.sbb.atlas.export.model.prm.PlatformVersionCsvModel;

public class CsvPlatformVersionWriter extends BaseCsvWriter<PlatformVersionCsvModel>{

    @Override
    String[] getCsvHeader() {
        return new String[0];
    }
}
