package ch.sbb.atlas.configuration;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.api.lidi.LineVersionModelV2;
import java.util.stream.Stream;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.context.annotation.Import;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.ObjectMapper;

@JsonTest(properties = "spring.main.banner-mode=off")
@Import(JacksonJsonConfig.class)
class JacksonJsonConfigTest {

  @Autowired
  private ObjectMapper objectMapper;

  @ParameterizedTest
  @MethodSource("longNameTestCases")
  void shouldTrimLeadingWhitespaceTest(Pair<String, String> longNameTestcase) {
    LineVersionModelV2 lineVersionModel = LineVersionModelV2.builder().build();
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

  private LineVersionModelV2 serializeThenDeserializeModel(LineVersionModelV2 lineVersionModel) {
    String serializedLineVersionModel = objectMapper.writeValueAsString(lineVersionModel);
    try (JsonParser parser = objectMapper.createParser(serializedLineVersionModel)) {
      return parser.readValueAs(LineVersionModelV2.class);
    }
  }
}
