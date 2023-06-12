package ch.sbb.exportservice.aggregator;

import com.nimbusds.jose.shaded.gson.stream.JsonWriter;
import java.io.IOException;
import java.io.Writer;
import org.springframework.batch.item.file.FlatFileFooterCallback;
import org.springframework.batch.item.file.FlatFileHeaderCallback;

public class ServicePointFooterCallBack implements FlatFileHeaderCallback, FlatFileFooterCallback {

  private static final String JSON_ROOT_NODE = "ServicePointVersions";
  private JsonWriter jsonWriter;

  @Override
  public void writeHeader(Writer writer) throws IOException {
    this.jsonWriter = new JsonWriter(writer);
    jsonWriter.beginArray();
  }

  @Override
  public void writeFooter(Writer writer) throws IOException {
    jsonWriter.endArray();
    jsonWriter.close();
  }

}
