package ch.sbb.exportservice.writer;

import ch.sbb.atlas.export.model.prm.PlatformVersionCsvModel;
import org.springframework.stereotype.Component;

@Component
public class CsvPlatformVersionWriter extends BaseCsvWriter<PlatformVersionCsvModel>{

    @Override
    String[] getCsvHeader() {
        return new String[0];
    }
}
