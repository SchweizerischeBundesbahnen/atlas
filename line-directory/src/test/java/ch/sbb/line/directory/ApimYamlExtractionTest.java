package ch.sbb.line.directory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ch.sbb.atlas.model.controller.IntegrationTest;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@IntegrationTest
@AutoConfigureMockMvc(addFilters = false)
@Slf4j
class ApimYamlExtractionTest {

  private static final Pattern OPERATION_PATTERN = Pattern.compile("^ {4}(get|post|put|patch|delete):.*$");

  @Autowired
  private MockMvc mvc;

  @Value("${info.app.name}")
  private String appName;

  @Test
  void shouldProvideApiYaml() throws Exception {
    MvcResult mvcResult = mvc.perform(get("/v3/api-docs.yaml"))
        .andExpect(status().isOk())
        .andReturn();
    Path specYamlFile = Paths.get("..", "apim-configuration",
        "src/main/resources/apis/", appName, "spec.yaml");

    Path parentDir = specYamlFile.getParent();
    assertThat(parentDir).isNotNull();
    if (!Files.exists(parentDir)) {
      Files.createDirectories(parentDir);
    }

    log.info("Exporting OpenAPI spec.yaml to {}", specYamlFile.toAbsolutePath().normalize());

    byte[] specYamlAsBytes = mvcResult.getResponse().getContentAsByteArray();
    assertThat(specYamlAsBytes).isNotEmpty();

    Files.write(specYamlFile, specYamlAsBytes);
  }

  @Test
  void shouldProvideLineV3PathsAndSchemas() throws Exception {
    //when
    String spec = readApiSpec();

    //then
    assertThat(spec).contains("/v3/lines/versions:")
        .contains("/v3/lines/versions/{slnid}:")
        .contains("LineVersionV3:")
        .contains("ReferencedLineV3:")
        .contains("LineObjectType:")
        .contains("LineMeanOfTransport:")
        .contains("Financing:")
        .contains("ConcessionTypeV3:")
        .contains("LineTypeV3:");
  }

  @Test
  void shouldDeclareCablewayAttributesAsStringsOnLineVersionV3() throws Exception {
    //when
    String spec = readApiSpec();

    //then
    assertThat(spec).contains("cablewayNumber:")
        .contains("cablewayType:")
        .contains("cablewayName:");
  }

  @Test
  void shouldProvideDistinctOperationIdsForLineV3() throws Exception {
    //when
    String spec = readApiSpec();

    //then
    assertThat(spec).contains("getLineVersionsV3Paged")
        .contains("getLineVersionsV3")
        .doesNotContain("getLineVersions_1");
  }

  @Test
  void shouldNotExposeWriteOperationsOnLineV3() throws Exception {
    //when
    String spec = readApiSpec();

    //then
    assertThat(operationsOf(spec, "/v3/lines/versions:")).containsExactly("get");
    assertThat(operationsOf(spec, "/v3/lines/versions/{slnid}:")).containsExactly("get");
  }

  private String readApiSpec() throws Exception {
    return mvc.perform(get("/v3/api-docs.yaml"))
        .andExpect(status().isOk())
        .andReturn()
        .getResponse()
        .getContentAsString();
  }

  /**
   * Collects the HTTP methods declared directly under the given OpenAPI path key. The generated YAML indents path keys with two
   * spaces and their operations with four.
   */
  private static List<String> operationsOf(String spec, String pathKey) {
    List<String> operations = new ArrayList<>();
    String[] lines = spec.split("\n");
    boolean insidePath = false;
    for (String line : lines) {
      if (line.strip().equals(pathKey.strip())) {
        insidePath = true;
        continue;
      }
      if (insidePath) {
        if (!line.startsWith("    ") || line.isBlank()) {
          break;
        }
        Matcher matcher = OPERATION_PATTERN.matcher(line);
        if (matcher.matches()) {
          operations.add(matcher.group(1));
        }
      }
    }
    return operations;
  }

}
