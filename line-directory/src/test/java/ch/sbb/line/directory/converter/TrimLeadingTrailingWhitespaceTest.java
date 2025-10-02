package ch.sbb.line.directory.converter;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.api.lidi.LineVersionModelV2;
import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.line.directory.module.line.LineTestData;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@IntegrationTest
class TrimLeadingTrailingWhitespaceTest {

  @Autowired
  private ObjectMapper objectMapper;

  @Test
  void shouldTrimLeadingWhitespaceTest() throws IOException {
    LineVersionModelV2 lineVersionModel = LineTestData.createLineVersionModelBuilder().build();
    lineVersionModel.setLongName("   TEST");
    LineVersionModelV2 deserializedVersionModel = serializeThenDeserializeModel(lineVersionModel);
    assertThat(deserializedVersionModel.getLongName()).isEqualTo("TEST");
  }

  @Test
  void shouldTrimTrailingWhitespaceTest() throws IOException {
    LineVersionModelV2 lineVersionModel = LineTestData.createLineVersionModelBuilder().build();
    lineVersionModel.setLongName("TEST   ");
    LineVersionModelV2 deserializedVersionModel = serializeThenDeserializeModel(lineVersionModel);
    assertThat(deserializedVersionModel.getLongName()).isEqualTo("TEST");
  }

  @Test
  void shouldNotTrimWhitespacesBetweenTest() throws IOException {
    LineVersionModelV2 lineVersionModel = LineTestData.createLineVersionModelBuilder().build();
    lineVersionModel.setLongName("   TEST  TEST  . ");
    LineVersionModelV2 deserializedVersionModel = serializeThenDeserializeModel(lineVersionModel);
    assertThat(deserializedVersionModel.getLongName()).isEqualTo("TEST  TEST  .");
  }

  private LineVersionModelV2 serializeThenDeserializeModel(LineVersionModelV2 lineVersionModel)
      throws IOException {
    String serializedLineVersionModel = objectMapper.writeValueAsString(lineVersionModel);
    JsonParser parser = objectMapper.getFactory().createParser(serializedLineVersionModel);
    return parser.readValueAs(LineVersionModelV2.class);
  }
}
