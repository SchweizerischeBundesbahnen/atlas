package ch.sbb.line.directory.converter;

import static ch.sbb.line.directory.LineTestData.lineVersionModelBuilder;
import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.base.service.model.controller.IntegrationTest;
import ch.sbb.line.directory.api.LineVersionVersionModel;
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
  public void shouldTrimLeadingWhitespaceTest() throws IOException {
    LineVersionVersionModel lineVersionModel = lineVersionModelBuilder().build();
    lineVersionModel.setAlternativeName("   TEST");
    LineVersionVersionModel deserializedVersionModel = serializeThenDeserializeModel(lineVersionModel);
    assertThat(deserializedVersionModel.getAlternativeName()).isEqualTo("TEST");
  }

  @Test
  public void shouldTrimTrailingWhitespaceTest() throws IOException {
    LineVersionVersionModel lineVersionModel = lineVersionModelBuilder().build();
    lineVersionModel.setAlternativeName("TEST   ");
    LineVersionVersionModel deserializedVersionModel = serializeThenDeserializeModel(lineVersionModel);
    assertThat(deserializedVersionModel.getAlternativeName()).isEqualTo("TEST");
  }

  @Test
  public void shouldNotTrimWhitespacesBetweenTest() throws IOException {
    LineVersionVersionModel lineVersionModel = lineVersionModelBuilder().build();
    lineVersionModel.setAlternativeName("   TEST  TEST  . ");
    LineVersionVersionModel deserializedVersionModel = serializeThenDeserializeModel(lineVersionModel);
    assertThat(deserializedVersionModel.getAlternativeName()).isEqualTo("TEST  TEST  .");
  }

  private LineVersionVersionModel serializeThenDeserializeModel(LineVersionVersionModel lineVersionModel)
      throws IOException {
    String serializedLineVersionModel = objectMapper.writeValueAsString(lineVersionModel);
    JsonParser parser = objectMapper.getFactory().createParser(serializedLineVersionModel);
    return parser.readValueAs(LineVersionVersionModel.class);
  }
}
