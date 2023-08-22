package ch.sbb.atlas.export;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.util.MinimalPrettyPrinter;

import java.io.IOException;

public class PrettyPrinterCustom extends MinimalPrettyPrinter {

//    @Override
//    public DefaultPrettyPrinter createInstance() {
//        return new PrettyPrinterCustom();
//    }

    public PrettyPrinterCustom() {
        super();
    }

    @Override
    public void beforeArrayValues(JsonGenerator g) throws IOException {
        g.writeRaw(System.lineSeparator());
    }

    @Override
    public void writeRootValueSeparator(JsonGenerator g) throws IOException {
        g.writeRaw(System.lineSeparator());
    }

//    @Override
//    public void writeArrayValueSeparator(JsonGenerator g) throws IOException {
//        g.writeRaw(System.lineSeparator());
//    }

//    @Override // this is between every property inside of BO object
//    public void writeObjectEntrySeparator(JsonGenerator g) throws IOException {
//        g.writeRaw(System.lineSeparator());
//    }
}
