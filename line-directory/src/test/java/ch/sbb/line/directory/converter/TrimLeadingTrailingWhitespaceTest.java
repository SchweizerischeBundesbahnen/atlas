package ch.sbb.line.directory.converter;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.api.lidi.LineVersionModelV2;
import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.line.directory.module.line.LineTestData;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.stream.Stream;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;

@IntegrationTest
class TrimLeadingTrailingWhitespaceTest {

  @Autowired
  private ObjectMapper objectMapper;

  @ParameterizedTest
  @MethodSource("longNameTestCases")
  void shouldTrimLeadingWhitespaceTest(Pair<String, String> longNameTestcase) throws IOException {
    LineVersionModelV2 lineVersionModel = LineTestData.createLineVersionModelBuilder().build();
    lineVersionModel.setLongName(longNameTestcase.getLeft());
    LineVersionModelV2 deserializedVersionModel = serializeThenDeserializeModel(lineVersionModel);
    assertThat(deserializedVersionModel.getLongName()).isEqualTo(longNameTestcase.getRight());
  }

  private static Stream<Pair<String, String>> longNameTestCases() {
    return Stream.of(
        Pair.of("   TEST", "TEST"),
        Pair.of("TEST    ", "TEST"),
        Pair.of("   TEST  TEST  . ", "TEST  TEST  .")
    );
  }

  private LineVersionModelV2 serializeThenDeserializeModel(LineVersionModelV2 lineVersionModel)
      throws IOException {
    String serializedLineVersionModel = objectMapper.writeValueAsString(lineVersionModel);
    JsonParser parser = objectMapper.getFactory().createParser(serializedLineVersionModel);
    return parser.readValueAs(LineVersionModelV2.class);
  }
}
